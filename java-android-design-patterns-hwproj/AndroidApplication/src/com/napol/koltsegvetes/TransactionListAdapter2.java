package com.napol.koltsegvetes;

import static com.napol.koltsegvetes.util.Util.debug;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Locale;

import android.content.Context;
import android.os.Debug;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.napol.koltsegvetes.db.AbstractQuery;
import com.napol.koltsegvetes.db.EColumnNames;
import com.napol.koltsegvetes.db.IColumn;

/**
 * 
 * @author Polcz Péter <ppolcz@gmail.com>
 * Created on Nov 18, 2014 3:06:54 PM
 */
public class TransactionListAdapter2 extends ArrayAdapter<Object[]>
{
    private int resId;
    private HashMap<EColumnNames, IndexID> lut;

    private Context context;
    private AbstractQuery query;

    public TransactionListAdapter2(Context context, AbstractQuery query, int resId)
    {
        super(context, resId, query);
        debug("start + resId = " + resId);
        this.context = context;
        this.query = query;
        this.resId = resId;

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
        lut = new HashMap<EColumnNames, TransactionListAdapter2.IndexID>();
        View rowView = LayoutInflater.from(context).inflate(resId, null);

        int i = -1;
        for (Field field : R.id.class.getDeclaredFields())
        {
            try
            {
                EColumnNames c = EColumnNames.valueOf(field.getName().toUpperCase(Locale.ENGLISH));
                if (query.getPosition(c) >= 0)
                {
                    int twResourceId = (Integer) getFieldValue(field);
                    if (rowView.findViewById(twResourceId) != null)
                    {
                        lut.put(c, new IndexID(++i, (Integer) getFieldValue(field), query.getPosition(c)));
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
            TextView[] holder = new TextView[lut.size()];

            for (EColumnNames c : lut.keySet())
            {
                IndexID i = lut.get(c);
                holder[i.twIndexInHolder] = (TextView) rowView.findViewById(i.twResourceId);
            }

            rowView.setTag(holder);
        }

        TextView[] holder = (TextView[]) rowView.getTag();
        Object[] row = query.get(position);

        for (IColumn c : query.getTypes())
        {
            IndexID i = lut.get(c);
            if (i != null)
            {
                holder[i.twIndexInHolder].setText(c.toDisplayString(row[i.dataIndexInRow]));
            }
        }

        return rowView;
    }
}
