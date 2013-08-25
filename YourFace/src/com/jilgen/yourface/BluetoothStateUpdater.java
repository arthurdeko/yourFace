package com.jilgen.yourface;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.util.Log;

public class BluetoothStateUpdater extends StateUpdater {

    static final String TAG = "CF_BluetoothStateUpdater";
    public String action = BluetoothAdapter.ACTION_STATE_CHANGED;

	public BluetoothStateUpdater(Context context) {
        super(context);
        Log.d(TAG, "Created receiver");
	}

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "received");
        BluetoothStateChangeDatabaseHandler db = new BluetoothStateChangeDatabaseHandler(context);

        int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, 1 );

        if ( state == BluetoothAdapter.STATE_ON ) {
            db.addStateChange( 1 );
            Log.d(TAG, "Updated bluetooth state: " + state + " ON");
        } else if ( state == BluetoothAdapter.STATE_OFF ) {
            db.addStateChange( 0 );
            Log.d(TAG, "Updated bluetooth state: " + state + " OFF");
        } else {
            Log.d(TAG, "No state found: "+state);
        }

        db.close();

    }
}
