package com.jilgen.yourface;

import android.app.WallpaperManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.CallLog;
import android.app.Activity;
import android.database.Cursor;
import android.graphics.Paint;
import android.text.format.Time;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;

public class MainActivity extends Activity {

	final static String TAG = "YF_Main";
    int _refreshRate = 5000;

	public Time now = new Time();
    public DisplayMetrics screenMetrics;
    public float scale;
    public float screenHeight;
    public float screenWidth;

    public YourFace application;
    public Preferences preferences;
    private RelativeLayout mainLayout;
    private static Context context;
    final Handler handler = new Handler();

    final Runnable screenUpdater = new Runnable()
    {
        public void run()
        {
            updateScreen();
            Preferences localPreferences = new Preferences(context);
            handler.postDelayed( this, localPreferences.getRefreshRate() );
        }
    };

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

        MainActivity.context = getApplicationContext();
		setContentView(R.layout.activity_main);
        this.mainLayout = (RelativeLayout) findViewById(R.id.main_layout);
        this.application = ((YourFace) this.getApplication());

        this.screenMetrics = getResources().getDisplayMetrics();
        this.scale = screenMetrics.density;
        this.screenHeight = screenMetrics.heightPixels;
        this.screenWidth = screenMetrics.widthPixels;
        Log.d(TAG, "width " + this.screenWidth + " height " + this.screenHeight);

        this.preferences = ((YourFace) this.getApplication()).getPreferences();

        this._refreshRate = this.preferences.getRefreshRate() * 1000;

        SignalStrengthListener signalStrengthListener = new SignalStrengthListener(this);
        BatteryUpdater batteryUpdater = new BatteryUpdater(this);
        registerReceiver( batteryUpdater, new IntentFilter(Intent.ACTION_BATTERY_CHANGED) );

        this.updateScreen();

		handler.post( screenUpdater );

	}

    public void onClickSettingsMenu( MenuItem item ) {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    public void clickDiagButton(View view) {
        Intent intent = new Intent(this, DiagActivity.class);
        startActivity(intent);
    }

    // Button to set the Wallpaper
    public void setWallpaper(View view) {
        Log.d( TAG, "Setting wallpaper");
        Intent intent = new Intent(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER);
        intent.putExtra(WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT,
                new ComponentName(this, FaceWallpaperService.class));
        startActivity(intent);
    }

    public Cursor getSmsMessages( String uri ) {
        Uri smsUri = Uri.parse(uri);

        String[] smsColumns = { "date", "body" };
        String where = "date > "+this.preferences.getStartDate();
        String limit = "date DESC LIMIT 100";
        return getContentResolver().query( smsUri, smsColumns, where, null, limit );

    }


    public void makeSpiralArc(long date, int duration) {

        if ( duration <= 0 ) {
            duration = 30;
        }

        Time startTime = new Time();
        startTime.set(date);

        this.now.setToNow();
        long nowSeconds = this.now.toMillis(false) / 1000;
        long startTimeSeconds = startTime.toMillis(false) / 1000;

        float dayDiff = ( (float)nowSeconds ) - ( (float)startTimeSeconds );

        float callShift = dayDiff / 10000;
        float arcRadius = this.preferences.getRadius() - callShift;

        //Log.d( TAG, "Shift: "+callShift+" radius: "+arcRadius+" now: "+nowSeconds+" time: "+startTimeSeconds );


        /*
        TimeArc arc = new TimeArc( );
        arc.setRadius( arcRadius / 2 );
        arc.setHours( this.preferences.getHours() );
        arc.setStartTime( startTime );
        arc.setDuration( duration );
        arc.setStrokeWidth( this.preferences.getStrokeWidth() );
        arc.setCenterX( this.preferences.getCenterX() );
        arc.setCenterY( this.preferences.getCenterY() );


        return arc;
        */
    }


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

    @Override
    public void onResume( ) {
        super.onResume();
        this.updateScreen();

    }

    public void updateScreen() {

        Log.d( TAG, "Updating screen");

        ((YourFace) this.getApplication()).updatePreferences();

        mainLayout.removeAllViewsInLayout();

        if ( this.preferences.getShowSms() ) {
            Log.d(TAG, "Making Sms arcs");
            //this.makeSmsArcs("content://sms/sent");
            //this.makeSmsArcs("content://sms/inbox");
        }

        if (this.preferences.getShowCalls() ) {
            Log.d(TAG, "Making phone arcs");
            this.makePhoneArcs();
        }

        if ( this.preferences.getShowBattery() ) {
            Log.d(TAG, "Making strength arcs");
            this.makeBatteryStrengthArcs();
        }

    }

    public void makeSmsArcs( String uri ) {
        Cursor smsMessages = getSmsMessages(uri);

        if ( smsMessages != null && this.preferences.getShowSms() ) {
            int smsDateIndex = smsMessages.getColumnIndex("date");
            int smsBodyIndex = smsMessages.getColumnIndex("body");

            while (smsMessages.moveToNext()) {
                long date = smsMessages.getLong(smsDateIndex);
                String body = smsMessages.getString(smsBodyIndex);
                int messageLength = 0;

                if ( body != null ) {
                    messageLength = body.length();
                }
                /*
                TimeArc smsArc=this.makeSpiralArc(date, messageLength);
                if ( smsArc != null ) {
                    smsArc.setColor(getResources().getColor(R.color.deep_purple_thin));
                    smsArc.setStrokeWidth(messageLength);
                    smsArc.setStrokeCap(Paint.Cap.BUTT);
                    smsArc.setHours(this.preferences.getHours());
                    //this.mainLayout.addView(smsArc);
                }
                */
            }
        }
    }

    public void makeBatteryStrengthArcs( ) {
        long date = preferences.getStartDate();
        Cursor strengths = new StatsDatabaseHandler(context).getBatteryStrengthsByDate( date );

        if ( strengths != null ) {
            if ( strengths.moveToFirst() ) {
                int dateIndex = strengths.getColumnIndex(StatsDatabaseHandler.KEY_TIME);
                int strengthIndex = strengths.getColumnIndex(StatsDatabaseHandler.KEY_BATTERY_STRENGTH);

                Log.d( TAG, "Battery Strengths: "+Integer.toString( strengths.getCount() ) );

                /*
                TimeArc strengthArc=this.makeSpiralArc(strengths.getLong(dateIndex), preferences.getPollingInterval() * 100);

                if ( strengthArc != null ) {
                    strengthArc.setColor(getResources().getColor(R.color.magenta_solid));
                    strengthArc.setStrokeWidth(strengths.getInt(strengthIndex));
                    strengthArc.setStrokeCap(Paint.Cap.BUTT);
                    strengthArc.setHours(this.preferences.getHours());
                    //this.mainLayout.addView(strengthArc);

                }
                */
            }
        }

    }

    public Cursor getCalls() {
        return this.application.getCallsByStartDate( this.preferences.getStartDate() );
    }

    public void makePhoneArcs() {

        Cursor calls = this.getCalls();
        Log.d( TAG, "Start date: "+this.preferences.getStartDate() );

        if ( calls != null ) {

            int durationIndex = calls.getColumnIndex( CallLog.Calls.DURATION );
            int dateIndex = calls.getColumnIndex( CallLog.Calls.DATE );
            int typeIndex = calls.getColumnIndex( CallLog.Calls.TYPE );

            Log.d(TAG, "Call count "+calls.getCount() );

            while (calls.moveToNext()) {

                long date = calls.getLong( dateIndex );
                int type = Integer.parseInt( calls.getString(typeIndex ) );
                int duration = calls.getInt( durationIndex );

                /*
                TimeArc callArc=this.makeSpiralArc( date, duration );
                callArc.setStrokeCap( Paint.Cap.ROUND );

                if ( type == CallLog.Calls.MISSED_TYPE ) {

                    callArc.setColor( getResources().getColor( R.color.missed_call ) );
                    callArc.setDuration( this.preferences.getMissedCallWidth() );

                } else if ( type == CallLog.Calls.OUTGOING_TYPE ) {

                    callArc.setColor( getResources().getColor(R.color.incoming_call ) );

                } else if ( type == CallLog.Calls.INCOMING_TYPE ) {

                    callArc.setColor( getResources().getColor(R.color.outgoing_call ) );

                }
                */
                //this.mainLayout.addView(callArc);

            }
        }
    }

    public static Context getAppContext() {
        return MainActivity.context;
    }
}
