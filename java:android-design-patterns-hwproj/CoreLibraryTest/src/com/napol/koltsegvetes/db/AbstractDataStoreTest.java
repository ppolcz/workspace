package com.napol.koltsegvetes.db;

import static com.napol.koltsegvetes.db.EColumnNames.*;
import static com.napol.koltsegvetes.db.EColumnNames.TR_AMOUNT;
import static com.napol.koltsegvetes.db.EColumnNames.TR_CAID;
import static com.napol.koltsegvetes.db.EColumnNames.TR_DATE;
import static com.napol.koltsegvetes.db.EColumnNames.TR_ID;
import static com.napol.koltsegvetes.db.EColumnNames.TR_MKNAME;
import static com.napol.koltsegvetes.db.EColumnNames.TR_NEWBALANCE;
import static org.junit.Assert.*;
import static com.polcz.matchers.RegexMatcher.*;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.napol.koltsegvetes.db.AbstractQueryTest.Table;
import com.napol.koltsegvetes.dbdriver.ISQLiteHelper;
import com.napol.koltsegvetes.dbdriver.SQLiteDriverJDBC;

public class AbstractDataStoreTest
{
    private static AbstractDataStore db;

    @BeforeClass
    public static void setUpClass() throws Exception
    {
        db = new AbstractDataStore()
        {
            public static final String DBNAME = "database-test";

            @Override
            protected String getDatabaseNameWithoutExtension()
            {
                return DBNAME;
            }

            @Override
            protected boolean isCreated()
            {
                return new File(this.sqlInitHelper.getFilename()).exists();
            }

            @Override
            protected ISQLiteHelper getHelperInstance()
            {
                return SQLiteDriverJDBC.INSTANCE;
            }

            @Override
            protected boolean initialize()
            {
                return true;
            }
        };
        db.onOpen();
    }

    @AfterClass
    public static void tearDownClass() throws Exception
    {
        System.out.println("TEAR_DOWN");
        // db.onDestroy();
    }

    // @Test
    // public void testAbstractDataStore()
    // {
    // fail("Not yet implemented");
    // }

    // @Test
    // public void testOnOpen()
    // {
    // fail("Not yet implemented");
    // }

    // @Test
    // public void testOnDestroy()
    // {
    // fail("Not yet implemented");
    // }

    // @Test
    // public void testOnUpgrade()
    // {
    // fail("Not yet implemented");
    // }

    @Test
    public void testGetHelperInstance()
    {
        assertEquals(SQLiteDriverJDBC.INSTANCE, db.getHelperInstance());
    }

    @Test
    public void testIsCreated()
    {
        assertTrue(db.isCreated());
    }

    @Test
    public void testInsertIColumnArrayObjectArray()
    {
        IColumn[] cols = { TR_ACID, TR_ID, TR_AMOUNT, TR_MKNAME };
        IColumn[] otherCols = { TR_CAID, TR_NEWBALANCE, TR_AMOUNT };
        IColumn[] noCommonCols = { TR_CAID, TR_NEWBALANCE, TR_DATE };

        Table query = (Table) QueryBuilder.create(new Table(), cols);
        query.appendRecord(cols, "ppolcz", 12, 1311, "Interspar");
        query.appendRecord(otherCols, "ptop", 13131, 13);
        query.appendRecord(noCommonCols, "ptop", 13131, "2014-03-20");

        Table table = (Table) QueryBuilder.create(new Table(), otherCols, "potp", 13131, 14);
        table.appendRecord(noCommonCols, "pkez", 1414, "2014-03-20");
        query.appendQuery(table);

        for (Object[] row : query)
        {
            int insertId = db.insert(query.getCols(), row);
            System.out.println(insertId);
        }
        AbstractQuery trans = db.select(ETableNames.TRANZACTIONS);
        trans.print();
    }

    @Test
    public void testInsertAllAbstractQuery()
    {
        fail("Not yet implemented");
    }

    @Test
    public void testInsertAllAbstractQueryIColumn()
    {
        fail("Not yet implemented");
    }

    @Test
    public void testInsertMapOfIColumnObject()
    {
        fail("Not yet implemented");
    }

    @Test
    public void testDelete()
    {
        fail("Not yet implemented");
    }

    @Test
    public void testUpdate()
    {
        fail("Not yet implemented");
    }

    @Test
    public void testSelectETableNamesString()
    {
        fail("Not yet implemented");
    }

    @Test
    public void testSelectIColumnArray()
    {
        fail("Not yet implemented");
    }

    @Test
    public void testSelectListOfIColumn()
    {
        fail("Not yet implemented");
    }

    @Test
    public void testSelectStringIColumnArray()
    {
        fail("Not yet implemented");
    }

    // ------------------------------------------------------- //

    private static SimpleDateFormat isoDateTimePattern = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss\\..*", Locale.getDefault());
    private static SimpleDateFormat isoDatePattern = new SimpleDateFormat("yyyy-MM-dd 00:00:00\\..*", Locale.getDefault());

    public static String getIsoDateTime()
    {
        return isoDateTimePattern.format(Calendar.getInstance().getTime());
    }

    public static String getIsoDate()
    {
        return isoDatePattern.format(Calendar.getInstance().getTime());
    }

    public static String getSimpleDate()
    {
        return IColumn.simpleDateFormat.format(Calendar.getInstance().getTime());
    }
    
    @Test
    public void testSqlInsert_Simple()
    {
        IColumn[] cols = { TR_AMOUNT };
        String sqlCommand = AbstractDataStore.sqlInsert(cols, 1231);
        String expectedSqlCommand = escapeParathesis("insert into tranzactions (tr_amount, tr_insert_date) values (1231, '" + getIsoDateTime() + "')");
        assertThat(sqlCommand, matchesRegex(expectedSqlCommand));
    }

    @Test
    public void testSqlInsert_Id()
    {
        IColumn[] cols = { TR_ID, TR_AMOUNT, TR_INSERT_DATE };
        String sqlCommand = AbstractDataStore.sqlInsert(cols, 12, 1231, Calendar.getInstance().getTime());
        String expectedSqlCommand = escapeParathesis("insert into tranzactions (tr_amount, tr_insert_date) values (1231, '" + getIsoDateTime() + "')");
        assertThat(sqlCommand, matchesRegex(expectedSqlCommand));
    }

    @Test
    public void testSqlInsert_Date()
    {
        IColumn[] cols = { TR_ID, TR_AMOUNT, TR_DATE };
        String sqlCommand = AbstractDataStore.sqlInsert(cols, 12, 1231, Calendar.getInstance().getTime());
        String expectedSqlCommand = escapeParathesis("insert into tranzactions (tr_amount, tr_date, tr_insert_date) "
            + "values (1231, '" + getIsoDateTime() + "', '" + getIsoDateTime() + "')");
        assertThat(sqlCommand, matchesRegex(expectedSqlCommand));
    }

    @Test
    public void testSqlInsert_DateString()
    {
        IColumn[] cols = { TR_ID, TR_AMOUNT, TR_DATE };
        String sqlCommand = AbstractDataStore.sqlInsert(cols, 12, 1231, getSimpleDate());
        String expectedSqlCommand = escapeParathesis("insert into tranzactions (tr_amount, tr_date, tr_insert_date) "
            + "values (1231, '" + getIsoDate() + "', '" + getIsoDateTime() + "')");
        assertThat(sqlCommand, matchesRegex(expectedSqlCommand));
    }

    @Test
    public void testSqlDelete()
    {
        fail("Not yet implemented");
    }

    @Test
    public void testSqlUpdate()
    {
        fail("Not yet implemented");
    }

    @Test
    public void testSqlSelectETableNames()
    {
        fail("Not yet implemented");
    }

    @Test
    public void testSqlSelectETableNamesString()
    {
        fail("Not yet implemented");
    }

    @Test
    public void testSqlSelectStringIColumnArray()
    {
        fail("Not yet implemented");
    }
}
