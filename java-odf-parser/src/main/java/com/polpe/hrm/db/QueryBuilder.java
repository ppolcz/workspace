package com.polpe.hrm.db;

import java.util.ArrayList;

/**
 * These static methods are very similar to the C++ std::make_pair, std::make_tuple functions.
 * They are proposed to simplify the template parameters declarations, here I intend 
 * to define constructors which do not need to be redefined in the different subclasses.
 * 
 * @author Polcz Péter <ppolcz@gmail.com>
 * Created on Nov 21, 2014 4:55:40 PM 
 * 
 * 1. Review on Nov 21, 2014
 */
public class QueryBuilder
{
    public static AbstractQuery create(AbstractQuery q, IColumn... cols)
    {
        q.setCols(cols);
        return q;
    }
    
    public static AbstractQuery create(AbstractQuery q, IColumn[] cols, Object... vals)
    {
        q.setCols(cols);
        q.appendRecord(cols, vals);
        return q;
    }

    public static AbstractQuery create(AbstractQuery q, ArrayList<IColumn> cols)
    {
        q.setCols(cols.toArray(new IColumn[cols.size()]));
        return q;
    }
}
