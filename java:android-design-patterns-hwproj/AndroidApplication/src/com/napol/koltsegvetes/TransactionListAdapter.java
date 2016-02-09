package com.napol.koltsegvetes;

import static com.napol.koltsegvetes.util.Util.debug;
import static com.napol.koltsegvetes.util.Util.verbose;

import java.lang.reflect.Field;
import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.napol.koltsegvetes.db.AbstractQuery;

/**
 * @author Polcz Péter <ppolcz@gmail.com>
 * 
 * Created on Oct 10, 2014 9:52:50 AM
 */
public class TransactionListAdapter extends ArrayAdapter<Object[]>
{
    private static final String TW_PATTERN = "pcz_listview_item_tw_";

    private int resId;
    private ArrayList<Integer> twIndices;
    private ArrayList<Integer> twIds;

    private Context context;
    private AbstractQuery query;

    public TransactionListAdapter(Context context, AbstractQuery query, int resId)
    {
        super(context, resId, query);
        debug("start + resId = " + resId);
        this.context = context;
        this.query = query;
        this.resId = resId;

        calcTwCount();
    }

    private void calcTwCount()
    {
        twIndices = new ArrayList<Integer>();
        twIds = new ArrayList<Integer>();
        View rowView = LayoutInflater.from(context).inflate(resId, null);

        for (Field field : R.id.class.getDeclaredFields())
        {
            if (field.getName().startsWith(TW_PATTERN))
            {
                // info(field.getName());

                int i = Integer.parseInt(field.getName().replace(TW_PATTERN, ""));
                int id = (Integer) getFieldValue(field);
                View tw = rowView.findViewById(id);

                if (tw != null)
                {
                    verbose("index = %d, id = %d", i, id);
                    twIndices.add(i);
                    twIds.add(id);
                }
                else
                {
                    verbose("index = %d  IS NULL", i);
                }
            }
        }

        if (twIds.size() != twIndices.size()) throw new AssertionError("twIds.size() != twIndices.size()");
    }

    private Object getFieldValue(Field field)
    {
        try
        {
            return field.get(null);
        }
        catch (IllegalAccessException e)
        {
            e.printStackTrace();
        }
        catch (IllegalArgumentException e)
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
    public int getCount()
    {
        return query.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        // info("position = " + position);
        View rowView = convertView;
        if (rowView == null)
        {
            // another approach: context.getSystemService(Context.LAYOUT...)
            rowView = LayoutInflater.from(context).inflate(resId, null);
            TextView[] holder = new TextView[twIds.size()];

            for (int i = 0; i < twIds.size(); ++i)
                holder[i] = (TextView) rowView.findViewById(twIds.get(i));

            rowView.setTag(holder);
        }

        TextView[] holder = (TextView[]) rowView.getTag();
        Object[] entry = query.get(position);

        // nr. of columns in the record
        int n = query.getColsCount();

        for (int i = 0; i < holder.length; ++i)
        {
            int index = twIndices.get(i);
            // info("i = %d, tw_index = %d, n = %d", i, index, n);
            if (index < n)
            {
                holder[i].setText(query.getTypes()[index].toDisplayString(entry[index]));
                // if (entry[index] instanceof Date) info("THIS IS A DATE");
                // if (entry[index] instanceof String) info("THIS IS A STRING: '%s'", entry[index]);

                // info("col = %s, val = '%s'", query.getTypes()[index].name(),
                // query.getTypes()[index].toDisplayString(entry[index]));
            }
        }

        return rowView;
    }
}
