package com.napol.koltsegvetes.dbdriver;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import com.napol.koltsegvetes.db.AbstractQuery;
import com.napol.koltsegvetes.db.IColumn;
import com.napol.koltsegvetes.db.QueryBuilder;
import com.napol.koltsegvetes.util.Debug;

/** 
 * @author Péter Polcz <ppolcz@gmail.com>
 * 
 * Created on September 21, 2014, 7:59 AM
 */
public enum SQLiteDriverJDBC implements ISQLiteHelper
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

    @Override
    public synchronized void onCreate()
    {
        for (String command : sql.sqlCreateTableCommands())
        {
            try
            {
                Statement s = c.createStatement();
                Debug.log(command);
                s.executeUpdate(command);
                s.close();
                Debug.log("<> Table created");
            }
            catch (SQLException e)
            {
                // System.out.println("pcz> Catched exception: ");
                // e.printStackTrace();
                e.printStackTrace();
            }
        }
    }

    @Override
    public synchronized void onUpgrade(int newVersion)
    {
        new NotImplementedException().printStackTrace();
    }

    @Override
    public synchronized void onDestroy()
    {
        if (!new File(sql.getFilename()).delete()) throw new RuntimeException("I could not delete the database file");
//        new NotImplementedException().printStackTrace();
    }

    @Override
    public synchronized void onOpen()
    {
        try
        {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:" + sql.getFilename());
        }
        catch (Exception e)
        {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            e.printStackTrace();
            System.exit(0);
        }
        Debug.log("<> Opened database successfully");
    }

    @Override
    public synchronized void onClose()
    {
        new NotImplementedException().printStackTrace();
    }

    @Override
    public AbstractQuery execSQL(String sqlcommand, IColumn... cols)
    {
        try
        {
            Statement s = c.createStatement();
            ResultSet r = s.executeQuery(sqlcommand);
            AbstractQuery q = QueryBuilder.create(new AbstractQuery(), cols);
            // System.out.println("cols.length = " + cols.length);
            while (r.next())
            {
                Object[] row = new Object[cols.length];
                // System.out.println("record.length = " + record.length);
                for (int i = 0; i < cols.length; ++i)
                    row[i] = r.getObject(cols[i].sqlname());
                q.appendRecord(cols, row);
            }
            s.close();
            
            return q;
        }
        catch (SQLException e)
        {
            Debug.debug("error ocured while inserting this row: \npcz>   " + sqlcommand, e);
            e.printStackTrace();
        }
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
        }
        catch (SQLException e)
        {
            // System.out.println("pcz> error ocured while inserting this row: \npcz>   " + sqlcommand);
            // new SQLException().printStackTrace();
            // e.printStackTrace();
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
            if (r.next())
            {
                return (int) r.getObject(1);
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        catch (NullPointerException e)
        {
            e.printStackTrace();
        }
        return -1;
    }

}
