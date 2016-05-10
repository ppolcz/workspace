package com.napol.koltsegvetes;

import static com.napol.koltsegvetes.db.EColumnNames.TR_AMOUNT;
import static com.napol.koltsegvetes.db.EColumnNames.TR_CAID;
import static com.napol.koltsegvetes.db.EColumnNames.TR_CLNAME;
import static com.napol.koltsegvetes.db.EColumnNames.TR_DATE;
import static com.napol.koltsegvetes.db.EColumnNames.TR_ID;
import static com.napol.koltsegvetes.db.EColumnNames.TR_MKNAME;
import static com.napol.koltsegvetes.db.EColumnNames.TR_NEWBALANCE;
import static com.napol.koltsegvetes.db.EColumnNames.TR_REMARK;
import static com.napol.koltsegvetes.db.EColumnNames.opEqual;
import static com.napol.koltsegvetes.util.Util.debug;
import android.support.v4.widget.DrawerLayout;

import java.util.ListIterator;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.PopupMenu.OnMenuItemClickListener;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.napol.koltsegvetes.db.AbstractQuery;
import com.napol.koltsegvetes.db.DataStore;
import com.napol.koltsegvetes.db.ETableNames;
import com.napol.koltsegvetes.db.ParcelableQuery;

/**
 * @author Polcz Péter <ppolcz@gmail.com>
 * Created on Oct 13, 2014 7:25:33 PM
 */
public class MainActivity extends ActionBarActivity
{
    /** package private - these should be accessible to {@link TrFormActivity} */
    static final String KEY_ABSQR = "absqr";
    static final int REQUEST_NEWTR = 1001;
    static final int REQUEST_UPDATETR = 1002;

    private ListView lw;
    private DataStore db;
    private AbstractQuery q;
    private TransactionListAdapter2 ladapter;

    // @SuppressLint("NewApi")
    private OnItemLongClickListener itemLongClickListener = new OnItemLongClickListener()
    {
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id)
        {
            PopupMenu popup = new PopupMenu(MainActivity.this, view);
            popup.setOnMenuItemClickListener(new OnMenuItemClickListener()
            {
                public boolean onMenuItemClick(MenuItem item)
                {
                    String sqlwhere = TR_ID.sqlwhere(ladapter.getItem(position)[q.getPosition(TR_ID)], opEqual);
                    Toast.makeText(MainActivity.this, sqlwhere, Toast.LENGTH_LONG).show();
                    switch (item.getItemId())
                    {
                        case R.id.action_update:
                            Intent i = new Intent(MainActivity.this, TrFormActivity.class);
                            i.putExtra(KEY_ABSQR, (Parcelable) db.select(ETableNames.TRANZACTIONS, sqlwhere));
                            startActivityForResult(i, REQUEST_UPDATETR);
                            break;

                        case R.id.action_delete:
                            db.delete(ETableNames.TRANZACTIONS, sqlwhere);
                            ladapter.remove(ladapter.getItem(position));
                            ladapter.notifyDataSetChanged();
                            break;

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

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        // MetaUtil.GeneratePlantUML(DataStore.class);
        // MetaUtil.GeneratePlantUML(MainActivity.class);
        // MetaUtil.GeneratePlantUML(TrFormActivity.class);
        // MetaUtil.GeneratePlantUML(TransactionListAdapter.class);
        // MetaUtil.GeneratePlantUML(ParcelableQuery.class);
        // MetaUtil.GeneratePlantUML(MySQLiteHelper.class);
        // MetaUtil.GeneratePlantUML(MySQLiteOpenHelper.class);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        db = DataStore.instance();
        db.setContext(this);
        db.onOpen();

        q = db.select(TR_ID, TR_AMOUNT, TR_REMARK, TR_DATE, TR_CAID, TR_CLNAME, TR_NEWBALANCE, TR_MKNAME);
        ladapter = new TransactionListAdapter2(this, q, R.layout.mainlw_item4);

        lw = (ListView) findViewById(R.id.mainlw);
        lw.setAdapter(ladapter);
        lw.setOnItemLongClickListener(itemLongClickListener);
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
        if (id == R.id.action_newtr)
        {
            Intent i = new Intent(this, TrFormActivity.class);
            startActivityForResult(i, REQUEST_NEWTR);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (resultCode == RESULT_OK)
        {
            ParcelableQuery q;
            switch (requestCode)
            {
                case REQUEST_NEWTR:
                    q = (ParcelableQuery) data.getExtras().getParcelable(KEY_ABSQR);
                    if (q.size() > 0)
                    {
                        int id = db.insert(q.getTypes(), q.getFirst());
                        q.getFirst()[0] = id;
                        ladapter.getQuery().appendQuery(q);
                        ladapter.notifyDataSetChanged();

                        // Toast.makeText(MainActivity.this,
                        // "last_insert_rowid = " + id,
                        // Toast.LENGTH_LONG).show();
                    }

                    // Toast.makeText(this,
                    // String.format("%s %s - %s",
                    // q.get(0)[0].toString(), q.get(0)[1].toString(),
                    // q.getTypes()[0].name()), Toast.LENGTH_LONG).show();
                    // debug("q = %s %s %s %s", q.getTypes()[0],
                    // q.getTypes()[1], q.getFirst()[0].toString(),
                    // q.getFirst()[1].toString());
                    break;
                    
                case REQUEST_UPDATETR:
                    // TODO
                    break;
                    
                default:
                    break;
            }
        }
    }

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
