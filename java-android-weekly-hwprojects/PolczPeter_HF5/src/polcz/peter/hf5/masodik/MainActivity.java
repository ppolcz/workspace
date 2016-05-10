package polcz.peter.hf5.masodik;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import polcz.peter.hf5.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

/** 
 * Polcz Peter - KUE5RC 
 */
public class MainActivity extends Activity {
    public static final String ID_MEDIA_TYPE = "MediaType";
    public static final String ID_MEDIA_URL = "MediaURL";

    public static enum MimeTypes {
        IMAGE("IMAGE"), AUDIO("AUDIO"), VIDEO("VIDEO");
        public String str;

        private MimeTypes(String str) {
            this.str = str;
        }
    }

    // constants, IDs
    static final String RESULT_CODE_CONTACT = "_contact";

    // Log tag
    String tag = "polpe.hf2.MainActivity";

    // handling list view
    private ArrayAdapter<MediaURL> mAdapter;
    private ListView mListView;
    private Button mDownloadButton;

    private OnClickListener downloadListener = new OnClickListener() {

        @Override
        public void onClick (View v) {

            Intent intent = new Intent(MainActivity.this, MediaPlayerActivity.class);
            ArrayListParcelable mediaTypes = new ArrayListParcelable();
            ArrayListParcelable mediaUrls = new ArrayListParcelable();

            final SparseBooleanArray checkedItems = mListView.getCheckedItemPositions();

            if (checkedItems == null) throw new NullPointerException();

            // For each element in the status array
            // --
            final int checkedItemsCount = checkedItems.size();
            for (int i = 0; i < checkedItemsCount; ++i) {

                // This tells us the item position we are looking at
                // --
                final int position = checkedItems.keyAt(i);

                // This tells us the item status at the above position
                // --
                final boolean isChecked = checkedItems.valueAt(i);
                Log.d(tag, String.format("deleting checkedItems[%d] = %d - adapter.size = %d ... isChecked = ", i, position, mAdapter.getCount()) + String.valueOf(isChecked));

                if (isChecked) {
                    String url = mAdapter.getItem(position).getAddress();

                    switch (MimeTypes.valueOf(getMediaType(url).toUpperCase(Locale.getDefault()))) {
                        case IMAGE:
                            System.out.println("IMAGE");

                            mediaUrls.add(url);
                            mediaTypes.add(MimeTypes.IMAGE.str);
                            break;

                        case AUDIO:
                            System.out.println("AUDIO");

                            mediaUrls.add(url);
                            mediaTypes.add(MimeTypes.AUDIO.str);
                            break;

                        case VIDEO:
                            System.out.println("VIDEO");

                            mediaUrls.add(url);
                            mediaTypes.add(MimeTypes.VIDEO.str);
                            break;

                        default:
                            break;
                    }
                    getMediaType(url);
                }
            }

            if (mediaTypes.size() > 0) {
                intent.putExtra(ID_MEDIA_TYPE, (Parcelable) mediaTypes);
                intent.putExtra(ID_MEDIA_URL, (Parcelable) mediaUrls);

                startActivity(intent);
            }

            // reload ListView
            // mAdapter.notifyDataSetChanged();
        }
    };

    // url = file path or whatever suitable URL you want.
    public static String getMimeType (String url) {
        String type = null;
        String extension = MimeTypeMap.getFileExtensionFromUrl(url);
        if (extension != null) {
            MimeTypeMap mime = MimeTypeMap.getSingleton();
            type = mime.getMimeTypeFromExtension(extension);
        }
        return type;
    }

    public static String getMediaType (String url) {
        String mimeType = getMimeType(url);
        return mimeType.split("/")[0];
    }

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        Log.i(tag, "onCreate() - MainActivity");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.masodik_main);

        mListView = (ListView) findViewById(R.id.list_view);
        mDownloadButton = (Button) findViewById(R.id.btn_download);

        mDownloadButton.setOnClickListener(downloadListener);

        // opening database
        List<MediaURL> contactlist = new ArrayList<MediaURL>();
        contactlist.add(new MediaURL(16, "PNG kep", "http://upload.wikimedia.org/wikipedia/commons/6/64/Gnu_meditate_levitate.png"));
        contactlist.add(new MediaURL(12, "Fenykep", "http://airbornescience.jpl.nasa.gov/sites/default/files/public/glisten-plane-image3.jpg"));
        contactlist.add(new MediaURL(13, "Audio", "http://freedownloads.last.fm/download/241837905/Saga.mp3"));
        contactlist.add(new MediaURL(14, "Tobb gigas video", "archive.org/download/VoyagetothePlanetofPrehistoricWomen/VoyagetothePlanetofPrehistoricWomen_512kb.mp4"));
        // contactlist.add(new MediaURL(15, "Parszasz megas video", "archive.org/download/night_of_the_living_dead/night_of_the_living_dead.mpeg"));

        mAdapter = new DownloadListAdapter(this, R.layout.list_item_as_marvinlabs, contactlist);
        mListView.setAdapter(mAdapter);
        mListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
    }

    @Override
    public boolean onCreateOptionsMenu (Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected (MenuItem item) {
        switch (item.getItemId()) {
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
