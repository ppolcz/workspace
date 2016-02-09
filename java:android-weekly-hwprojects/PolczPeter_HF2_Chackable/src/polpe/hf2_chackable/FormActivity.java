package polpe.hf2_chackable;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

public class FormActivity extends Activity {
	private static final int REQUEST_PICTURE = 125;

	private String tag = "polpe.hf2.FormActivity";
	private ContactListDataSource datasource;

	private String mFilemanagerString;
	private String mImgPath;
	private Uri mImgUri;
	private ImageView mImg;
	private EditText mFirstName;
	private EditText mLastName;
	private EditText mTelNr;
	private Button mSave;
	private Button mReplace;


	/**
	 * @brief ON_CREATE --------------------------------------------------------------------------------------------------
	 * @remark Each time the phone is rotated this activity will follow: onPause - onStop - onDestroy - onCreate - onStart - onResume
	 * Because of this fact, each time we have to close the datasource in member onPause
	 * @remark Each time I browse for image the following is produced: onPause - onStop - onActivityResult - onRestart - onStart - onResume
	 * Consequently in member onResult, datasource should be opened
	 * 
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.d(tag, "onCreate()");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.form_layout);

		// creating handles
		mImg = (ImageView) findViewById(R.id.form_iw);
		mFirstName = (EditText) findViewById(R.id.form_fname);
		mLastName = (EditText) findViewById(R.id.form_lname);
		mTelNr = (EditText) findViewById(R.id.form_telnr);
		mSave = (Button) findViewById(R.id.form_save);
		mReplace = (Button) findViewById(R.id.form_replace);
		
		// set mTelNr keypad to the old-fashioned keypad with 12 keys
		mTelNr.setRawInputType(Configuration.KEYBOARD_12KEY);

		// open database
		datasource = new ContactListDataSource(this);
		datasource.open();

		// set on-click listener - to choose a photo
		mImg.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setType("image/*");
				intent.setAction(Intent.ACTION_GET_CONTENT);
				startActivityForResult(intent, REQUEST_PICTURE);
			}
		});

		// clicking on save produces the same as add_contact in form menu
		mSave.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				save();
			}
		});
	}

	@Override
	protected void onStart() {
		Log.d(tag, "onStart()");
		super.onStart();
	}
	
	@Override
	protected void onRestart() {
		Log.d(tag, "onRestart()");
		super.onRestart();
	}
	
	@Override
	protected void onResume() {
		Log.d(tag, "onResume()");
		datasource.open();
		super.onResume();
	}

	@Override
	protected void onPause() {
		Log.d(tag, "onPause()");

		// reopen database because each time the phone is reoriented (from landscape to portrait and vice-versa)
		// the onDestroy() is called but
		datasource.close();
		super.onPause();
	}

	@Override
	protected void onStop() {
		Log.d(tag, "onStop()");
		super.onStop();
	}
	
	@Override
	protected void onDestroy() {
		Log.d(tag, "onDestroy()");
		super.onDestroy();
	}
	
	
	/**
	 * @name ON_ACTIVITY_RESULT --------------------------------------------------------------------------------------------
	 * @brief Getting information from child activities
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK && data != null && requestCode == REQUEST_PICTURE) {
			
			// get image uri from child activity
			mImgUri = data.getData();

			// get image path from IO file manager - I do not know what is this exactly since I have been using Android for 3 weeks.
			mFilemanagerString = mImgUri.getPath();

			// get absolute image path
			mImgPath = getPath(mImgUri);

			// change the content of the image view
			mImg.setImageURI(mImgUri);

			// logging
			Log.d(tag, "onActivityResult()");
			Log.d(tag, "mFilemanagerString = " + mFilemanagerString);
			Log.d(tag, "mImgPath = " + mImgPath);
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	/**
	 * 
	 * @param uri
	 * @return path the uri has been loaded from
	 */
    public String getPath(Uri uri) {
    	
    	// I have no idea what is this needed for - but it works, and I am glad!
        String[] projection = { MediaStore.Images.Media.DATA };
        
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        if(cursor != null) {
            //HERE YOU WILL GET A NULLPOINTER IF CURSOR IS NULL
            //THIS CAN BE, IF YOU USED OI FILE MANAGER FOR PICKING THE MEDIA
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        }
        else return null;
    }

    /**
     * @brief ON_CREATE_OPTIONS_MENU ---------------------------------------------------------------------------
     */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.form_layout_menu, menu);
		return true;
	}

	/**
	 * @brief ON_OPTIONS_ITEM_SELECTED -------------------------------------------------------------------------
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.fmenu_add:
			save();
			return true;

		case R.id.fmenu_help:
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}

		//		adapter.notifyDataSetChanged();
	}

	private void save () {
		Contact contact = new Contact();
		contact.setfName(mFirstName.getText().toString());
		contact.setlName(mLastName.getText().toString());
		contact.setTelNr(mTelNr.getText().toString());
		contact.setImgPath(mImgPath);
		contact = datasource.createContact(contact);

		Intent output = new Intent();
		output.putExtra(MainActivity.RESULT_CODE_CONTACT, (Parcelable) contact);
		setResult(RESULT_OK, output);
		finish();
	}
}

//TextView.OnEditorActionListener exampleListener = new TextView.OnEditorActionListener() {
//
//	@Override
//	public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
//		if (actionId == EditorInfo.IME_NULL && event.getAction() == KeyEvent.ACTION_DOWN) { 
//			example_confirm(); //match this behavior to your 'Send' (or Confirm) button
//		}
//		return true;
//	}
//
//	private void example_confirm() {
//		Toast.makeText(FormActivity.this, "Kutyagumi", Toast.LENGTH_LONG).show();
//	}
//
//};
//
//mFirstName.setOnEditorActionListener(exampleListener);
//mFirstName.setOnEditorActionListener(new OnEditorActionListener() {
//
//	@Override
//	public boolean onEditorAction(TextView v, int keyCode, KeyEvent event) {
//		if ( (event.getAction() == KeyEvent.ACTION_DOWN  ) && (keyCode == KeyEvent.KEYCODE_ENTER) ) {               
//			// hide virtual keyboard
//			InputMethodManager imm = (InputMethodManager)FormActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
//			imm.hideSoftInputFromWindow(FormActivity.this.mFirstName.getWindowToken(), 0);
//			return true;
//		}
//		return false;
//	}
//});