package polcz.budget.service;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.faces.bean.ManagedProperty;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.jboss.logging.Logger;
import org.junit.Assert;
import org.odftoolkit.simple.SpreadsheetDocument;
import org.odftoolkit.simple.table.Row;
import org.odftoolkit.simple.table.Table;

import polcz.budget.global.R;
import polcz.budget.model.TChargeAccount;
import polcz.budget.model.TCluster;
import polcz.budget.model.TMarket;
import polcz.budget.model.TTransaction;
import polcz.budget.model.TTransactionType;
import polcz.budget.service.helper.TransactionArguments;
import polcz.budget.session.TransactionBean;

@Stateless
// @Named("odfLoader")
public class OdfLoaderService
{
    /// @formatter:off
    @EJB StartupService ss;
    @EJB MarketService mks;
    @EJB ClusterService cls;
    @EJB TransactionService trs;
    /// @formatter:on

    @ManagedProperty(name = "transactionBean", value = "")
    TransactionBean trBean;

    @PersistenceContext(unitName = "mysqlPU")
    EntityManager em;

    Logger logger;

    private static final int NR_TRS = 12;
    private static final int TR_OFFSET = 10;
    private static final int TR_LENGTH = 5; // amount, ca, cluster, market, remark

    private static final int IND_DATE = 0;
    private static final int IND_CAIDS = 2;
    private static final int[] IND_TRS; // { 6, 11, 16, 21, 26, 31, ... };

    static
    {
        IND_TRS = new int[NR_TRS];
        for (int i = 0; i < NR_TRS; ++i)
        {
            IND_TRS[i] = i * TR_LENGTH + TR_OFFSET;
        }
    }

    private Map<TChargeAccount, Integer> indCa = new HashMap<>();

    private Map<String, TCluster> cllist;
    private Map<String, TChargeAccount> calist;
    private Map<String, TMarket> mklist;

    private TChargeAccount none;
    private TChargeAccount pkez;
    private TChargeAccount potp;
    private TChargeAccount dkez;
    private TChargeAccount dotp;
    private TChargeAccount nptc;
    private TChargeAccount info;
    private TChargeAccount pinfo;

    private TCluster Utazas;
    private TCluster Lakas_Berendezes;
    private TCluster Ruhazkodas;
    private TCluster Munkaeszkozok;

    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
    // private static final Date nptcIntroductionDate = new GregorianCalendar(2015, Calendar.DECEMBER, 17).getTime();
    private static final int nptcIntroductionRowNr = 897;

    private static final Set<String> nMarkets = new HashSet<>(Arrays.asList(new String[] {
        "spar", "interspar", "lidl", "aldi", "tesco", "auchan", "dezsoba", "izlelo", "coop", "rossmann",
        "groby", "pizzaking", "muller", "cba", "vikinger", "moriczgyros", "allee", "mcdonalds", "florian",
        "hadik", "oktogonbisztro"
    }));

    private static final Set<String> szuksegesMarkets = new HashSet<>(Arrays.asList(new String[] {
        "praterny", "copyguru"
    }));

    private static final Set<String> ruhaMarkets = new HashSet<>(Arrays.asList(new String[] {
        "sansha", "c&a", "decathlon"
    }));

    private static final Set<String> utazasMarkets = new HashSet<>(Arrays.asList(new String[] { "mav", }));
    private static final Set<String> lkbrnMarkets = new HashSet<>(Arrays.asList(new String[] { "ikea" }));
    private static final Set<String> munkaMarkets = new HashSet<>(Arrays.asList(new String[] { "pirex" }));

    private static final Map<String, String> marketCorrections;

    static
    {
        marketCorrections = new HashMap<>();
        marketCorrections.put("Mueller", "Muller");
        marketCorrections.put("M1Gyros", "MoriczGyros"); // TODO
    }

    @PostConstruct
    public void init()
    {
        logger = R.getJBossLogger(this.getClass());
        logger.info(this.getClass().getSimpleName() + "::init() [@PostConstruct]");

        none = ss.none();
        pkez = ss.pkez();
        potp = ss.ca(new TChargeAccount("potp", "Peti OTP Bank"));
        dkez = ss.ca(new TChargeAccount("dkez", "Dori kezpenz"));
        dotp = ss.ca(new TChargeAccount("dotp", "Dori OTP Bank"));
        nptc = ss.ca(new TChargeAccount("nptc", "nagypénztárca"));

        info = new TChargeAccount("info"); /* not persisted */
        info.setUid(123123);

        pinfo = new TChargeAccount("pinfo"); /* not persisted */
        pinfo.setUid(1931212);

        Utazas = ss.cluster(new TCluster("Utazas"));
        Lakas_Berendezes = ss.cluster(new TCluster("Lakas_Berendezes"));
        Ruhazkodas = ss.cluster(new TCluster("Ruhazkodas"));
        Munkaeszkozok = ss.cluster(new TCluster("Munkaeszkozok"));
    }

    public void process()
    {
        parse();
    }

    /**
     * @param odfDocumentPath
     * @param startIndex
     */
    private void parse()
    {
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

        String odfDocumentPath = "/home/polpe/Dropbox/koltsegvetes.ods";
        int startIndex = 3;

        try
        {
            logger.info("Elkezdtem olvasni a dokumentumot");
            SpreadsheetDocument data = SpreadsheetDocument.loadDocument(odfDocumentPath);

            logger.info("Kivalasztom a Validity tablat");
            Table valTable = data.getTableByName("Validity");
            final int valFirstRow = 1;

            cllist = new HashMap<>();
            mklist = new HashMap<>();

            for (int i = valFirstRow; i < valTable.getRowCount(); ++i)
            {
                Row row = valTable.getRowByIndex(i);
                String odfclname = getString(row, 0).toLowerCase();
                String sqlclname = getString(row, 1);

                if (sqlclname.isEmpty()) break;

                if (calist.containsKey(sqlclname)) continue;

                TCluster cluster = ss.cluster(new TCluster(sqlclname));
                cllist.put(odfclname, cluster);

                // logger.infof("odf = %s, sql = %s ---> %s", odfclname, sqlclname, cluster);
            }

            /* parse table Koltsegvetes_Uj */
            logger.info("Kivalasztom a fo tablat");
            Table table = data.getTableByName("Koltsegvetes_Uj");
            koltsegvetesUj(table, startIndex);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        logger.info("Minden rendben, lefutottam");
    }

    // private Map<TChargeAccount, Integer> balance = new HashMap<>();

    void initialBalance(Date date, TChargeAccount ca, int balance)
    {
        TTransaction tr = new TTransaction();
        tr.setDate(date);
        tr.setCa(ca);
        tr.setCluster(ss.Szamolas());
        tr.setPivot(true);
        tr.setBalance(balance);
        tr.setCatransfer(ss.none());
        tr.setMarket(ss.Market_Not_Applicable());
        tr.setRemark("initial pivot [the very first]");
        tr.setType(TTransactionType.pivot);

        trs.makeTransaction(new TransactionArguments(tr, null, R.TR_INSERTION));
    }

    /**
     * @param table
     * @param startIndex
     */
    public void koltsegvetesUj(Table table, int startIndex)
    {
        int count = table.getRowCount();
        logger.info("Ennyi sor van a tablaban: " + count);

        /* I read the very first row after the header, which contains the first balance */
        {
            Row row = table.getRowByIndex(startIndex - 1);
            Date date = getDate(row, IND_DATE);

            int i = 0;
            for (TChargeAccount ca : new TChargeAccount[] { potp, pkez, dotp, dkez })
            {
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
        for (int i = startIndex; i < count; i++)
        {
            if (i == nptcIntroductionRowNr)
            {
                calist.remove("dkez");
                calist.put("dkez", nptc);
            }

            String errmsg = "Problem: row nr. " + (i + 1) + ", ";

            Row row = table.getRowByIndex(i);
            Date date = getDate(row, IND_DATE);

            /* stop when we have left the actual date */
            if (date.after(today)) break;

            /* run throw all transactions of this row */
            for (int t = 0; t < IND_TRS.length; ++t)
                transaction(row, t, date, errmsg);
        }
    }

    public void transaction(Row row, int t, Date date, String errmsg)
    {
        errmsg += "trans. nr. " + (t + 1) + ", date: " + sdf.format(date) + ".\n";

        int o = IND_TRS[t]; /* column offset of transaction nr t. */
        int amount = getInteger(row, o + 0);
        String formula = getFormula(row, o + 0);
        String caname = getString(row, o + 1).toLowerCase();
        String clname = getString(row, o + 2).toLowerCase();
        String mkname = getString(row, o + 3);
        String remark = getString(row, o + 4);
        String rextra = "tr_" + Integer.toString(t + 1);

        /* [1] resolve charge account */
        TChargeAccount ca = calist.get(caname);
        if (ca == null || (amount == 0 && !ca.equals(info)))
        {
            /* this transaction's form is not filled in */
            Assert.assertTrue(
                errmsg + "amount == 0 && !caid.isinfo, BUT remark || market || cluster is NOT NULL",
                remark.isEmpty() && mkname.isEmpty() && clname.isEmpty());
            return;
        }
        else if (ca.equals(info) || ca.equals(pinfo))
        {
            logger.infof(errmsg + "ca = %s, amount = %d, remark = %s",
                ca.getName(), amount, remark);
            return;
        }
        else if (ca.equals(pinfo))
        {
            // TODO: TProductInfo
            return;
        }
        Assert.assertNotEquals(errmsg + "Charge account mustn't be none", ca, none);

        /* [ASSERT] For sure: ca != (null | info | pinfo | none) */

        /* [2] resolve remark: decorate the remark with the transaction's amount or its formula */
        if (!remark.isEmpty()) remark += " ";
        if (!formula.isEmpty()) remark += "[" + formula.substring(4) + "] ";
        remark += "{ " + rextra + " }";
        remark.trim();

        /* [3] resolve balance (only after the charge account is known) */
        int balance = getInteger(row, IND_CAIDS + indCa.get(ca));

        /* Avoid errors in the older parts, not applicable any more */
        // if (mkname.contains("+")) new Exception("Warning:" + row.getRowIndex() + ""
        // + row.getCellByIndex(o).getCellStyleName() + " mkname contains `+'").printStackTrace();

        /* [4.1] correct misspelled market names */
        if (marketCorrections.containsKey(mkname)) mkname = marketCorrections.get(mkname);

        /* [4.2] resolve market */
        TMarket market;
        if (mkname.isEmpty())
        {
            market = ss.Market_Not_Applicable();
        }
        else if (mklist.containsKey(mkname))
        {
            market = mklist.get(mkname);
        }
        else
        {
            market = ss.market(new TMarket(mkname, ""));
            mklist.put(mkname, market);
        }

        /* [ASSERT] this transaction is likely to be valid */
        TTransaction tr = new TTransaction();
        tr.setBalance(balance);
        tr.setCa(ca);
        tr.setMarket(market);
        tr.setRemark(remark);
        tr.setDate(date);
        tr.setProductInfo(remark.startsWith(R.ODF_PRODUCT_INFO_SUFFIX));

        /* [5] resolve cluster */
        if (calist.containsKey(clname))
        {
            /* transfer */
            tr.setCatransfer(calist.get(clname));
            tr.setCluster(ss.Athelyezes());
            tr.setPivot(false);
            tr.setAmount(amount * ss.Athelyezes().getSgn());
            tr.setType(TTransactionType.transfer);
        }
        else
        {
            /* simple or pivot transaction */
            tr.setCatransfer(ss.none());

            TCluster cluster = null;
            if (!clname.isEmpty())
            {
                cluster = cllist.get(clname);
            }

            /* Guess cluster knowing the market's name */
            if (cluster == null)
            {
                mkname = mkname.toLowerCase();
                if (market.equals(ss.Market_Not_Applicable()))
                {
                    cluster = ss.Egyeb_Kiadas();
                }
                else if (cluster == null && nMarkets.contains(mkname))
                {
                    cluster = ss.Napi_Szukseglet();
                }
                else if (utazasMarkets.contains(mkname))
                {
                    cluster = Utazas;
                }
                else if (ruhaMarkets.contains(mkname))
                {
                    cluster = Ruhazkodas;
                }
                else if (lkbrnMarkets.contains(mkname))
                {
                    cluster = Lakas_Berendezes;
                }
                else if (szuksegesMarkets.contains(mkname))
                {
                    cluster = ss.Szukseges();
                }
                else if (munkaMarkets.contains(mkname))
                {
                    cluster = Munkaeszkozok;
                }
            }

            if (cluster == null)
            {
                logger.errorf(errmsg + "Cluster is null: clname = %s", clname);
                logger.errorf("Transaction = %s", tr);
                return;
            }

            if (cluster.equals(ss.Szamolas()))
            {
                tr.setPivot(true);
                tr.setType(TTransactionType.pivot);
            }
            else tr.setType(TTransactionType.simple);

            tr.setCluster(cluster);
            tr.setAmount(amount * cluster.getSgn());
        }

        logger.infof("%s", tr);
        trs.makeTransaction(new TransactionArguments(tr, null, R.TR_INSERTION));

        Assert.assertFalse(errmsg + "amount shouldn't be zero, cluster: " + tr.getCluster(),
            tr.isPivot() && tr.getAmount() == 0);
    }

    private Date getDate(Row row, int index)
    {
        try
        {
            return row.getCellByIndex(IND_DATE).getDateValue().getTime();
        }
        catch (Exception e)
        {
            logger.info("Problem");
            return new Date();
        }
    }

    private int getInteger(Row row, int index)
    {
        try
        {
            return row.getCellByIndex(index).getDoubleValue().intValue();
        }
        catch (Exception e)
        {
            return 0;
        }
    }

    private String getString(Row row, int index)
    {
        String ret = row.getCellByIndex(index).getStringValue();
        if (ret == null) return "";
        return ret.trim();
    }

    private String getFormula(Row row, int index)
    {
        String ret = row.getCellByIndex(index).getFormula();
        if (ret == null) return "";
        return ret;
    }

    // UNUSED

    @SuppressWarnings("unused")
    private double getDouble(Row row, int index)
    {
        try
        {
            return row.getCellByIndex(index).getDoubleValue();
        }
        catch (Exception e)
        {
            return 0;
        }
    }

    @SuppressWarnings("unused")
    private int getIntegerOrThrow(Row row, int index)
    {
        return row.getCellByIndex(index).getDoubleValue().intValue();
    }

    @SuppressWarnings("unused")
    private String getNote(Row row, int index)
    {
        return row.getCellByIndex(index).getNoteText();
    }
}
