package com.jilgen.yourface;

import android.content.Context;

public class GPSStateChangeDatabaseHandler extends StateChangeDatabaseHandler {

    final static String TAG = "CF_WifiChangeDB";

	private static final int DATABASE_VERSION = 1;
	private static final String DATABASE_NAME = "statesManager";
	public static final String TABLE_STATES = "GPSStateChange";

	public GPSStateChangeDatabaseHandler(Context context) {
		super(context,DATABASE_NAME , null, DATABASE_VERSION);
	}

}
