package com.jilgen.yourface;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

public abstract class StateUpdater extends BroadcastReceiver {
    private String _state = "";

    static final String TAG = "CF_StateUpdater";
    private String action = BluetoothAdapter.ACTION_STATE_CHANGED;

	public StateUpdater(Context context) {
        IntentFilter filter = new IntentFilter(this.action);
        context.registerReceiver(this, filter);
	}

    public void start() {
        Log.d(TAG, "Started updater");
    }

    @Override
    public void onReceive(Context context, Intent intent) {

    }
}
