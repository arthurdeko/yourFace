package com.jilgen.yourface;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.format.Time;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public abstract class StateChangeDatabaseHandler extends SQLiteOpenHelper {

    final static String TAG = "CF_StatsDB";

	private static final int DATABASE_VERSION = 3;
	private static final String DATABASE_NAME = "statesManager";
	public static final String TABLE_STATES = "stateChange";
	private static final String KEY_ID = "_id";
	public static final String KEY_TIME = "time";
    public static final String KEY_STATE = "state";
    public static final String KEY_TIME_FORMATTED = "formattedTime";

	public StateChangeDatabaseHandler(Context context, String databaseName, SQLiteDatabase.CursorFactory factory, int version ) {
		super(context,DATABASE_NAME , null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String CREATE_STATS_TABLE="CREATE TABLE " + TABLE_STATES + "("
				+ KEY_ID + " INTEGER PRIMARY KEY,"
                + KEY_STATE + " INTEGER,"
				+ KEY_TIME + " INTEGER)";
		db.execSQL(CREATE_STATS_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		if ( oldVersion != newVersion ) {
			db.execSQL("DROP TABLE IF EXISTS "+ TABLE_STATES);
			onCreate(db);
		}
	}	
	
	public void addStateChange( int state ) {
		SQLiteDatabase db = this.getWritableDatabase();
		
		long timeMillis = System.currentTimeMillis() / 1000;
		double timeSec=Math.floor(timeMillis);
		
		ContentValues values = new ContentValues();
		values.put(KEY_TIME, timeSec);
        values.put(KEY_STATE, state);
		
		db.insert(TABLE_STATES, null, values);
		db.close();
	}
	
	public void initialize() {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_STATES, null, null);
		db.close();
	}

    public Cursor getAllRecordsForView() {
		String selectQuery = "SELECT "
				+ KEY_ID + "," 
				+ KEY_TIME + ", datetime(" + KEY_TIME + ", 'unixepoch') as " + KEY_TIME_FORMATTED + "," 
				+ " FROM " + TABLE_STATES;
		
		SQLiteDatabase db = this.getWritableDatabase();
		return db.rawQuery(selectQuery, null);
	}

    public Cursor getByTimeDelta( long timeDelta ) {
        Time now = new Time();
        now.setToNow();
        long nowSeconds = now.toMillis(false) / 1000;

        long startTime = nowSeconds - timeDelta;
        String selectQuery = "SELECT "
                + KEY_STATE + ","
                + KEY_TIME
                + " FROM " + TABLE_STATES
                + " WHERE " + KEY_TIME + " > " + startTime;

        Log.d(TAG, "getByTimeDelta: "+selectQuery );

        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery(selectQuery, null);

    }

}
