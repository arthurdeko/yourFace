package com.jilgen.yourface;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class StatsDatabaseHandler extends SQLiteOpenHelper {

    final static String TAG = "CF_StatsDB";

	private static final int DATABASE_VERSION = 4;
	private static final String DATABASE_NAME = "statsManager";
	public static final String TABLE_STATS = "stats";
	private static final String KEY_ID = "_id";
	public static final String KEY_TIME = "time";
	public static final String KEY_TIME_FORMATTED = "formattedTime";
	public static final String KEY_BATTERY_STRENGTH = "batteryStrength";
    public static final String KEY_BATTERY_TEMPERATURE = "batteryTemperature";
    public static final String KEY_BATTERY_VOLTAGE = "batteryVoltage";
	public static final String KEY_SIGNAL_STRENGTH = "signalStrength";
    public static final String KEY_BYTES = "bytes";
	public static final String KEY_PROC_COUNT = "processCount";
    public static final String MAX_BATTERY_STRENGTH = "max("+KEY_BATTERY_STRENGTH+")";
    public static final String MIN_BATTERY_STRENGTH = "min("+KEY_BATTERY_STRENGTH+")";
    public static final String MAX_BATTERY_VOLTAGE = "max("+KEY_BATTERY_VOLTAGE+")";
    public static final String MIN_BATTERY_VOLTAGE = "min("+KEY_BATTERY_VOLTAGE+")";
    public static final String MAX_BATTERY_TEMPERATURE = "max("+KEY_BATTERY_TEMPERATURE+")";
    public static final String MIN_BATTERY_TEMPERATURE = "min("+KEY_BATTERY_TEMPERATURE+")";
    public static final String MAX_PROC_COUNT = "max("+KEY_PROC_COUNT+")";
    public static final String MIN_PROC_COUNT = "min("+KEY_PROC_COUNT+")";
    public static final String MAX_BYTES = "max("+KEY_BYTES+")";
    public static final String MIN_BYTES = "min("+KEY_BYTES+")";
    public static final String DELTA_BATTERY_STRENGTH = MAX_BATTERY_STRENGTH + "-" + MIN_BATTERY_STRENGTH;
    public static final String DELTA_BATTERY_VOLTAGE = MAX_BATTERY_VOLTAGE + "-" + MIN_BATTERY_VOLTAGE;
    public static final String DELTA_BATTERY_TEMPERATURE = MAX_BATTERY_TEMPERATURE + "-" + MIN_BATTERY_TEMPERATURE;
    public static final String DELTA_PROC_COUNT = MAX_PROC_COUNT + "-" + MIN_PROC_COUNT;
    public static final String DELTA_BYTES = MAX_BYTES + "-" + MIN_BYTES;

	public StatsDatabaseHandler(Context context) {
		super(context,DATABASE_NAME , null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String CREATE_STATS_TABLE="CREATE TABLE " + TABLE_STATS + "("
				+ KEY_ID + " INTEGER PRIMARY KEY," 
				+ KEY_BATTERY_STRENGTH + " INTEGER,"
                + KEY_BATTERY_TEMPERATURE + " INTEGER,"
                + KEY_BATTERY_VOLTAGE + " INTEGER,"
				+ KEY_SIGNAL_STRENGTH + " INTEGER,"
				+ KEY_PROC_COUNT + " INTEGER,"
                + KEY_BYTES + " INTEGER,"
				+ KEY_TIME + " INTEGER)";
		db.execSQL(CREATE_STATS_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		if ( oldVersion != newVersion ) {
			db.execSQL("DROP TABLE IF EXISTS "+ TABLE_STATS);
			onCreate(db);
		}
	}	
	
	public void addStat(InternalStats stats) {
		SQLiteDatabase db = this.getWritableDatabase();
		
		long timeMillis = System.currentTimeMillis() / 1000;
		double timeSec=Math.floor(timeMillis);
		
		ContentValues values = new ContentValues();
		values.put(KEY_BATTERY_STRENGTH, stats.getBatteryStrength());
		values.put(KEY_SIGNAL_STRENGTH, stats.getSignalStrength());
        values.put(KEY_BATTERY_TEMPERATURE, stats.getBatteryTemperature() );
        values.put(KEY_BATTERY_VOLTAGE, stats.getBatteryVoltage() );
		values.put(KEY_PROC_COUNT, stats.getProcCount());
        values.put(KEY_BYTES, stats.getBytes());
		values.put(KEY_TIME, timeSec);
		
		db.insert(TABLE_STATS, null, values);
		db.close();
	}
	
	public void initialize() {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_STATS, null, null);
		db.close();
	}
	
	public List<InternalStats> getAllBatteryStrengths() {
		List<InternalStats> batteryStrengthList = new ArrayList<InternalStats>();
		
		String selectQuery = "SELECT "
                + KEY_TIME + ","
                + KEY_BATTERY_STRENGTH
                    + " FROM " + TABLE_STATS;
		
		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);
		
		if (cursor.moveToFirst()) {
			do {
				InternalStats internalStats = new InternalStats();
				internalStats.setTime(cursor.getLong(0));
				internalStats.setBatteryStrength(Integer.parseInt(cursor.getString(1)));
				batteryStrengthList.add(internalStats);
			} while (cursor.moveToNext());
		}

		return batteryStrengthList;
	}

    public Cursor getBatteryStrengthsByDate( long date ) {
        String selectQuery = "SELECT "
                + KEY_TIME + ","
                + KEY_BATTERY_STRENGTH
                + " FROM " + TABLE_STATS
                + " WHERE " + KEY_TIME + " > " + date;

        Log.d(TAG, "db query: " + selectQuery);

        SQLiteDatabase db = this.getWritableDatabase();

        return db.rawQuery(selectQuery, null);

    }

	public List<InternalStats> getAllSignalStrengths() {
		List<InternalStats> signalStrengthList = new ArrayList<InternalStats>();
		
		String selectQuery = "SELECT " + KEY_TIME +"," + KEY_SIGNAL_STRENGTH + " FROM " + TABLE_STATS;
		
		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);
		
		if (cursor.moveToFirst()) {
			do {
				InternalStats internalStats = new InternalStats();
				internalStats.setTime(cursor.getLong(0));
				internalStats.setSignalStrength(Integer.parseInt(cursor.getString(1)));
				signalStrengthList.add(internalStats);
			} while (cursor.moveToNext());
		}

		return signalStrengthList;
	}

    public List<InternalStats> getAllProcCounts() {
        List<InternalStats> procCountList = new ArrayList<InternalStats>();

        String selectQuery = "SELECT " + KEY_TIME +"," + KEY_PROC_COUNT + " FROM " + TABLE_STATS;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                InternalStats internalStats = new InternalStats();
                internalStats.setTime(cursor.getLong(0));
                internalStats.setProcCount(Integer.parseInt(cursor.getString(1)));
                procCountList.add(internalStats);
            } while (cursor.moveToNext());
        }

        return procCountList;
    }


    public Cursor getAllRecordsForView() {
		String selectQuery = "SELECT "
				+ KEY_ID + "," 
				+ KEY_TIME + ", datetime(" + KEY_TIME + ", 'unixepoch') as " + KEY_TIME_FORMATTED + "," 
				+ KEY_BATTERY_STRENGTH + "," 
				+ KEY_SIGNAL_STRENGTH + "," 
				+ KEY_PROC_COUNT
				+ " FROM " + TABLE_STATS;
		
		SQLiteDatabase db = this.getWritableDatabase();
		return db.rawQuery(selectQuery, null);
	}

    public Cursor getBounds( ) {
        String selectQuery = "SELECT "
            + MIN_BATTERY_TEMPERATURE + ","
            + MIN_BATTERY_VOLTAGE + ","
            + MIN_BATTERY_STRENGTH + ","
            + MIN_BYTES + ","
            + MIN_PROC_COUNT + ","
            + DELTA_BATTERY_STRENGTH + ","
            + DELTA_PROC_COUNT + ","
            + DELTA_BATTERY_TEMPERATURE + ","
            + DELTA_BATTERY_VOLTAGE + ","
            + DELTA_BYTES
                + " FROM " + TABLE_STATS;

        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery(selectQuery, null);
    }

    public Cursor getAllRecordsByDate( long seconds ) {
        String selectQuery = "SELECT "
                + KEY_ID + ","
                + KEY_TIME + ","
                + KEY_BATTERY_STRENGTH + ","
                + KEY_SIGNAL_STRENGTH + ","
                + KEY_BATTERY_VOLTAGE + ","
                + KEY_BATTERY_TEMPERATURE + ","
                + KEY_BYTES + ","
                + KEY_PROC_COUNT
                + " FROM " + TABLE_STATS
                + " WHERE " + KEY_TIME + " > " + seconds;

        Log.d( TAG, "Get stats by date: " + selectQuery );

        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery(selectQuery, null);
    }

}
