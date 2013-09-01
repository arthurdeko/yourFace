package com.jilgen.yourface;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.os.Bundle;
import android.os.Handler;
import android.provider.CallLog;
import android.text.format.Time;
import android.util.FloatMath;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by jilgen on 7/22/13.
 */
public class DiagActivity extends Activity {

    final static String TAG = "CF_diag";
    public Preferences preferences;
    public RelativeLayout diagLayout;
    private TextView legend;

    final static float RING_PROC_COUNT = 50.0f;
    final static float RING_PHONE = 200.0f;
    final static float RING_SIGNAL_STRENGTH = 130.0f;
    final static float RING_BYTES = 100.0f;
    final static float RING_BLUETOOTH = 200.0f;
    final static float RING_BATTERY_STRENGTH = 250.0f;
    final static float RING_BATTERY_VOLTAGE = 500.0f;
    final static float RING_BATTERY_TEMPERATURE = 600.0f;

    final static String BATTERY_STRENGTH_TYPE = "batteryStrength";
    final static String PROC_COUNT_TYPE = "procCount";
    final static String SIGNAL_STRENGTH_TYPE = "signalStrength";

    long startDate;

    float[] lastEvent = null;
    float d = 0f;
    float newRot = 0f;
    float oldDist = 1f;
    Matrix matrix = new Matrix();
    Matrix matrix1 = new Matrix();
    Matrix savedMatrix = new Matrix();
    Matrix savedMatrix2 = new Matrix();
    static final int NONE = 0;
    static final int DRAG = 1;
    static final int ZOOM = 2;
    int mode = 1;
    PointF start = new PointF();
    PointF mid = new PointF();

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        matrix.setTranslate(1f, 1f);
        matrix1.setTranslate(1f, 1f);

        this.preferences = ((YourFace) this.getApplication()).getPreferences();

        this.setStartDate();

        setContentView(R.layout.activity_diag);
        this.diagLayout = (RelativeLayout) findViewById( R.id.diag_layout );
        this.legend = (TextView) findViewById( R.id.legend );

        SignalStrengthListener signalStrengthListener = new SignalStrengthListener(this);
        BatteryUpdater batteryUpdater = new BatteryUpdater(this);
        registerReceiver( batteryUpdater, new IntentFilter(Intent.ACTION_BATTERY_CHANGED) );

        this.updateScreen();

        final Handler handler = new Handler();
        final Runnable r = new Runnable()
        {
            public void run()
            {
                updateScreen();
                handler.postDelayed( this, preferences.getRefreshRate() );
            }
        };
        handler.post(r);
    }

    public boolean onTouchEvent(MotionEvent ev ) {
        switch (ev.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                savedMatrix.set(matrix);
                start.set(ev.getX(), ev.getY());
                mode = DRAG;
                lastEvent = null;
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                oldDist = spacing(ev);
                savedMatrix.set(matrix);
                midPoint(mid, ev);
                mode = ZOOM;

                lastEvent = new float[4];
                lastEvent[0] = ev.getX(0);
                lastEvent[1] = ev.getX(1);
                lastEvent[2] = ev.getY(0);
                lastEvent[3] = ev.getY(1);
                d = rotation(ev);
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
                mode = NONE;
                lastEvent = null;
                break;
            case MotionEvent.ACTION_MOVE:
                if (mode == DRAG) {
                    // ...
                    matrix.set(savedMatrix);
                    //matrix.postTranslate(ev.getX() - start.x, ev.getY() - start.y);
                    diagLayout.setTranslationX( ev.getX() - start.x );
                    diagLayout.setTranslationY( ev.getY() - start.y );
                    diagLayout.invalidate();

                }
                else if (mode == ZOOM && ev.getPointerCount()==2) {
                    float newDist = spacing(ev);

                    matrix.set(savedMatrix);
                    if (newDist > 10f) {
                        float scale = newDist / oldDist;
                        matrix.postScale(scale, scale, mid.x, mid.y);
                        diagLayout.setScaleX(scale);
                        diagLayout.setScaleY( scale );
                        diagLayout.setTranslationX( ev.getX(0) - start.x );
                        diagLayout.setTranslationY( ev.getY(0) - start.y );
                        diagLayout.invalidate();
                    }
                    if (lastEvent!=null){
                        newRot = rotation(ev);
                        float r = newRot-d;
                        diagLayout.setRotation( r );
                        diagLayout.invalidate();
                    }
                }

                break;
        }

        return true; // indicate event was handled
    }

    private float rotation(MotionEvent ev) {
        double delta_x = (ev.getX(0) - ev.getX(1));
        double delta_y = (ev.getY(0) - ev.getY(1));
        double radians = Math.atan2(delta_y, delta_x);
        return (float) Math.toDegrees(radians);
    }

    /** Determine the space between the first two fingers */
    private float spacing( MotionEvent ev) {
        // ...
        float x = ev.getX(0) - ev.getX(1);
        float y = ev.getY(0) - ev.getY(1);
        return FloatMath.sqrt(x * x + y * y);
    }

    /** Calculate the mid point of the first two fingers */
    private void midPoint(PointF point, MotionEvent ev) {
        // ...
        float x = ev.getX(0) + ev.getX(1);
        float y = ev.getY(0) + ev.getY(1);
        point.set(x / 2, y / 2);
    }

    public void updateScreen() {
        ((YourFace) this.getApplication()).updatePreferences();

        diagLayout.removeAllViewsInLayout();
        this.legend.setText("");

        this.makeGridRing( DiagActivity.RING_BATTERY_STRENGTH, getResources().getColor( R.color.blue_tooth_thin ) );
        this.makeGridRing( DiagActivity.RING_BYTES, getResources().getColor( R.color.battery_voltage_thin ) );
        this.makeGridRing( DiagActivity.RING_BATTERY_VOLTAGE, getResources().getColor( R.color.blue_tooth_thin ) );
        this.makeGridRing( DiagActivity.RING_BATTERY_TEMPERATURE, getResources().getColor( R.color.battery_temperature_thin ) );
        this.makeGridRing( DiagActivity.RING_PROC_COUNT, getResources().getColor( R.color.blue_tooth_thin ) );

        this.setStartDate();
        this.makePhoneArcs();
        this.makePhoneGutter();
        this.makeStatArcs();
    }


    public void setStartDate() {
        Time now = new Time();
        now.setToNow();
        this.startDate = now.toMillis(false) - this.preferences.getHours() * 3600000;
    }

    public Cursor getCalls() {

        return ((YourFace) getApplication()).getCallsByStartDate(this.startDate);
    }

    public Cursor getStats( ) {
        StatsDatabaseHandler db = new StatsDatabaseHandler(this);
        Cursor stats = db.getAllRecordsByDate(this.startDate / 1000);

        return stats;
    }

    public void onClickSettingsMenu( MenuItem item ) {
        Intent intent = new Intent( this, SettingsActivity.class );
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu( Menu menu ) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate( R.menu.main, menu );
        return true;
    }

    public void makePhoneGutter() {

        TimeArcView gutterArc = new TimeArcView( this );
        gutterArc.setRadius( DiagActivity.RING_PHONE );
        gutterArc.setStrokeWidth( this.preferences.getStrokeWidth() );
        gutterArc.setDuration( this.preferences.getHours() * 3600000 );
        gutterArc.setColor( getResources().getColor( R.color.call_gutter ) );
        gutterArc.setStrokeCap( Paint.Cap.BUTT );
        gutterArc.setCenterX( this.preferences.getCenterX() );
        gutterArc.setCenterY( this.preferences.getCenterY() );

        this.diagLayout.addView( gutterArc );
    }

    public void makeGridRing( float radius, int color ) {
        TimeArcView arc = new TimeArcView(this);

        arc.setColor( getResources().getColor( R.color.grid_lines ) );
        arc.setHours( this.preferences.getHours() );
        arc.setDuration(this.preferences.getHoursInSeconds());
        arc.setCenterX(this.preferences.getCenterX());
        arc.setCenterY(this.preferences.getCenterY());
        arc.setColor( color );
        arc.setRadius( radius );
        arc.setStrokeWidth( this.preferences.getStrokeWidth() );
        arc.setStrokeCap( Paint.Cap.BUTT );

        diagLayout.addView(arc);

    }

    public void makeGrid() {
        float rings = 10;
        float radius = this.preferences.getRadius();
        float ringWidth = radius / rings;
        int ringColor = getResources().getColor( R.color.call_gutter );

        while ( radius > 0 ) {
            this.makeGridRing( radius, ringColor  );
            Log.d( TAG, "Radius: "+ radius );
            radius -= ringWidth;
        }

    }

    public void makeStatArc( String ringName , float ringRadius, int color ) {
        long startDate = this.preferences.getStartDate();

            StatDatabaseHandler db = new StatDatabaseHandler(this);

            ArrayList<Bundle> dataPoints = db.getAdjustedValues( startDate, ringName );
            int min = db.getMinByStartDate( startDate, ringName );
            int delta = db.getDeltaByStartDate(startDate, ringName);

            db.close();

            this.legend.append( ringName+": "+dataPoints.size()+" Min: "+min+" Delta: "+delta+"\n" );
            Log.d( TAG, ringName+": "+dataPoints.size()+" Min: "+min+" Delta: "+delta+"\n" );

            IntensityArcView arc = new IntensityArcView( this );

            arc.data = dataPoints;
            arc.setRadius( ringRadius );
            arc.color = color;
            arc.centerX = this.preferences.getCenterX();
            arc.centerY = this.preferences.getCenterY();

            diagLayout.addView( arc );

    }

    public void makeStatArcs() {
        this.makeStatArc( BATTERY_STRENGTH_TYPE , DiagActivity.RING_BATTERY_STRENGTH, getResources().getColor( R.color.missed_call ) );
        this.makeStatArc( SIGNAL_STRENGTH_TYPE, DiagActivity.RING_SIGNAL_STRENGTH, getResources().getColor( R.color.blue_tooth) );
        this.makeStatArc( PROC_COUNT_TYPE, DiagActivity.RING_PROC_COUNT, getResources().getColor( R.color.canary_yellow_solid));
    }

    public void makePhoneArcs() {

        Cursor calls = this.getCalls();

        if ( calls != null ) {

            int durationIndex = calls.getColumnIndex( CallLog.Calls.DURATION );
            int dateIndex = calls.getColumnIndex( CallLog.Calls.DATE );
            int typeIndex = calls.getColumnIndex( CallLog.Calls.TYPE );

            Log.d(TAG, "Call count " + calls.getCount());

            while ( calls.moveToNext() ) {

                long date = calls.getLong( dateIndex );
                int type = Integer.parseInt( calls.getString(typeIndex) );
                int duration = calls.getInt( durationIndex );

                TimeArcView callArc=this.makeRingArc(date, duration, DiagActivity.RING_PHONE);
                callArc.setStrokeCap( Paint.Cap.ROUND );

                if ( type == CallLog.Calls.MISSED_TYPE) {
                    callArc.setColor( getResources().getColor(R.color.missed_call) );
                    callArc.setDuration( this.preferences.getMissedCallWidth() );
                } else if ( type == CallLog.Calls.OUTGOING_TYPE ) {
                    callArc.setColor( getResources().getColor(R.color.incoming_call) );
                } else if ( type == CallLog.Calls.INCOMING_TYPE ) {
                    callArc.setColor( getResources().getColor(R.color.outgoing_call) );
                }

                this.diagLayout.addView(callArc);

            }
            calls.close();

        }
    }

    public void makeBluetoothArcs() {
        BluetoothStateChangeDatabaseHandler db = new BluetoothStateChangeDatabaseHandler(this);
        Cursor records = db.getByTimeDelta( this.preferences.getHours() * 3600 );

        if ( records != null ) {
            int dateIndex = records.getColumnIndex( BluetoothStateChangeDatabaseHandler.KEY_TIME );
            int stateIndex = records.getColumnIndex( BluetoothStateChangeDatabaseHandler.KEY_STATE );
            Log.d( TAG, "Bluetooth records: "+records.getCount() );

            long start = 0;
            int duration = 1;
            while (records.moveToNext()) {
                int state = records.getInt(stateIndex);
                long date = records.getLong(dateIndex);

                if ( state == 1 ) {
                    start = date;
                } else if ( state == 0 && start > 0 ) {
                    duration = (int) ( date - start );
                    TimeArcView arc = makeRingArc( date, duration, DiagActivity.RING_BLUETOOTH );
                    arc.setColor(getResources().getColor( R.color.blue_tooth ));
                    arc.setStrokeWidth(15.0f);

                    this.diagLayout.addView( arc );
                }

            }
            records.close();
        }

    }

    public TimeArcView makeIntensityArc( long date, long duration, float ringRadius, float ringWidth ) {

        Time startTime = new Time();
        startTime.set(date);

        TimeArcView arc = new TimeArcView( this );
        arc.setRadius( ringRadius );
        arc.setHours(this.preferences.getHours());
        arc.setStartTime( startTime );
        arc.setDuration( (int)duration );
        arc.setStrokeWidth( ringWidth );
        arc.setCenterX( this.preferences.getCenterX() );
        arc.setCenterY( this.preferences.getCenterY() );

        return arc;
    }

    public TimeArcView makeRingArc(long date, int duration, float ringRadius ) {

        if ( duration <= 0 ) {
            duration = 30;
        }

        Time startTime = new Time();
        startTime.set(date);

        TimeArcView arc = new TimeArcView( this );
        arc.setRadius( ringRadius );
        arc.setHours( this.preferences.getHours() );
        arc.setStartTime( startTime );
        arc.setDuration( duration );
        arc.setStrokeWidth( this.preferences.getStrokeWidth() );
        arc.setCenterX( this.preferences.getCenterX() );
        arc.setCenterY( this.preferences.getCenterY() );

        return arc;

    }

    @Override
    public void onPause() {
        super.onPause();

    }

    @Override
    public void onResume() {
        super.onResume();
        this.updateScreen();

    }

}
