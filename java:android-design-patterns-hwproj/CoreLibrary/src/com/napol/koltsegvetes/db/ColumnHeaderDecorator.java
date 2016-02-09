package com.napol.koltsegvetes.db;

/**
 * @author Polcz Péter <ppolcz@gmail.com>
 * Created on Nov 19, 2014 2:04:39 PM
 */
public class ColumnHeaderDecorator extends ColumnDecorator
{
    private String headerName;
    
    public ColumnHeaderDecorator(IColumn component, String headerName)
    {
        super(component);
        this.headerName = headerName;
    }
    
    @Override
    public String displayName()
    {
        return headerName;
    }
}
