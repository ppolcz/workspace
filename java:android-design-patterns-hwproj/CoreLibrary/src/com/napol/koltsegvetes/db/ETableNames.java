package com.napol.koltsegvetes.db;

import java.util.Locale;

/** 
 * @author Péter Polcz <ppolcz@gmail.com>
 * 
 * Created on September 21, 2014, 7:59 AM
 */
public enum ETableNames
{
    ACCOUNTS("AC_"),
    CHARGE_ACCOUNTS("CA_"), // folyoszamlak
    CLUSTERS("CL_"), // tranzakcio tipusok
    MARKETS("MK_"), // nagyobb uzletek

    TRANZACTIONS("TR_"), // tranzakciok
    PRODUCT_INFO("PI_"), // termek informaciok - mit-hol-mennyiert

    /*
    METATABLE("MT_"), // metatabla
     */

    // NONE("."),
    ;

    public static final String COL_INSERT_DATE = "INSERT_DATE";

    public static final String SQL_TYPE_AUTOIDKEY = "integer primary key autoincrement not null unique";
    public static final String SQL_TYPE_8BYTEKEY = "varchar(8) primary key not null unique";
    public static final String SQL_TYPE_32BYTEKEY = "varchar(32) primary key not null";
    public static final String SQL_TYPE_DATE = "varchar(10) /* DATE */"; // 2015-03-12
    public static final String SQL_TYPE_DATETIME = "varchar(50) /* DATETIME */";
    public static final String MySQL_TYPE_DATE = "DATE"; // 2015-03-12
    public static final String MySQL_TYPE_DATETIME = "DATETIME";
    public static final String SQL_TYPE_INTEGER = "integer";

    private final String pref;

    private ETableNames(String pref)
    {
        this.pref = pref;
    }

    public String prefix()
    {
        return pref;
    }

    public String sqlname()
    {
        return name().toLowerCase(Locale.getDefault());
    }

    public String sqlname(String prefix)
    {
        return sqlname() + " as " + prefix;
    }

    public static ETableNames getTable(EColumnNames col)
    {
        for (ETableNames t : ETableNames.values())
            if (col.name().startsWith(t.pref)) return t;
        return null;
    }

    public EColumnNames getInsertDateColumn()
    {
        // return insertDate;
        return EColumnNames.valueOf(pref + COL_INSERT_DATE);
    }

    // public boolean isSimpleTable()
    // {
    // return this != NONE /* && this != METATABLE */;
    // }
    //
    // public boolean isNone()
    // {
    // return this == NONE;
    // }
}
