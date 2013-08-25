package com.jilgen.yourface;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.IntentService;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.TrafficStats;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.jilgen.yourface.BatteryStats;
import com.jilgen.yourface.BatteryValues;
import com.jilgen.yourface.SignalStrengthListener;
import com.jilgen.yourface.MainActivity;

import java.util.List;

public class SimpleIntentService extends IntentService {

	final Context context = MainActivity.getAppContext();
	static final String TAG = "SimpleIntentService";
	public BatteryValues batteryValues = new BatteryValues();

	public SimpleIntentService() {
		super("SimpleIntentService");
	}

	@Override
	protected void onHandleIntent(Intent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public int onStartCommand(final Intent intent, final int flags, final int startId) {

        final Context context = MainActivity.getAppContext();

		Log.d(TAG,"Service Started");
        if ( context != null ) {
            Log.d(TAG, "context");
        } else {
            Log.d(TAG, "No context");
        }

        BluetoothStateUpdater bluetoothUpdater = new BluetoothStateUpdater(context);
        WifiStateUpdater wifiUpdater = new WifiStateUpdater(context);

		final BatteryStats batteryStats = new BatteryStats(this.context);
		final SignalStrengthListener signalStrengthListener = new SignalStrengthListener(this.context);

		final Handler handler = new Handler();

		final Runnable r = new Runnable()
		{
			public void run() 
			{

                Context context = MainActivity.getAppContext();
                long lastBytes = 0;

                if ( context != null ) {

                    Preferences preferences = new Preferences(context);
                    Log.d(TAG, "Interval from prefs: "+preferences.getPollingInterval() );
                    int pollingInterval=preferences.getPollingInterval() * 1000;

                    if ( preferences.getShowBattery() ) {

                        Log.d(TAG, "Battery Strength: "+batteryStats.getStrength()+" temp: "+batteryStats.getTemperature()+" voltage: "+batteryStats.getVoltage());

                        ActivityManager activityManager = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
                        List<RunningAppProcessInfo> procs = activityManager.getRunningAppProcesses();

                        Log.d(TAG, "Proc count " + procs.size());

                        StatsDatabaseHandler db = new StatsDatabaseHandler(context);

                        int batteryStrength = batteryStats.getStrength();
                        int batteryVoltage = batteryStats.getVoltage();
                        int batteryTemperature = batteryStats.getTemperature();
                        int signalStrength = signalStrengthListener.getValue();

                        int bluetoothState = BluetoothAdapter.getDefaultAdapter().getState();

                        Log.d( TAG, "Blue tooth state: "+bluetoothState );
                        long mobileReadBytes = TrafficStats.getTotalRxBytes();
                        Log.d( TAG, "Mobile bytes read: "+ mobileReadBytes );
                        long mobileTransmitBytes = TrafficStats.getTotalTxBytes();
                        Log.d( TAG, "Mobile bytes read: "+ mobileTransmitBytes );

                        long bytes = mobileTransmitBytes - lastBytes;

                        lastBytes = mobileTransmitBytes;

                        InternalStats stats = new InternalStats( batteryStrength, signalStrength, procs.size(), batteryVoltage, batteryTemperature, bytes );

                        db.addStat( stats );
                        db.close();

                    }
                    handler.postDelayed(this, pollingInterval);
                } else {
                    Log.e(TAG, "Could not get context");
                }
			}
		};

        Preferences preferences = new Preferences(context);
        if ( context != null && preferences.getShowBattery() ) {
		    handler.post( r );

        }

	    return super.onStartCommand(intent, flags, startId);

	}

}
