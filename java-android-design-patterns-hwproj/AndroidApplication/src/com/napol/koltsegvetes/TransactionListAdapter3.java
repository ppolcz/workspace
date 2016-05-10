package com.napol.koltsegvetes;

import static com.napol.koltsegvetes.util.Util.debug;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Locale;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.napol.koltsegvetes.db.AbstractQuery;
import com.napol.koltsegvetes.db.EColumnNames;
import com.napol.koltsegvetes.db.IColumn;

/**
 * 
 * @author Polcz Péter <ppolcz@gmail.com>
 * Created on Nov 18, 2014 3:06:54 PM
 */
public class TransactionListAdapter3 extends BaseExpandableListAdapter
{
    // class Group
    // {
    // Object[] transaction;
    // AbstractQuery[] products;
    //
    // public Group(Object[] transaction, AbstractQuery[] products)
    // {
    // this.transaction = transaction;
    // this.products = products;
    // }
    // }

    private int groupResId;
    private int childResId;
    private HashMap<IColumn, IndexID> groupLut;
    private HashMap<IColumn, IndexID> childLut;

    private Context context;
    
    /* data model */
    private AbstractQuery query;
    private HashMap<Object[], AbstractQuery> children;

    public TransactionListAdapter3(Context context, AbstractQuery query, int resId)
    {
        super();
        debug("start + resId = " + resId);
        this.context = context;
        this.query = query;
        this.groupResId = resId;

        detectTwResourceIds();
    }

    class IndexID
    {
        int twIndexInHolder;
        int twResourceId;
        int dataIndexInRow;

        public IndexID(int twIndexInHolder, int twResourceId, int dataIndexInRow)
        {
            this.twIndexInHolder = twIndexInHolder;
            this.twResourceId = twResourceId;
            this.dataIndexInRow = dataIndexInRow;
        }
    }

    private void detectTwResourceIds()
    {
        groupLut = new HashMap<IColumn, TransactionListAdapter3.IndexID>();
        childLut = new HashMap<IColumn, TransactionListAdapter3.IndexID>();
        View rowView = LayoutInflater.from(context).inflate(groupResId, null);

        int i = -1;
        for (Field field : R.id.class.getDeclaredFields())
        {
            try
            {
                IColumn c = EColumnNames.valueOf(field.getName().toUpperCase(Locale.ENGLISH));
                if (query.getPosition(c) >= 0)
                {
                    int twResourceId = (Integer) getFieldValue(field);
                    if (rowView.findViewById(twResourceId) != null)
                    {
                        groupLut.put(c, new IndexID(++i, (Integer) getFieldValue(field), query.getPosition(c)));
                    }
                }
            }
            catch (IllegalArgumentException e)
            {
            }
        }
    }

    private Object getFieldValue(Field field)
    {
        try
        {
            return field.get(null);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }

    public AbstractQuery getQuery()
    {
        return query;
    }

    @Override
    public int getGroupCount()
    {
        return query.size();
    }

    @Override
    public int getChildrenCount(int groupPosition)
    {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public Object[] getGroup(int groupPosition)
    {
        return query.get(groupPosition);
    }

    @Override
    public Object[] getChild(int groupPosition, int childPosition)
    {
        return children.get(query.get(groupPosition)).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition)
    {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition)
    {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public boolean hasStableIds()
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent)
    {
        // info("position = " + position);
        View rowView = convertView;
        if (rowView == null)
        {
            // another approach: context.getSystemService(Context.LAYOUT...)
            rowView = LayoutInflater.from(context).inflate(groupResId, null);
            TextView[] holder = new TextView[groupLut.size()];

            for (IColumn c : groupLut.keySet())
            {
                IndexID i = groupLut.get(c);
                holder[i.twIndexInHolder] = (TextView) rowView.findViewById(i.twResourceId);
            }

            rowView.setTag(holder);
        }

        TextView[] holder = (TextView[]) rowView.getTag();
        Object[] row = query.get(groupPosition);

        debug("------------------------------------------------");
        for (IColumn c : query.getTypes())
        {
            IndexID i = groupLut.get(c);
            if (i != null)
            {
                if (row[i.dataIndexInRow] != null) debug(c.name() + ": " + c.javatype().getName() + " - " + row[i.dataIndexInRow].getClass().getName() + " " + row[i.dataIndexInRow].toString());
                else debug(c.name() + ": " + c.javatype().getName() + " - null");
                holder[i.twIndexInHolder].setText(c.toDisplayString(row[i.dataIndexInRow]));
            }
        }

        return rowView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition)
    {
        // TODO Auto-generated method stub
        return false;
    }

    public void remove(int groupPosition)
    {
        query.remove(groupPosition);
    }

    public void update(int groupPosition, AbstractQuery q)
    {
        Object[] row = query.get(groupPosition);
        for (int i = 0; i < query.getTypes().length; ++i)
        {
            Object value = q.getValue(query.getTypes()[i], 0);
            if (value != null) row[i] = value;
            // debug(query.getTypes()[i].name() + " - " + q.getTypes()[i].name() + " " + q.get(0)[i]);
        }
    }
    
    public void addGroup(AbstractQuery g, AbstractQuery c)
    {
        if (g.size() != 1) throw new IllegalArgumentException("group[1st arg].size() should be 1");
        query.addRecord(g.getFirst());
        children.put(g.getFirst(), c);
    }
}
