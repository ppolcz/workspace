package polpe.hf2_chackable;


import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Polcz Peter - KUE5RC
 */
public class MainActivity extends Activity {
	//	static final String RESULT_CODE_FNAME = "_lname";
	//	static final String RESULT_CODE_LNAME = "_fname";
	//	static final String RESULT_CODE_TELNR = "_telnr";
	//	static final String RESULT_CODE_IMGPATH = "_imgpath";
	static final String RESULT_CODE_CONTACT = "_contact";
	//	static final String RESULT_CODE_IMG_URI = "_uri";

	//	private static final int PICK_CONTACT_REQUEST = 123;
	private static final int REQUEST_CONTACT = 124;
	private static final int REQUEST_PICTURE = 125;

	private String selectedImagePath;
	private String filemanagerstring;
	private ImageView imageView1;

	private String tag = "polpe.hf2.MainActivity";
	private ContactListDataSource datasource;

	private ArrayAdapter<MyListItem> adapter;
	private ListView listView;
	//	private List<Integer> checkedItemPosition;

	/* =========================================== Override [BEGIN] ========================================== */

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.d(tag, "onCreate()");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		//		checkedItemPosition = new ArrayList<Integer>();

		listView = (ListView) findViewById(R.id.list_view);

		datasource = new ContactListDataSource(this);
		datasource.open();

		List<Contact> contactlist = datasource.getAllContacts();

		ArrayList<MyListItem> elemek = new ArrayList<MyListItem>();

		for (Contact contact : contactlist) {
			elemek.add(new MyListItem(contact).setPic(contact.getImgPath()));
		}

		adapter = new AdapterViewHolder(this, R.layout.list_item, elemek);
		listView.setAdapter(adapter);

		listView.setOnItemClickListener(new OnItemClickListener() {

			/**
			 * Consider inserting ` android:descendantFocusability="blocksDescendants" `
			 * This needs because the item_layout contains at least one widget that is listening on click events.
			 */

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				//				Log.d(tag, "OnItemClickListener");
				//				AlertDialog.Builder adb=new AlertDialog.Builder(MainActivity.this);
				//				adb.setTitle("Delete?");
				//				adb.setMessage("Are you sure you want to delete " + position);
				//				final int positionToRemove = position;
				//				adb.setNegativeButton("Cancel", null);
				//				adb.setPositiveButton("Ok", new AlertDialog.OnClickListener() {
				//					public void onClick(DialogInterface dialog, int which) {
				//						adapter.remove(adapter.getItem(positionToRemove));
				//						adapter.notifyDataSetChanged();
				//					}});
				//				adb.show();
			}
		});

		listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		listView.setItemsCanFocus(false);
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

		//		for (int position : checkedItemPosition) {
		//			adapter.getItem(position).cb.setChecked(true);
		//		}
		//		
		super.onResume();
	}

	@Override
	protected void onPause() {
		Log.d(tag, "onPause()");
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

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.menu_new:
			Intent intent = new Intent(this, FormActivity.class);
			startActivityForResult(intent, MainActivity.REQUEST_CONTACT);
			return true;

		case R.id.menu_remove:


			try {
				Log.d(tag, String.valueOf(Thread.currentThread().getStackTrace()[2].getLineNumber()));
				SparseBooleanArray valami = listView.getCheckedItemPositions();



				Log.d(tag, String.valueOf(Thread.currentThread().getStackTrace()[2].getLineNumber()));
				String kmadkamsdk = "";
				valami.size();
				Log.d(tag, String.valueOf(Thread.currentThread().getStackTrace()[2].getLineNumber()));

				Log.d(tag, String.format("valami.size = %d", valami.size()));

				for (int i = 0; i < valami.size(); ++i) {
					kmadkamsdk += String.valueOf(valami.get(i));
				}
				Log.d(tag, String.valueOf(Thread.currentThread().getStackTrace()[2].getLineNumber()));

				Log.d(tag, kmadkamsdk);
			} catch (NullPointerException e) {
			}

			for (int i = 0; i < adapter.getCount(); ++i) {
				MyListItem listItem = adapter.getItem(i);
				if (listItem.cb.isChecked()) {
					Log.d(tag, "deletion");
					datasource.deleteContact(listItem.id);
					adapter.remove(listItem);
				}
			}

			adapter.notifyDataSetChanged();

			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	/**
	 * @brief HANDLING_RESULTS
	 * 
	 * Handling results sent by child activities identifying them by their request code.
	 * {@link MainActivity}.REQUEST_CODE stands for the FormLayout's intent.
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		// If child activity has sent data
		if (resultCode == RESULT_OK && data != null) {

			switch (requestCode) {

			// handling data sent by FormActivity
			case MainActivity.REQUEST_CONTACT : {

				// retrieving parcelable object
				Contact contact = data.getParcelableExtra(MainActivity.RESULT_CODE_CONTACT);

				// adding it to the adaptor
				adapter.add(new MyListItem(contact).setPic(contact.getImgPath()));

				// notifying the adaptors to update its content
				adapter.notifyDataSetChanged();
				break;
			}

			// handling data sent by browse pictures dialog
			case MainActivity.REQUEST_PICTURE : {

				Uri selectedImageUri = data.getData();

				//OI FILE Manager
				filemanagerstring = selectedImageUri.getPath();

				//MEDIA GALLERY
				selectedImagePath = getPath(selectedImageUri);
				//just to display the imagepath
				Toast.makeText(this.getApplicationContext(), selectedImagePath, Toast.LENGTH_SHORT).show();
				//change imageView1
				imageView1.setImageURI(selectedImageUri);

			}

			}
		}
		// calling the overrided member of the superclass
		super.onActivityResult(requestCode, resultCode, data);
	}

	public String getPath(Uri uri) {
		String[] projection = { MediaStore.Images.Media.DATA };
		Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
		if(cursor!=null)
		{
			//HERE YOU WILL GET A NULLPOINTER IF CURSOR IS NULL
			//THIS CAN BE, IF YOU USED OI FILE MANAGER FOR PICKING THE MEDIA
			int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
			cursor.moveToFirst();
			return cursor.getString(column_index);
		}
		else return null;
	}

	/* =========================================== Override [END] =========================================== */


	/* =========================================== MyListItem [BEGIN] =========================================== */

	/**
	 * Majd a jovoben igy: extends RelativeLayout implements Chackable
	 *
	 */
	private class MyListItem {
		private long id;
		private Drawable pic;
		private String name;
		private String telnr;
		private CheckBox cb;

		public MyListItem (Contact contact) {
			this.id = contact.getId();
			this.pic = null;
			this.name = contact.getfName() + " " + contact.getlName(); 
			this.telnr = contact.getTelNr();
			this.cb = new CheckBox(MainActivity.this);
			this.cb.setChecked(false);
		}

		public MyListItem setPic (String imagePath) { 

			/**
			 * if: imagePath not initialized or file not exists
			 *  └── the default 'noface' image will be chosen
			 * else: the file exists
			 *  └── creating a drawable from data laying under image path
			 */

			try {
				File file = new File(imagePath);
				if (!file.exists()) throw new RuntimeException();

				setPic(Drawable.createFromPath(imagePath));
			} catch (RuntimeException e) {
				setPic(getResources().getDrawable(R.drawable.noface));
			}

			return this;
		}

		public MyListItem setPic (Drawable pic) {
			this.pic = pic;
			return this;
		}

		@SuppressWarnings("unused")
		public MyListItem setChecked (boolean checked) {
			this.cb.setChecked(checked);
			return this;
		}

	}

	private static class ViewHolder {
		private ImageView iw;
		private TextView tw_name;
		private TextView tw_telnr;
		private CheckBox cb;
	}

	/* =========================================== MyListItem [END] =========================================== */



	// http://developer.android.com/training/improving-layouts/smooth-scrolling.html
	private class AdapterViewHolder extends ArrayAdapter<MyListItem> {
		private ArrayList<MyListItem> items;

		public AdapterViewHolder(Context context, int textViewResourceId, ArrayList<MyListItem> elemek) {
			super(context, textViewResourceId, elemek);
			items = elemek;
		}

		@Override
		public void add(MyListItem object) {
			super.add(object);
		}

		@Override
		public MyListItem getItem(int position) {
			return super.getItem(position);
		}

		@Override
		public int getCount() {
			return super.getCount();
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {

			ViewHolder vh = null;
			if (convertView == null) {

				LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = vi.inflate(R.layout.list_item, null);

				vh = new ViewHolder();
				vh.iw = (ImageView) convertView.findViewById(R.id.iw);
				vh.tw_name = (TextView) convertView.findViewById(R.id.tw_name);
				vh.tw_telnr = (TextView) convertView.findViewById(R.id.tw_telnr);
				//				vh.cb = (CheckBox) convertView.findViewById(R.id.chbox);

				convertView.setTag(vh);
			} else {
				// ez az ami osszes-12 szer fut le

				vh = (ViewHolder) convertView.getTag();
			}

			// ez az ami MINDIG lefut amikor scrollozunk

			// valodi tartalom atadasa
			vh.tw_name.setText(items.get(position).name.toString());
			vh.tw_telnr.setText(items.get(position).telnr.toString());
			vh.iw.setImageDrawable(items.get(position).pic);
			//			vh.cb.setChecked(items.get(position).cb.isChecked());

			//			vh.cb.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			//
			//				@Override
			//				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			//					if (isChecked) MainActivity.this.checkedItemPosition.add(position);
			//					else MainActivity.this.checkedItemPosition.remove((Integer) position);
			//					
			//					adapter.getItem(position).cb.setChecked(isChecked);
			//					MainActivity.this.adapter.notifyDataSetChanged();
			//					
			//					Log.d(tag, "checked" + String.valueOf(position) + "size_of_checkedItemPosition = " + String.valueOf(MainActivity.this.checkedItemPosition.size()));
			//				}
			//			});

			return convertView;
		}
	}
}

//Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//startActivityForResult(i, 123);

//Intent fileIntent = new Intent(Intent.ACTION_GET_CONTENT);
//fileIntent.setType("image/*"); // intent type to filter application based on your requirement
//startActivityForResult(fileIntent, 123);	            

//// When the user center presses, let them pick a contact.
//startActivityForResult(
//    new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI),
//    PICK_CONTACT_REQUEST);


//public MyListItem (Drawable pic, String name, String telnr, boolean b) {
//this.id = 0;
//this.pic = pic;
//this.name = name; 
//this.telnr = telnr;
//this.cb = new CheckBox(MainActivity.this);
//this.cb.setChecked(b);
//}


//
//elemek.add(new MyListItem(getResources().getDrawable(R.drawable.polpe), "Polcz Peter", "+36(20)667-43-23", false));
//elemek.add(new MyListItem(getResources().getDrawable(R.drawable.polpe), "Polcz Peter", "+36(20)667-43-23", false));
//elemek.add(new MyListItem(getResources().getDrawable(R.drawable.ic_launcher), "Polcz Peter", "+36(20)667-43-23", false));
//elemek.add(new MyListItem(getResources().getDrawable(R.drawable.polpe), "Polcz Peter", "+36(20)667-43-23", false));
//elemek.add(new MyListItem(getResources().getDrawable(R.drawable.polpe), "Polcz Peter", "+36(20)667-43-23", false));
//elemek.add(new MyListItem(getResources().getDrawable(R.drawable.polpe), "Polcz Peter", "+36(20)667-43-23", false));
//elemek.add(new MyListItem(getResources().getDrawable(R.drawable.polpe), "Polcz Peter", "+36(20)667-43-23", false));
//elemek.add(new MyListItem(getResources().getDrawable(R.drawable.ic_launcher), "Polcz Peter", "+36(20)667-43-23", false));
//elemek.add(new MyListItem(getResources().getDrawable(R.drawable.polpe), "Polcz Peter", "+36(20)667-43-23", false));
//elemek.add(new MyListItem(getResources().getDrawable(R.drawable.polpe), "Polcz Peter", "+36(20)667-43-23", false));
//elemek.add(new MyListItem(getResources().getDrawable(R.drawable.polpe), "Polcz Peter", "+36(20)667-43-23", false));
//elemek.add(new MyListItem(getResources().getDrawable(R.drawable.polpe), "Polcz Peter", "+36(20)667-43-23", false));
//elemek.add(new MyListItem(getResources().getDrawable(R.drawable.polpe), "Polcz Peter", "+36(20)667-43-23", false));
//elemek.add(new MyListItem(getResources().getDrawable(R.drawable.ic_launcher), "Polcz Peter", "+36(20)667-43-23", false));
//elemek.add(new MyListItem(getResources().getDrawable(R.drawable.polpe), "Polcz Peter", "+36(20)667-43-23", false));
//elemek.add(new MyListItem(getResources().getDrawable(R.drawable.polpe), "Polcz Peter", "+36(20)667-43-23", false));
//elemek.add(new MyListItem(getResources().getDrawable(R.drawable.polpe), "Polcz Peter", "+36(20)667-43-23", false));
//elemek.add(new MyListItem(getResources().getDrawable(R.drawable.polpe), "Polcz Peter", "+36(20)667-43-23", false));
//elemek.add(new MyListItem(getResources().getDrawable(R.drawable.ic_launcher), "Polcz Peter", "+36(20)667-43-23", false));
//elemek.add(new MyListItem(getResources().getDrawable(R.drawable.polpe), "Polcz Peter", "+36(20)667-43-23", false));
//elemek.add(new MyListItem(getResources().getDrawable(R.drawable.polpe), "Polcz Peter", "+36(20)667-43-23", false));
//elemek.add(new MyListItem(getResources().getDrawable(R.drawable.polpe), "Polcz Peter", "+36(20)667-43-23", false));
//elemek.add(new MyListItem(getResources().getDrawable(R.drawable.polpe), "Polcz Peter", "+36(20)667-43-23", false));
//elemek.add(new MyListItem(getResources().getDrawable(R.drawable.polpe), "Polcz Peter", "+36(20)667-43-23", false));
//elemek.add(new MyListItem(getResources().getDrawable(R.drawable.polpe), "Polcz Peter", "+36(20)667-43-23", false));
//elemek.add(new MyListItem(getResources().getDrawable(R.drawable.polpe), "Polcz Peter", "+36(20)667-43-23", false));

//Log.d(tag, "onCreate");
