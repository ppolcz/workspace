package com.polpe.hrm.db;

import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * 
 * @author Polcz Péter <ppolcz@gmail.com>
 * Created on Nov 19, 2014 12:37:54 PM
 */
public interface IColumn
{
    static final SimpleDateFormat isoDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSZ", Locale.getDefault());
    static final SimpleDateFormat fancyDateFormat = new SimpleDateFormat("yyyy-MM-dd MMMM EEEE", Locale.getDefault());
    static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

    static final String opl = "<";
    static final String ople = "<=";
    static final String opg = ">";
    static final String opge = ">=";
    static final String ope = "=";
    static final String opLike = "like";

    ETableNames table();

    /**
     * TR_NEWBALANCE --> "New Balance"
     * @return
     */
    String displayName();

    /**
     * TR_DATE --> "TR_DATE"
     */
    String name();

    /**
     * TR_DATE --> tr_date
     * decorated --> tr_newbalance - tr_amount
     */
    String sqlname();

    /**
     * TR_DATE --> tr_date
     * decorated --> tr_oldbalance
     */
    String idname();

    /**
     * TR_ID --> integer primary key autoincrement unique
     */
    String sqltypeall();

    /**
     * TR_ID --> integer
     * @return
     */
    String sqltype();

    /**
     * TR_DATE, prefix = "tr" --> tr.tr_date
     * @param prefix
     */
    String sqlname(String prefix);

    /**
     * TR_DATE --> Date.class
     */
    Class<?> javatype();

    /**
     * TR_CLNAME --> CL_NAME
     */
    IColumn ref();

    /**
     * TR_DATE --> true
     * TR_INSERT_DATE --> true
     * TR_ID --> false
     */
    boolean isDateType();

    /**
     * TR_INSERT_DATE --> true
     * TR_DATE --> false
     */
    boolean isInsertDate();

    /**
     * Parse date string and convert to Date
     * @param date
     * @return
     */
    Object toDate(String date);

    /**
     * "asdasda" --> "\"asdasda\""
     * 12 --> "12"
     */
    String toSqlString(Object data);

    /**
     * "asdasda" --> "asdasda"
     * 12 --> "12"
     * date -- "2014-03-20" (or some other date format)
     */
    String toDisplayString(Object data);

    /**
     * TR_ID, comperand = 12, operator = "=" --> "tr_id = 12"
     * @param comperand
     * @param operator
     */
    String sqlwhere(Object comperand, String operator);

    IColumn isa();

    boolean isa(IColumn c);
    
    boolean isAutoIncrement();
}
