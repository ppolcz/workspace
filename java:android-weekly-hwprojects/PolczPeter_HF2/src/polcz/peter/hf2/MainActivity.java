package polcz.peter.hf2;

import java.util.List;

import polcz.peter.hf2.db.Contact;
import polcz.peter.hf2.db.ContactListDataSource;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

/** 
 * Polcz Peter - KUE5RC 
 *
 * Csak azert irok a kommentekben angolul, mert utalom ha alahuzodik a spell-check miatt
 * 
 * VERY IMPORTANT - if the application returns from the FormActivity the following call order happens:
 * onPause() --> onStop() --> !onActivityResult()! --> onRestart() --> onStart() --> onResume()
 * 
 * VERY IMPORTANT:
 * BlockingQueue - communication between threads - synchronization etc..
 * for example: how to delete marked elements from an adapter using multiple threads without any Exception
 * Note that if the adapter has 3 elements, and I want to delete the first and the last than I must start with the last.
 * But using threads this reverse order is not assured.
 * 
 * <code>
 * class Delete(index)
 *     function run()
 *         delete_from_adapter(index)         
 * 
 * for (i : indexes_marked_to_delete_in_reversed_order)
 *     Delete(i).run()                     ----> crash - IndexOutOfBoundsException - because the threads usually are not running in the order they were started
 * </code>
 * 
 * instead:
 * <code>
 * BlockingQueue<List> queue
 * 
 * class Delete(index)
 *     function run()
 *         removed_indexes = queue.take()
 *         for (i : removed_indexes)
 *             if (i < index) index--
 *         delete_from_adapter(index)
 *         removed_indexes.push(index)   
 *         queue.offer(removed_indexes)      
 * 
 * for (i : indexes_marked_to_delete)
 *     Delete(i).run()          
 *     
 * # now the threads are waiting for trigger          
 * 
 * queue.offer(EmptyList)
 * # trigger has just been fired - deletion is started
 * </code>
 * 
 */
public class MainActivity extends Activity {

    // constants, IDs
    static final String RESULT_CODE_CONTACT = "_contact";
    private static final int REQUEST_CONTACT = 124;

    // Log tag
    String tag = "polpe.hf2.MainActivity";

    // handling database
    private ContactListDataSource mDataSource;

    // handling list view
    private ArrayAdapter<Contact> mAdapter;
    private ListView mListView;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        Log.i(tag, "onCreate() - MainActivity");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mListView = (ListView) findViewById(R.id.list_view);

        // opening database
        mDataSource = new ContactListDataSource(this);
        mDataSource.open();

        List<Contact> contactlist = mDataSource.getAllContacts();

        for (Contact contact : contactlist) {
            contact.initialize(this);
        }

        mAdapter = new ContactListAdapter(this, R.layout.list_item, contactlist);
        mListView.setAdapter(mAdapter);

        mListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
    }

    @Override
    protected void onStart () {
        Log.i(tag, "onStart() - MainActivity");
        super.onStart();
    }

    @Override
    protected void onRestart () {
        Log.i(tag, "onRestart() - MainActivity");
        super.onRestart();
    }

    @Override
    protected void onResume () {
        Log.i(tag, "onResume() - MainActivity");
        mDataSource.open();

        super.onResume();
    }

    @Override
    protected void onPause () {
        Log.i(tag, "onPause() - MainActivity");
        mDataSource.close();
        super.onPause();
    }

    @Override
    protected void onStop () {
        Log.i(tag, "onStop() - MainActivity");
        super.onStop();
    }

    @Override
    protected void onDestroy () {
        Log.i(tag, "onDestroy() - MainActivity");
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu (Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected (MenuItem item) {

        switch (item.getItemId()) {

            case R.id.menu_new: {
                Intent intent = new Intent(this, FormActivity.class);
                startActivityForResult(intent, MainActivity.REQUEST_CONTACT);
                return true;
            }

            case R.id.menu_remove: {

                // Get an array that tells us for each position whether the item is
                // checked or not
                // --
                final SparseBooleanArray checkedItems = mListView.getCheckedItemPositions();

                if (checkedItems == null) {
                    Toast.makeText(this, "No selection info available", Toast.LENGTH_LONG).show();
                } else if (checkedItems.size() > 0) {

                    AlertDialog.Builder adb = new AlertDialog.Builder(MainActivity.this);
                    adb.setTitle("Delete?");
                    adb.setMessage("Are you sure you want to delete them?");
                    adb.setNegativeButton("Cancel", null);
                    adb.setPositiveButton("Ok", new AlertDialog.OnClickListener() {
                        public void onClick (DialogInterface dialog, int which) {
                            Log.d(tag, "checkedItems = " + checkedItems.toString());
                            Log.d(tag, "checkedItems.size = " + String.valueOf(checkedItems.size()));

                            // For each element in the status array
                            // --
                            final int checkedItemsCount = checkedItems.size();
                            for (int i = checkedItemsCount - 1; i >= 0; --i) {

                                // This tells us the item position we are looking at
                                // --
                                final int position = checkedItems.keyAt(i);

                                // This tells us the item status at the above position
                                // --
                                final boolean isChecked = checkedItems.valueAt(i);
                                Log.d(tag, String.format("deleting checkedItems[%d] = %d - adapter.size = %d ... isChecked = ", i, position, mAdapter.getCount()) + String.valueOf(isChecked));

                                if (isChecked) {
                                    Log.d(tag, String.format("mAdapter.size = %d -- position = %d ", mAdapter.getCount(), position));
                                    mDataSource.deleteContact(mAdapter.getItem(position));
                                    mAdapter.remove(mAdapter.getItem(position));

                                    mAdapter.notifyDataSetChanged();
                                }
                            }

                            reloadListView();
                            mAdapter.notifyDataSetChanged();
                        }
                    });

                    adb.show();
                }
                return true;
            }

            case R.id.menu_marvinlabs: {
                Intent intent = new Intent(this, fr.marvinlabs.selectablelisttutorial.MainActivity.class);
                startActivity(intent);

                return true;
            }

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /** @brief HANDLING_RESULTS
     * 
     *        Handling results sent by child activities identifying them by
     *        their request code. {@link MainActivity}.REQUEST_CODE stands for
     *        the FormLayout's intent. */
    @Override
    protected void onActivityResult (int requestCode, int resultCode, Intent data) {
        Log.i(tag, "onActivityResult - MainActivity");

        // If child activity has sent data
        if (resultCode == RESULT_OK && data != null) {

            switch (requestCode) {

            // handling data sent by FormActivity
                case MainActivity.REQUEST_CONTACT:

                    Log.d(tag, "     ---> REQUEST_CONTACT");

                    // retrieving parcelable object - contact information stored
                    // in a Contact object
                    Contact contact = data.getParcelableExtra(MainActivity.RESULT_CODE_CONTACT);
                    contact.initialize(this);

                    try {
                        // adding it to the database
                        Log.d(tag, "     ---> putting it to the database");
                        mDataSource.open();
                        contact = mDataSource.createContact(contact);
                    } catch (Exception e) {
                        Log.e("sqlite", "Nem tudta berakni! Line: " + String.valueOf(Thread.currentThread().getStackTrace()[2].getLineNumber()));
                        e.printStackTrace();
                    }

                    // adding it to the adaptor
                    mAdapter.add(contact);

                    // notifying the adaptors to update its content
                    mAdapter.notifyDataSetChanged();
                    break;
            }
        }
        // calling the overridden member of the superclass
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void reloadListView () {
        List<Contact> contactlist = mDataSource.getAllContacts();

        for (Contact contact : contactlist) {
            contact.initialize(this);
        }

        mAdapter = new ContactListAdapter(this, R.layout.list_item, contactlist);
        mListView.setAdapter(mAdapter);
    }
}
