package com.napol.koltsegvetes.db;

import static com.napol.koltsegvetes.db.ETableNames.*;
import static com.napol.koltsegvetes.db.ETableNames.SQL_TYPE_32BYTEKEY;
import static com.napol.koltsegvetes.db.ETableNames.SQL_TYPE_8BYTEKEY;
import static com.napol.koltsegvetes.db.ETableNames.SQL_TYPE_AUTOIDKEY;
import static com.napol.koltsegvetes.db.ETableNames.SQL_TYPE_DATE;
import static com.napol.koltsegvetes.db.ETableNames.SQL_TYPE_INTEGER;
import static com.napol.koltsegvetes.util.Debug.debug;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/** 
 * @author Péter Polcz <ppolcz@gmail.com>
 * 
 * Created on September 21, 2014, 7:59 AM
 */
public enum EColumnNames implements IColumn
{
	// AC_ID(SQL_TYPE_AUTOIDKEY),
	AC_USERNAME("varchar(50) unique"),
	// AC_INSERT_DATE(SQL_TYPE_DATETIME),

	CA_ID(SQL_TYPE_8BYTEKEY), // folyoszamla ID (varchar: 'otp', 'kezp')
	CA_NAME("varchar(20)"), // folyoszamla neve ('Otp Bank Folyoszamla')
	CA_DATE(SQL_TYPE_DATE), // utolso valtozas datuma - VAN-E ERRE SZUKSEG?
	CA_BALANCE(SQL_TYPE_INTEGER), // utolso egyenleg - VAN-E ERRE SZUKSEG?
	// CA_ACID(AC_USERNAME),
	// CA_INSERT_DATE(SQL_TYPE_DATETIME),

	CL_NAME("varchar(20) primary key not null unique"), // do not contains white spaces, not visible to the user
	CL_DESCRIPTION("varchar(50)"), // user can see it
	CL_DIRECTION(SQL_TYPE_INTEGER), // milyen iranyba folyik a penz: ha negativ kimenet, ha pozitiv bemenet
	// CL_INSERT_DATE(SQL_TYPE_DATETIME),

	/* spar, lidl, aldi, dechatlon, ikea, groby, auchan, dezsoba, izlelo, itkmenza */
	MK_ID(SQL_TYPE_32BYTEKEY), // 8 karakteres id
	MK_NAME("varchar(20)"), // neve
	// MK_INSERT_DATE(SQL_TYPE_DATETIME), // mikor szurtam be

	TR_ID(SQL_TYPE_AUTOIDKEY), // tranzakcio ID (autoincrement)
	TR_DATE(SQL_TYPE_DATE), // datum
	TR_CAID(CA_ID), // folyoszamla ID
	TR_AMOUNT(SQL_TYPE_INTEGER), // osszeg
	TR_NEWBALANCE(SQL_TYPE_INTEGER), // uj egyenleg
	TR_CLNAME(CL_NAME), // a tranzakcio 'klasztere': mobilfeltoltes, napi szuksegletek, luxus stb...
	TR_MKNAME(MK_ID), // hol vasaroltam
	TR_REMARK("varchar(128)"), // megjegyzes, komment
	TR_REMARK_EXTRA("varchar(128)"),
	TR_PIVOT("boolean default false"),
	TR_TMP_NEWBALANCE(SQL_TYPE_INTEGER),
	// TR_ACID(AC_USERNAME),
	// TR_INSERT_DATE(SQL_TYPE_DATETIME),

	PI_ID(SQL_TYPE_AUTOIDKEY), // autoincrement id
	PI_TRID(TR_ID),
	PI_DATE(SQL_TYPE_DATE), // mikor vasaroltam
	PI_AMOUNT(SQL_TYPE_INTEGER), // mennyiert
	PI_WHAT("varchar(50)"), // mit
	PI_MKID(MK_ID), // hol
	PI_CLNAME(CL_NAME), // milyen tipusba sorolhato a vasarlas: lasd CLUSTERS
	PI_CAID(CA_ID),
	// PI_ACID(AC_USERNAME),
	// PI_INSERT_DATE(SQL_TYPE_DATETIME), // mikor szurtam be

	QR_INTEGER(Integer.class),
	QR_STRING(String.class),
	QR_DATE(Date.class),

	// /* regarding to my former odf spreadsheet, which contains my whole budget history */
	// ODF_CLNAME(CL_NAME), // referenced table = NONE
	// ODF_CAID(CA_ID), // referenced table = NONE

	/*
	MT_ID(SQL_TYPE_AUTOIDKEY),
	MT_NAME("varchar(20)"),
	MT_FNAME("varchar(40)"),
	 */

	NONE(Object.class);

	private final String sqltype;
	private final Class<?> javatype;
	private final ETableNames table;
	private final EColumnNames ref;
	private final boolean autoincrement;
	private final SimpleDateFormat dateFormat;

	private EColumnNames(EColumnNames col)
	{
		this.sqltype = col.sqltype();
		this.javatype = col.javatype;
		this.table = ETableNames.getTable(this);
		this.ref = col;
		this.autoincrement = col.autoincrement;
		dateFormat = isoDateFormat;
	}

	private EColumnNames(Class<?> javatype)
	{
		this.sqltype = "NO_TYPE_SPECIFIED";
		this.javatype = javatype;
		this.table = null;
		this.ref = null;
		this.autoincrement = false;
		dateFormat = isoDateFormat;
	}

	private EColumnNames(String sqltype)
	{
		this.sqltype = sqltype;
		this.table = ETableNames.getTable(this);
		this.ref = null;

		if (isDateType()) javatype = Date.class;
		else if (sqltype.startsWith("varchar")) javatype = String.class;
		else if (sqltype.startsWith(SQL_TYPE_INTEGER)) javatype = Integer.class;
		else if (sqltype.startsWith("boolean")) javatype = Boolean.class;
		else javatype = Object.class;

		autoincrement = sqltype.contains("autoincrement");
		if (sqltype.equals(SQL_TYPE_DATE)) dateFormat = simpleDateFormat;
		else dateFormat = isoDateFormat;

	}

	@Override
	public String displayName()
	{
		return sqlname();
	}

	@Override
	public String sqlname()
	{
		return name().toLowerCase(Locale.getDefault());
	}

	@Override
	public String idname()
	{
		return sqlname();
	}

	@Override
	public String sqltypeall()
	{
		return sqltype;
	}

	@Override
	public String sqltype()
	{
		return sqltype.split(" ")[0];
	}

	@Override
	public String sqlname(String prefix)
	{
		return prefix + "." + name().toLowerCase(Locale.getDefault());
	}

	@Override
	public Class<?> javatype()
	{
		return javatype;
	}

	@Override
	public ETableNames table()
	{
		return this.table;
	}

	@Override
	public IColumn ref()
	{
		return this.ref;
	}

	@Override
	public boolean isDateType()
	{
		/*
		 * TODO This is a very bad approach
		 */
		return name().contains("DATE"); // && sqltype.startsWith(SQL_TYPE_DATE);
	}

	@Override
	public boolean isInsertDate()
	{
		return name().equals(table.prefix() + COL_INSERT_DATE);
	}

	private String toQuote(String str)
	{
		return "'" + str + "'";
	}

	// @formatter:off
    @Override
    public Object toDate(String str)
    {
        try
        {
            if (javatype != Date.class) return str;
            
            /* try some different date format to parse the date string */
            try { return simpleDateFormat.parse(str); } catch (ParseException e1)
            {
                try { return isoDateFormat.parse(str); } catch (ParseException e2)
                {
                    try { return fancyDateFormat.parse(str); } catch (ParseException e3) { e3.printStackTrace(); debug(e3); }
                }
            }
        } catch (NullPointerException e) { e.printStackTrace(); debug(e); }
        return null;
    }
    // @formatter:on

	private String nullToString()
	{
		if (javatype == Integer.class) return "0";
		return "null";
	}

	private String toString(Object data, SimpleDateFormat format)
	{
		if (data == null) return nullToString();

		if (javatype == Boolean.class) return (Boolean) data ? "1" : "0";
		
		if (javatype == Integer.class) return data.toString();

		else if (javatype == Date.class)
		{
			// System.out.println("1> " + data.toString());
			if (data instanceof String)
			{
				// System.out.println("String instance");
				data = toDate((String) data);
			}
			// System.out.println("2> " + data.toString());
			if (data instanceof Date) return format.format((Date) data);
		}

		else if (javatype == String.class) { return (String) data; }

		return null;
	}

	@Override
	public String toSqlString(Object data)
	{
		if (data == null) return "null";

		String str = toString(data, dateFormat);
		if (str == null)
		{
			// System.out.println("THIS IS NULL");
			return null;
		}

		if (javatype == Integer.class) return str;

		return toQuote(str);
	}

	@Override
	public String toDisplayString(Object data)
	{
		return toString(data, simpleDateFormat);
	}

	@Override
	public String sqlwhere(Object comperand, String operator)
	{
		// Log.e("pcz>", sqlname());
		return sqlname() + operator + toSqlString(comperand);
	}

	public static ArrayList<IColumn> getColumns(ETableNames t)
	{
		ArrayList<IColumn> ret = new ArrayList<IColumn>();
		for (EColumnNames c : values())
		{
			if (c.table == t) ret.add(c);
		}
		return ret;
	}

	@Override
	public IColumn isa()
	{
		return this;
	}

	@Override
	public boolean isa(IColumn c)
	{
		return this == c;
	}

	@Override
	public boolean isAutoIncrement()
	{
		return autoincrement;
	}
}
