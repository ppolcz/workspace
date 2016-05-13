package polcz.budget.service;

import static polcz.budget.service.OdfValidationService.IND_CAIDS;
import static polcz.budget.service.OdfValidationService.IND_DATE;
import static polcz.budget.service.OdfValidationService.IND_TRS;
import static polcz.budget.service.OdfValidationService.lkbrnMarkets;
import static polcz.budget.service.OdfValidationService.market2market;
import static polcz.budget.service.OdfValidationService.munkaMarkets;
import static polcz.budget.service.OdfValidationService.nMarkets;
import static polcz.budget.service.OdfValidationService.nptcIntroductionRowNr;
import static polcz.budget.service.OdfValidationService.ruhaMarkets;
import static polcz.budget.service.OdfValidationService.sdf;
import static polcz.budget.service.OdfValidationService.szuksegesMarkets;
import static polcz.budget.service.OdfValidationService.utazasMarkets;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.jboss.logging.Logger;
import org.junit.Assert;
import org.odftoolkit.simple.SpreadsheetDocument;
import org.odftoolkit.simple.table.Row;
import org.odftoolkit.simple.table.Table;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import polcz.budget.global.R;
import polcz.budget.model.TChargeAccount;
import polcz.budget.model.TCluster;
import polcz.budget.model.TMarket;
import polcz.budget.model.TTransaction;
import polcz.budget.model.TTransactionType;
import polcz.budget.service.helper.OdfRule;
import polcz.budget.service.helper.TransactionArguments;

@Service
// @Transactional
public class OdfLoaderService {

    @Autowired
    EntityService service;

    @Autowired
    TransactionService trService;

    @Autowired
    StartupService ss;

    @Autowired
    OdfValidationService odfs;

    // @PersistenceContext
    // EntityManager em;

    Logger logger;

    private Map<TChargeAccount, Integer> indCa = new HashMap<>();

    private Map<String, TCluster> cllist;
    private Map<String, TChargeAccount> calist;
    private Map<String, TMarket> mklist;

    /* charge accounts */
    private TChargeAccount none, pkez, potp, dkez, dotp, nptc, info, pinfo;

    /* clusters */
    private TCluster /* Nem_Adott, */ Utazas, Lakas_Berendezes, Ruhazkodas, Munkaeszkozok, Szamolas,
            Athelyezes, Szukseges, Napi_Szukseglet, Egyeb_Kiadas /* , Rezsi */;

    /* markets */
    private TMarket Market_Not_Applicable;

    public void process() {
        init();
        parse();
    }

    private void init() {
        logger = R.getJBossLogger(this.getClass());

        none = ss.none();
        pkez = ss.pkez();
        potp = ss.ca(new TChargeAccount("potp", "Peti OTP Bank"));
        dkez = ss.ca(new TChargeAccount("dkez", "Dori kezpenz"));
        dotp = ss.ca(new TChargeAccount("dotp", "Dori OTP Bank"));
        nptc = ss.ca(new TChargeAccount("nptc", "nagypenztarca"));

        info = new TChargeAccount("info"); /* not persisted */
        info.setUid(123123);

        pinfo = new TChargeAccount("pinfo"); /* not persisted */
        pinfo.setUid(1931212);

        Szamolas = ss.Szamolas();
        // Nem_Adott = ss.Nem_Adott();
        Athelyezes = ss.Athelyezes();
        Szukseges = ss.Szukseges();
        Napi_Szukseglet = ss.Napi_Szukseglet();
        Egyeb_Kiadas = ss.Egyeb_Kiadas();
        Utazas = ss.cluster(new TCluster("Utazas"));
        Lakas_Berendezes = ss.cluster(new TCluster("Lakas_Berendezes"));
        Ruhazkodas = ss.cluster(new TCluster("Ruhazkodas"));
        Munkaeszkozok = ss.cluster(new TCluster("Munkaeszkozok"));
        // Rezsi = ss.cluster(new TCluster("Rezsi"));

        Market_Not_Applicable = ss.Market_Not_Applicable();
    }

    /**
     * @param odfDocumentPath
     * @param startIndex
     */
    private void parse() {
        /* Charge accounts appearing in the ODF document */
        calist = new HashMap<String, TChargeAccount>();
        {
            calist.put("pkez", pkez);
            calist.put("potp", potp);
            calist.put("dkez", dkez);
            calist.put("dotp", dotp);
            // calist.put("nptc", nptc);
            calist.put("info", info);
            calist.put("pinfo", pinfo);
        }

        // --------------------

        String odfDocumentPath = "/home/ppolcz/Dropbox/koltsegvetes.ods";
        int startIndex = 3;

        try {
            logger.info("Elkezdtem olvasni a dokumentumot");
            SpreadsheetDocument data = SpreadsheetDocument.loadDocument(odfDocumentPath);

            logger.info("Kivalasztom a Validity tablat");
            Table valTable = data.getTableByName("Validity");
            final int valFirstRow = 1;

            cllist = new HashMap<>();
            mklist = new HashMap<>();

            for (int i = valFirstRow; i < valTable.getRowCount(); ++i) {
                Row row = valTable.getRowByIndex(i);
                String odfclname = getString(row, 0).toLowerCase();
                String sqlclname = getString(row, 1);
                String parentname = getString(row, 3);
                int sgn = getInteger(row, 2);

                if (sqlclname.isEmpty())
                    break;

                if (calist.containsKey(sqlclname))
                    continue;

                /* find parent by its name */
                TCluster parent = service.findByName(parentname, TCluster.class);

                /* if parent not exists (YET), initialize it (later will be updated) */
                if (parent == null && !parentname.isEmpty()) parent = service.update(new TCluster(parentname));

                /* insert or update cluster */
                TCluster cluster = service.update(new TCluster(sqlclname, sgn, parent), TCluster.class);
                cllist.put(odfclname, cluster);

                // logger.infof("odf = %s, sql = %s ---> %s", odfclname, sqlclname, cluster);
            }

            /* parse table Koltsegvetes_Uj */
            logger.info("Kivalasztom a fo tablat");
            Table table = data.getTableByName("Koltsegvetes_Uj");
            koltsegvetesUj(table, startIndex);
        } catch (Exception e) {
            e.printStackTrace();
        }
        logger.info("Minden rendben, lefutottam");
    }

    // private Map<TChargeAccount, Integer> balance = new HashMap<>();

    private void initialBalance(Date date, TChargeAccount ca, int balance) {
        TTransaction tr = new TTransaction();
        tr.setDate(date);
        tr.setCa(ca);
        tr.setCluster(Szamolas);
        tr.setPivot(true);
        tr.setBalance(balance);
        tr.setCatransfer(none);
        tr.setMarket(Market_Not_Applicable);
        tr.setRemark("initial pivot [the very first]");
        tr.setType(TTransactionType.pivot);

        // service.update(tr); // TODO: legyegesen le van egyszerusitve a regihez kepest
        trService.makeTransaction(new TransactionArguments(tr, null, R.TR_INSERTION, "initial balance"));
    }

    /**
     * @param table
     * @param startIndex
     */
    public void koltsegvetesUj(Table table, int startIndex) {
        int count = table.getRowCount();
        logger.info("Ennyi sor van a tablaban: " + count);

        /* I read the very first row after the header, which contains the first balance */
        {
            Row row = table.getRowByIndex(startIndex - 1);
            Date date = getDate(row, IND_DATE);

            int i = 0;
            for (TChargeAccount ca : new TChargeAccount[] { potp, pkez, dotp, dkez }) {
                indCa.put(ca, i);
                initialBalance(date, ca, getInteger(row, IND_CAIDS + i));
                ++i;
            }

            /* nptc */
            indCa.put(nptc, 3);
            row = table.getRowByIndex(nptcIntroductionRowNr);
            initialBalance(getDate(row, IND_DATE), nptc, getInteger(row, IND_CAIDS + 3));
        }

        Date today = new Date();

        /* loop over the rows of the table */
        for (int i = startIndex; i < count; i++) {
            if (i == nptcIntroductionRowNr) {
                calist.remove("dkez");
                calist.put("dkez", nptc);
            }

            String errmsg = "Problem: row nr. " + (i + 1) + ", ";

            Row row = table.getRowByIndex(i);
            Date date = getDate(row, IND_DATE);

            /* stop when we have left the actual date */
            if (date.after(today))
                break;

            /* run throw all transactions of this row */
            for (int t = 0; t < IND_TRS.length; ++t)
                transaction(new OdfTransactionInterpreter(row, t, date, errmsg));
        }
    }

    private class OdfTransactionInterpreter {
        String errmsg;
        int index;
        int amount, balance;
        String formula;
        String caname;
        String clname;
        String mkname;
        String remark;
        String rextra;
        Date date;

        private Row row;
        TTransaction tr = new TTransaction();

        void loadBalance() {
            balance = getInteger(row, IND_CAIDS + indCa.get(tr.getCa()));

            /* update transaction object */
            tr.setBalance(balance);
            tr.setEndofdayBalance(balance);
        }

        public OdfTransactionInterpreter(Row row, int index, Date date, String errmsg) {
            this.row = row;
            this.date = date;
            this.index = index;
            this.errmsg = errmsg;
            load();
        }

        private void load() {
            errmsg += "trans. nr. " + (index + 1) + ", date: " + sdf.format(date) + ".\n";

            /* load data from ODF */
            int o = IND_TRS[index]; /* column offset of transaction nr t. */
            amount = getInteger(row, o + 0);
            formula = getFormula(row, o + 0);
            caname = getString(row, o + 1).toLowerCase();
            clname = getString(row, o + 2).toLowerCase();
            mkname = getString(row, o + 3);
            remark = getString(row, o + 4);
            rextra = "tr_" + Integer.toString(index + 1);

            /* decorate remark - this can be done anywhere */
            if (!remark.isEmpty())
                remark += " ";
            if (!formula.isEmpty())
                remark += "[" + formula.substring(4) + "] ";
            remark += "{ " + rextra + " }";
            remark.trim();

            /* initialize transaction object */
            tr.setRemark(remark);
            tr.setDate(date);
            tr.setProductInfo(remark.startsWith(R.ODF_PRODUCT_INFO_SUFFIX));
        }

        public boolean resolveCa() {
            TChargeAccount ca = calist.get(caname);
            if (ca == null) {
                try {
                    /* this transaction's form is not filled in */
                    Assert.assertTrue(errmsg + "ca is NOT info, BUT market || cluster is NOT NULL. "
                            + "Perhaps, this transaction's form is not filled in. tr: " + tr,
                            mkname.isEmpty() && clname.isEmpty());
                } catch (AssertionError e) {
                    logger.warn(e.getMessage());
                }
                return false;
            }

            if (ca.equals(info)) {
                // logger.infof(errmsg + "ca = %s, amount = %d, remark = %s", ca.getName(), amount, remark);
                return false;
            }

            if (ca.equals(pinfo)) {
                // TODO: TProductInfo
                logger.warnf(errmsg + "Try handling the product info entries: %s, tr: %s", ca, tr);
                return false;
            }
            Assert.assertNotEquals(errmsg + "At this point the charge account mustn't be null", ca, null);
            Assert.assertNotEquals(errmsg + "At this point the charge account mustn't be none", ca, none);
            Assert.assertNotEquals(errmsg + "At this point the charge account mustn't be pinfo", ca, pinfo);
            Assert.assertNotEquals(errmsg + "At this point the charge account mustn't be info", ca, info);

            /* [ASSERT] For sure: ca != (null | info | pinfo | none) */

            /* update transaction object */
            tr.setCa(ca);

            /* resolve balance (only after the charge account is known) */
            loadBalance();

            return true;
        }

        public void resolveMarket() {

            /* correct misspelled market names */
            if (market2market.containsKey(mkname))
                mkname = market2market.get(mkname);

            TMarket market;
            if (mkname.isEmpty()) {
                market = Market_Not_Applicable;
            } else if (mklist.containsKey(mkname)) {
                market = mklist.get(mkname);
            } else {
                market = service.update(new TMarket(mkname, ""), TMarket.class);
                mklist.put(mkname, market);
            }

            tr.setMarket(market);
        }

        public boolean isTransfer() {
            if (calist.containsKey(clname)) {

                /* this is a transfer transaction */
                tr.setCatransfer(calist.get(clname));
                tr.setCluster(Athelyezes);
                tr.setPivot(false);
                tr.setType(TTransactionType.transfer);
                return true;

            }

            /* simple or pivot transaction */
            tr.setCatransfer(none);
            return false;
        }

        public boolean resolveClusterIfNotTransfer() {
            TCluster cluster = null;
            if (!clname.isEmpty()) {
                cluster = cllist.get(clname);
            }

            tr.setCluster(cluster);

            return guessCluster();
        }

        public boolean guessCluster() {
            TCluster cluster = tr.getCluster();

            /* Guess cluster knowing the market's name and some predefined rules */
            if (cluster == null) {
                mkname = mkname.toLowerCase();
                if (tr.getMarket().equals(Market_Not_Applicable)) {
                    cluster = Egyeb_Kiadas;
                } else if (cluster == null && nMarkets.contains(mkname)) {
                    cluster = Napi_Szukseglet;
                } else if (utazasMarkets.contains(mkname)) {
                    cluster = Utazas;
                } else if (ruhaMarkets.contains(mkname)) {
                    cluster = Ruhazkodas;
                } else if (lkbrnMarkets.contains(mkname)) {
                    cluster = Lakas_Berendezes;
                } else if (szuksegesMarkets.contains(mkname)) {
                    cluster = Szukseges;
                } else if (munkaMarkets.contains(mkname)) {
                    cluster = Munkaeszkozok;
                }
            }

            tr.setCluster(cluster);

            if (cluster == null) {
                logger.errorf(errmsg + "Cluster is null: clname = %s", clname);
                logger.errorf("Transaction = %s", tr);
                return false;
            }

            return true;
        }

        public void resolveTrTypeIfNotTransfer() {
            if (tr.getCluster().equals(Szamolas)) {
                tr.setPivot(true);
                tr.setType(TTransactionType.pivot);
            } else
                tr.setType(TTransactionType.simple);
        }

        public void finalize() {
            tr.setAmount(amount * tr.getCluster().getSgn());
        }

        public void postResolveMarketIfItIsABill() {
            for (OdfRule rule : odfs.getPostProcessingRules()) {
                try {
                    rule.apply(tr, errmsg);
                } catch (AssertionError | Exception ex) {
                    logger.fatal(ex.getMessage());
                }
            }
        }

    } /* end of OdfTransactionInterpreter */

    public void transaction(OdfTransactionInterpreter d) {

        if (!d.resolveCa()) return;

        d.resolveMarket();

        if (!d.isTransfer()) {

            if (!d.resolveClusterIfNotTransfer()) return;

            d.resolveTrTypeIfNotTransfer();
        }

        d.finalize();

        d.postResolveMarketIfItIsABill();

        logger.infof("%s", d.tr);
        trService.makeTransaction(new TransactionArguments(d.tr, null, R.TR_INSERTION, d.errmsg));

        // Assert.assertFalse(errmsg + "amount shouldn't be zero, cluster: " + tr.getCluster(),
        // tr.isPivot() && tr.getAmount() == 0);
    }

    private Date getDate(Row row, int index) {
        try {
            return row.getCellByIndex(IND_DATE).getDateValue().getTime();
        } catch (Exception e) {
            logger.info("Problem");
            return new Date();
        }
    }

    private int getInteger(Row row, int index) {
        try {
            return row.getCellByIndex(index).getDoubleValue().intValue();
        } catch (Exception e) {
            return 0;
        }
    }

    private String getString(Row row, int index) {
        String ret = row.getCellByIndex(index).getStringValue();
        if (ret == null)
            return "";
        return ret.trim();
    }

    private String getFormula(Row row, int index) {
        String ret = row.getCellByIndex(index).getFormula();
        if (ret == null)
            return "";
        return ret;
    }

    // UNUSED

    @SuppressWarnings("unused")
    private double getDouble(Row row, int index) {
        try {
            return row.getCellByIndex(index).getDoubleValue();
        } catch (Exception e) {
            return 0;
        }
    }

    @SuppressWarnings("unused")
    private int getIntegerOrThrow(Row row, int index) {
        return row.getCellByIndex(index).getDoubleValue().intValue();
    }

    @SuppressWarnings("unused")
    private String getNote(Row row, int index) {
        return row.getCellByIndex(index).getNoteText();
    }
}
