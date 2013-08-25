package com.jilgen.yourface;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Canvas;
import android.widget.ScrollView;

/**
 * Created by jilgen on 7/29/13.
 */
public class StatRing extends ScrollView {

    private StatDatabaseHandler db;

    public StatRing(Context context) {
        super(context);
    }

    public void setDatabaseHandler( StatDatabaseHandler db ) {
        this.db = db;
    }

    public void onDraw( Canvas canvas) {

    }
}
