package polpe.hf2;

import android.os.Parcel;
import android.os.Parcelable;

public final class Contact implements Parcelable {

	private long id;
	private String fName;
	private String lName;
	private String telNr;
	private String imgPath;

	public Contact() { }

	public String getfName() {
		return fName;
	}
	public void setfName(String fName) {
		this.fName = fName;
	}
	public String getlName() {
		return lName;
	}
	public void setlName(String lName) {
		this.lName = lName;
	}
	public String getTelNr() {
		return telNr;
	}
	public void setTelNr(String telNr) {
		this.telNr = telNr;
	}
	public String getImgPath() {
		return imgPath;
	}
	public void setImgPath(String imgPath) {
		this.imgPath = imgPath;
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel out, int flags) {
		out.writeLong(id);
		out.writeString(fName);
		out.writeString(lName);
		out.writeString(telNr);
		out.writeString(imgPath);
	}

	public static final Parcelable.Creator<Contact> CREATOR = new Parcelable.Creator<Contact>() {
		public Contact createFromParcel(Parcel in) {
			return new Contact(in);
		}

		public Contact[] newArray(int size) {
			return new Contact[size];
		}
	};

	private Contact(Parcel in) {
		id = in.readLong();
		fName = in.readString();
		lName = in.readString();
		telNr = in.readString();
		imgPath = in.readString();
	}

}
