package polpe.hf2_chackable;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;

import android.util.Log;

public class ContactList {

	String filename = "res/assets/contacts.ser";

	private class ContactItem implements Serializable {
		private static final long serialVersionUID = 1L;

		private String name;
		private String telnr;

		public ContactItem(String name, String telnr) {
			this.name = name;
			this.telnr = telnr;
		}
	}

	ArrayList<ContactItem> contactList;

	public ContactList() {
		contactList = new ArrayList<ContactList.ContactItem>();

		ContactItem item = null;
		try {
			FileInputStream fileIn = new FileInputStream("/tmp/employee.ser");
			ObjectInputStream in = new ObjectInputStream(fileIn);
			item = (ContactItem) in.readObject();
			in.close();
			fileIn.close();
		} catch(IOException i) {
			i.printStackTrace();
			return;
		} catch(ClassNotFoundException c) {
			System.out.println("Employee class not found");
			c.printStackTrace();
			return;
		}
		
		contactList.add(item);
	}

	public void addContact(String name, String telnr) {
		ContactItem item = new ContactItem(name, telnr);
		try {
			FileOutputStream fileOut = new FileOutputStream(filename);
			ObjectOutputStream out = new ObjectOutputStream(fileOut);
			out.writeObject(item);
			out.close();
			fileOut.close();
		} catch(IOException e) {
			e.printStackTrace();
		}
	}	

	public void removeContact(String name, String telnr) {
		Log.e("polpe.hf2", "Not implemented yet!");
	}
	
}
