package com.jilgen.yourface;

import android.content.Context;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.util.Log;

public class SignalStrengthListener extends PhoneStateListener {
	static final String TAG = "CF_SignalStrengthListener";
	protected TelephonyManager telephonyManager;
	public Context context;
	private int _value;
	
	public SignalStrengthListener(Context context) {
        this.context=context;
		telephonyManager = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
		telephonyManager.listen(this, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS );
	}
	
	public void onSignalStrengthsChanged(SignalStrength signalStrength) { 
		this._value=signalStrength.getGsmSignalStrength();

        SignalStrengthDatabaseHandler db = new SignalStrengthDatabaseHandler( context );
        db.addStatValue( Integer.toString( this._value ), DiagActivity.SIGNAL_STRENGTH_TYPE );

        Log.d(TAG, "Updated Signal Strength "+this._value);

	}
	
	public int getValue() {
		return this._value;
	}
}
