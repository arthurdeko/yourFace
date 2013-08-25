package com.jilgen.yourface;

import android.app.ActivityManager;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.os.BatteryManager;
import android.util.Log;

import java.util.List;

/**
 * Created by jilgen on 7/28/13.
 */
public class BatteryUpdater extends StateUpdater {

    static final String TAG = "CF_BatteryUpdater";
    private String action = Intent.ACTION_BATTERY_CHANGED;
    private Intent intent;
    private Context context;

    public BatteryUpdater( Context context ) {
        super( context );
        Log.d(TAG, "Created receiver");
    }

    public void onReceive( Context context, Intent intent ) {
        this.context = context;
        this.intent = intent;
        this.updateBatteryStrength();
        this.updateProcCount();
    }

    public void updateBatteryStrength() {
        StatDatabaseHandler batteryStrengthDB = new StatDatabaseHandler( this.context );

        int batteryStrength = this.intent.getIntExtra( BatteryManager.EXTRA_LEVEL, -1 );
        batteryStrengthDB.addStatValue( Integer.toString( batteryStrength ), DiagActivity.BATTERY_STRENGTH_TYPE );
        Log.d( TAG, "updated battery Strength "+batteryStrength );

    }

    public void updateProcCount() {
        ActivityManager activityManager = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> procs = activityManager.getRunningAppProcesses();

        StatDatabaseHandler procCountDB = new StatDatabaseHandler( this.context );

        if ( procs != null ) {
            procCountDB.addStatValue( Integer.toString( procs.size()), DiagActivity.PROC_COUNT_TYPE );

            Log.d(TAG, "Proc count " + procs.size());
        }
    }
}
