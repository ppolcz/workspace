package com.polcz.odftools;

import com.napol.koltsegvetes.db.AbstractQuery;
import com.napol.koltsegvetes.db.IColumn;

/**
 * This class is typically for displaying the data,
 * and not proposed to use for creating, building the data.
 * 
 * @author Polcz Péter <ppolcz@gmail.com>
 * Created on Nov 18, 2014 12:40:41 PM
 */
public class QueryDecorator extends AbstractQuery
{
    private static final long serialVersionUID = -2530161344960308573L;

    static abstract class Visitor
    {
        private AbstractQuery component;
        private QueryDecorator decorator;

        protected final Object getValue(IColumn c, Object[] values)
        {
            try
            {
                return values[component.getPosition(c)];
            }
            catch (Exception e)
            {
                return null;
            }
        }

        private final Object[] transform(Object[] values)
        {
            Object[] ret = new Object[decorator.getCols().length];
            for (int i = 0; i < decorator.getCols().length; ++i)
            {
                ret[i] = transform(decorator.getCols()[i], values);
            }
            return ret;
        }

        abstract Object transform(IColumn c, Object[] values);
    }

    private AbstractQuery component;
    private Visitor visitor;

    public QueryDecorator(AbstractQuery component, IColumn... cols)
    {
        super();
        this.setCols(cols);
        this.component = component;
    }

    public QueryDecorator setVisitor(Visitor vis)
    {
        visitor = vis;
        visitor.component = component;
        visitor.decorator = this;
        return this;
    }

    // ================================================================== //
    // delegate functionalities
    
    @Override
    public int size()
    {
        return component.size();
    }
    
    @Override
    public Object[] get(int index)
    {
        return visitor.transform(component.get(index));
    }

    @Override
    public Object getValue(IColumn c, int row)
    {
        Object ret = visitor.transform(c, component.get(row));
        if (ret == null) ret = component.getValue(c, row);
        return ret;
    }

    // ================================================================== //
    // deprecated functions

    @Override
    @Deprecated
    public AbstractQuery addRecord(Object... objs)
    {
        return component.addRecord(objs);
    }

    @Override
    @Deprecated
    public void addLast(Object[] record)
    {
        component.addLast(record);
    }
}
