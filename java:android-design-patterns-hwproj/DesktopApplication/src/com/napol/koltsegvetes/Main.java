package com.napol.koltsegvetes;

import static com.napol.koltsegvetes.db.EColumnNames.CA_BALANCE;
import static com.napol.koltsegvetes.db.EColumnNames.CA_NAME;
import static com.napol.koltsegvetes.db.EColumnNames.CL_NAME;
import static com.napol.koltsegvetes.db.EColumnNames.QR_DATE;
import static com.napol.koltsegvetes.db.EColumnNames.TR_AMOUNT;
import static com.napol.koltsegvetes.db.EColumnNames.TR_CAID;
import static com.napol.koltsegvetes.db.EColumnNames.TR_CLNAME;
import static com.napol.koltsegvetes.db.EColumnNames.TR_REMARK;
import static com.napol.koltsegvetes.db.IColumn.opEqual;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import com.napol.koltsegvetes.db.AbstractDataStore;
import com.napol.koltsegvetes.db.AbstractQuery;
import com.napol.koltsegvetes.db.ETableNames;
import com.napol.koltsegvetes.db.IColumn;
import com.napol.koltsegvetes.db.QueryBuilder;
import com.napol.koltsegvetes.dbdriver.ISQLiteHelper;
import com.napol.koltsegvetes.dbdriver.SQLiteDriverJDBC;
import com.napol.koltsegvetes.net.NetworkInterface;
import com.napol.koltsegvetes.util.Debug;
import com.napol.koltsegvetes.util.MetaUtil;

/**
 * @author Péter Polcz <ppolcz@gmail.com>
 * 
 * Created on September 21, 2014, 7:59 AM
 */
public class Main
{

    AbstractDataStore db = new AbstractDataStore()
    {
        @Override
        protected ISQLiteHelper getHelperInstance()
        {
//            Debug.plantSequence(new NullPointerException());
            return SQLiteDriverJDBC.INSTANCE;
        }

        @Override
        protected boolean isCreated()
        {
            return new File(sqlCommands.getFilename()).exists();
        }
    };

    public Main()
    {
//        MetaUtil.GeneratePlantUML(SQLiteDriverJDBC.class);
//        MetaUtil.GeneratePlantUML(NetworkInterface.class);

        db.onOpen();
        db.onDestroy();

        // testing if EColumnNames works good
        System.out.println(TR_CAID.ref());
        System.out.println(TR_AMOUNT.table());
        System.out.println(QR_DATE.table());
        System.out.println(CA_BALANCE.table());

        AbstractQuery q = db.select(TR_AMOUNT, TR_CAID, TR_CLNAME, CA_BALANCE, CA_NAME);

        IColumn[] cols = { TR_AMOUNT, TR_CLNAME, CL_NAME };
        AbstractQuery q1 = QueryBuilder.create(new AbstractQuery(), cols);
        q1.appendRecord(cols, 12, "Kutyagumi", "ugyanaz");
//Itt egy komment.
        q.appendQuery(q1);

        for (Object[] r : q)
        {
            System.out.println("----------------------------");
            for (int i = 0; i < r.length; ++i)
            {
                IColumn t = q.getCols()[i];
                System.out.println(t.name() + ": " + t.toSqlString(r[i] != null ? r[i] : "null")
                    + " - " + t.toDisplayString(r[i] != null ? r[i] : "null"));
            }
        }
        System.out.println("----------------------------");

        Map<IColumn, Object> row = new HashMap<>();

        row.clear();
        row.put(TR_AMOUNT, 40000);
        row.put(TR_REMARK, "10 bax narancs nektar");
        row.put(TR_CAID, "potp");
        System.out.println(db.insert(row));

        row.clear();
        row.put(TR_AMOUNT, 4000);
        row.put(TR_REMARK, "10 bax narancs nektar");
        row.put(TR_CAID, "potp");
        System.out.println(db.insert(row));

        row.clear();
        row.put(TR_AMOUNT, 400);
        row.put(TR_REMARK, "10 bax narancs nektar");
        row.put(TR_CAID, "potp");
        System.out.println(db.insert(row));

        row.clear();
        row.put(TR_AMOUNT, 40);
        row.put(TR_REMARK, "10 bax narancs nektar");
        row.put(TR_CAID, "potp");
        System.out.println(db.insert(row));

        // testing Abstract query
        IColumn[] cols2 = { TR_AMOUNT, TR_REMARK };
        AbstractQuery table = QueryBuilder.create(new AbstractQuery(), cols2);
        table.appendRecord(cols2, 1231, "Kutyafule");

        System.out.println(table.isEmpty());
        System.out.println(table.getFirst()[0]);

        db.delete(ETableNames.TRANZACTIONS, TR_AMOUNT.sqlwhere(40000, opEqual));

        // new Thread(new NetworkInterface()).start();
    }

    public static void main(String[] args)
    {
        new Main();
        System.out.println("SUCCES: The application returned successfully!");
    }

}
