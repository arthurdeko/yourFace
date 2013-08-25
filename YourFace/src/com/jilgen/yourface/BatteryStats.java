package com.jilgen.yourface;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.util.Log;

public class BatteryStats extends BroadcastReceiver {
    private int _strength = -1;
    private int _voltage = -1;
    private int _temperature = -1;

    static final String TAG = "CF::BatteryStats";
	
	public BatteryStats(Context context) {
        IntentFilter filter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        context.registerReceiver(this, filter);
	}
	
	public int getStrength() {
		return this._strength;
	}

    public int getVoltage() {
        return this._voltage;
    }

    public int getTemperature() {
        return this._temperature;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        this._strength = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        this._voltage = intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -1);
        this._temperature = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1);
    }
}
