package com.napol.koltsegvetes;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Locale;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.napol.koltsegvetes.db.AbstractQuery;
import com.napol.koltsegvetes.db.EColumnNames;
import com.napol.koltsegvetes.db.IColumn;
import com.napol.koltsegvetes.db.ParcelableQuery;
import com.napol.koltsegvetes.db.QueryBuilder;
import com.napol.koltsegvetes.util.Util;

/**
 * 
 * @author Polcz Péter <ppolcz@gmail.com>
 * Created on Nov 18, 2014 3:06:54 PM
 */
public class TransactionListAdapter4 extends BaseExpandableListAdapter
{
	private int groupResId;
	private int childResId;
	private HashMap<IColumn, IndexID> groupLut;
	private HashMap<IColumn, IndexID> childLut;

	private Context context;

	/* data model */
	private AbstractQuery query;
	private AbstractQuery sampleChild;
	private HashMap<Object[], AbstractQuery> children;

	public TransactionListAdapter4(Context context, AbstractQuery query, AbstractQuery sampleChild, int groupResId, int childResId)
	{
		super();
		if (!sampleChild.isEmpty()) throw new IllegalArgumentException("sampleChild should be empty!");

		// debug("start + resId = " + groupResId);
		this.context = context;
		this.query = query;
		this.sampleChild = sampleChild;
		this.children = new HashMap<Object[], AbstractQuery>();
		this.groupResId = groupResId;
		this.childResId = childResId;

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
		groupLut = new HashMap<IColumn, TransactionListAdapter4.IndexID>();
		childLut = new HashMap<IColumn, TransactionListAdapter4.IndexID>();
		View groupView = LayoutInflater.from(context).inflate(groupResId, null);
		View childView = LayoutInflater.from(context).inflate(childResId, null);

		int i = -1; // index for the parents (groups)
		int j = -1; // index for the children
		for (Field field : R.id.class.getDeclaredFields())
		{
			try
			{
				IColumn c = EColumnNames.valueOf(field.getName().toUpperCase(Locale.ENGLISH));
				if (query.getPosition(c) >= 0)
				{
					int twResourceId = (Integer) getFieldValue(field);
					if (groupView.findViewById(twResourceId) != null)
					{
						groupLut.put(c, new IndexID(++i, (Integer) getFieldValue(field), query.getPosition(c)));
					}
				}
				if (sampleChild.getPosition(c) >= 0)
				{
					// debug("sampleChild.getRecordLength() = " + sampleChild.getColsCount());
					// debug("sampleChild.getPosition(c) = " + sampleChild.getPosition(c));
					int twResourceId = (Integer) getFieldValue(field);
					if (childView.findViewById(twResourceId) != null)
					{
						childLut.put(c, new IndexID(++j, (Integer) getFieldValue(field), sampleChild.getPosition(c)));
					}
				}
			} catch (IllegalArgumentException e)
			{}
		}
		// debug("groupLUT.size = %d", groupLut.size());
		// debug("childLUT.size = %d", childLut.size());
	}

	private Object getFieldValue(Field field)
	{
		try
		{
			return field.get(null);
		} catch (Exception e)
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
		AbstractQuery child = children.get(query.get(groupPosition));
		if (child != null) return child.size();
		return 0;
	}

	@Override
	public Object[] getGroup(int groupPosition)
	{
		// debug("[egyszer itt mar volt hiba] requested [groupPosition] = %d,  size = %d", groupPosition, query.size());
		return query.get(groupPosition);
	}

	public ParcelableQuery getGroupAsQuery(int groupPosition)
	{
		return (ParcelableQuery) QueryBuilder.create(new ParcelableQuery(), query.getCols(), getGroup(groupPosition));
	}
	
	@Override
	public Object[] getChild(int groupPosition, int childPosition)
	{
		return children.get(query.get(groupPosition)).get(childPosition);
	}

	public ParcelableQuery getChildren(int groupPosition)
	{
		return (ParcelableQuery) children.get(query.get(groupPosition));
	}
	
	@Override
	public long getGroupId(int groupPosition)
	{
		// NullPointerException e = new NullPointerException();
		// Log.e("pcz> ", "stack trace", e);

		// long g = groupPosition;
		// Util.debug("int = " + groupPosition + "    long = " + ExpandableListView.getPackedPositionForGroup(groupPosition));
		// Util.debug("groupPosition = " + groupPosition);
		// return groupPosition << 0;
		// return 0;
		// long g = groupPosition;
		// return (g << 32) & 0xFFFFFFFF00000000l;
		// Util.debug("groupPosition" + groupPosition);
		// Util.debug("getPackedPositionForGroup = " + ExpandableListView.getPackedPositionForGroup(groupPosition));
		return ExpandableListView.getPackedPositionForGroup(groupPosition);
	}

	@Override
	public long getChildId(int groupPosition, int childPosition)
	{
		// long g = groupPosition;
		// Util.debug("groupPosition = " + groupPosition + "   childPosition = " + childPosition);
		// return (g << 32) | childPosition;
		// return 0;
		return ExpandableListView.getPackedPositionForChild(groupPosition, childPosition);
	}

	// EZ SEM EGY ROSSZ MEGOLDAS:
	// --
	public static boolean isGroup(long id)
	{
		return (id & 0x00000000FFFFFFFFl) == 0l;
	}

	public static int getGroupPosition(long id)
	{
		return (int) ((id & 0xFFFFFFFF00000000l) >> 32);
	}

	public static int getChildPosition(long id)
	{
		return (int) (id & 0x00000000FFFFFFFFl);
	}

	@Override
	public boolean hasStableIds()
	{
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

		// debug("------------------------------------------------");
		for (IColumn c : query.getCols())
		{
			IndexID i = groupLut.get(c);
			if (i != null)
			{
				// if (row[i.dataIndexInRow] != null) debug(c.name() + ": " + c.javatype().getName() + " - " + row[i.dataIndexInRow].getClass().getName() + " " + row[i.dataIndexInRow].toString());
				// else debug(c.name() + ": " + c.javatype().getName() + " - null");
				holder[i.twIndexInHolder].setText(c.toDisplayString(row[i.dataIndexInRow]));
			}
		}

		return rowView;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent)
	{
		// info("position = " + position);
		View rowView = convertView;
		if (rowView == null)
		{
			// another approach: context.getSystemService(Context.LAYOUT...)
			rowView = LayoutInflater.from(context).inflate(childResId, null);
			TextView[] holder = new TextView[childLut.size()];

			for (IColumn c : childLut.keySet())
			{
				IndexID i = childLut.get(c);
				holder[i.twIndexInHolder] = (TextView) rowView.findViewById(i.twResourceId);
			}

			rowView.setTag(holder);
		}

		TextView[] holder = (TextView[]) rowView.getTag();
		Object[] row = children.get(query.get(groupPosition)).get(childPosition);

		// debug("------------------------------------------------");
		for (IColumn c : sampleChild.getCols())
		{
			IndexID i = childLut.get(c);
			if (i != null)
			{
				// if (row[i.dataIndexInRow] != null) debug(c.name() + ": " + c.javatype().getName() + " - " + row[i.dataIndexInRow].getClass().getName() + " " + row[i.dataIndexInRow].toString());
				// else debug(c.name() + ": " + c.javatype().getName() + " - null");
				holder[i.twIndexInHolder].setText(c.toDisplayString(row[i.dataIndexInRow]));
			}
		}

		return rowView;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition)
	{
		return false;
	}

	public void remove(int groupPosition)
	{
		query.remove(groupPosition);
	}

	public void update(int groupPosition, AbstractQuery group, AbstractQuery children)
	{
		Object[] row = query.get(groupPosition);
		for (int i = 0; i < query.getCols().length; ++i)
		{
			Object value = group.getValue(query.getCols()[i], 0);
			if (value != null) row[i] = value;
			// debug(query.getTypes()[i].name() + " - " + q.getTypes()[i].name() + " " + q.get(0)[i]);
		}
		
		if (children != null)
		{
			this.children.remove(row);
			this.children.put(row, children);
		}
	}

	private void appendChild(Object[] row, AbstractQuery c)
	{
		AbstractQuery child = children.get(row);
		if (child == null) child = QueryBuilder.create(new ParcelableQuery(), sampleChild.getCols());
		child.appendQuery(c);

		children.put(row, child);
	}

	public void appendChild(int index, AbstractQuery c)
	{
		Object[] row = query.get(index);
		appendChild(row, c);
	}

	// public void addGroup(Object[] row, AbstractQuery c)
	// {
	// query.addRecord(row);
	// appendChild(row, c);
	// }

	public void addGroup(AbstractQuery g, AbstractQuery c)
	{
		if (g.size() != 1) throw new IllegalArgumentException("group[1st arg].size() should be 1");
		appendChild(query.appendRecord(g.getCols(), g.getFirst()), c);
		// debug("id = " + g.getLast()[0]);
	}
}
