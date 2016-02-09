package com.polpe.hrm.db;

import static com.polpe.hrm.db.ETableNames.MySQL_TYPE_DATE;
import static com.polpe.hrm.db.ETableNames.MySQL_TYPE_DATETIME;
import static com.polpe.hrm.db.ETableNames.SQL_TYPE_DATE;
import static com.polpe.hrm.db.ETableNames.SQL_TYPE_DATETIME;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import com.polpe.hrm.util.Debug;

/** 
 * @author Péter Polcz <ppolcz@gmail.com>
 * 
 * Created on September 21, 2014, 7:59 AM
 * 
 *
 * CREATE USER guest@localhost IDENTIFIED BY 'guestpass';
 * GRANT ALL PRIVILEGES ON koltsegvetes.* TO guest@localhost WITH GRANT OPTION;
 */
public class MySQLDriverJDBC implements ISQLiteHelper
{
	private ISQLCommands sql;
	private Connection c = null;

	private String uname;
	private String pass;
	private String dbname;

	public static ISQLiteHelper instance()
	{
		return instance;
	}

	public static ISQLiteHelper instance(String uname, String pass, String tablename)
	{
		if (instance == null) instance = new MySQLDriverJDBC(uname, pass, tablename);
		return instance;
	}

	private static MySQLDriverJDBC instance;

	private MySQLDriverJDBC(String uname, String pass, String dbname)
	{
		this.uname = uname;
		this.pass = pass;
		this.dbname = dbname;
	}

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
		String[] sql = {
			"DROP FUNCTION IF EXISTS last_insert_rowid;",
			"CREATE FUNCTION last_insert_rowid() RETURNS INT\n" +
				"BEGIN\n" +
				"    DECLARE id INT;\n" +
				"    SELECT last_insert_id() INTO id;\n" +
				"    RETURN id;\n" +
				"END;\n"
		};
		for (String str : sql)
		{
			try
			{
				Statement s = c.createStatement();
				s.execute(str);
				s.close();
			} catch (SQLException e)
			{
				Debug.debug(e);
				System.out.println(str);
				System.exit(1);
			}
		}
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
		String[] sql = AbstractDataStore.sqldroptables();
		for (String stm : sql)
		{
			quickex(stm);
		}
		quickex("DROP DATABASE IF EXISTS " + dbname);
	}

	private void quickex(String sql)
	{
		try
		{
			c.createStatement().executeUpdate(sql);
		} catch (SQLException e)
		{
			System.out.println(sql);
			e.printStackTrace();
			System.exit(0);
		}
	}

	@Override
	public synchronized void onOpen()
	{
		try
		{
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			c = DriverManager.getConnection("jdbc:mysql://localhost/?user=" + uname + "&password=" + pass);
			c.createStatement().executeUpdate("CREATE DATABASE IF NOT EXISTS " + dbname + ";");
			c.createStatement().executeQuery("USE " + dbname).close();
		} catch (Exception e)
		{
			System.out.println("jdbc:mysql://localhost/?user=" + uname + "&password=" + pass);
			System.out.println("CREATE DATABASE IF NOT EXISTS " + dbname + ";");
			System.out.println("USE " + dbname);
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
	public boolean execSQL(String sqlcommand) throws SQLException
	{
		// try
		// {
		Statement s = c.createStatement();
		s.executeUpdate(sqlcommand);
		s.close();
		return true;
		// } catch (SQLException e)
		// {
		// System.out.println("pcz> error ocured while inserting this row: \npcz>   " + sqlcommand);
		// new SQLException().printStackTrace();
		// e.printStackTrace();
		// Debug.debug(e);
		// if (e.getMessage().contains("foreign"))
		// {
		// System.out.println(sqlcommand);
		// }
		// }
		// return false;
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
