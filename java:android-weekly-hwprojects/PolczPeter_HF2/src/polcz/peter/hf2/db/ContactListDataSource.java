package polcz.peter.hf2.db;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class ContactListDataSource {

    // Database fields
    private SQLiteDatabase mDatabase;
    private ContactListSQLiteHelper mDatabaseHelper;
    private String[] mAllColumns = {
            ContactListSQLiteHelper.COLUMN_ID,
            ContactListSQLiteHelper.COLUMN_FNAME,
            ContactListSQLiteHelper.COLUMN_LNAME,
            ContactListSQLiteHelper.COLUMN_TELNR,
            ContactListSQLiteHelper.COLUMN_IMGPATH
    };

    public ContactListDataSource(Context context) {
        mDatabaseHelper = new ContactListSQLiteHelper(context);
    }

    public void open() throws SQLException {
        mDatabase = mDatabaseHelper.getWritableDatabase();
    }

    public void close() {
        mDatabaseHelper.close();
    }

    public Contact createContact(Contact contact) {
        ContentValues values = new ContentValues();
        values.put(ContactListSQLiteHelper.COLUMN_FNAME, contact.getfName());
        values.put(ContactListSQLiteHelper.COLUMN_LNAME, contact.getfName());
        values.put(ContactListSQLiteHelper.COLUMN_TELNR, contact.getTelNr());
        values.put(ContactListSQLiteHelper.COLUMN_IMGPATH, contact.getImgPath());

        long insertId = mDatabase.insert(ContactListSQLiteHelper.TABLE_CONTACTLIST, null, values);

        contact.setId(insertId);
        return contact;
    }

    public void deleteContact(long id) {
        System.out.println("Comment deleted with id: " + id);
        mDatabase.delete(ContactListSQLiteHelper.TABLE_CONTACTLIST, ContactListSQLiteHelper.COLUMN_ID + " = " + id, null);
    }

    public void deleteContact(Contact contact) {
        long id = contact.getId();
        deleteContact(id);
    }

    public List<Contact> getAllContacts() {
        List<Contact> contactlist = new ArrayList<Contact>();

        Cursor cursor = mDatabase.query(ContactListSQLiteHelper.TABLE_CONTACTLIST, mAllColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            contactlist.add(cursorToContact(cursor));
            cursor.moveToNext();
        }

        cursor.close();
        return contactlist;
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
