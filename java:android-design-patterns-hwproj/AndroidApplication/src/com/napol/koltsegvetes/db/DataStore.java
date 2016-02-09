package com.napol.koltsegvetes.db;

import android.content.Context;

import com.napol.koltsegvetes.dbdriver.ISQLiteHelper;
import com.napol.koltsegvetes.dbdriver.MySQLiteHelper;

/**
 * @author Polcz Péter <ppolcz@gmail.com>
 * 
 *         Created on Oct 13, 2014 7:25:59 PM
 */
public class DataStore extends AbstractDataStore {
	private static DataStore INSTANCE = null;

	private boolean firstTime;
	
	public static synchronized DataStore instance(boolean firstTime) {
		if (INSTANCE == null)
			INSTANCE = new DataStore();
		INSTANCE.firstTime = firstTime;
		return INSTANCE;
	}

	public static synchronized DataStore instance() {
		return instance(false);
	}

	/**
	 * Should not be synchronized because it will be called inside a
	 * synchronized block
	 */
	@Override
	protected ISQLiteHelper getHelperInstance() {
		return MySQLiteHelper.instance();
	}

	public void setContext(Context context) {
		MySQLiteHelper.instance().setContext(context);
	}

	@Override
	protected boolean isCreated() {
		boolean ret = !firstTime;
		firstTime = false;
		return ret;
	}
}
