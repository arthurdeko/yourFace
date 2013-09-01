package com.jilgen.yourface;

import android.app.Activity;
import android.app.WallpaperManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Canvas;

import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.CallLog;
import android.service.wallpaper.WallpaperService;
import android.text.format.Time;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.View;
import android.widget.RelativeLayout;

/**
 * Created by jilgen on 8/25/13.
 */
public class FaceWallpaperService extends WallpaperService {

    private final static String TAG = "YF_WallpaperService";

    @Override
    public Engine onCreateEngine() {

        Log.d( TAG, "Engine Created");

        return new MyWallpaperEngine();
    }

    private class MyWallpaperEngine extends Engine {

        private boolean visible = true;
        private boolean touchEnabled;
        private int width;
        private int height;
        private Paint paint = new Paint();
        int _refreshRate = 5000;

        public Time now = new Time();
        public DisplayMetrics screenMetrics;
        public float scale;
        public float screenHeight;
        public float screenWidth;
        public Canvas canvas;

        public Preferences preferences;
        private RelativeLayout mainLayout;
        private Context context;

        private final Handler handler = new Handler();
        final Runnable screenUpdater = new Runnable()
        {
            public void run()
            {
                updateScreen();
                Preferences localPreferences = new Preferences(context);
                handler.postDelayed( this, localPreferences.getRefreshRate() );
            }
        };

        public MyWallpaperEngine() {
            this.context = getApplicationContext();
            this.preferences = new Preferences(this.context);

            this._refreshRate = this.preferences.getRefreshRate() * 1000;

            this.canvas = getSurfaceHolder().lockCanvas();

            handler.post( screenUpdater );

        }

        @Override
        public void onVisibilityChanged(boolean visible) {
            this.visible = visible;

            if (visible) {
                handler.post(screenUpdater);
            } else {
                handler.removeCallbacks(screenUpdater);
            }
        }

        @Override
        public void onSurfaceDestroyed( SurfaceHolder holder ) {
            super.onSurfaceDestroyed( holder );
            this.visible = false;
            handler.removeCallbacks(screenUpdater);
        }

        @Override
        public void onSurfaceChanged( SurfaceHolder holder, int format, int width, int height ) {
            this.width=width;
            this.height=height;
            super.onSurfaceChanged( holder, format, width, height );
        }

        @Override
        public void onTouchEvent( MotionEvent event ) {
            if ( touchEnabled ) {
                float x = event.getX();
                float y = event.getY();

                super.onTouchEvent(event);
            }
        }

        public Cursor getSmsMessages( String uri ) {
            Uri smsUri = Uri.parse(uri);

            String[] smsColumns = { "date", "body" };
            String where = "date > "+this.preferences.getStartDate();
            String limit = "date DESC LIMIT 100";
            return getContentResolver().query( smsUri, smsColumns, where, null, limit );

        }

        public TimeArc makeSpiralArc(long date, int duration) {

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

            TimeArc arc = new TimeArc( this.canvas );
            arc.setRadius( arcRadius / 2 );
            arc.setHours( this.preferences.getHours() );
            arc.setStartTime( startTime );
            arc.setDuration( duration );
            arc.setStrokeWidth( this.preferences.getStrokeWidth() );
            arc.setCenterX( this.preferences.getCenterX() );
            arc.setCenterY( this.preferences.getCenterY() );

            return arc;
        }

        public void updateScreen() {

            Log.d( TAG, "Updating screen");

            try {
                getSurfaceHolder().unlockCanvasAndPost(this.canvas);
            } catch( IllegalArgumentException e ) {
                Log.e( TAG, "unlock and post failed");

            }

            this.canvas = null;
            try {

                this.canvas = getSurfaceHolder().lockCanvas();

            } finally {

                if ( this.canvas != null ) {

                    if ( this.preferences.getShowSms() ) {
                        Log.d(TAG, "Making Sms arcs");
                        this.makeSmsArcs("content://sms/sent");
                        this.makeSmsArcs("content://sms/inbox");
                    }

                    if (this.preferences.getShowCalls() ) {
                        Log.d(TAG, "Making phone arcs");
                        this.makePhoneArcs();
                    }

                    if ( this.preferences.getShowBattery() ) {
                        Log.d(TAG, "Making strength arcs");
                        this.makeBatteryStrengthArcs();
                    }

                    getSurfaceHolder().unlockCanvasAndPost(this.canvas);
                }
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
                    TimeArc smsArc=this.makeSpiralArc(date, messageLength);
                    if ( smsArc != null ) {
                        smsArc.setColor(getResources().getColor(R.color.deep_purple_thin));
                        smsArc.setStrokeWidth(messageLength);
                        smsArc.setStrokeCap(Paint.Cap.BUTT);
                        smsArc.setHours(this.preferences.getHours());
                        smsArc.drawArc(this.canvas);
                    }
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

                    TimeArc strengthArc=this.makeSpiralArc(strengths.getLong(dateIndex), preferences.getPollingInterval() * 100);

                    if ( strengthArc != null ) {
                        strengthArc.setColor(getResources().getColor(R.color.magenta_solid));
                        strengthArc.setStrokeWidth(strengths.getInt(strengthIndex));
                        strengthArc.setStrokeCap(Paint.Cap.BUTT);
                        strengthArc.setHours(this.preferences.getHours());
                        strengthArc.drawArc(canvas);

                    }
                }
            }

        }

        public Cursor getCalls() {
            return this.getCallsByStartDate( this.preferences.getStartDate() );
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

                    callArc.drawArc(canvas);

                }
            }

        }

        public Cursor getCallsByStartDate( long startDate ) {

            Uri callUri =  CallLog.Calls.CONTENT_URI;
            String[] callColumns={
                    CallLog.Calls.NUMBER,
                    CallLog.Calls.DATE,
                    CallLog.Calls.DURATION,
                    CallLog.Calls.TYPE
            };

            Time now = new Time();
            now.setToNow();

            String where = CallLog.Calls.DATE+" > "+startDate;
            String limit = CallLog.Calls.DATE+" DESC LIMIT "+preferences.getCallLimit();
            Log.d(TAG, "WHERE " + where + " " + limit);

            return getContentResolver().query( callUri, callColumns, where, null, limit );
        }
    }
}
