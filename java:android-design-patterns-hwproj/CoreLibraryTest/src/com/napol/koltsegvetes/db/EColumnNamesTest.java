package com.napol.koltsegvetes.db;

import static com.napol.koltsegvetes.db.EColumnNames.*;
import static com.napol.koltsegvetes.db.EColumnNames.CA_ID;
import static com.napol.koltsegvetes.db.EColumnNames.MK_INSERT_DATE;
import static com.napol.koltsegvetes.db.EColumnNames.PI_AMOUNT;
import static com.napol.koltsegvetes.db.EColumnNames.PI_DATE;
import static com.napol.koltsegvetes.db.EColumnNames.PI_INSERT_DATE;
import static com.napol.koltsegvetes.db.EColumnNames.TR_CAID;
import static com.napol.koltsegvetes.db.EColumnNames.TR_DATE;
import static com.napol.koltsegvetes.db.EColumnNames.TR_ID;
import static com.napol.koltsegvetes.db.EColumnNames.TR_INSERT_DATE;
import static com.napol.koltsegvetes.db.EColumnNames.TR_NEWBALANCE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class EColumnNamesTest
{
    class Table extends AbstractQuery
    {
        private static final long serialVersionUID = 1010364751094173432L;
    }

    AbstractDataStore db;

    @Before
    public void setUp() throws Exception
    {
        // db = new AbstractDataStore()
        // {
        // @Override
        // protected boolean isCreated()
        // {
        // return false;
        // }
        //
        // @Override
        // protected ISQLiteHelper getHelperInstance()
        // {
        // return null;
        // }
        // };
        // db.onOpen();
    }

    @After
    public void tearDown() throws Exception
    {
    }

    @Test
    public void testSqlname()
    {
        assertEquals("tr_id", TR_ID.sqlname());
    }

    @Test
    public void testSqltypeall()
    {
        assertEquals("integer primary key autoincrement not null unique", TR_ID.sqltypeall());
    }

    @Test
    public void testSqltype()
    {
        assertEquals("integer", TR_ID.sqltype());
    }

    @Test
    public void testSqlnameString()
    {
        // TODO
    }

    @Test
    public void testJavatype()
    {
        assertEquals(Date.class, TR_DATE.javatype());
        assertEquals(Date.class, TR_INSERT_DATE.javatype());
        assertEquals(Date.class, PI_DATE.javatype());
        assertEquals(Date.class, PI_INSERT_DATE.javatype());
    }

    @Test
    public void testTable()
    {
        assertEquals(ETableNames.TRANZACTIONS, TR_NEWBALANCE.table());
        assertEquals(ETableNames.PRODUCT_INFO, PI_AMOUNT.table());
        assertEquals(ETableNames.ACCOUNTS, AC_USERNAME.table());
        assertEquals(ETableNames.MARKETS, MK_INSERT_DATE.table());
    }

    @Test
    public void testRef()
    {
        assertEquals(CA_ID, TR_CAID.ref());
    }

    @Test
    public void testIsDateType()
    {
        assertTrue(TR_INSERT_DATE.isDateType());
        assertTrue(TR_DATE.isDateType());
        assertTrue(PI_DATE.isDateType());
        assertTrue(PI_INSERT_DATE.isDateType());
    }

    @Test
    public void testIsInsertDate()
    {
        assertFalse(TR_DATE.isInsertDate());
        assertFalse(PI_DATE.isInsertDate());

        assertTrue(TR_INSERT_DATE.isInsertDate());
        assertTrue(PI_INSERT_DATE.isInsertDate());
    }

    @Test
    public void testToDate()
    {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String str = "2014-03-20";
        Object obj = TR_DATE.toDate(str);
        assertTrue(obj instanceof Date);
        Date date = (Date) obj;
        assertEquals(str, df.format(date));

        // printouts:
        System.out.println(EColumnNames.fancyDateFormat.format(date));
        System.out.println(EColumnNames.isoDateFormat.format(date));
        System.out.println(EColumnNames.simpleDateFormat.format(date));
    }

    @Test
    public void testToDisplayString()
    {
        String str = "2014-03-20";
        Date refdate = null;
        try
        {
            refdate = IColumn.simpleDateFormat.parse(str);
        }
        catch (ParseException e)
        {
            fail(e.getMessage());
        }
        Date date = (Date) TR_DATE.toDate(str);
        assertEquals(refdate, date);
        assertEquals(str, TR_DATE.toDisplayString(date));
        assertEquals(str, TR_DATE.toDisplayString(str));
        assertEquals("2014-03-20", str);
    }

    @Test
    public void testToSqlString()
    {
        System.out.println("TR_DATE.toDisplayString(null) = " + TR_DATE.toDisplayString(null));
        assertEquals("null", TR_DATE.toDisplayString(null));
        assertEquals("null", TR_DATE.toSqlString(null));
        assertEquals("0", TR_ID.toDisplayString(null));
        assertEquals("null", TR_ID.toSqlString(null));
        assertEquals("null", TR_REMARK.toDisplayString(null));
        assertEquals("null", TR_REMARK.toSqlString(null));
    }

}
