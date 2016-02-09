package com.polpe.panogl.db;

import java.text.SimpleDateFormat;

import android.annotation.SuppressLint;
import android.os.Parcel;
import android.os.Parcelable;

@SuppressLint("SimpleDateFormat")
public class PanoItem implements Parcelable {
    final static String DATE_FORMAT = "yyyy-MM-dd HH:mm";

    long id = -1;
    String name;
    String dirname;
    String date;

    public PanoItem() {
        date = new SimpleDateFormat(DATE_FORMAT).format(new java.util.Date());
    }

    public PanoItem(String name, String dirname) {
        this.name = name;
        this.dirname = dirname;

        date = new SimpleDateFormat(DATE_FORMAT).format(new java.util.Date());
    }

    public PanoItem(Parcel in) {
        this.id = in.readLong();
        this.name = in.readString();
        this.dirname = in.readString();
        this.date = in.readString();
    }

    public static final Parcelable.Creator<PanoItem> CREATOR = new Parcelable.Creator<PanoItem>() {
        public PanoItem createFromParcel (Parcel in) {
            return new PanoItem(in);
        }

        public PanoItem[] newArray (int size) {
            return new PanoItem[size];
        }
    };

    @Override
    public int describeContents () {
        return 0;
    }

    @Override
    public void writeToParcel (Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(name);
        dest.writeString(dirname);
        dest.writeString(date);
    }

    // GETTERS - SETTERS

    public String getName () {
        return name;
    }

    public String getDirname () {
        return dirname;
    }

    public void setName (String name) {
        this.name = name;
    }

    public void setDirname (String dirname) {
        this.dirname = dirname;
    }
    
    public void setDateToNow () {
        date = new SimpleDateFormat(DATE_FORMAT).format(new java.util.Date());
    }

    public String getDate () {
        return date;
    }

    public long getId () {
        return id;
    }
}
