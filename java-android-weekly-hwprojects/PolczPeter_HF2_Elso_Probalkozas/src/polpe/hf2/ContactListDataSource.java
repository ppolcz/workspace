package polpe.hf2;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class ContactListDataSource {

	// Database fields
	private SQLiteDatabase database;
	private ContactListSQLiteHelper dbHelper;
	private String[] allColumns = { 
			ContactListSQLiteHelper.COLUMN_ID, 
			ContactListSQLiteHelper.COLUMN_FNAME,
			ContactListSQLiteHelper.COLUMN_LNAME,
			ContactListSQLiteHelper.COLUMN_TELNR,
			ContactListSQLiteHelper.COLUMN_IMGPATH
	};

	public ContactListDataSource(Context context) {
		dbHelper = new ContactListSQLiteHelper(context);
	}
	
	public void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
	}

	public void close() {
		dbHelper.close();
	}

	public Contact createContact(Contact contact) {
		ContentValues values = new ContentValues();
		values.put(ContactListSQLiteHelper.COLUMN_FNAME, contact.getfName());
		values.put(ContactListSQLiteHelper.COLUMN_LNAME, contact.getfName());
		values.put(ContactListSQLiteHelper.COLUMN_TELNR, contact.getTelNr());
		values.put(ContactListSQLiteHelper.COLUMN_IMGPATH, contact.getImgPath());
		
		long insertId = database.insert(ContactListSQLiteHelper.TABLE_CONTACTLIST, null, values);
		
		contact.setId(insertId);
		return contact;
	}

	public void deleteContact(long id) {
		System.out.println("Comment deleted with id: " + id);
		database.delete(ContactListSQLiteHelper.TABLE_CONTACTLIST, ContactListSQLiteHelper.COLUMN_ID + " = " + id, null);
	}
	
	public void deleteContact(Contact contact) {
		long id = contact.getId();
		deleteContact(id);
	}

	public List<Contact> getAllContacts() {
		List<Contact> comments = new ArrayList<Contact>();

		Cursor cursor = database.query(ContactListSQLiteHelper.TABLE_CONTACTLIST, allColumns, null, null, null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Contact comment = cursorToContact(cursor);
			comments.add(comment);
			cursor.moveToNext();
		}
		
		cursor.close();
		return comments;
	}

	private Contact cursorToContact(Cursor cursor) {
		
		Contact contact = new Contact();
		contact.setId(cursor.getLong(0));
		contact.setfName(cursor.getString(1));
		contact.setlName(cursor.getString(2));
		contact.setTelNr(cursor.getString(3));
		contact.setImgPath(cursor.getString(4));

		return contact;
	}
	
}
