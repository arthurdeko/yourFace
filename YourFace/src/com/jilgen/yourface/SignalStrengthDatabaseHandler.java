package com.jilgen.yourface;

import android.content.Context;

public class SignalStrengthDatabaseHandler extends StatDatabaseHandler {

    final static String TAG = "CF_SignalStrengthDB";

	private static final int DATABASE_VERSION = 3;
	public static final String TABLE_STATS = "signalStrength";

	public SignalStrengthDatabaseHandler(Context context) {
		super( context );
	}

}
