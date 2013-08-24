package com.jilgen.yourface;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Switch;

/**
 * Created by jilgen on 7/15/13.
 */
public class SettingsFragment extends PreferenceFragment {

    static String TAG = "SettingsFrag";

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.preferences);
    }

    public void onClickBatteryService( Switch item ) {
        Log.d(TAG, item.toString());

        Context context = MainActivity.getAppContext();
        Intent intent = new Intent(context, SimpleIntentService.class);
        context.startService(intent);
    }
}
