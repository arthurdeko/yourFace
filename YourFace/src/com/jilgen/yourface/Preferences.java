package com.jilgen.yourface;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.text.format.Time;
import android.util.DisplayMetrics;
import android.util.Log;

import java.util.Dictionary;

/**
 * Created by jilgen on 7/11/13.
 */
public class Preferences {

    private static final String TAG = "CF_Preferences";
    public SharedPreferences preferences;
    private Context context;
    public Time now = new Time();

    public Preferences(Context context) {
        this.context = context;
        this.updatePreferences();
    }

    public void updatePreferences() {
        this.preferences = PreferenceManager.getDefaultSharedPreferences(this.context);
    }

    public Float getCenterX() {
        Float centerX = Float.parseFloat(this.preferences.getString( "centerX", "225.0" ));
        return centerX;
    }

    public Float getCenterY() {
        Float centerY = Float.parseFloat(this.preferences.getString( "centerY", "300.0" ));
        return centerY;
    }

    public Float getRadius() {
        Float radius = Float.parseFloat(this.preferences.getString( "size", "300.0" ));
        return radius;
    }

    public Boolean getShowSms() {
        Boolean showSms = this.preferences.getBoolean( "showSms", true );
        return showSms;
    }

    public Boolean getShowCalls() {
        Boolean showCalls = this.preferences.getBoolean( "showCalls", true );
        return showCalls;
    }

    public Boolean getShowBattery() {
        Boolean showBattery = this.preferences.getBoolean( "showBattery", true );
        return showBattery;
    }

    /*
    Only show statistics from during the first n hours.
     */
    public Boolean getMatchHours() {
        Boolean matchHours = this.preferences.getBoolean( "matchHours", true );
        return matchHours;
    }

    public Float getStrokeWidth() {
        Float strokeWidth = Float.parseFloat(this.preferences.getString("stroke", "10.0f"));
        return strokeWidth;
    }

    public int getMissedCallWidth() {
        int missedCallWidth = Integer.parseInt(this.preferences.getString("missedCallDuration", "300"));
        return missedCallWidth;
    }

    public long getHoursAgo() {
        long hoursAgo = Long.parseLong(this.preferences.getString("hoursAgo", "24" ));
        return hoursAgo;
    }

    public int getHours() {
        int hours = Integer.parseInt(this.preferences.getString( "hours", "6" ));
        return hours;
    }

    public int getCallLimit() {
        return Integer.parseInt(this.preferences.getString( "callLimit", "50"));
    }

    public int getHoursInSeconds() {
        int hours = Integer.parseInt(this.preferences.getString( "hours", "6" ));
        hours *= 3600;
        return hours;
    }

    public int getPollingInterval() {
        int pollingInterval = Integer.parseInt(this.preferences.getString( "pollingInterval", "600" )) * 1000;
        return pollingInterval;
    }

    public int getRefreshRate() {
        int refreshRate = Integer.parseInt(this.preferences.getString( "refreshRate", "600" )) * 1000;
        return refreshRate;
    }

    public long getStartDate() {

        this.now.setToNow();
        long nowMilliseconds=this.now.toMillis(false);
        long nowSeconds=nowMilliseconds / 1000;
        long startDate;
        if ( this.getMatchHours() == true ) {
            startDate = ( nowSeconds - ( this.getHours() * 3600 ) );
            Log.d(TAG, "hours " + this.getHours() + "Start " + startDate);
        } else {
            startDate = ( nowSeconds - ( this.getHoursAgo() * 3600 ) );
        }

        return startDate * 1000;
    }

}
