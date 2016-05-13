package polcz.budget.service;

import static polcz.budget.global.R.CLNAME_PIVOT;
import static polcz.budget.global.R.CLNAME_TRANSFER;
import static polcz.budget.global.R.MKNAME_NOT_APPLICABLE;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.jboss.logging.Logger;
import org.junit.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import polcz.budget.global.R;
import polcz.budget.model.TCluster;
import polcz.budget.model.TMarket;
import polcz.budget.service.helper.OdfRule;

@Service
public class OdfValidationService {

    @Autowired
    StartupService ss;

    /* POST PROCESSING RULES */
    private Map<TCluster, TMarket> cluster2market; /* during post-processing */
    private final OdfRule[] postProcessingRules = {
        (t, msg) -> {
            if (cluster2market.containsKey(t.getCluster()) && t.getCluster().equals(ss.Market_Not_Applicable()))
                t.setMarket(cluster2market.get(t.getCluster()));
        },

        /* test if each cluster has a parent */
        (t, msg) -> {
            Assert.assertNotNull(msg + "Each cluster must have a parent, only the root cluster does not have a parent."
                    + "cluster: " + t.getCluster(), t.getCluster().getParent());
        }
    };

    /* PREPROCESSING RULES */
    public static final Map<String, String> market2market; /* during pre-processing */

    @PostConstruct
    public void init() {
        logger = R.getJBossLogger(getClass());
        cluster2market = new HashMap<>();
        for (String[] rule : cluster2market_str) {
            cluster2market.put(ss.cluster(new TCluster(rule[0])), new TMarket(rule[1]));
        }
    }

    /* SPREADSHEET RULES */
    public static final int NR_TRS = 12;
    public static final int TR_OFFSET = 10;
    public static final int TR_LENGTH = 5; // amount, ca, cluster, market, remark

    public static final int IND_DATE = 0;
    public static final int IND_CAIDS = 2;
    public static final int[] IND_TRS;

    static {
        // IND_TRS = { 6, 11, 16, 21, 26, 31, ... };
        IND_TRS = new int[NR_TRS];
        for (int i = 0; i < NR_TRS; ++i) {
            IND_TRS[i] = i * TR_LENGTH + TR_OFFSET;
        }
    }

    public static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
    // public static final Date nptcIntroductionDate = new GregorianCalendar(2015, Calendar.DECEMBER, 17).getTime();
    public static final int nptcIntroductionRowNr = 897;

    public static final Set<String> nMarkets = new HashSet<>(Arrays.asList(new String[] {
        "spar", "interspar", "lidl", "aldi", "tesco", "auchan", "dezsoba", "izlelo", "coop", "rossmann",
        "groby", "pizzaking", "muller", "cba", "vikinger", "moriczgyros", "allee", "mcdonalds", "florian",
        "hadik", "oktogonbisztro"
    }));

    public static final Set<String> szuksegesMarkets = new HashSet<>(Arrays.asList(new String[] {
        "praterny", "copyguru"
    }));

    public static final Set<String> ruhaMarkets = new HashSet<>(Arrays.asList(new String[] {
        "sansha", "c&a", "decathlon"
    }));

    public static final Set<String> utazasMarkets = new HashSet<>(Arrays.asList(new String[] { "mav", }));
    public static final Set<String> lkbrnMarkets = new HashSet<>(Arrays.asList(new String[] { "ikea" }));
    public static final Set<String> munkaMarkets = new HashSet<>(Arrays.asList(new String[] { "pirex" }));

    public static final String[][] cluster2market_str = {
        { "Rezsi_Bkv", "BKV" },
        { "Rezsi_Gaz", "FOGAZ" },
        { "Rezsi_Viz", "FOCSM" },
        { "Rezsi_Futes", "FOTAV" },
        { "Rezsi_Elmu", "ELMU" },
        { "Rezsi_Kozosk", "Tarsashaz" },
        { "Rezsi_Upc", "UPC" },
        { "Rezsi_Otp", "OTP_Bank" },
        { "Rezsi_Otp", "OTP_Bank" },
        { "Mobil_Peti", "Telenor" },
        { "Mobil_Dori", "TMobil" },
        { "Mobil_Helga", "Telenor" },
        { "Hivatalos", "Hivatal" },
        { "Otthon", "PolczOtthon" },
        { "Alberlet", "Lakaskiado" },
        { CLNAME_TRANSFER, MKNAME_NOT_APPLICABLE + "(transfer)" },
        { CLNAME_PIVOT, MKNAME_NOT_APPLICABLE + "(pivot)" },
    };

    public static final String[][] market2market_str = {
        { "Examlpe", "Example" },
    };

    static {
        market2market = new HashMap<>();
        for (String[] rule : market2market_str) {
            market2market.put(rule[0], rule[1]);
        }
    }

    public OdfRule[] getPostProcessingRules() {
        return postProcessingRules;
    }

    @SuppressWarnings("unused")
    private Logger logger;
}
