package com.napol.koltsegvetes.db;

/**
 * @author Polcz Péter <ppolcz@gmail.com>
 * Created on Nov 19, 2014 12:37:47 PM
 */
public class ColumnDecorator implements IColumn
{
    protected IColumn component;

    public ColumnDecorator(IColumn component)
    {
        this.component = component;
    }

    @Override
    public String displayName()
    {
        return component.displayName();
    }
    
    @Override
    public String sqlname()
    {
        return component.sqlname();
    }

    @Override
    public String idname()
    {
        return component.idname();
    }
    
    @Override
    public String sqltypeall()
    {
        return component.sqltypeall();
    }

    @Override
    public String sqltype()
    {
        return component.sqltype();
    }

    @Override
    public String sqlname(String prefix)
    {
        return component.sqlname(prefix);
    }

    @Override
    public Class<?> javatype()
    {
        return component.javatype();
    }

    @Override
    public IColumn ref()
    {
        return component.ref();
    }

    @Override
    public boolean isDateType()
    {
        return component.isDateType();
    }

    @Override
    public boolean isInsertDate()
    {
        return component.isInsertDate();
    }

    @Override
    public Object toDate(String date)
    {
        return component.toDate(date);
    }

    @Override
    public String toSqlString(Object data)
    {
        return component.toSqlString(data);
    }

    @Override
    public String toDisplayString(Object data)
    {
        return component.toDisplayString(data);
    }

    @Override
    public String sqlwhere(Object comperand, String operator)
    {
        return sqlwhere(comperand, operator);
    }

    @Override
    public ETableNames table()
    {
        return component.table();
    }

    @Override
    public String name()
    {
        return component.name();
    }

    @Override
    public IColumn isa()
    {
        return component.isa();
    }
    
    @Override
    public boolean isa(IColumn c)
    {
        return component.isa(c);
    }

    @Override
    public boolean isAutoIncrement()
    {
        return component.isAutoIncrement();
    }
}
