package com.jilgen.yourface;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.text.format.Time;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class StatDatabaseHandler extends SQLiteOpenHelper {

    final static String TAG = "CF_StatsDB";

    private String tableName;

	private static final int DATABASE_VERSION = 7;
	private static final String DATABASE_NAME = "statManager";
	public static final String TABLE_STATS = "stats";
	private static final String KEY_ID = "_id";
	public static final String KEY_TIME = "time";
    public static final String KEY_VALUE = "value";
    public static final String KEY_TYPE = "type";
    public static final String KEY_TIME_FORMATTED = "formattedTime";
    public static final String KEY_MIN = "min";
    public static final String MIN = "min("+KEY_VALUE+") as "+KEY_MIN;
    public static final String KEY_MAX = "max";
    public static final String MAX = "max("+KEY_VALUE+") as "+KEY_MAX;
    public static final String KEY_DELTA = "delta";
    public static final String DELTA = "max("+KEY_VALUE+") - min("+KEY_VALUE+") as "+KEY_DELTA;

	public StatDatabaseHandler(Context context ) {
		super(context, DATABASE_NAME , null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String CREATE_STATS_TABLE="CREATE TABLE " + TABLE_STATS + "("
				+ KEY_ID + " INTEGER PRIMARY KEY,"
                + KEY_VALUE + " INTEGER,"
                + KEY_TYPE + " TEXT, "
				+ KEY_TIME + " INTEGER )";
		db.execSQL(CREATE_STATS_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		if ( oldVersion != newVersion ) {
			db.execSQL("DROP TABLE IF EXISTS "+ TABLE_STATS);
			onCreate(db);
		}
	}

    public ArrayList<Bundle> getAdjustedValues( long startDate, String type ) {

        ArrayList<Bundle> values = new ArrayList<Bundle>();

        int min = this.getMinByStartDate( startDate, type );
        int delta = this.getDeltaByStartDate( startDate, type );
        Cursor results = this.getByStartTime( startDate, type );

        if ( results != null ) {
            int timeIndex = results.getColumnIndex(KEY_TIME);
            int valueIndex = results.getColumnIndex(KEY_VALUE);

            long lastTime = 0;
            float lastIntensity = -1;

            while ( results.moveToNext() ) {
                Bundle dataPoint = new Bundle();

                long time = results.getLong(timeIndex);
                float value = (float)results.getInt(valueIndex);
                float intensity;


                if ( delta == 0 ) {
                    intensity = 0;
                } else {
                    intensity = ( value - min ) / delta * 100;
                }

                Log.d( TAG, "Intensity "+Math.floor(intensity));

                if ( lastTime == 0 ) {
                    lastTime = time;
                }

                if ( lastIntensity == -1 ) {
                    lastIntensity = intensity;
                }

                long duration = time - lastTime;
                float dx = intensity - lastIntensity;
                float rateOfChange = 0;

                if ( duration != 0 ) {
                    rateOfChange = Math.abs( dx / duration );
                }

                dataPoint.putLong( "time", time );
                dataPoint.putInt( "duration", (int)duration );
                dataPoint.putInt( "intensity", (int)Math.floor(intensity) );
                dataPoint.putFloat( "rateOfChange", rateOfChange );
                values.add(dataPoint);
            }
        }

        return values;
    }

    public int getMinByStartDate( long startDate, String type ) {

        startDate /= 1000;

        String selectQuery = "SELECT "
                + MIN
                + " FROM " + TABLE_STATS
                + " WHERE " + KEY_TIME +" > ? "
                + " AND " + KEY_TYPE +" = ? ";

        SQLiteDatabase db = this.getWritableDatabase();
        String[] args = { Long.toString(startDate), type };
        Cursor results = db.rawQuery( selectQuery, args );

        int min = 0;
        if ( results != null ) {
            results.moveToFirst();
            min = results.getInt(results.getColumnIndex(KEY_MIN));
        }

        return min;

    }

    public int getDeltaByStartDate( long startDate, String type ) {

        startDate /= 1000;

        String selectQuery = "SELECT "
                + DELTA
                + " FROM " + TABLE_STATS
                + " WHERE " + KEY_TIME +" > ? "
                + " AND " + KEY_TYPE +" = ? ";

        SQLiteDatabase db = this.getWritableDatabase();
        String[] args = { Long.toString(startDate), type };
        Cursor results = db.rawQuery( selectQuery, args );

        int delta = 0;
        if ( results != null ) {
            results.moveToFirst();
            delta = results.getInt(results.getColumnIndex(KEY_DELTA));
        }

        return delta;

    }

    public void addStatValue( String stat, String type ) {
        SQLiteDatabase db = this.getWritableDatabase();

        long timeMillis = System.currentTimeMillis() / 1000;
        double timeSec=Math.floor(timeMillis);

        ContentValues values = new ContentValues();
        values.put( KEY_TIME, timeSec );
        values.put( KEY_VALUE, stat );
        values.put( KEY_TYPE, type );

        db.insert(TABLE_STATS, null, values);
        db.close();
    }
	
	public void initialize() {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_STATS, null, null);
		db.close();
	}

    public Cursor getAllRecordsForView() {
		String selectQuery = "SELECT "
				+ KEY_ID + ","
                + KEY_VALUE + ","
				+ KEY_TIME + ", datetime(" + KEY_TIME + ", 'unixepoch') as " + KEY_TIME_FORMATTED + "," 
				+ " FROM " + TABLE_STATS;
		
		SQLiteDatabase db = this.getWritableDatabase();

		return db.rawQuery(selectQuery, null);
	}

    public Cursor getByStartTime( long startTime, String type ) {

        startTime /= 1000;

        String selectQuery = "SELECT "
                + KEY_VALUE + ","
                + KEY_TIME
                + " FROM " + TABLE_STATS
                + " WHERE " + KEY_TIME +" > ? "
                + " AND " + KEY_TYPE +" = ? ";

        Log.d(TAG, "getByStartTime: "+selectQuery+" "+startTime+" "+type);

        SQLiteDatabase db = this.getWritableDatabase();

        String[] args = { Long.toString(startTime), type };
        return db.rawQuery( selectQuery, args );

    }

}
