package com.polpe.panogl.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import com.polpe.panogl.main.gallery.GalleryListActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

public class Util {
    private static String TAG = "panogl.Util";

    public static void debug (String format) {
        Log.d(GalleryListActivity.TAG, getCodeLocation(4) + " - " + format);
    }

    public static void debug (String format, Throwable ex) {
        Log.d(GalleryListActivity.TAG, getCodeLocation(4) + " - " + format, ex);
    }

    public static void debug (String format, Object ... args) {
        Log.d(GalleryListActivity.TAG, getCodeLocation(4) + " - " + String.format(format, args));
    }

    public static void debug () {
        Log.d(GalleryListActivity.TAG, getCodeLocation(4));
    }

    @SuppressWarnings("unused")
    private static void myDebug () {
        myDebug("");
    }

    private static void myDebug (String format) {
        Log.d(TAG, getCodeLocation(4) + " - " + format);
    }

    public static String getCodeLocation (int depth) {
        return "in " + Thread.currentThread().getStackTrace()[depth].getMethodName() + ":" + Thread.currentThread().getStackTrace()[depth].getLineNumber();
    }

    public static void assertation (boolean condition) {
        if (!condition) throw new AssertionError();
    }

    public boolean mkdirIfNotExists (String dirname) {
        File file = new File(dirname);
        if (!file.exists()) {
            file.mkdir();
            return true;
        } else {
            return false;
        }
    }

    public static String createNonExistentFileWithPrefix (String dirname, String prefix, String extension) {
        extension = "." + extension;
        File file = new File(dirname, prefix + extension);
        for (int i = 1; file.exists(); ++i, file = new File(dirname, prefix + i + extension));
        try {
            file.createNewFile();
        } catch (IOException e) {
            // Util.debug("Nem tudtam letrehozni a mappat mert: ", e);
            Log.e(TAG, "Nem tudtam letrehozni a mappat mert: ", e);
        }
        return file.toString();
    }

    public static String createNonExistentDir (String dirname) {
        File file = new File(dirname);
        for (int i = 1; file.exists(); ++i, file = new File(dirname + i));
        file.mkdirs();
        return file.toString();
    }

    public static void deleteRecursively (File fileOrDirectory) {
        if (fileOrDirectory.isDirectory())
            for (File child : fileOrDirectory.listFiles())
                deleteRecursively(child);

        fileOrDirectory.delete();
    }

    public static void saveBitmapToFile (Bitmap bmp, String filename) {
        File file = new File(filename);
        try {
            FileOutputStream fOut = new FileOutputStream(file);

            bmp.compress(Bitmap.CompressFormat.PNG, 85, fOut);
            fOut.flush();
            fOut.close();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unused")
    private void loadBitmap (String filename) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        Bitmap bmp = BitmapFactory.decodeFile(filename, options);
    }

    static public void showAlert (Context context, String title, String text, DialogInterface.OnClickListener listener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(text);
        if (title != null) {
            builder.setTitle(title);
        }

        builder.setPositiveButton("OK", listener);

        AlertDialog alert = builder.create();
        alert.show();
    }
}
