package polcz.peter.hf2.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class ContactListSQLiteHelper extends SQLiteOpenHelper {

	public static final String TABLE_CONTACTLIST = "contactlist";
	public static final String COLUMN_ID = "uniqueid";
	public static final String COLUMN_FNAME = "firstname";
	public static final String COLUMN_LNAME = "lastname";
	public static final String COLUMN_TELNR = "telnumber";
	public static final String COLUMN_IMGPATH = "imgpath";
	
	private static final String DATABASE_NAME = TABLE_CONTACTLIST + ".db";
	private static final int DATABASE_VERSION = 1;

	// Database creation sql statement
	private static final String DATABASE_CREATE = "create table "
			+ TABLE_CONTACTLIST + "(" 
			+ COLUMN_ID + " integer primary key autoincrement, " 
			+ COLUMN_FNAME + " text not null, "
			+ COLUMN_LNAME + " text not null, "
			+ COLUMN_TELNR + " text not null, " 
			+ COLUMN_IMGPATH + " text not null );";
	
	public ContactListSQLiteHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
	
	@Override
	public void onCreate(SQLiteDatabase database) {
		database.execSQL(DATABASE_CREATE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w(ContactListSQLiteHelper.class.getName(),
				"Upgrading database from version " + oldVersion + " to "
						+ newVersion + ", which will destroy all old data");
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACTLIST);
		onCreate(db);
	}
	
}
