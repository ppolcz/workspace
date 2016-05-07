package com.polpe.panogl.main.gallery;

import java.io.File;
import java.util.ArrayList;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.PopupMenu.OnMenuItemClickListener;
import android.widget.TextView;
import android.widget.Toast;

import com.polpe.panogl.R;
import com.polpe.panogl.db.PanoDBDataSource;
import com.polpe.panogl.db.PanoItem;
import com.polpe.panogl.demo.camera.GLCameraActivity;
import com.polpe.panogl.demo.view.PanoViewerActivity;
import com.polpe.panogl.main.camera.PanoGLCameraActivity;
import com.polpe.panogl.main.camera.PanoGLViewActivity;
import com.polpe.panogl.main.gallery.adapter.ImagePagerAdapter;
import com.polpe.panogl.main.gallery.adapter.ListItemAdapter;
import com.polpe.panogl.util.GlobalConstants;
import com.polpe.panogl.util.Util;

public class GalleryListActivity extends Activity implements OnPagerPositionChanged, OnClickListener {
    public static final String TAG = "panogl.GalleryListActivity";

    public static final String KEY_PANORAMA_DIRNAME = "KEY_PANORAMA_DIRNAME";
    public static final String KEY_IMAGE_NR = "KEY_IMAGE_NR";
    private static final String KEY_LAST_SELECTED_ITEM = "KEY_LAST_SELECTED_ITEM";
    public static final String KEY_CREATE_NEW_PANO = "KEY_CREATE_NEW_PANO";

    private static final int REQUEST_CODE_CREATE_PANORAMA = 123;
    private static final int REQUEST_CODE_VIEW_FULLSCREEN = 124;
    private static final int REQUEST_CODE_NEW_PANORAMA_REQUESTED = 125;

    // DataSource
    private PanoDBDataSource db;

    // ListView stuff
    private ListView lw;
    private ListItemAdapter lwadapter;
    private ArrayList<PanoItem> items;

    // ViewPager stuff
    private ViewPager pw;
    private ImagePagerAdapter pwadapter;
    ArrayList<String> images;

    // Buttons, widgets above the ViewPager
    Button btnPrev;
    Button btnNext;
    Button btnDelete;
    TextView twLabel;
    TextView twEmpty;

    private int lastSelectedItem = -1;
    private int itemToBeSelected = 0;
    private int lastImagePosition = -1;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // restore instance state
        if (savedInstanceState != null) itemToBeSelected = savedInstanceState.getInt(KEY_LAST_SELECTED_ITEM, 0);

        // opening database using a singleton dataSource object
        db = PanoDBDataSource.getInstance(this);
        db.open();

        // creating lists for the adaptors
        items = db.getAllPanoramaTuples();
        images = new ArrayList<String>();

        // if there are no panoramas at all
        if (items.isEmpty()) {
            initializeGUINoPanoramas();
        }

        // if there are panoramas
        else {
            initializeGUI();

            // If first creation of fragment, select the default item
            if (lwadapter.getCount() > 0) {
                // Default the selection to the first item
                setImageSelected(itemToBeSelected);
            }
        }
    }

    @Override
    protected void onResume () {
        super.onResume();

        if (!db.isOpen()) db.open();
    }

    @Override
    protected void onPause () {
        super.onPause();

        if (db.isOpen()) db.close();
    }

    @Override
    protected void onDestroy () {
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu (Menu menu) {

        getMenuInflater().inflate(R.menu.gallery_listview, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected (MenuItem item) {

        switch (item.getItemId())
        {
            case R.id.action_new_panorama:
            {
                createNewPanoramaDialog(items == null || items.isEmpty());
                break;
            }
            case R.id.action_view3D_demo:
            {
                Intent intent = new Intent(GalleryListActivity.this, PanoViewerActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.action_demo_camera:
            {
                Intent intent = new Intent(GalleryListActivity.this, GLCameraActivity.class);
                startActivity(intent);
                break;
            }
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult (int requestCode, int resultCode, Intent data) {
        Util.debug();
        switch (requestCode)
        {
            case REQUEST_CODE_CREATE_PANORAMA:
                Util.assertation(0 < items.size());
                setImageSelected(itemToBeSelected);
                break;

            case REQUEST_CODE_NEW_PANORAMA_REQUESTED:
                Util.debug();
                if (resultCode == RESULT_OK && data.getStringExtra(KEY_CREATE_NEW_PANO) != null) {
                    Util.debug();
                    createNewPanoramaDialog(false);
                }
                break;

            default:
                break;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onSaveInstanceState (Bundle outState) {
        outState.putInt(KEY_LAST_SELECTED_ITEM, lastSelectedItem);
        super.onSaveInstanceState(outState);
    }

    // Simple click on the panoramas' list
    private OnItemClickListener itemClickListener = new OnItemClickListener() {

        @Override
        public void onItemClick (AdapterView<?> parent, View view, int position, long id) {
            setImageSelected(position);
        }
    };

    // Long click on the panoramas' list
    private OnItemLongClickListener itemLongClickListener = new OnItemLongClickListener() {

        private int choice = 0;

        // TODO Ezzel valamit kezdeni kell, mert API 8 -on is szeretnem ha menne
        @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
        @Override
        public boolean onItemLongClick (AdapterView<?> parent, View view, final int position, long id) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH)

            // in case of a higher API level (>=14)
            {
                PopupMenu popup = new PopupMenu(GalleryListActivity.this, view);
                popup.setOnMenuItemClickListener(new OnMenuItemClickListener() {
                    public boolean onMenuItemClick (MenuItem item) {
                        switch (item.getItemId())
                        {
                            case R.id.view:

                                // if the directory of the clicked panorama is not empty
                                if (new File(items.get(position).getDirname()).list().length > 0) {
                                    startFullScreenViewer(position, 0);
                                } else {
                                    Toast.makeText(GalleryListActivity.this, "This panorama has no images", Toast.LENGTH_SHORT).show();
                                }
                                break;

                            case R.id.view3D:
                                if (new File(items.get(position).getDirname()).list().length > 0) {
                                    Intent intent = new Intent(GalleryListActivity.this, PanoGLViewActivity.class);
                                    intent.putExtra(KEY_PANORAMA_DIRNAME, items.get(position).getDirname());
                                    startActivityForResult(intent, REQUEST_CODE_NEW_PANORAMA_REQUESTED);
                                } else {
                                    Toast.makeText(GalleryListActivity.this, "This panorama has no images", Toast.LENGTH_SHORT).show();
                                }
                                break;

                            case R.id.delete:
                                deletePanorama(position);
                                break;

                            default:
                                return false;
                        }
                        return true;
                    }
                });
                popup.inflate(R.menu.dialog_longclick_actions);
                popup.show();
            } else

            // in case of a lower API level (<14)
            {
                AlertDialog dialog = new AlertDialog.Builder(GalleryListActivity.this)
                        .setTitle(R.string.panorama_actions)
                        .setSingleChoiceItems(R.array.panorama_action_items, 0, new DialogInterface.OnClickListener() {
                            public void onClick (DialogInterface dialog, int whichButton) {
                                choice = whichButton;
                            }
                        })
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            public void onClick (DialogInterface dialog, int whichButton) {
                                switch (choice)
                                {
                                    case 0:
                                        if (new File(items.get(position).getDirname()).list().length > 0) {
                                            startFullScreenViewer(position, 0);
                                        } else {
                                            Toast.makeText(GalleryListActivity.this, "This panorama has no images", Toast.LENGTH_SHORT).show();
                                        }
                                        break;

                                    case 1:
                                        if (new File(items.get(position).getDirname()).list().length > 0) {
                                            Intent intent = new Intent(GalleryListActivity.this, PanoGLViewActivity.class);
                                            intent.putExtra(KEY_PANORAMA_DIRNAME, items.get(position).getDirname());
                                            startActivityForResult(intent, REQUEST_CODE_NEW_PANORAMA_REQUESTED);
                                        } else {
                                            Toast.makeText(GalleryListActivity.this, "This panorama has no images", Toast.LENGTH_SHORT).show();
                                        }
                                        break;
                                        
                                    case 2:
                                        deletePanorama(position);
                                        break;
                                }
                            }
                        })
                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            public void onClick (DialogInterface dialog, int whichButton) {}
                        })
                        .create();
                dialog.show();
            }

            return false;
        }
    };

    // Showing the dialog, in which the user can give the name of a new panorama instance
    private void createNewPanoramaDialog (final boolean isFirst) {

        final View view = getLayoutInflater().inflate(R.layout.dialog_give_pano_name, null);

        AlertDialog dialog = new AlertDialog.Builder(GalleryListActivity.this)
                .setTitle(R.string.create_panorama)
                .setView(view)
                .setPositiveButton(R.string.create, new DialogInterface.OnClickListener() {
                    public void onClick (DialogInterface dialog, int whichButton) {
                        createNewPanorama((EditText) view.findViewById(R.id.dialog_te), isFirst);
                    }
                }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick (DialogInterface dialog, int whichButton) {}
                }).create();
        dialog.show();
    }

    // Creating new panorama
    private void createNewPanorama (EditText et, boolean isFirst) {

        // retaining the name of the new panorama, which is assured to be a non-empty string
        String panoname = et.getText().toString();
        if (panoname == null || panoname.length() == 0) {
            panoname = et.getHint().toString();
        }

        // [1] creating a new panorama item and its directory
        PanoItem item = new PanoItem();
        item.setName(panoname);
        item.setDirname(Util.createNonExistentDir(GlobalConstants.DIR_ROOT_PANO_PREFIX));
        item.setDateToNow();

        // [2] insert it into the database
        db.insertPanoTuple(item);

        // [3] insert it into the items' list
        items.add(item);

        if (isFirst) initializeGUI();
        itemToBeSelected = items.size() - 1;

        // notifying the listview's adapter about the change
        lwadapter.notifyDataSetChanged();

        // starting CreatePanoramaActivity
        Intent intent = new Intent(GalleryListActivity.this, PanoGLCameraActivity.class);
        intent.putExtra(KEY_PANORAMA_DIRNAME, item.getDirname());
        startActivityForResult(intent, REQUEST_CODE_CREATE_PANORAMA);
    }

    // Delete existing panorama
    private void deletePanorama (int position) {
        Util.assertation(0 <= position && position < items.size());

        PanoItem item = items.get(position);

        // [1] delete from database
        db.removePanoTuple(item.getId());

        // [2] delete folder
        Util.deleteRecursively(new File(item.getDirname()));

        // [3] delete from the adapter
        items.remove(position);

        // notify the adapter
        lwadapter.notifyDataSetChanged();

        // 1. case - that was the single existing panorama
        if (items.isEmpty()) {
            initializeGUINoPanoramas();
            return;
        }

        // 2. case - this panorama was last panorama
        if (position == items.size()) {
            setImageSelected(items.size() - 1);
            return;
        }

        // 3. case
        if (lastSelectedItem == position) {
            lastSelectedItem--;
            setImageSelected(position);
        }
    }

    private void startFullScreenViewer (int panoNr, int position) {
        Intent intent = new Intent(GalleryListActivity.this, GalleryFullScreenViewActivity.class);
        intent.putExtra(KEY_PANORAMA_DIRNAME, items.get(panoNr).getDirname());
        intent.putExtra(KEY_IMAGE_NR, position);
        startActivityForResult(intent, REQUEST_CODE_VIEW_FULLSCREEN);
    }

    // Select panorama to preview its images
    private void setImageSelected (int position) {

        // If the selected position is valid, and different than what is
        // currently selected, highlight that row in the list and scroll to it

        if (position >= 0 && position < lwadapter.getCount() && position != lastSelectedItem) {

            itemToBeSelected = 0;
            lastSelectedItem = position;

            // Highlight the selected row and scroll to it
            lw.setItemChecked(position, true);
            lw.smoothScrollToPosition(position);

            // if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            // }
            
            File dir = new File(items.get(position).getDirname());
            Util.assertation(dir.isDirectory());

            // clearing preview images
            images.clear();

            // adding new images to the ArrayList
            for (File child : dir.listFiles()) {
                String filename = child.getAbsolutePath().toString();
                if (filename.endsWith("." + GlobalConstants.FILE_IMAGE_EXTENSION)) {
                    images.add(filename);
                }
            }

            // setting up the adapter
            pw.setAdapter(pwadapter);
        }
    }

    @Override
    // com.polpe.panogl.view.OnPagerPositinoChanged.~
    public void positionChanged (int position) {
        lastImagePosition = position - 1;

        if (pwadapter.getCount() == 0) {
            twLabel.setVisibility(ViewGroup.GONE);
            btnPrev.setVisibility(ViewGroup.GONE);
            btnNext.setVisibility(ViewGroup.GONE);

            // show widgets that stand for the empty list case
            btnDelete.setVisibility(ViewGroup.VISIBLE);
            twEmpty.setVisibility(ViewGroup.VISIBLE);

            return;
        }

        // hide widgets that stand for the empty list case
        btnDelete.setVisibility(ViewGroup.GONE);
        twEmpty.setVisibility(ViewGroup.GONE);

        // show widgets that stand for the easy navigation between photos
        twLabel.setVisibility(ViewGroup.VISIBLE);
        twLabel.setText(position + " / " + pwadapter.getCount());
        if (position == 1) {
            btnPrev.setVisibility(ViewGroup.GONE);
        } else {
            btnPrev.setVisibility(ViewGroup.VISIBLE);
        }
        if (position == pwadapter.getCount()) {
            btnNext.setVisibility(ViewGroup.GONE);
        } else {
            btnNext.setVisibility(ViewGroup.VISIBLE);
        }
    }

    protected void initializeGUI () {
        setContentView(R.layout.gallery_main);

        btnPrev = (Button) findViewById(R.id.gallery_pager_prev);
        btnNext = (Button) findViewById(R.id.gallery_pager_next);
        btnDelete = (Button) findViewById(R.id.gallery_pano_delete_pano);
        twLabel = (TextView) findViewById(R.id.gallery_pager_label);
        twEmpty = (TextView) findViewById(R.id.gallery_pager_no_images_found);

        // creating adaptors
        lwadapter = new ListItemAdapter(this, R.layout.gallery_list_item, R.id.list_item_tw_name, R.id.list_item_tw_date, getLayoutInflater(), items);
        pwadapter = new ImagePagerAdapter(this, images, this);

        // setting up the list view
        lw = (ListView) findViewById(R.id.gallery_listview);
        lw.setAdapter(lwadapter);
        lw.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
        lw.setOnItemClickListener(itemClickListener);
        lw.setOnItemLongClickListener(itemLongClickListener);

        // initializing the ViewPager, pw.setAdapter(pwadapter) will
        // be called later in the method setImageSelected(position)
        pw = (ViewPager) findViewById(R.id.gallery_pager);

        // delete panorama button
        btnDelete.setOnClickListener(this);
    }

    // if no panaramas found, the whole layout is redefined
    private void initializeGUINoPanoramas () {
        setContentView(R.layout.gallery_empty);
        findViewById(R.id.btn_create_pano).setOnClickListener(new OnClickListener() {
            public void onClick (View v) {
                createNewPanoramaDialog(true);
            }
        });
    }

    @Override
    // OnClickListener.~
    public void onClick (View v) {
        switch (v.getId())
        {
            case R.id.gallery_pano_delete_pano:
                deletePanorama(lastSelectedItem);
                break;

            case R.id.gallery_pager_iw:
                startFullScreenViewer(lastSelectedItem, lastImagePosition);
                break;

            default:
                break;
        }
    }
}
