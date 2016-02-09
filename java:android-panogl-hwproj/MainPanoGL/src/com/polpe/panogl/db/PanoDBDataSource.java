package com.polpe.panogl.db;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.polpe.panogl.util.Util;

public class PanoDBDataSource {
    private static PanoDBDataSource instance = null;
    
    private static SQLiteDatabase db = null;
    private static PanoDBHelper dbHelper;

    private static final String[] QUERY_PANORAMAS_ALL = {
            PanoDBHelper.COL_P_ID,
            PanoDBHelper.COL_P_NAME,
            PanoDBHelper.COL_P_DIRNAME,
            PanoDBHelper.COL_P_DATE
    };

    // Class level lock - be not able to be called concurrently
    public static synchronized PanoDBDataSource getInstance (Context context) {
        if (instance == null) {
            instance = new PanoDBDataSource(context.getApplicationContext());
        }
        return instance;
    }
    
    // invisible
    private PanoDBDataSource(Context context) {
        dbHelper = PanoDBHelper.getInstace(context);
    }

    // Object level lock, but the object is singleton
    public synchronized void open () throws SQLException {
        if (db == null || !db.isOpen()) db = dbHelper.getWritableDatabase();
    }

    // Object level lock, but the object is singleton
    public synchronized void close () {
        if (isOpen()) dbHelper.close();
    }

    // Object level lock, but the object is singleton
    public synchronized boolean isOpen () {
        return db != null && db.isOpen();
    }

    // Object level lock, but the object is singleton
    public synchronized PanoItem insertPanoTuple (PanoItem item) {
        ContentValues values = new ContentValues();
        values.put(PanoDBHelper.COL_P_NAME, item.name);
        values.put(PanoDBHelper.COL_P_DIRNAME, item.dirname);
        values.put(PanoDBHelper.COL_P_DATE, item.date);

        long id = db.insert(PanoDBHelper.TABLE_PANORAMAS, null, values);
        item.id = id;

        // just for test reasons - whether the column ID is equal with our auto incrementing ID
        // TODO - erase it in release
        Util.assertation(item.name.equals(getPanoramaName(id)));

        return item;
    }

    public void removePanoTuple (long id) {
        db.delete(PanoDBHelper.TABLE_PANORAMAS, PanoDBHelper.COL_P_ID + " = " + id, null);
    }

    public ArrayList<PanoItem> getAllPanoramaTuples () {
        ArrayList<PanoItem> items = new ArrayList<PanoItem>();

        Cursor cursor = db.query(PanoDBHelper.TABLE_PANORAMAS, QUERY_PANORAMAS_ALL, null, null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            items.add(cursorToPanoTuple(cursor));
            cursor.moveToNext();
        }
        cursor.close();
        
        return items;
    }

    // just for testing reasons
    private String getPanoramaName (long id) {
        String name = null;

        Cursor cursor = db.query(PanoDBHelper.TABLE_PANORAMAS, QUERY_PANORAMAS_ALL, PanoDBHelper.COL_P_ID + " = " + id, null, null, null, null);
        cursor.moveToFirst();
        if (!cursor.isAfterLast()) {
            name = cursor.getString(cursor.getColumnIndex(PanoDBHelper.COL_P_NAME));

            // assert - an ID must not appear twice - TODO - erase it in release
            cursor.moveToNext();
            Util.assertation(cursor.isAfterLast());
        }

        cursor.close();
        
        // name must not be empty
        Util.assertation(name != null);
        
        return name;
    }

    // retrieve column values of a particular tuple under the given cursor in the argument
    private PanoItem cursorToPanoTuple (Cursor cursor) {
        PanoItem item = new PanoItem();
        item.id = cursor.getLong(cursor.getColumnIndex(PanoDBHelper.COL_P_ID));
        item.name = cursor.getString(cursor.getColumnIndex(PanoDBHelper.COL_P_NAME));
        item.dirname = cursor.getString(cursor.getColumnIndex(PanoDBHelper.COL_P_DIRNAME));
        item.date = cursor.getString(cursor.getColumnIndex(PanoDBHelper.COL_P_DATE));
        return item;
    }
}
