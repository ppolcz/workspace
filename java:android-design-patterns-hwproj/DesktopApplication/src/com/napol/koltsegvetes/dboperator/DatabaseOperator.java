package com.napol.koltsegvetes.dboperator;

import java.io.File;
import com.napol.koltsegvetes.db.AbstractDataStore;
import com.napol.koltsegvetes.db.AbstractQuery;
import com.napol.koltsegvetes.db.EColumnNames;
import com.napol.koltsegvetes.db.ETableNames;
import com.napol.koltsegvetes.db.IColumn;
import com.napol.koltsegvetes.dbdriver.ISQLiteHelper;
import com.napol.koltsegvetes.dbdriver.SQLiteDriverJDBC;

public class DatabaseOperator {
	public AbstractDataStore database;

	public DatabaseOperator() {
		database = new AbstractDataStore() {

			protected ISQLiteHelper getHelperInstance() {
				return SQLiteDriverJDBC.INSTANCE;
			}

			@Override
			protected boolean isCreated() {
				return new File(sqlCommands.getFilename()).exists();
			}
		};
		database.onOpen();
	}

	public String getSingleField(String colName, String value, String selectFrom) {
		String statement = selectFrom + " = " + "\'" + value + "\'";
		AbstractQuery qField = database.select(statement,
				this.getIColumn(colName));
		try {
			return qField.getValue(this.getIColumn(colName), 0).toString();
		} catch (Exception e) {
			System.err.println("A mezõ üres: " + e);
		}
		return "0";
	}

	/*
	 * public void upDate() { database.onUpgrade(); }
	 */
	public String[] getTableNames() {
		String[] tables;
		ETableNames[] t = ETableNames.values();
		tables = new String[t.length];
		for (int i = 0; i < t.length; i++) {
			tables[i] = t[i].name();
		}
		return tables;
	}

	public String[] getTablePrefixes() {
		String[] prefixes;
		ETableNames[] t = ETableNames.values();
		prefixes = new String[t.length];
		for (int i = 0; i < t.length; i++) {
			prefixes[i] = t[i].prefix();
		}

		return prefixes;
	}

	/*
	 * public AbstractQuery getTableByName(String name) {
	 * 
	 * ETableNames table = ETableNames.valueOf(name); AbstractQuery ret = null;
	 * EColumnNames[] cols = EColumnNames.values(); EColumnNames[] needed; int j
	 * = 0; for (int i = 0; i<cols.length; i++) { if
	 * (cols[i].table().equals(table)) { j++; } } String sql = ""; if (j>0) {
	 * int k = 0; needed = new EColumnNames[j]; for (int i = 0; i<cols.length;
	 * i++) { if (cols[i].table().equals(table)) { needed[k] = cols[i]; sql +=
	 * cols[i].sqlname(); } }
	 * 
	 * } //ret = database.select(needed);
	 * System.out.println("Ez lenne a parancsban: " + sql); return ret; }
	 * 
	 * public AbstractQuery getTableByPrefix(String prefix) {
	 * 
	 * AbstractQuery table = db.select(); }
	 */

	public AbstractQuery getTable(String name) {
		ETableNames table = null;
		for (ETableNames t : ETableNames.values()) {
			if (t.toString().equals(name)) {
				table = t;
				break;
			}
		}
		AbstractQuery q = database.select(table);
		return q;
	}

	public AbstractQuery[] getTables() {

		String[] names = getTableNames();
		AbstractQuery[] tables = new AbstractQuery[names.length];
		for (int i = 0; i < names.length; i++) {
			tables[i] = this.getTable(names[i]);
		}
		return tables;
	}

	public AbstractQuery getColumn(String colName) {
		EColumnNames[] cols = EColumnNames.values();
		EColumnNames actCol = null;
		for (int i = 0; i < cols.length; i++) {
			if (cols[i].name() == colName) {
				actCol = cols[i];
				break;
			}
		}
		AbstractQuery ret = null;

		if (actCol != null) {
			ret = database.select(actCol);
		}

		return ret;
	}

	public IColumn getIColumn(String colName) {
		IColumn[] cols = EColumnNames.values();
		IColumn actCol = null;
		for (int i = 0; i < cols.length; i++) {
			if (cols[i].name() == colName) {
				actCol = cols[i];
				break;
			}
		}
		return actCol;
	}

	public String[] getColumnValues(String cname) {
		String[] ret = null;
		AbstractQuery col = getColumn(cname);
		int i = 0;
		for (Object[] c : col) {
			for (@SuppressWarnings("unused")
			Object o : c) {
				i++;
			}
		}
		ret = new String[i];
		i = 0;
		for (Object[] c : col) {
			for (Object o : c) {
				ret[i] = o.toString();
				i++;
			}
		}
		return ret;
	}

	public boolean addRow(String tableName, String[] values) {
		IColumn[] q = this.getTable(tableName).getCols();
		database.insert(q, (Object[]) values);
		return true;
	}

	public AbstractQuery getQuery(String tableName, String where) {
		ETableNames table = null;

		for (ETableNames t : ETableNames.values()) {
			if (t.toString().equals(tableName)) {
				table = t;
				break;
			}
		}
		try {
			AbstractQuery q = database.select(table, where);
			return q;
		} catch (Exception e) {
			System.err.println("Nem sikerült a lekérdezés: " + e.toString());
			return null;
		}
	}

	public void upDate() {
		database.onUpgrade();
	}
}
