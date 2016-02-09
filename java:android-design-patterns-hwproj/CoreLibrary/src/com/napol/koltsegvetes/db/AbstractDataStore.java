package com.napol.koltsegvetes.db;

import static com.napol.koltsegvetes.db.EColumnNames.*;
import static com.napol.koltsegvetes.db.IColumn.opEqual;
import static com.napol.koltsegvetes.util.Debug.*;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.napol.koltsegvetes.dbdriver.ISQLCommands;
import com.napol.koltsegvetes.dbdriver.ISQLiteHelper;

/**
 * @author Péter Polcz <ppolcz@gmail.com>
 * 
 * Created on September 21, 2014, 7:59 AM
 * 
 * Important remarks: [1] retrieve last insert row ID: > 'select
 * last_insert_rowid()' --> doesn't work if a new session is started >
 * 'select * from sqlite_sequence' --> works fine every time
 * 
 * The sqlXxxx() : String static methods are just for generating the SQL commands and return them as a string.
 *   - eg: sqlInsert, sqlUpdate, ...
 * 
 * The insert(...), update(...) ... functions are meant to be replaced with commands.
 * 
 * TODO:
 * [1] handle and generate INSERT_DATE everywhere if its necessary (insert, update, delete)
 * [2] onUpgrade()
 */
public abstract class AbstractDataStore
{
	private static final String DBNAME = "koltsegvetes";

	private ISQLiteHelper helper = null;
	private boolean created;

	/**
	 * Should not be synchronized.
	 */
	protected AbstractDataStore()
	{
		helper = getHelperInstance();
		helper.setSqlInterface(sqlCommands);
	}

	public synchronized void onOpen()
	{

		if (!isCreated())
		{
			helper.onOpen();
			helper.onCreate();

			if (initialize()) sqlCommands.initAfterCreate();
		}
		else
		{
			helper.onOpen();
		}
	}

	public synchronized void onDestroy()
	{
		created = false;
		helper.onDestroy();
	}

	public synchronized void onUpgrade()
	{
		// TODO
	}

	/**
	 * Factory method of the helper
	 * @return {@link ISQLiteHelper} instance to initialize this.helper
	 */
	protected abstract ISQLiteHelper getHelperInstance();

	/**
	 * Factory method of the helper
	 * @return true if the database has been created yet in the software's life cycle
	 */
	protected abstract boolean isCreated();

	/**
	 * Factory method with a defult implementation.
	 * @return
	 */
	protected String getDatabaseNameWithoutExtension()
	{
		return DBNAME;
	}

	/**
	 * This will point the fact if it needed the database to be initialized when its newly created.
	 */
	protected boolean initialize()
	{
		return true;
	}

	/**
	 * @author Polcz Péter <ppolcz@gmail.com>
	 * @param prefix
	 */
	private String sqlcolsdecl(String prefix)
	{
		String ret = "";
		IColumn[] cols = EColumnNames.values();
		for (IColumn c : cols)
			if (c.name().startsWith(prefix)) ret += "    " + c.sqlname() + " " + c.sqltypeall() + ",\n";
		for (IColumn c : cols)
			if (c.name().startsWith(prefix) && c.ref() != null)
			{
				ret += "    foreign key (" + c.sqlname() + ") references "
					+ c.ref().table().sqlname() + "(" + c.ref().sqlname()
					+ ")" + ",\n";
			}
		return ret.substring(0, ret.length() - 2);
	}

	/**
	 * @author Polcz Péter <ppolcz@gmail.com>
	 * @param table
	 */
	private String sqlcreatetable(ETableNames table)
	{
		return "CREATE TABLE IF NOT EXISTS " + table.sqlname() + " ( \n"
			+ sqlcolsdecl(table.prefix()) + "\n) DEFAULT CHARACTER SET = utf8;";
	}

	/**
	 * @author Polcz Péter <ppolcz@gmail.com>
	 */
	/*
	 * private final ISQLCommands sqlMetaHelper = new ISQLCommands() {
	 * 
	 * @Override public List<String> sqlCreateTableCommands() {
	 * ArrayList<String> l = new ArrayList<String>();
	 * l.add(sqlcreatetable(ETableNames.METATABLE)); return l; }
	 * 
	 * @Override public boolean initAfterCreate() { return true; }
	 * 
	 * @Override public int getVersion() { return 1; }
	 * 
	 * @Override public String getFilename() { return DBNAME; } };
	 */

	/**
	 * This is the interface that {@link ISQLiteHelper} (i.e.
	 * {@link SQLiteDriverJDBC}) will be given as a callback.
	 * 
	 * @author Péter Polcz <ppolcz@gmail.com>
	 */
	protected final ISQLCommands sqlCommands = new ISQLCommands()
	{
		@Override
		public List<String> sqlCreateTableCommands()
		{
			ArrayList<String> l = new ArrayList<String>();

			for (ETableNames t : ETableNames.values())
			{
				l.add(sqlcreatetable(t));
			}

			return l;
		}

		@Override
		public String getFilename()
		{
			return getDatabaseNameWithoutExtension() + "-" + getVersion() + ".db";
		}

		@Override
		public int getVersion()
		{
			return 1;
		}

		@Override
		public String getVersionName()
		{
			return "0.1";
		}

		@Override
		public boolean initAfterCreate()
		{
			// @formatter:off
			
//			if (helper == null) throw new NullPointerException("helper is null");
//
//			IColumn[] typesCl = { CL_NAME, CL_DIRECTION };
//			Object[][] clsCl = {
//				{ "Elelem", -1 },
//				{ "Ruhazkodas", -1 },
//				{ "Alberlet", -1 },
//				{ "Osztondij", +1 },
//				{ "Fizetes", +1 },
//				{ "Egyeb_Kiadas", -1 },
//				{ "Egyeb_Bevetel", +1 },
//				{ "Athelyezes_Innen", -1 },
//				{ "Athelyezes_Ide", +1 }
//			};
//
//			for (Object[] cl : clsCl) insert(typesCl, cl);
//
//			IColumn[] typesCa = { CA_ID, CA_NAME };
//			Object[][] clsCa = {
//				{ "potp", "Peti OTP Bank" },
//				{ "pkez", "Peti kezpenz" },
//				{ "dotp", "Dori OTP Bank" },
//				{ "dkez", "Dori kezpenz" }
//			};
//
//			for (Object[] cl : clsCa) insert(typesCa, cl);
//
//			IColumn[] typesMk = { MK_ID, MK_NAME };
//			Object[][] clsMk = {
//				{ "none", "" },
//				{ "Interspar", "" },
//				{ "Spar", "" },
//				{ "Decathlon", "" },
//				{ "Auchan", "" },
//				{ "IKEA", "" },
//			};
//
//			for (Object[] cl : clsMk) insert(typesMk, cl);
//
//			String acid = "ppolcz";
//			HashMap<IColumn, Object> tr = new HashMap<IColumn, Object>();
//			tr.put(TR_ID, null);
//			tr.put(TR_ACID, acid);
//			tr.put(TR_AMOUNT, 1200);
//			tr.put(TR_CAID, "pkez");
//			tr.put(TR_CLNAME, clsCl[1][0]);
//			tr.put(TR_DATE, Calendar.getInstance().getTime());
//			tr.put(TR_INSERT_DATE, Calendar.getInstance().getTime());
//			tr.put(TR_MKNAME, clsMk[1][0]);
//			tr.put(TR_NEWBALANCE, 100000);
//			tr.put(TR_REMARK, "Sample Tranzaction");
//			int id = insert(tr);

////			IColumn[] typesPi = { PI_ID, PI_TRID, PI_ACID, PI_AMOUNT, PI_CAID, PI_CLNAME, PI_DATE, PI_INSERT_DATE, PI_MKID, PI_WHAT };
//			IColumn[] typesPi = { PI_ID, PI_TRID, PI_AMOUNT, PI_CAID, PI_CLNAME, PI_DATE, PI_MKID, PI_WHAT };
//			Object[][] clsPi = {
//				{ null, id, 1000, "pkez", clsCl[1][0], Calendar.getInstance().getTime(), clsMk[1][0], "Sample product 1" },
//				{ null, id, 2000, "pkez", clsCl[2][0], Calendar.getInstance().getTime(), clsMk[1][0], "Sample product 2" }
////				{ null, id, acid, 1000, "pkez", clsCl[1][0], Calendar.getInstance().getTime(), Calendar.getInstance().getTime(), clsMk[1][0], "Sample product 1" },
////				{ null, id, acid, 2000, "pkez", clsCl[2][0], Calendar.getInstance().getTime(), Calendar.getInstance().getTime(), clsMk[1][0], "Sample product 2" }
//			};
//			for (Object[] cl : clsPi) insert(typesPi, cl);
			
			return true;
			
			// @formatter:on
		}
	}; /* end of implementation of the ISQLCommands */

	public static String sqlInsert(IColumn[] c, Object... v)
	{
		ETableNames table = c[0].table();
		if (c.length != v.length) throw new IndexOutOfBoundsException("cols.length != vals.length");

		String sql = "insert into " + table.sqlname();
		String cols = "";
		String vals = "";
		for (int i = 0; i < c.length; ++i)
		{
			// if (c[i] == table.getInsertDateColumn())
			// {
			// System.out.println("TODO: TABLENAME_insert_data");
			// }
			cols += ", " + c[i].sqlname();
			vals += ", " + c[i].toSqlString(v[i]);
		}

		// append insert_date column
		// cols += ", " + table.getInsertDateColumn().sqlname();
		// vals += ", " + table.getInsertDateColumn().toSqlString(new Date());

		sql = sql + " (" + cols.substring(2) + ") values (" + vals.substring(2) + ")";
		log(sql);

		return sql;
	}

	public static String sqlDelete(ETableNames t, String whereStatement)
	{
		return "delete from " + t.sqlname() + " where " + whereStatement;
	}

	public static String sqlUpdate(AbstractQuery singleRow, String whereStatement)
	{
		// update tranzactions set tr_caid = "anyam", tr_remark = "kutya" where tr_id = 156;
		if (singleRow.size() != 1 && singleRow.getColsCount() == 0) throw new IllegalArgumentException("singleRow should contain a SINGLE valid row");
		IColumn[] types = singleRow.getCols();
		ETableNames t = types[0].table();

		String sql = "update " + t.sqlname() + " set ";
		String setterStatement = "";

		for (int i = 0; i < singleRow.getColsCount(); ++i)
		{
			if (types[i].table() == t)
			{
				setterStatement += ", " + types[i].sqlname() + " = " + types[i].toSqlString(singleRow.getFirst()[i]);
			}
		}
		sql += setterStatement.substring(2) + " where " + whereStatement;
		log(sql);

		return sql;
	}

	public static String sqlUpdate(AbstractQuery singleRow, int index, String whereStatement)
	{
		IColumn[] types = singleRow.getCols();
		ETableNames t = types[0].table();

		String sql = "update " + t.sqlname() + " set ";
		String setterStatement = "";

		for (int i = 0; i < singleRow.getColsCount(); ++i)
		{
			if (types[i].table() == t)
			{
				setterStatement += ", " + types[i].sqlname() + " = " + types[i].toSqlString(singleRow.get(index)[i]);
			}
		}
		sql += setterStatement.substring(2) + " where " + whereStatement;
		log(sql);

		return sql;
	}

	public static String sqlSelect(ETableNames t)
	{
		return "SELECT * FROM " + t.sqlname();
	}

	public static String sqlSelect(ETableNames t, String where)
	{
		return "SELECT * FROM " + t.sqlname() + " WHERE " + where;
	}

	@SafeVarargs
	public static String sqlSelect(String where, IColumn... cols)
	{
		String sqlcols = "";
		String sqltables = "";
		String sql = "SELECT %s FROM %s";
		String whereAdditional = "";
		String whereAll = "";

		Set<ETableNames> tables = new HashSet<ETableNames>();

		for (IColumn c : cols)
		{
			tables.add(c.table());
		}

		// detecting references - generating when statement
		if (tables.size() > 1)
		{
			for (IColumn c : cols)
			{
				// if the current column do not references nothing
				if (c.ref() == null) continue;

				// if the table of current column's reference do no appear in
				// the query
				if (!tables.contains(c.ref().table())) continue;

				whereAdditional += " and " + c.sqlwhere(c.ref().sqlname(), opEqual);
			}
		}
		debug("!where.isEmpty() = %b", !where.isEmpty());
		debug("!whereAdditional.isEmpty() = %b", !whereAdditional.isEmpty());
		debug("!where.isEmpty() || !whereAdditional.isEmpty() = %b", !where.isEmpty() || !whereAdditional.isEmpty());
		debug("where = |_%s_|", where);
		if (!where.isEmpty()) whereAll += where;
		if (!whereAdditional.isEmpty()) whereAll += (whereAll.isEmpty() ? "" : " and ") + whereAdditional.substring(5);
		if (!whereAll.isEmpty()) whereAll = " WHERE " + whereAll;

		// generate tables list
		for (ETableNames t : tables)
			sqltables += ", " + t.sqlname();

		// generate columns list
		for (IColumn c : cols)
			sqlcols += ", " + c.sqlname();

		sql = String.format(sql, sqlcols.substring(2), sqltables.substring(2) + whereAll);
		log(sql);

		return sql;
	}

	/**
	 * Insert into table
	 * 
	 * @param table
	 * @param c
	 * @param v
	 * @return last_insert_rowid()
	 * 
	 * @author Polcz Péter <ppolcz@gmail.com>
	 */
	public synchronized int insert(IColumn[] c, Object... v)
	{
		if (!helper.execSQL(sqlInsert(c, v))) return -1;
		return helper.lastInsertRowID();
	}

	public synchronized int insert(AbstractQuery query, int position)
	{
		return insert(query.getCols(), query.get(position));
	}

	public synchronized int[] insertAll(AbstractQuery query)
	{
		int[] ret = new int[query.size()];
		int i = 0;

		for (Object[] tuple : query)
		{
			log("Inserting " + (i + 1) + " out of " + ret.length);
			ret[i++] = insert(query.getCols(), tuple);
		}
		return ret;
	}

	public synchronized AbstractQuery insertAll(AbstractQuery query, IColumn insertIdCol)
	{
		int[] ids = insertAll(query);
		int idPos = query.getPosition(insertIdCol);

		if (idPos == -1) throw new IllegalArgumentException("insertIdCol should be one of the element of query.getTypes()");

		for (int i = 0; i < query.size(); ++i)
		{
			query.get(i)[idPos] = ids[i];
		}

		return query;
	}

	public synchronized int insert(Map<IColumn, Object> values)
	{
		IColumn[] cols = new IColumn[values.size()];
		Object[] vals = new Object[values.size()];

		int i = 0;
		for (Entry<IColumn, Object> entry : values.entrySet())
		{
			cols[i] = entry.getKey();
			vals[i] = entry.getValue();
			++i;
		}

		return insert(cols, vals);
	}

	public synchronized boolean delete(ETableNames t, String whereStatement)
	{
		return helper.execSQL(sqlDelete(t, whereStatement));
	}

	public synchronized boolean update(AbstractQuery singleRow, String whereStatement)
	{
		return helper.execSQL(sqlUpdate(singleRow, whereStatement));
	}

	public synchronized boolean update(AbstractQuery query, int index, String whereStatement)
	{
		return helper.execSQL(sqlUpdate(query, index, whereStatement));
	}

	public synchronized AbstractQuery select(ETableNames t)
	{
		ArrayList<IColumn> cols = EColumnNames.getColumns(t);
		return helper.execSQL(sqlSelect(t), cols.toArray(new IColumn[cols.size()]));
	}

	public synchronized AbstractQuery select(ETableNames t, String where)
	{
		ArrayList<IColumn> cols = EColumnNames.getColumns(t);
		return helper.execSQL(sqlSelect(t, where), cols.toArray(new IColumn[cols.size()]));
	}

	/**
	 * Generates a SELECT SQL command from the given columns using their natural joint product.
	 * @author Polcz Péter <ppolcz@gmail.com> 
	 * @return a table model (AbstractQuery)
	 */
	@SafeVarargs
	public synchronized final AbstractQuery select(IColumn... cols)
	{
		return select("", cols);
	}

	/**
	 * @param cols
	 */
	public synchronized AbstractQuery select(List<IColumn> cols)
	{
		return select("", cols.toArray(new EColumnNames[cols.size()]));
	}

	/**
	 * @author Polcz Péter <ppolcz@gmail.com>
	 */
	@SafeVarargs
	public synchronized final AbstractQuery select(String where, IColumn... cols)
	{
		return helper.execSQL(sqlSelect(where, cols), cols);
	}

	@Deprecated
	public synchronized boolean insert(ETableNames table, Object[] obj)
	{
		return true;
	}
}
