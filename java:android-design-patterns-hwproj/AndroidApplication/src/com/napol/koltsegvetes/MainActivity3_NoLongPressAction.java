package com.napol.koltsegvetes;

import static com.napol.koltsegvetes.db.EColumnNames.PI_ID;
import static com.napol.koltsegvetes.db.EColumnNames.PI_TRID;
import static com.napol.koltsegvetes.db.EColumnNames.TR_ID;
import static com.napol.koltsegvetes.db.IColumn.opEqual;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.PopupMenu.OnMenuItemClickListener;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.AbsListView;

import com.napol.koltsegvetes.db.AbstractQuery;
import com.napol.koltsegvetes.db.DataStore;
import com.napol.koltsegvetes.db.EColumnNames;
import com.napol.koltsegvetes.db.ETableNames;
import com.napol.koltsegvetes.db.ParcelableQuery;
import com.napol.koltsegvetes.db.QueryBuilder;
import com.napol.koltsegvetes.util.Util;

/**
 * This is an activity to visualize data in a simple or expandable list view.
 * 
 * @author Polcz Péter <ppolcz@gmail.com>
 * Created on Oct 13, 2014 7:25:33 PM
 * 
 * 1. Review on Nov 21, 2014
 * 2. Review on Jan 17, 2015
 */
public class MainActivity3_NoLongPressAction extends ActionBarActivity
{
	/** package private - these should be accessible to {@link TrFormActivity} */
	static final String KEY_TR_QUERY = "trq";
	static final String KEY_PI_QUERY = "piq";
	static final String KEY_LADAPTER_INDEX = "adind";
	static final int REQUEST_NEWTR = 1001;
	static final int REQUEST_UPDATETR = 1002;

	private static final Class<?> TrFormActivity_class = TrFormActivity2_UpdateProductInfos.class;
	
	private ExpandableListView lw;
	private DataStore db;
	private AbstractQuery q;
	private TransactionListAdapter4 ladapter;

	final String PREFS_NAME = "pcz_koltsegvetes_prefs";
	final String PREFS_FIRST_TIME = "first_time";

	private int lastSelectedGroup = -1;

	void initDatabase()
	{
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);

		boolean firstTime = settings.getBoolean(PREFS_FIRST_TIME, true);
		if (firstTime)
		{
			settings.edit().putBoolean(PREFS_FIRST_TIME, false).commit();
		}

		db = DataStore.instance(firstTime);
		db.setContext(this);
		db.onOpen();
	}

	void setupAdapter()
	{
		// retrieve all transactions from the database
		q = db.select(EColumnNames.getColumns(ETableNames.TRANZACTIONS));

		// create a child item prototype, with which all group items will be initialized
		ParcelableQuery sampleProductInfo = (ParcelableQuery) QueryBuilder.create(new ParcelableQuery(), EColumnNames.getColumns(ETableNames.PRODUCT_INFO));

		// initialize expandable list adapter
		ladapter = new TransactionListAdapter4(this, q, sampleProductInfo, R.layout.mainlw_item4, R.layout.mainlw_subitem);

		int tridPos = q.getPosition(TR_ID);
		for (int i = 0; i < ladapter.getGroupCount(); ++i)
		{
			// this is now the actual group
			Object[] row = ladapter.getGroup(i);

			// retrieve child list (i.e. the products belonging to this actual group)
			AbstractQuery childList = db.select(PI_TRID.sqlwhere(row[tridPos], opEqual), sampleProductInfo.getCols());

			// append children items to the list adapter
			ladapter.appendChild(i, childList);

			// Util.debug("  <> childList.size = " + childList.size());
		}
	}

	void setupListView()
	{
		lw = (ExpandableListView) findViewById(R.id.mainlw);
		lw.setAdapter(ladapter);
		lw.setGroupIndicator(null);
		lw.setOnItemLongClickListener(itemLongClickListener);
		lw.setOnGroupClickListener(new OnGroupClickListener()
		{
			@Override
			public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id)
			{
				lastSelectedGroup = groupPosition;
				// Util.debug("onGroupClickListener + group position = " + groupPosition + "        id = " + id);
				return false;
			}
		});
	}

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main2);

		initDatabase();
		setupAdapter();
		setupListView();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_update)
		{
			if (ladapter.getGroupCount() > 0 && lastSelectedGroup >= 0)
			{
				Intent i = new Intent(MainActivity3_NoLongPressAction.this, TrFormActivity_class);
				i.putExtra(KEY_TR_QUERY, (Parcelable) ladapter.getGroupAsQuery(lastSelectedGroup));
				i.putExtra(KEY_LADAPTER_INDEX, lastSelectedGroup);
				i.putExtra(KEY_PI_QUERY, (Parcelable) ladapter.getChildren(lastSelectedGroup));
				Util.debug(((ParcelableQuery) ladapter.getChildren(lastSelectedGroup)).toString());
				startActivityForResult(i, REQUEST_UPDATETR);
			}
			return true;
		}
		if (id == R.id.action_delete)
		{
			if (ladapter.getGroupCount() > 0 && lastSelectedGroup >= 0)
			{
				String sqlwhere = TR_ID.sqlwhere(ladapter.getGroup(lastSelectedGroup)[q.getPosition(TR_ID)], opEqual);

				db.delete(ETableNames.TRANZACTIONS, sqlwhere);
				ladapter.remove(lastSelectedGroup);
				ladapter.notifyDataSetChanged();
			}
			return true;
		}
		if (id == R.id.action_newtr)
		{
			Intent i = new Intent(this, TrFormActivity_class);
			startActivityForResult(i, REQUEST_NEWTR);
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		if (resultCode == RESULT_OK)
		{
			ParcelableQuery group;
			ParcelableQuery child;
			switch (requestCode)
			{
			case REQUEST_NEWTR:
			{
				// retrieve main transaction data
				group = data.getExtras().getParcelable(KEY_TR_QUERY);

				// retrieve children data (i.e. product items)
				child = data.getExtras().getParcelable(KEY_PI_QUERY);

				Util.debug("c.size = " + child.size());
				if (group.size() > 0)
				{
					int id = db.insert(group.getCols(), group.getFirst());

					group.getFirst()[group.getPosition(TR_ID)] = id;
					child.update(PI_TRID, id);
					child = (ParcelableQuery) db.insertAll(child, PI_ID);

					ladapter.addGroup(group, child);
					ladapter.notifyDataSetChanged();
				}
				break;
			}

			case REQUEST_UPDATETR:
			{
				group = data.getExtras().getParcelable(KEY_TR_QUERY);
				child = data.getExtras().getParcelable(KEY_PI_QUERY);
				int groupPosition = data.getExtras().getInt(KEY_LADAPTER_INDEX);
				if (group.size() > 0)
				{
					String sqlwhere = TR_ID.sqlwhere(group.getValue(TR_ID, 0), opEqual);
					db.update(group, sqlwhere);
					ladapter.update(groupPosition, group, child);
					
					for (int i = 0; i < child.size(); ++i)
					{
						child.update(PI_TRID, group.getValue(TR_ID, 0));
						if (child.getValue(PI_ID, i) == null)
						{
							child.setValue(PI_ID, i, db.insert(child, i));
						}
						else
						{
							sqlwhere = PI_ID.sqlwhere(child.getValue(PI_ID, i), opEqual);
							Util.debug("sqlwhere  from pi = " + sqlwhere);
							db.update(child, i, sqlwhere);
						}
					}
						
					ladapter.notifyDataSetChanged();
				}
				break;
			}

			default:
				break;
			}
		}
	}

	/**
	 * Menu on long click on the group items.
	 * (This requires at least API level 11)
	 */
	private OnItemLongClickListener itemLongClickListener = new OnItemLongClickListener()
	{
		@Override
		public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id)
		{
			Util.debug("id     = " + id);
			Util.debug("position = " + position);

			final int childPosition = TransactionListAdapter4.getChildPosition(id);
			final int groupPosition = TransactionListAdapter4.getGroupPosition(id);

			// final int childPosition;
			// final int groupPosition;
			//
			// int itemType = ExpandableListView.getPackedPositionType(id);
			//
			// if (itemType == ExpandableListView.PACKED_POSITION_TYPE_CHILD)
			// {
			// childPosition = ExpandableListView.getPackedPositionChild(id);
			// groupPosition = ExpandableListView.getPackedPositionGroup(id);
			// }
			// else if (itemType == ExpandableListView.PACKED_POSITION_TYPE_GROUP)
			// {
			// groupPosition = ExpandableListView.getPackedPositionGroup(id);
			// childPosition = -1;
			// }
			// else
			// {
			// groupPosition = -1;
			// childPosition = -1;
			// }

			// Util.debug("0: " + ExpandableListView.getPackedPositionForGroup(0));
			// Util.debug("1: " + ExpandableListView.getPackedPositionForGroup(1));
			// Util.debug("2: " + ExpandableListView.getPackedPositionForGroup(2));
			// Util.debug("3: " + ExpandableListView.getPackedPositionForGroup(3));

			Util.debug("groupPosition = " + groupPosition);
			Util.debug("childPosition = " + childPosition);

			PopupMenu popup = new PopupMenu(MainActivity3_NoLongPressAction.this, view);
			popup.setOnMenuItemClickListener(new OnMenuItemClickListener()
			{
				/**
				 * Handling click events on the menu items
				 */
				public boolean onMenuItemClick(MenuItem item)
				{
					String sqlwhere = TR_ID.sqlwhere(ladapter.getGroup(groupPosition)[q.getPosition(TR_ID)], opEqual);
					Util.debug(sqlwhere);
					switch (item.getItemId())
					{
					/**
					 * Instead of sending the row : Object[] record from the array adapter,
					 * we request the row from the DataStore.
					 * TODO - think on it, is it worth?
					 */
					case R.id.action_update:
					{
						Intent i = new Intent(MainActivity3_NoLongPressAction.this, TrFormActivity_class);
						i.putExtra(KEY_TR_QUERY, (Parcelable) db.select(ETableNames.TRANZACTIONS, sqlwhere));
						i.putExtra(KEY_LADAPTER_INDEX, groupPosition);
						startActivityForResult(i, REQUEST_UPDATETR);
						break;
					}

					case R.id.action_delete:
					{
						db.delete(ETableNames.TRANZACTIONS, sqlwhere);
						ladapter.remove(groupPosition);
						ladapter.notifyDataSetChanged();
						break;
					}

					default:
						return false;
					}
					return true;
				}
			});
			popup.inflate(R.menu.trlist_longclick);
			popup.show();
			return false;
		}
	};

	// @SuppressWarnings("unused")
	// private void insertDummyDate()
	// {
	// debug("Inserting some dummy data into the sqlite datebase");
	// AbstractQuery query = new AbstractQuery();
	// query.setTypes(TR_AMOUNT, TR_REMARK);
	// query.addRecord(1200, "Kajat vettem ennyiert");
	// query.addRecord(3200, "Lidl-ben vasaroltam");
	// query.addRecord(850, "Dezso ba menu");
	// query.addRecord(40000, "Kivettem a bankbol");
	//
	// ListIterator<Object[]> it = query.listIterator();
	// while (it.hasNext())
	// {
	// db.insert(query.getTypes(), it.next());
	// }
	// }
}
