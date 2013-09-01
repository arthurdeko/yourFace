package com.jilgen.yourface;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;
import android.text.format.Time;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * TODO: document your custom view class.
 */
public class IntensityArcView extends TimeArcView {

	final static String TAG =  "CF_IntensityArc";

	final float scale = getResources().getDisplayMetrics().density;
	public float radius = 150 * scale;
	public float strokeWidth = 30.0f;
	public int color;
    public ArrayList<Bundle> data;
	private final Paint arcPaint = new Paint();


	public IntensityArcView(Context context) {
		super(context);
		arcPaint.setStyle( Paint.Style.STROKE );
		arcPaint.setColor( Color.WHITE );
		arcPaint.setStrokeWidth( this.strokeWidth );
		arcPaint.setAntiAlias( true );

	}

    @Override
    public void drawArc( Canvas canvas ) {
        Iterator<Bundle> iterator = this.data.iterator();
        RectF oval = this.getOval();
        this.setBackgroundColor( getResources().getColor(R.color.transparent) );

        if ( iterator != null ) {

            while ( iterator.hasNext() ) {

                Bundle dataPoint = iterator.next();
                Log.d( TAG, "Drawing arc "+dataPoint.getLong("time")+" "+dataPoint.getInt("duration")+" "+dataPoint.getInt("intensity")+" "+dataPoint.toString());
                Time time = new Time();
                time.set(dataPoint.getLong("time"));
                float startDeg = this.timeToDegrees( time );

                float sweepDeg = this.secondsToDegrees(( dataPoint.getInt("duration") ));
                arcPaint.setStrokeWidth( dataPoint.getInt("intensity") / 2);
                arcPaint.setStrokeCap( Paint.Cap.ROUND );
                arcPaint.setColor( this.color );

                canvas.drawArc( oval, startDeg, sweepDeg, false, arcPaint );
            }
        }

    }


}
