package com.jilgen.yourface;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class WifiStateChangeDatabaseHandler extends StateChangeDatabaseHandler {

    final static String TAG = "CF_WifiChangeDB";

	private static final int DATABASE_VERSION = 1;
	private static final String DATABASE_NAME = "statesManager";
	public static final String TABLE_STATES = "wifiStateChange";

	public WifiStateChangeDatabaseHandler(Context context) {
		super(context,DATABASE_NAME , null, DATABASE_VERSION);
	}

}
