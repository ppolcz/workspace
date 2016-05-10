package com.polpe.panogl.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class PanoDBHelper extends SQLiteOpenHelper {
    private static PanoDBHelper instance = null;
    
    private static final String TAG = "PanoDBHelper";

    private static final String DATABASE_NAME = "polpePanoGL.db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_PANORAMAS = "panoramas";
    public static final String COL_P_ID = "p_id";
    public static final String COL_P_NAME = "p_name";
    public static final String COL_P_DIRNAME = "p_dirname";
    public static final String COL_P_DATE = "p_date";

    public static final String TABLE_IMAGES = "images";
    public static final String COL_I_FILENAME = "i_filename";
    public static final String COL_I_PANORAMA = "i_panorama";
    public static final String COL_I_COORDINATES = "i_coordinates";
    
    private static final String DATABASE_CREATE_PANORAMAS = "create table " + TABLE_PANORAMAS + "("
            + COL_P_ID + " integer primary key autoincrement, "
            + COL_P_NAME + " text not null, "
            + COL_P_DIRNAME + " text not null, "
            + COL_P_DATE + " text not null);";

    private static final String DATABASE_CREATE_IMAGES = "create table " + TABLE_IMAGES + "("
            + COL_I_FILENAME + " text not null, "
            + COL_I_PANORAMA + " integer,"
            + COL_I_COORDINATES + " real,"
            + "FOREIGN KEY(" + COL_I_PANORAMA + ") REFERENCES " + TABLE_PANORAMAS + "(" + COL_P_ID + "),"
            + "PRIMARY KEY(" + COL_I_FILENAME + ","  + COL_I_PANORAMA + ") );";            
    
    // Visibility : only at package level
    // Class level lock : be not able to be called concurrently
    static synchronized PanoDBHelper getInstace (Context context) {
        if (instance == null) {
            instance = new PanoDBHelper(context.getApplicationContext());
        }
        return instance;
    }
    
    private PanoDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate (SQLiteDatabase db) {
        db.execSQL(DATABASE_CREATE_PANORAMAS);
        db.execSQL(DATABASE_CREATE_IMAGES);
    }

    @Override
    public void onUpgrade (SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(TAG, "Upgrading database from version " + oldVersion + " to " + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PANORAMAS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_IMAGES);
        onCreate(db);
    }
}
