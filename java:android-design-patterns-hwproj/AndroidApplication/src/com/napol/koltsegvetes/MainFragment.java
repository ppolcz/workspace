package com.napol.koltsegvetes;

import static com.napol.koltsegvetes.db.EColumnNames.PI_TRID;
import static com.napol.koltsegvetes.db.EColumnNames.TR_ID;
import static com.napol.koltsegvetes.db.IColumn.opEqual;
import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.PopupMenu.OnMenuItemClickListener;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ExpandableListView;

import com.napol.koltsegvetes.db.AbstractQuery;
import com.napol.koltsegvetes.db.DataStore;
import com.napol.koltsegvetes.db.EColumnNames;
import com.napol.koltsegvetes.db.ETableNames;
import com.napol.koltsegvetes.db.ParcelableQuery;
import com.napol.koltsegvetes.db.QueryBuilder;
import com.napol.koltsegvetes.util.Util;

/**
 * This fragment comes from MainActivity2
 * @author Polcz Péter <ppolcz@gmail.com>
 * Created on Nov 23, 2014 5:05:51 PM
 */
public class MainFragment extends Fragment
{
    /** package private - these should be accessible to {@link TrFormActivity} */
    static final String KEY_TR_QUERY = "trq";
    static final String KEY_PI_QUERY = "piq";
    static final String KEY_LADAPTER_INDEX = "adind";
    static final int REQUEST_NEWTR = 1001;
    static final int REQUEST_UPDATETR = 1002;

    private ExpandableListView lw;
    private DataStore db;
    private AbstractQuery q;
    private TransactionListAdapter4 ladapter;

    private View view;
    
    final String PREFS_NAME = "pcz_koltsegvetes_prefs";
    final String PREFS_FIRST_TIME = "first_time";

    void initDatabase()
    {
    	SharedPreferences settings = getActivity().getSharedPreferences(PREFS_NAME, 0);

		boolean firstTime = settings.getBoolean(PREFS_FIRST_TIME, true);
    	if (firstTime) {
    	    settings.edit().putBoolean(PREFS_FIRST_TIME, false).commit(); 
    	}

        db = DataStore.instance(firstTime);
        db.setContext(getActivity());
        db.onOpen();
    }

    void setupAdapter()
    {
        q = db.select(EColumnNames.getColumns(ETableNames.TRANZACTIONS));
        ParcelableQuery sampleProductInfo = (ParcelableQuery) QueryBuilder.create(new ParcelableQuery(), EColumnNames.getColumns(ETableNames.PRODUCT_INFO));
        ladapter = new TransactionListAdapter4(getActivity(), q, sampleProductInfo, R.layout.mainlw_item4, R.layout.mainlw_subitem);

        int tridPos = q.getPosition(TR_ID);
        for (int i = 0; i < ladapter.getGroupCount(); ++i)
        {
            Object[] row = ladapter.getGroup(i);
            AbstractQuery childList = db.select(PI_TRID.sqlwhere(row[tridPos], opEqual), sampleProductInfo.getCols());
            Util.debug("  <> childList.size = " + childList.size());
            ladapter.appendChild(i, childList);
        }
    }

    void setupListView()
    {
        lw = (ExpandableListView) view.findViewById(R.id.mainlw);
        lw.setAdapter(ladapter);
        lw.setGroupIndicator(null);
        lw.setOnItemLongClickListener(itemLongClickListener);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        view = inflater.inflate(R.layout.activity_main2, container, false);
        
        initDatabase();
        setupAdapter();
        setupListView();

        return view;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_newtr)
        {
            Intent i = new Intent(getActivity(), TrFormActivity.class);
            startActivityForResult(i, REQUEST_NEWTR);
        }
        return super.onOptionsItemSelected(item);
    }

    // @Override
    // protected void onActivityResult(int requestCode, int resultCode, Intent data)
    // {
    // if (resultCode == RESULT_OK)
    // {
    // ParcelableQuery group;
    // ParcelableQuery child;
    // switch (requestCode)
    // {
    // case REQUEST_NEWTR:
    // {
    // group = data.getExtras().getParcelable(KEY_TR_QUERY);
    // child = data.getExtras().getParcelable(KEY_PI_QUERY);
    // Util.debug("c.size = " + child.size()); // TODO
    // if (group.size() > 0)
    // {
    // int id = db.insert(group.getCols(), group.getFirst());
    //
    // group.getFirst()[group.getPosition(TR_ID)] = id;
    // child.update(PI_TRID, id);
    // child = (ParcelableQuery) db.insertAll(child, PI_ID);
    //
    // ladapter.addGroup(group, child);
    // ladapter.notifyDataSetChanged();
    // }
    // break;
    // }
    //
    // case REQUEST_UPDATETR:
    // {
    // group = data.getExtras().getParcelable(KEY_TR_QUERY);
    // child = data.getExtras().getParcelable(KEY_PI_QUERY);
    // int groupPosition = data.getExtras().getInt(KEY_LADAPTER_INDEX);
    // if (group.size() > 0)
    // {
    // String sqlwhere = TR_ID.sqlwhere(group.getValue(TR_ID, 0), opEqual);
    // db.update(group, sqlwhere);
    // // TODO - update child in database and in
    // ladapter.update(groupPosition, group);
    // ladapter.notifyDataSetChanged();
    // }
    // break;
    // }
    //
    // default:
    // break;
    // }
    // }
    // }

    /**
     * Menu on long click on the group items.
     * (This requires at least API level 11)
     */
    private OnItemLongClickListener itemLongClickListener = new OnItemLongClickListener()
    {
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id)
        {
            PopupMenu popup = new PopupMenu(getActivity(), view);
            popup.setOnMenuItemClickListener(new OnMenuItemClickListener()
            {
                /**
                 * Handling click events on the menu items
                 */
                public boolean onMenuItemClick(MenuItem item)
                {
                    String sqlwhere = TR_ID.sqlwhere(ladapter.getGroup(position)[q.getPosition(TR_ID)], opEqual);
                    switch (item.getItemId())
                    {
                    /**
                     * Instead of sending the row : Object[] record from the array adapter,
                     * we request the row from the DataStore.
                     * TODO - think on it, is it worth?
                     */
                        case R.id.action_update:
                        {
                            Intent i = new Intent(getActivity(), TrFormActivity.class);
                            i.putExtra(KEY_TR_QUERY, (Parcelable) db.select(ETableNames.TRANZACTIONS, sqlwhere));
                            i.putExtra(KEY_LADAPTER_INDEX, position);
                            startActivityForResult(i, REQUEST_UPDATETR);
                            break;
                        }

                        case R.id.action_delete:
                        {
                            db.delete(ETableNames.TRANZACTIONS, sqlwhere);
                            ladapter.remove(position);
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
}
