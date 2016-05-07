package polcz.peter.hf4.task2;

import java.util.ArrayList;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

public class ArrayListParcelable extends ArrayList<String> implements Parcelable {
    private static final String TAG = ArrayListParcelable.class.getSimpleName();
    private static final long serialVersionUID = 4784490221755008598L;

    public ArrayListParcelable(String str) {
        String[] strings = str.split("\\s+");
        for (String s : strings) {
            add(s);
        }
    }

    @Override
    public int describeContents () {
        return 0;
    }

    @Override
    public void writeToParcel (Parcel dest, int flags) {
        Log.d(TAG, "write size = " + String.valueOf(size()));
        dest.writeInt(size());
        for (int i = 0; i < size(); i++) {
            dest.writeString(get(i));
        }
    }

    private ArrayListParcelable(Parcel in) {
        int size = in.readInt();

        Log.d(TAG, "read size = " + String.valueOf(size));

        for (int i = 0; i < size; ++i) {
            add(in.readString());
        }
    }

    public static final Parcelable.Creator<ArrayListParcelable> CREATOR = new Parcelable.Creator<ArrayListParcelable>() {

        @Override
        public ArrayListParcelable createFromParcel (Parcel in) {
            return new ArrayListParcelable(in);
        }

        public ArrayListParcelable[] newArray (int size) {
            return new ArrayListParcelable[size];
        }

    };

}
