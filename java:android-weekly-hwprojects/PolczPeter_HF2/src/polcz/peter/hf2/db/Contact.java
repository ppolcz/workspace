package polcz.peter.hf2.db;

import java.io.File;

import polcz.peter.hf2.R;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

/**
 * This class represents a raw in the table of my database.
 * This class contains only "primitive" types of concrete data that have to be stored.
 */
public final class Contact implements Parcelable, Comparable<Contact> {

    private long mId; // primary key
    private String mFName; // first name
    private String mLName; // last name
    private String mTelNr; // telephone number
    private String mImgPath; // path to a portrait image

    private String mName = null;
    private Drawable mImg = null;

    public static final Parcelable.Creator<Contact> CREATOR = new Parcelable.Creator<Contact>() {
        public Contact createFromParcel (Parcel in) {
            return new Contact(in);
        }

        public Contact[] newArray (int size) {
            return new Contact[size];
        }
    };

    public Contact() {}

    private Contact(Parcel in) {
        mId = in.readLong();
        mFName = in.readString();
        mLName = in.readString();
        mTelNr = in.readString();
        mImgPath = in.readString();
    }

    public Contact initialize (Context context) {

        /** 
         * if: imagePath not initialized or file not exists
         *  └── the default 'noface.png' image will be chosen
         * else: the file exists
         *  └── creating a drawable from data laying under image path */
        try {
            File file = new File(mImgPath);
            if (!file.exists()) throw new RuntimeException();

            mImg = Drawable.createFromPath(mImgPath);
        } catch (RuntimeException e) {
            mImg = context.getResources().getDrawable(R.drawable.noface);
        } catch (OutOfMemoryError e) {
            Log.e("polcz.peter.hf2.db.Contact", "OUT OF MEMORY");
        }

        // initializing full name
        try {
            mName = mFName + " " + mLName;
        } catch (RuntimeException e) {
            mName = "[no name]";
        }

        return this;
    }

    @Override
    public void writeToParcel (Parcel out, int flags) {
        out.writeLong(mId);
        out.writeString(mFName);
        out.writeString(mLName);
        out.writeString(mTelNr);
        out.writeString(mImgPath);
    }

    @Override
    public int describeContents () {
        return 0;
    }

    @Override
    public int compareTo (Contact another) {
        return (int) (mId - another.getId());
    }

    public String getfName () {
        return mFName;
    }

    public void setfName (String fName) {
        this.mFName = fName;
    }

    public String getlName () {
        return mLName;
    }

    public void setlName (String lName) {
        this.mLName = lName;
    }

    public String getTelNr () {
        return mTelNr;
    }

    public void setTelNr (String telNr) {
        this.mTelNr = telNr;
    }

    public String getImgPath () {
        return mImgPath;
    }

    public void setImgPath (String imgPath) {
        this.mImgPath = imgPath;
    }

    public long getId () {
        return mId;
    }

    public void setId (long id) {
        this.mId = id;
    }

    public Drawable getImg () throws NullPointerException {
        if (mImg == null) throw new NullPointerException("Consider calling Contact.initialize(Context context) before");
        return mImg;
    }

    public String getName () throws NullPointerException {
        if (mName == null) throw new NullPointerException("Consider calling Contact.initialize(Context context) before");
        return mName;
    }
}
