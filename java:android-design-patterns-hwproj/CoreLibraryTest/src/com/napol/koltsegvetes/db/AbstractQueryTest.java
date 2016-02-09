/**
 * Created on Nov 21, 2014 6:51:03 PM
 */

package com.napol.koltsegvetes.db;

import static com.napol.koltsegvetes.db.EColumnNames.TR_ACID;
import static com.napol.koltsegvetes.db.EColumnNames.TR_AMOUNT;
import static com.napol.koltsegvetes.db.EColumnNames.TR_CAID;
import static com.napol.koltsegvetes.db.EColumnNames.TR_CLNAME;
import static com.napol.koltsegvetes.db.EColumnNames.TR_DATE;
import static com.napol.koltsegvetes.db.EColumnNames.TR_ID;
import static com.napol.koltsegvetes.db.EColumnNames.TR_INSERT_DATE;
import static com.napol.koltsegvetes.db.EColumnNames.TR_MKNAME;
import static com.napol.koltsegvetes.db.EColumnNames.TR_NEWBALANCE;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * @author Polcz Péter <ppolcz@gmail.com>
 * Created on Nov 21, 2014 6:51:03 PM 
 */
public class AbstractQueryTest
{
    static class Table extends AbstractQuery
    {
        private static final long serialVersionUID = -7912857343481202627L;
    }

    /**
     * Test method for {@link com.napol.koltsegvetes.db.AbstractQuery#setCols(com.napol.koltsegvetes.db.IColumn[])}.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testSetCols_SameCol()
    {
        QueryBuilder.create(new AbstractQuery(), TR_ID, TR_AMOUNT, TR_CAID, TR_AMOUNT);
    }

    /**
     * Test method for {@link com.napol.koltsegvetes.db.AbstractQuery#setCols(com.napol.koltsegvetes.db.IColumn[])}.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testSetCols_QuasiSameCol()
    {
        ColumnDecorator col = new ColumnDecorator(TR_AMOUNT)
        {
            @Override
            public String displayName()
            {
                return "TR_AMOUNT___";
            }

            @Override
            public String sqlname()
            {
                return "abs(tr_amount)";
            }
        };
        QueryBuilder.create(new AbstractQuery(), TR_ID, TR_AMOUNT, TR_CAID, col);
    }

    /**
     * Test method for {@link com.napol.koltsegvetes.db.AbstractQuery#getCols()}.
     */
    @Test
    public void testGetCols()
    {
        // fail("Not yet implemented");
    }

    /**
     * Test method for {@link com.napol.koltsegvetes.db.AbstractQuery#getColsCount()}.
     */
    @Test
    public void testGetColsCount()
    {
        IColumn[] cols = { TR_ACID, TR_ID, TR_AMOUNT, TR_MKNAME };
        Table query = (Table) QueryBuilder.create(new Table(), cols);
        assertEquals(4, query.getColsCount());
    }

    /**
     * Test method for {@link com.napol.koltsegvetes.db.AbstractQuery#newRecord()}.
     */
    @Test
    public void testNewRecord()
    {
        AbstractQuery query = QueryBuilder.create(new Table(), TR_ID, TR_DATE, TR_INSERT_DATE, TR_AMOUNT);
        query.newRecord();
        assertEquals(null, query.getValue(TR_ID, 0));
        assertEquals(null, query.getValue(TR_DATE, 0));
        assertEquals(null, query.getValue(TR_INSERT_DATE, 0));
        assertEquals(null, query.getValue(TR_AMOUNT, 0));
        assertEquals(4, query.getFirst().length);
    }

    /**
     * Test method for {@link com.napol.koltsegvetes.db.AbstractQuery#appendRecord(com.napol.koltsegvetes.db.IColumn[], java.lang.Object[])}.
     */
    @Test
    public void testAppendRecord()
    {
        IColumn[] cols = { TR_ACID, TR_ID, TR_AMOUNT, TR_MKNAME };
        Table query = (Table) QueryBuilder.create(new Table(), cols);

        query.appendRecord(cols, "ppolcz", 12, 1311, "Interspar");
        assertEquals("ppolcz", query.getValue(TR_ACID, 0));
        assertEquals(12, query.getValue(TR_ID, 0));
        assertEquals(1311, query.getValue(TR_AMOUNT, 0));
        assertEquals("Interspar", query.getValue(TR_MKNAME, 0));

        IColumn[] otherCols = { TR_CAID, TR_NEWBALANCE, TR_AMOUNT };
        query.appendRecord(otherCols, "ptop", 13131, 13);
        assertEquals(null, query.getValue(TR_ACID, 1));
        assertEquals(null, query.getValue(TR_ID, 1));
        assertEquals(13, query.getValue(TR_AMOUNT, 1));
        assertEquals(null, query.getValue(TR_MKNAME, 1));

        IColumn[] noCommonCols = { TR_CAID, TR_NEWBALANCE, TR_DATE };
        query.appendRecord(noCommonCols, "ptop", 13131, "2014-03-20");
        assertEquals(null, query.getValue(TR_ACID, 2));
        assertEquals(null, query.getValue(TR_ID, 2));
        assertEquals(null, query.getValue(TR_AMOUNT, 2));
        assertEquals(null, query.getValue(TR_MKNAME, 2));
    }

    /**
     * Test method for {@link com.napol.koltsegvetes.db.AbstractQuery#appendQuery(com.napol.koltsegvetes.db.AbstractQuery)}.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testAppendQuery()
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

        assertEquals(null, query.getValue(TR_ACID, 3));
        assertEquals(null, query.getValue(TR_ID, 3));
        assertEquals(14, query.getValue(TR_AMOUNT, 3));
        assertEquals(null, query.getValue(TR_MKNAME, 3));

        assertEquals(null, query.getValue(TR_ACID, 4));
        assertEquals(null, query.getValue(TR_ID, 4));
        assertEquals(null, query.getValue(TR_AMOUNT, 4));
        assertEquals(null, query.getValue(TR_MKNAME, 4));

        Table otherTable = (Table) QueryBuilder.create(new Table(), noCommonCols, "potp", 131313, "2014-03-20");
        query.appendQuery(otherTable);
    }

    /**
     * Test method for {@link com.napol.koltsegvetes.db.AbstractQuery#print()}.
     */
    @Test
    public void testPrint()
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

        query.print();
    }

    /**
     * Test method for {@link com.napol.koltsegvetes.db.AbstractQuery#getPosition(com.napol.koltsegvetes.db.IColumn)}.
     */
    @Test
    public void testGetPosition()
    {
        AbstractQuery q = QueryBuilder.create(new AbstractQuery(), TR_ID, TR_ACID, TR_AMOUNT, TR_CAID, TR_CLNAME);
        assertEquals(0, q.getPosition(TR_ID));
        assertEquals(1, q.getPosition(TR_ACID));
        assertEquals(2, q.getPosition(TR_AMOUNT));
        assertEquals(3, q.getPosition(TR_CAID));
        assertEquals(4, q.getPosition(TR_CLNAME));
        assertEquals(-1, q.getPosition(TR_NEWBALANCE));
    }

    /**
     * Test method for {@link com.napol.koltsegvetes.db.AbstractQuery#getValue(com.napol.koltsegvetes.db.IColumn, int)}.
     */
    @Test
    public void testGetValue()
    {
        IColumn[] cols = { TR_ID, TR_ACID, TR_AMOUNT, TR_CAID, TR_CLNAME };
        AbstractQuery q = QueryBuilder.create(new AbstractQuery(), cols, 12, "ppolcz", 12312, "potp", "Etel");
        assertEquals("ppolcz", q.getValue(TR_ACID, 0));
    }

    /**
     * Test method for {@link com.napol.koltsegvetes.db.AbstractQuery#setValue(com.napol.koltsegvetes.db.IColumn, java.lang.Object[], java.lang.Object)}.
     */
    @Test(expected = IndexOutOfBoundsException.class)
    public void testSetValue_OutOfBounds()
    {
        IColumn[] cols = { TR_ID, TR_AMOUNT };
        AbstractQuery q = QueryBuilder.create(new AbstractQuery(), cols, 12, 1313);

        q.setValue(TR_ID, 123, 12312);
    }

    /**
     * Test method for {@link com.napol.koltsegvetes.db.AbstractQuery#setValue(com.napol.koltsegvetes.db.IColumn, java.lang.Object[], java.lang.Object)}.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testSetValue_IllegarArgument()
    {
        IColumn[] cols = { TR_ID, TR_AMOUNT };
        AbstractQuery q = QueryBuilder.create(new AbstractQuery(), cols, 12, 1313);
        q.appendRecord(cols, 13, 1414);
        q.appendRecord(cols, 14, 1514);
        q.appendRecord(cols, 15, 1614);
        q.appendRecord(cols, 17, 1714);
        q.appendRecord(cols, 18, 1814);

        assertEquals(1814, q.getValue(TR_AMOUNT, 5));
        q.setValue(TR_AMOUNT, 5, 9999);
        assertEquals(9999, q.getValue(TR_AMOUNT, 5));

        q.setValue(TR_NEWBALANCE, 4, 12312);
    }

    /**
     * Test method for {@link com.napol.koltsegvetes.db.AbstractQuery#update(com.napol.koltsegvetes.db.IColumn, java.lang.Object)}.
     */
    @Test
    public void testUpdateIColumnObject()
    {
        // fail("Not yet implemented");
    }

    /**
     * Test method for {@link com.napol.koltsegvetes.db.AbstractQuery#update(com.napol.koltsegvetes.db.IColumn[], java.lang.Object[])}.
     */
    @Test
    public void testUpdateIColumnArrayObjectArray()
    {
        IColumn[] cols = { TR_ID, TR_AMOUNT };
        AbstractQuery q = QueryBuilder.create(new AbstractQuery(), cols, 12, 1313);
        q.appendRecord(cols, 13, 1414);
        q.appendRecord(cols, 14, 1514);
        q.appendRecord(cols, 15, 1614);
        q.appendRecord(cols, 17, 1714);
        q.appendRecord(cols, 18, 1814);

        q.update(cols, 1111, 2222);
        for (int i = 0; i < q.size(); ++i)
        {
            assertEquals(1111, q.getValue(TR_ID, i));
            assertEquals(2222, q.getValue(TR_AMOUNT, i));
        }
    }

}
