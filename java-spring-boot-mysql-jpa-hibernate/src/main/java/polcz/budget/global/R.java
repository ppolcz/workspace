package polcz.budget.global;

import org.jboss.logging.Logger;

public class R {
    public static final String JSF_MK_ID = "mk_";
    public static final String JSF_CL_ID = "cl_";
    public static final String JSF_CA_ID = "ca_";
    public static final String JSF_CAT_ID = JSF_CA_ID + "transfer_";

    public static final String GLOBAL_MSG_ID = "globalMessage";
    public static final String REDIRECT = "?faces-redirect=true";
    public static final String REDIRECT_VIEW = "view" + REDIRECT;
    public static final String REDIRECT_CREATE = "create" + REDIRECT;
    public static final String REDIRECT_EDIT = "edit" + REDIRECT;

    public static final int TR_REMOVAL = 1;
    public static final int TR_INSERTION = 2;
    public static final int TR_UPDATE = 3;

    public static final String ODF_PRODUCT_INFO_SUFFIX = "PI: ";

    /* CHARGE ACCOUNT THINGS */

    public static final String CANAME_NONE = "none";
    public static final String CANAME_PKEZ = "pkez";

    /* CLUSTER THINGS */

    public static final String CLNAME_NOT_GIVEN = "Nem_Adott";
    public static final String CLNAME_PIVOT = "Szamolas";
    public static final String CLNAME_TRANSFER = "Athelyezes";

    public static final int CLSGN_NOT_GIVEN = 0;
    public static final int CLSGN_INCOME = 1;
    public static final int CLSGN_OUTCOME = -1;
    public static final int CLSGN_PIVOT = CLSGN_INCOME;
    public static final int CLSGN_TRANSFER = CLSGN_OUTCOME;

    /* MARKET THINGS */

    public static final String MKNAME_NOT_APPLICABLE = "Not_Applicable";
    public static final String MKNAME_NOT_GIVEN = "Nem_Adott";

    /* LOGGER THINGS */

    public static final String LOGGER_PREFIX = "PPOLCZ_";
    public static final String LOGGER_SUFFIX = "_PPOLCZ";

    public static final boolean LOG_TRANSACTIONAL_LOGIC = false;

    public static Logger getJBossLogger(Class<?> c) {
        return Logger.getLogger(LOGGER_PREFIX + c.getSimpleName());
    }

    public static Logger getJBossLogger(String str) {
        return Logger.getLogger(LOGGER_PREFIX + str);
    }

    public static java.util.logging.Logger getUtilLogger(Class<?> c) {
        return java.util.logging.Logger.getLogger(LOGGER_PREFIX, c.getSimpleName());
    }

    private R() {}

}
