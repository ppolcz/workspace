package polcz.budget.service;

import static polcz.budget.service.OdfValidationService.*;

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
import polcz.budget.model.ChargeAccount;
import polcz.budget.model.Cluster;
import polcz.budget.model.Market;
import polcz.budget.model.Ugylet;
import polcz.budget.model.UgyletType;
import polcz.budget.service.helper.OdfRule;
import polcz.budget.service.helper.TransactionArguments;

// 2018.06.01. (június  1, péntek), 06:42
// How to create entity to an SQL view
// https://stackoverflow.com/questions/21255016/how-to-create-call-a-sql-view-in-hibernate?utm_medium=organic&utm_source=google_rich_qa&utm_campaign=google_rich_qa
// <database-object>
//     <create><![CDATA[CREATE VIEW docView
//      AS
//      SELECT * from document;
//      GO]]></create>
//     <drop>DROP VIEW docView</drop>
//     <dialect-scope name='org.hibernate.dialect.SQLServerDialect' />
// </database-object>

@Service
// @Transactional
public class OdfLoaderService {

    @Autowired
    EntityService service;

    @Autowired
    UgyletService trService;

    @Autowired
    StartupService ss;

    @Autowired
    OdfValidationService odfs;

    // @PersistenceContext
    // EntityManager em;

    Logger logger;

    private final Map<ChargeAccount, Integer> indCa = new HashMap<>();

    private Map<String, Cluster> cllist;
    private Map<String, ChargeAccount> calist;
    private Map<String, Market> mklist;

    /* charge accounts */
    private ChargeAccount none, /* pkez, potp, dkez, dotp, nptc, */ info, pinfo;

    /* clusters */
    private Cluster /* Nem_Adott, */ Utazas, Lakas_Berendezes, Ruhazkodas, Munkaeszkozok, Szamolas,
            Athelyezes, Szukseges, Napi_Szukseglet, Egyeb_Kiadas /* , Rezsi */;

    /* markets */
    private Market Market_Not_Applicable;

    private String odfDocumentPath;

    public void process(String odfName) {
        odfDocumentPath = "/home/ppolcz/T/Dropbox/Peti/Desktop/Koltsegvetes/" + odfName;
        init();
        parse();
    }

    private void init() {
        logger = R.getJBossLogger(this.getClass());

        none = ss.none();
        // pkez = ss.pkez();
        // potp = ss.ca(new ChargeAccount("potp", "Peti OTP Bank"));
        // dkez = ss.ca(new ChargeAccount("dkez", "Dori kezpenz"));
        // dotp = ss.ca(new ChargeAccount("dotp", "Dori OTP Bank"));
        // nptc = ss.ca(new ChargeAccount("nptc", "nagypenztarca"));

        info = new ChargeAccount("info"); /* not persisted */
        info.setUid(123123);

        pinfo = new ChargeAccount("pinfo"); /* not persisted */
        pinfo.setUid(1931212);

        Szamolas = ss.Szamolas();
        // Nem_Adott = ss.Nem_Adott();
        Athelyezes = ss.Athelyezes();
        Szukseges = ss.Szukseges();
        Napi_Szukseglet = ss.Napi_Szukseglet();
        Egyeb_Kiadas = ss.Egyeb_Kiadas();
        Utazas = ss.cluster(new Cluster("Utazas"));
        Lakas_Berendezes = ss.cluster(new Cluster("Lakas_Berendezes"));
        Ruhazkodas = ss.cluster(new Cluster("Ruhazkodas"));
        Munkaeszkozok = ss.cluster(new Cluster("Munkaeszkozok"));
        // Rezsi = ss.cluster(new Cluster("Rezsi"));

        Market_Not_Applicable = ss.Market_Not_Applicable();
    }

    private void initializeAccounts(Table table, int startIndex)
    {
        Row rowAccountNames = table.getRowByIndex(1);
        Row rowInitialBalance = table.getRowByIndex(startIndex - 1);
        Date date = getDate(rowInitialBalance, IND_DATE);
        for (int i = IND_CAIDS; i < TR_OFFSET; i++) {
            String name = getString(rowAccountNames, i);

            if (!name.isEmpty())
            {
                ChargeAccount ca = ss.ca(new ChargeAccount(name));
                calist.put(name, ca);
                logger.info(name);

                indCa.put(ca, i);
                logger.info(getString(rowInitialBalance, i));
                initialBalance(date, ca, getInteger(rowInitialBalance, i));
            }
        }
    }

    /**
     * @param odfDocumentPath
     * @param startIndex
     */
    private void parse() {
        /* Charge accounts appearing in the ODF document */

        // --------------------

        int startIndex = 3;

        try {
            /* Load ODF document and its tables */

            logger.info("Elkezdtem olvasni a dokumentumot");
            SpreadsheetDocument data = SpreadsheetDocument.loadDocument(odfDocumentPath);

            logger.info("Kivalasztom a Validity tablat");
            Table valTable = data.getTableByName("Validity");

            logger.info("Kivalasztom a fo tablat");
            Table mainTable = data.getTableByName("Main");

            calist = new HashMap<>();
            {
                /* build charge accounts list: [1] those, who require update */

                initializeAccounts(mainTable, startIndex);

                // Row rowAccountNames = mainTable.getRowByIndex(1);
                // Row rowInitialBalance = mainTable.getRowByIndex(startIndex - 1);
                // Date date = getDate(rowInitialBalance, IND_DATE);
                // for (int i = IND_CAIDS; i < TR_OFFSET; i++) {
                //     String name = getString(rowAccountNames, i);
                //     ChargeAccount ca = ss.ca(new ChargeAccount(name));
                //     calist.put(name, ca);
                //     logger.info(name);

                //     indCa.put(ca, i);
                //     logger.info(getString(rowInitialBalance, i));
                //     initialBalance(date, ca, getInteger(rowInitialBalance, i));
                // }

                // calist.put("pkez", pkez);
                // calist.put("potp", potp);
                // calist.put("dkez", dkez);
                // calist.put("dotp", dotp);
                // calist.put("nptc", nptc);
                calist.put("info", info);
                calist.put("pinfo", pinfo);
                // calist.put("Koli", ss.ca(new ChargeAccount("Koli")));

                /* build charge accounts list: [2] those, who do not require update (informational transactions) */

                for (int i = 1; i < 1000; i++) {
                    Row row = valTable.getRowByIndex(i);
                    int prop = getInteger(row, VALIDITY_CA_PROP_COL);
                    String name = getString(row, VALIDITY_CA_NAME_COL).toLowerCase();
                    String desc = getString(row, VALIDITY_CA_DESC_COL);

                    logger.info(name + " " + desc);
                    if (prop != 1 && !calist.containsKey(name) && !name.isEmpty()) {
                        logger.info(name + " " + desc + " [OK]");
                        ChargeAccount ca = ss.ca(new ChargeAccount(name, desc));
                        calist.put(name, ca);
                    }

                    if (name.isEmpty()) break;
                }
            }

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
                Cluster parent = service.findByName(parentname, Cluster.class);

                /* if parent not exists (YET), initialize it (later will be updated) */
                if (parent == null && !parentname.isEmpty()) parent = service.update(new Cluster(parentname));

                /* insert or update cluster */
                Cluster cluster = service.update(new Cluster(sqlclname, sgn, parent), Cluster.class);
                cllist.put(odfclname, cluster);

                // logger.infof("odf = %s, sql = %s ---> %s", odfclname, sqlclname, cluster);
            }

            /* parse table Koltsegvetes_Uj */
            koltsegvetesUj(mainTable, startIndex);
        } catch (Exception e) {
            e.printStackTrace();
        }
        logger.info("Minden rendben, lefutottam");
    }

    // private Map<ChargeAccount, Integer> balance = new HashMap<>();

    private void initialBalance(Date date, ChargeAccount ca, int balance) {
        Ugylet tr = new Ugylet();
        tr.setDate(date);
        tr.setCa(ca);
        tr.setCluster(Szamolas);
        tr.setPivot(true);
        tr.setBalance(balance);
        tr.setCatransfer(none);
        tr.setMarket(Market_Not_Applicable);
        tr.setRemark("initial pivot [the very first]");
        tr.setType(UgyletType.pivot);

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
            Row row;
            // row = table.getRowByIndex(startIndex - 1);
            // Date date = getDate(row, IND_DATE);
            //
            // int i = 0;
            // for (ChargeAccount ca : new ChargeAccount[] { potp, pkez, dotp, dkez }) {
            // indCa.put(ca, i);
            // initialBalance(date, ca, getInteger(row, IND_CAIDS + i));
            // ++i;
            // }

            /* nptc */
            // indCa.put(nptc, indCa.get(calist.get("dkez")));
            // row = table.getRowByIndex(NPTC_INIT_ROW_NR);
            // initialBalance(getDate(row, IND_DATE), nptc, getInteger(row, IND_CAIDS + 3));
        }

        Date stopDate = new Date();

        long daysToAdd = 30;
        long secondsToAdd = daysToAdd*(1000*60*60*24);
        stopDate.setTime(stopDate.getTime() + secondsToAdd);

        /* loop over the rows of the table */
        for (int i = startIndex; i < count; i++) {
            // if (i == NPTC_INIT_ROW_NR) {
            // calist.remove("dkez");
            // calist.put("dkez", nptc);
            // }

            String errmsg = "Problem: row nr. " + (i + 1) + ", ";

            Row row = table.getRowByIndex(i);
            Date date = getDate(row, IND_DATE);

            /* stop when we have left the actual date */
            if (date.after(stopDate))
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

        private final Row row;
        Ugylet tr = new Ugylet();

        void loadBalance() {

            if (tr.getCa() != null && indCa.containsKey(tr.getCa()))
                balance = getInteger(row, indCa.get(tr.getCa()));
            else if (tr.getCa() != null && tr.getCa().getName() != null && !tr.getCa().getName().isEmpty()) {
                balance = 0;
                remark = "INFO: " + remark;
                tr.setInfo(true);
            } else throw new NullPointerException("loadBalance: " + tr.toString());

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
            errmsg += "trans. nr. " + (index + 1) + ", date: " + SDF.format(date) + ".\n";

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
            remark = remark.trim();

            /* initialize transaction object */
            tr.setRemark(remark);
            tr.setDate(date);
            // tr.setProductInfo(remark.startsWith(R.ODF_PRODUCT_INFO_SUFFIX)); // TODO
        }

        public boolean resolveCa() {
            // logger.info("resolveCa - debug: caname = " + caname);
            ChargeAccount ca = calist.get(caname);
            if (ca == null || ca.getName().isEmpty() /* for the sake of safety */) {
                try {
                    Assert.assertTrue(errmsg + OdfLoaderService.class.getName() + "::resolveCa() - "
                            + "calist should not contain the empty string!", ca == null);

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
                // TODO: ProductInfo
                // TODO: colored output: ... + (char)27 + "[36mbla-bla-bla" - it is working!!
                logger.warnf(errmsg + "Try handling the product info entries: %s, tr: %s", ca, tr);
                return false;
            }
            Assert.assertNotEquals(errmsg + "At this point the charge account mustn't be null or empty string", ca, null);
            Assert.assertFalse(errmsg + "At this point the charge account mustn't be null or empty string", ca.getName().isEmpty());
            Assert.assertNotEquals(errmsg + "At this point the charge account mustn't be none", ca, none);
            Assert.assertNotEquals(errmsg + "At this point the charge account mustn't be pinfo", ca, pinfo);
            Assert.assertNotEquals(errmsg + "At this point the charge account mustn't be info", ca, info);

            /* [ASSERT] For sure: ca != (null | info | pinfo | none) */

            /* update transaction object */
            tr.setCa(ca);

            /* resolve balance (only after the charge account is known) */
            loadBalance();

            logger.infof(errmsg + "transaction = [%s]", tr.toString());
            return true;
        }

        public void resolveMarket() {

            /* correct misspelled market names */
            if (MARKET2MARKET.containsKey(mkname))
                mkname = MARKET2MARKET.get(mkname);

            Market market;
            if (mkname.isEmpty()) {
                market = Market_Not_Applicable;
            } else if (mklist.containsKey(mkname)) {
                market = mklist.get(mkname);
            } else {
                market = service.update(new Market(mkname, ""), Market.class);
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
                tr.setType(UgyletType.transfer);
                return true;

            }

            /* simple or pivot transaction */
            tr.setCatransfer(none);
            return false;
        }

        public boolean resolveClusterIfNotTransfer() {
            Cluster cluster = null;
            if (!clname.isEmpty()) {
                cluster = cllist.get(clname);
            }

            tr.setCluster(cluster);

            return guessCluster();
        }

        public boolean guessCluster() {
            Cluster cluster = tr.getCluster();

            /* Guess cluster knowing the market's name and some predefined rules */
            if (cluster == null) {
                logger.warnf(errmsg + "Cluster is not defined: " + tr);
                mkname = mkname.toLowerCase();
                if (tr.getMarket().equals(Market_Not_Applicable)) {
                    cluster = Egyeb_Kiadas;
                } else if (cluster == null && MARKETS_N.contains(mkname)) {
                    cluster = Napi_Szukseglet;
                } else if (MARKETS_UTAZAS.contains(mkname)) {
                    cluster = Utazas;
                } else if (MARKETS_RUHA.contains(mkname)) {
                    cluster = Ruhazkodas;
                } else if (MARKETS_LKBRN.contains(mkname)) {
                    cluster = Lakas_Berendezes;
                } else if (MARKETS_SZUKSEGES.contains(mkname)) {
                    cluster = Szukseges;
                } else if (MARKETS_MUNKA.contains(mkname)) {
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
                tr.setType(UgyletType.pivot);
            } else
                tr.setType(UgyletType.simple);
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
            return row.getCellByIndex(index).getDateValue().getTime();
        } catch (Exception e) {
            logger.errorf(this.getClass().getSimpleName() + "::getDate(%s,%d)", row.toString(), index);
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
