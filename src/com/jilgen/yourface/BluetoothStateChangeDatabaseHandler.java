package com.jilgen.yourface;

import android.content.Context;
import android.util.Log;

public class BluetoothStateChangeDatabaseHandler extends StateChangeDatabaseHandler {

    final static String TAG = "CF_BluetoothChangeDB";

	private static final int DATABASE_VERSION = 1;
	private static final String DATABASE_NAME = "statesManager";
	public static final String TABLE_STATES = "bluetoothStateChange";

	public BluetoothStateChangeDatabaseHandler(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
        Log.d(TAG, DATABASE_NAME + " " + DATABASE_VERSION);
	}

}
