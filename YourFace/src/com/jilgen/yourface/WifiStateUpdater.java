package com.jilgen.yourface;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.util.Log;

public class WifiStateUpdater extends StateUpdater {

    static final String TAG = "CF_WifiStateUpdater";
    public String action = WifiManager.ACTION_PICK_WIFI_NETWORK;

	public WifiStateUpdater(Context context) {
        super(context);
        Log.d(TAG, "Created receiver");
        Log.d(TAG, this.action );
	}

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "received");
        WifiStateChangeDatabaseHandler db = new WifiStateChangeDatabaseHandler(context);

        int state = intent.getIntExtra( WifiManager.EXTRA_WIFI_STATE, 1 );

        Log.d(TAG, "Updated wifi state: " + state);

        if ( state == WifiManager.WIFI_STATE_ENABLED ) {
            db.addStateChange( 1 );
        } else if ( state == WifiManager.WIFI_STATE_DISABLED ) {
            db.addStateChange( 0 );
        } else {
            Log.d(TAG, "No state found: "+state);
        }

    }
}
