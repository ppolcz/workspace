package polcz.peter.hf2;

import polcz.peter.hf2.db.Contact;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

/**
 * @remark Each time the phone is rotated this activity will follow: onPause - onStop - onDestroy - onCreate - onStart - onResume
 * Consequently we have to store the previous session.
 * For this I used shared preferences.
 * But how to know that the there was previous session?
 * For this I used a boolean variable called mBelievePref.
 */
public class FormActivity extends Activity {
    private static final int REQUEST_PICTURE = 125;
    private static final String EMPTY_STRING = "";

    String tag = "polpe.hf2.FormActivity";

    private String mFilemanagerString;
    private String mImgPath;
    private Uri mImgUri;
    private ImageView mImg;
    private EditText mFirstName;
    private EditText mLastName;
    private EditText mTelNr;
    private Button mSave;
    private Button mReplace;

    private SharedPreferences mSharedPref;
    private SharedPreferences.Editor mEditor;
    private boolean mBelievePref;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        Log.i(tag, "onCreate()");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.form_layout);

        // shared pref. in order to store and save data during recreation of the activity
        // --
        mSharedPref = getPreferences(Context.MODE_PRIVATE);
        mEditor = mSharedPref.edit();

        // loading data from previous session
        // --
        mBelievePref = mSharedPref.getBoolean(getString(R.string.key_believe), false);
        mImgPath = mSharedPref.getString(getString(R.string.key_filename), EMPTY_STRING);
        if (!mBelievePref) mImgPath = EMPTY_STRING;

        // debugging informations
        // --
        Log.d(tag, "line = " + String.valueOf(Thread.currentThread().getStackTrace()[2].getLineNumber()) + " believe = " + String.valueOf(mBelievePref));
        Log.d(tag, "line = " + String.valueOf(Thread.currentThread().getStackTrace()[2].getLineNumber()) + " path = " + String.valueOf(mImgPath));

        // creating handles
        // --
        mImg = (ImageView) findViewById(R.id.form_iw);
        mFirstName = (EditText) findViewById(R.id.form_fname);
        mLastName = (EditText) findViewById(R.id.form_lname);
        mTelNr = (EditText) findViewById(R.id.form_telnr);
        mSave = (Button) findViewById(R.id.form_save);
        mReplace = (Button) findViewById(R.id.form_replace);

        // set mTelNr keypad to the old-fashioned keypad with 12 keys
        // --
        mTelNr.setRawInputType(Configuration.KEYBOARD_12KEY);

        // setting drawable to mImg
        // --
        if (mBelievePref && mImgPath != EMPTY_STRING) mImg.setImageDrawable(Drawable.createFromPath(mImgPath));
        mBelievePref = true;

        // set on-click listener - to choose a photo
        // --
        mImg.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick (View v) {

                // Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                // My first approach for browsing image
                // --
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);

                startActivityForResult(intent, REQUEST_PICTURE);
            }
        });

        // clicking on "Save" button produces the same as "Add Contact" in form menu
        // --
        mSave.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick (View v) {
                sendContact();
            }
        });

        // clicking on "Replace" button
        // --
        mReplace.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick (View v) {
                Toast.makeText(FormActivity.this, "Non implemented yet", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onStart () {
        Log.i(tag, "onStart()");
        super.onStart();
    }

    @Override
    protected void onRestart () {
        Log.i(tag, "onRestart()");
        super.onRestart();
    }

    @Override
    protected void onResume () {
        Log.i(tag, "onResume()");
        super.onResume();
    }

    @Override
    protected void onPause () {
        Log.i(tag, "onPause()");

        // saving session - saving image
        // --
        mEditor.putBoolean(getString(R.string.key_believe), mBelievePref);
        mEditor.putString(getString(R.string.key_filename), mImgPath);
        mEditor.commit();

        super.onPause();
    }

    @Override
    protected void onStop () {
        Log.i(tag, "onStop()");
        super.onStop();
    }

    @Override
    protected void onDestroy () {
        Log.i(tag, "onDestroy()");
        super.onDestroy();
    }

    @Override
    public boolean onKeyDown (int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            Log.i(tag, "onKeyDown()");
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onBackPressed () {
        Log.i(tag, "onBackPressed()");
        mBelievePref = false;
        super.onBackPressed();
    }

    /**
     * @brief Getting information from child activities */
    @Override
    protected void onActivityResult (int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && data != null && requestCode == REQUEST_PICTURE) {

            // get image URI from child activity
            // --
            mImgUri = data.getData();

            // get image path from IO file manager - I do not know what is this
            // exactly since I have been using Android for 3 weeks.
            // --
            mFilemanagerString = mImgUri.getPath();

            // get absolute image path
            // --
            mImgPath = getPath(mImgUri);

            // change the content of the image view
            // --
            mImg.setImageURI(mImgUri);

            // logging
            // --
            Log.i(tag, "onActivityResult()");
            Log.e(tag, "   - mFilemanagerString = " + mFilemanagerString);
            Log.e(tag, "   - mImgPath = " + mImgPath);

            Toast.makeText(this, mFilemanagerString + " - " + mImgPath, 5 * Toast.LENGTH_LONG).show();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     *
     * @param uri
     * @return path the uri has been loaded from
     */
    public String getPath (Uri uri) {

        // I have no idea what is this needed for - but it works, and I am glad!
        // --
        String[] projection = { MediaStore.Images.Media.DATA };

        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null) {
            // HERE YOU WILL GET A NULLPOINTER IF CURSOR IS NULL
            // THIS CAN BE, IF YOU USED OI FILE MANAGER FOR PICKING THE MEDIA
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } else return null;
    }

    @Override
    public boolean onCreateOptionsMenu (Menu menu) {
        getMenuInflater().inflate(R.menu.form_layout_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected (MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.fmenu_add:
                sendContact();
                return true;

            case R.id.fmenu_help:
                Toast.makeText(this, "TODO", Toast.LENGTH_SHORT).show();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Send data to the parent activity {@link MainActivity}.
     * Put an end to this activity.
     */
    private void sendContact () {
        Contact contact = new Contact();
        contact.setfName(mFirstName.getText().toString());
        contact.setlName(mLastName.getText().toString());
        contact.setTelNr(mTelNr.getText().toString());
        contact.setImgPath(mImgPath == null ? "[no_path]" : mImgPath);

        Intent output = new Intent();
        output.putExtra(MainActivity.RESULT_CODE_CONTACT, (Parcelable) contact);
        setResult(RESULT_OK, output);

        mBelievePref = false;

        finish();
    }
}
