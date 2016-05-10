package com.napol.koltsegvetes.dbdriver;

import static com.napol.koltsegvetes.util.Util.debug;
import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.napol.koltsegvetes.dbdriver.ISQLCommands;

/**
 * @author Polcz Péter <ppolcz@gmail.com>
 * 
 * Created on Sep 24, 2014 9:50:50 PM
 */
public class MySQLiteOpenHelper extends SQLiteOpenHelper
{
    // required interface from the CoreLibrary
    private ISQLCommands sql;

    MySQLiteOpenHelper(Context context, ISQLCommands sql)
    {
        super(context, sql.getFilename(), null, sql.getVersion());
        this.sql = sql;
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        // new NullPointerException().printStackTrace();
        // if (sql.sqlCreateTableCommands() == null) debug("sql.sqlCreateTableCommands() == null");
        for (String command : sql.sqlCreateTableCommands())
        {
            try
            {
                db.execSQL(command);
            }
            catch (SQLException e)
            {
                debug("Table creation exception", e);
            }
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        // TODO Auto-generated method stub
        debug("not implemented yet");
    }
}
