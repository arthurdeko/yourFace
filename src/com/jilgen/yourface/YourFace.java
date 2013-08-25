package com.jilgen.yourface;

import android.app.Application;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CallLog;
import android.util.Log;
import android.text.format.Time;

/**
 * Created by jilgen on 7/22/13.
 */
public class YourFace extends Application {

    final static String TAG = "YourFaceApp";
    public Preferences preferences;

    public void updatePreferences() {
        this.preferences.updatePreferences();
    }

    public Preferences getPreferences() {
        this.preferences = new Preferences(this);
        return this.preferences;
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
