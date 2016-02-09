package com.polcz.koltsegvetes.dbdriver;

import static com.napol.koltsegvetes.db.ETableNames.MySQL_TYPE_DATE;
import static com.napol.koltsegvetes.db.ETableNames.MySQL_TYPE_DATETIME;
import static com.napol.koltsegvetes.db.ETableNames.SQL_TYPE_DATE;
import static com.napol.koltsegvetes.db.ETableNames.SQL_TYPE_DATETIME;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import com.napol.koltsegvetes.db.AbstractQuery;
import com.napol.koltsegvetes.db.IColumn;
import com.napol.koltsegvetes.dbdriver.ISQLCommands;
import com.napol.koltsegvetes.dbdriver.ISQLiteHelper;
import com.napol.koltsegvetes.util.Debug;

/** 
 * @author Péter Polcz <ppolcz@gmail.com>
 * 
 * Created on September 21, 2014, 7:59 AM
 * 
 *
 * CREATE USER guest@localhost IDENTIFIED BY 'guestpass';
 * GRANT ALL PRIVILEGES ON koltsegvetes.* TO guest@localhost WITH GRANT OPTION;
 */
public enum MySQLDriverJDBC implements ISQLiteHelper
{
	INSTANCE;

	// public static ISQLiteHelper instance()
	// {
	// return instance;
	// }

	private ISQLCommands sql;
	private Connection c = null;

	@Override
	public synchronized ISQLiteHelper setSqlInterface(ISQLCommands sql)
	{
		this.sql = sql;
		return this;
	}

	public static String transformFromSQLite(String cmd)
	{
		String ret = cmd
			.replace(SQL_TYPE_DATE, MySQL_TYPE_DATE)
			.replace(SQL_TYPE_DATETIME, MySQL_TYPE_DATETIME)
			.toLowerCase()
			.replaceAll("autoincrement", "auto_increment");
		return ret;
	}

	private void sqlCreateLastInsertRowidFunction()
	{
		// String sql =
		// "DROP FUNCTION IF EXISTS last_insert_rowid;\n" +
		// "DELIMITER $$\n" +
		// "CREATE FUNCTION last_insert_rowid() RETURNS INT\n" +
		// "BEGIN\n" +
		// "    DECLARE id INT;\n" +
		// "    SELECT last_insert_id() INTO id;\n" +
		// "    RETURN id;\n" +
		// "END;\n" +
		// "$$\n" +
		// "DELIMITER ;\n";
		// try
		// {
		// Statement s = c.createStatement();
		// s.execute(sql);
		// s.close();
		// }
		// catch (SQLException e)
		// {
		// Debug.debug(e);
		// System.out.println(sql);
		// System.exit(1);
		// }
	}

	@Override
	public synchronized void onCreate()
	{
		for (String command : sql.sqlCreateTableCommands())
		{
			String mysqlcommand = transformFromSQLite(command);
			System.out.println(mysqlcommand);
			System.out.println("");
			try
			{
				Statement s = c.createStatement();
				Debug.log(mysqlcommand);
				s.executeUpdate(mysqlcommand);
				s.close();
				Debug.log("<> Table created");
			} catch (SQLException e)
			{
				// System.out.println("pcz> Catched exception: ");
				// e.printStackTrace();
				e.printStackTrace();
			}
		}
		sqlCreateLastInsertRowidFunction();
	}

	@Override
	public synchronized void onUpgrade(int newVersion)
	{
		new NotImplementedException().printStackTrace();
	}

	@Override
	public synchronized void onDestroy()
	{
		// if (!new File(sql.getFilename()).delete()) throw new RuntimeException("I could not delete the database file");
		// new NotImplementedException().printStackTrace();
	}

	@Override
	public synchronized void onOpen()
	{
		try
		{
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			c = DriverManager.getConnection("jdbc:mysql://localhost/?user=java&password=javapass");
			c.createStatement().executeQuery("use javadrafts2").close();
		} catch (Exception e)
		{
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			e.printStackTrace();
			System.exit(0);
		}
		Debug.log("<> Database opened successfully");
	}

	@Override
	public synchronized void onClose()
	{
		new NotImplementedException().printStackTrace();
	}

	@Override
	public AbstractQuery execSQL(String sqlcommand, IColumn... cols)
	{
		// try
		// {
		// Statement s = c.createStatement();
		// ResultSet r = s.executeQuery(sqlcommand);
		// AbstractQuery q = QueryBuilder.create(new AbstractQuery(), cols);
		// // System.out.println("cols.length = " + cols.length);
		// while (r.next())
		// {
		// Object[] row = new Object[cols.length];
		// // System.out.println("record.length = " + record.length);
		// for (int i = 0; i < cols.length; ++i)
		// row[i] = r.getObject(cols[i].sqlname());
		// q.appendRecord(cols, row);
		// }
		// s.close();
		//
		// return q;
		// } catch (SQLException e)
		// {
		// Debug.debug("error ocured while inserting this row: \npcz>   " + sqlcommand, e);
		// e.printStackTrace();
		// }
		return null;
	}

	@Override
	public boolean execSQL(String sqlcommand)
	{
		try
		{
			Statement s = c.createStatement();
			s.executeUpdate(sqlcommand);
			s.close();
			return true;
		} catch (SQLException e)
		{
			// System.out.println("pcz> error ocured while inserting this row: \npcz>   " + sqlcommand);
			// new SQLException().printStackTrace();
			// e.printStackTrace();
			Debug.debug(e);
			if (e.getMessage().contains("foreign"))
			{
				System.out.println(sqlcommand);
			}
		}
		return false;
	}

	@Override
	public int lastInsertRowID()
	{
		try
		{
			Statement s = c.createStatement();
			ResultSet r = s.executeQuery("select last_insert_rowid()");
			if (r.next()) { return (int) r.getObject(1); }
		} catch (SQLException e)
		{
			e.printStackTrace();
		} catch (NullPointerException e)
		{
			e.printStackTrace();
		}
		return -1;
	}

}
