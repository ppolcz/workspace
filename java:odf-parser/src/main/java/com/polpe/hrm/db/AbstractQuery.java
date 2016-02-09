package com.polpe.hrm.db;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;

/**
 * This class represents a table data model, just like a dynamic SQL table.
 * The name of this class can also be TableModel or PTable
 * 
 * Names:
 * record == row == tuple
 * query == table
 * types == column types == this.cols == header
 * 
 * @author Polcz Péter <ppolcz@gmail.com>
 * Created on Oct 13, 2014 7:27:52 PM
 */
public class AbstractQuery extends LinkedList<Object[]>
{
    private static final long serialVersionUID = 896112267010842564L;

    /**
     * These object represent the meta information of the columns in the table.
     * These are the column headers.
     */
    private IColumn[] cols;

    /**
     * This look-up-table helps us finding a given column in the array of columns.
     */
    private HashMap<IColumn, Integer> lut;

    /**
     * Set up column headers of the table. Be aware, this method will clear all previous data stored in it.
     * @param cols
     * @return
     */
    public AbstractQuery setCols(IColumn... cols)
    {
        clear();

        this.cols = cols;
        this.lut = new HashMap<IColumn, Integer>();

        for (int i = 0; i < cols.length; ++i)
        {
            if (lut.containsKey(cols[i].isa())) throw new IllegalArgumentException("On of the column's effective value (isa) is equivalent to anothers");
            lut.put(cols[i].isa(), i);
        }

        return this;
    }

    public IColumn[] getCols()
    {
        return cols;
    }

    public int getColsCount()
    {
        return cols.length;
    }

    public Object[] newRecord()
    {
        super.addLast(new Object[getColsCount()]);
        return getLast();
    }

    public Object[] appendRecord(IColumn[] types, Object... values)
    {
        Object[] row = new Object[getColsCount()];
        for (int i = 0; i < types.length; ++i)
        {
            int pos = getPosition(types[i]);
            if (pos >= 0) row[pos] = values[i];
        }
        super.addLast(row);
        return row;
    }

    /**
     * This method merges the query given as an argument (that) with this query (this).
     * @param table
     */
    public void appendQuery(AbstractQuery table)
    {
        IColumn[] thatCols = table.cols;

        int thisColsCount = this.getColsCount();
        int thisRowsCount = this.size();
        int thatRowsCount = table.size();

        Map<Integer, Integer> indices = new HashMap<Integer, Integer>();
        for (int thatPos = 0; thatPos < table.getColsCount(); ++thatPos)
        {
            int thisPos = getPosition(thatCols[thatPos]);
            if (thisPos >= 0) indices.put(thatPos, thisPos);
        }

        if (indices.isEmpty()) throw new IllegalArgumentException("table [i.e. query] given as an argument has no common column with this table [i.e. query]");
        
        /* preallocate memory */
        super.addAll(Arrays.asList(new Object[thatRowsCount][thisColsCount]));

        for (Entry<Integer, Integer> pair : indices.entrySet())
        {
            for (int i = 0; i < thatRowsCount; ++i)
                get(i + thisRowsCount)[pair.getValue()] = table.get(i)[pair.getKey()];
        }
    }

    public String headerToString()
    {
        StringBuffer buffer = new StringBuffer();
        if (cols.length != 0)
        {
            buffer.append("|");
            for (IColumn o : cols)
                buffer.append(o.sqlname() + "|");
        }
        return buffer.toString();
    }
    
    public void print()
    {
        System.out.println(headerToString());
        
        if (isEmpty())
        {
            System.out.println("  -- the table is empty -- ");
            return;
        }

        for (Object[] o : this)
        {
            System.out.print("|");
            for (int i = 0; i < o.length; ++i)
            {
                System.out.print(cols[i].toDisplayString(o[i]) + "|");
            }
            System.out.println("");
        }

        System.out.println(" -- nr of items: " + this.size() + " -- ");
    }

    public int getPosition(IColumn c)
    {
        try
        {
            return lut.get(c.isa());
        }
        catch (NullPointerException e)
        {
            return -1;
        }
    }

    public Object getValue(IColumn c, int row)
    {
        try
        {
            return get(row)[getPosition(c)];
        }
        catch (ArrayIndexOutOfBoundsException e)
        {
            throw new IllegalArgumentException(c.name() + " is not one of the columns of this query: " + headerToString());
        }
        catch (IndexOutOfBoundsException e)
        {
            throw e;
        }
    }

    public void setValue(IColumn c, int row, Object value) throws IndexOutOfBoundsException
    {
        try
        {
            get(row)[getPosition(c)] = value;
        }
        catch (ArrayIndexOutOfBoundsException e)
        {
            throw new IllegalArgumentException(c.name() + " is not one of the columns of this query: " + headerToString());
        }
        catch (IndexOutOfBoundsException e)
        {
            throw e;
        }
    }

    
    //
    // public void setValue(IColumn c, Object[] row, Object value)
    // {
    // try
    // {
    // int pos = getPosition(c);
    // row[pos] = value;
    // }
    // catch (Exception e)
    // {
    // e.printStackTrace();
    // }
    // }

    // from LinkedList there are several inherited methods, which are not meant to use,
    // because they do not check the validity of the data structure

    public AbstractQuery update(IColumn col, Object val)
    {
        int pos = getPosition(col);
        if (pos < 0) throw new IllegalArgumentException("no such column: " + col.name());

        for (Object[] row : this)
        {
            row[pos] = val;
        }

        return this;
    }

    public AbstractQuery update(IColumn[] cols, Object... vals)
    {
        if (cols.length != vals.length) throw new IllegalArgumentException("nr cols to update must be equal with nr of values");

        for (int i = 0; i < cols.length; ++i)
        {
            update(cols[i], vals[i]);
        }

        return this;
    }

    /**
     * @deprecated This test is not enough to ensure the validity of the data.
     * @param record
     */
    @Deprecated
    private void checkRecord(Object[] record)
    {
        if (cols == null || record.length < cols.length) throw new IndexOutOfBoundsException(
            "record length smaller that expected, or not initialized");
    }

    // /**
    // * @deprecated use QueryBuilder instead
    // */
    // @Deprecated
    // public AbstractQuery(IColumn[] types)
    // {
    // setTypes(types);
    // }
    //
    // /**
    // * @deprecated use QueryBuilder instead
    // */
    // @Deprecated
    // public AbstractQuery(List<IColumn> types)
    // {
    // setTypes(types.toArray(new IColumn[types.size()]));
    // }

    /**
     * @deprecated use getCols instead
     */
    @Deprecated
    public IColumn[] getTypes()
    {
        return cols;
    }

    /**
     * @deprecated use setCols instead
     */
    @Deprecated
    public AbstractQuery setTypes(IColumn... cols)
    {
        return setCols(cols);
    }

    /**
     * @deprecated use appendRecord instead
     * @param objs
     * @return
     */
    @Deprecated
    public AbstractQuery addRecord(Object... objs)
    {
        addLast(objs);
        return this;
    }

    /**
     * This function do not checks the validity of the inserted data.
     */
    @Override
    @Deprecated
    public void addFirst(Object[] e)
    {
        super.addFirst(e);
    }

    /**
     * @deprecated use appendRecord instead
     */
    @Deprecated
    @Override
    public void addLast(Object[] record)
    {
        checkRecord(record);
        super.addLast(record);
    }

    /**
     * This function do not checks the validity of the inserted data.
     */
    @Override
    @Deprecated
    public boolean add(Object[] e)
    {
        return super.add(e);
    }

    // from LinkedList there are several inherited methods, which are not meant to use,
    // because they do not check the validity of the data structure

    /**
     * This function do not checks the validity of the inserted data.
     */
    @Override
    @Deprecated
    public void add(int index, Object[] element)
    {
        super.add(index, element);
    }

    /**
     * This function do not checks the validity of the inserted data.
     */
    @Override
    @Deprecated
    public boolean addAll(Collection<? extends Object[]> c)
    {
        return super.addAll(c);
    }

    /**
     * This function do not checks the validity of the inserted data.
     */
    @Override
    @Deprecated
    public boolean addAll(int index, Collection<? extends Object[]> c)
    {
        return super.addAll(index, c);
    }

    /**
     * Is not type safe.
     */
    @Override
    @Deprecated
    public Object[] set(int location, Object[] record)
    {
        checkRecord(record);
        return super.set(location, record);
    }

    /**
     * This approach is interesting rather than not so useful
     * @param cols
     * @param vals
     * @return
     */
    @Deprecated
    public static AbstractQuery create(Class<? extends AbstractQuery> cls, EColumnNames[] cols, Object... vals)
    {
        AbstractQuery query;
        try
        {
            Constructor<? extends AbstractQuery> constructor = cls.getConstructor(IColumn[].class);
            query = constructor.newInstance((Object[]) cols);
            query.appendRecord(cols, vals);
            return query;
        }
        catch (NoSuchMethodException e)
        {
            e.printStackTrace();
        }
        catch (InstantiationException e)
        {
            e.printStackTrace();
        }
        catch (IllegalAccessException e)
        {
            e.printStackTrace();
        }
        catch (IllegalArgumentException e)
        {
            e.printStackTrace();
        }
        catch (InvocationTargetException e)
        {
            e.printStackTrace();
        }
        return null;
    }
}
