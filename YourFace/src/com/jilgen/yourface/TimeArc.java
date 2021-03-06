package com.jilgen.yourface;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.text.format.Time;
import android.util.Log;
import android.view.View;

/**
 * TODO: document your custom view class.
 */
public class TimeArc {

	final static String TAG =  "YF_TimeArcView";
	public int hours = 12;
	public int minutes = hours * 60;
	final static float DEGREES = 360;
    public float centerX = 0.0f;
    public float centerY = 0.0f;
    public float radius = 200.0f;
    public float diameter=radius * 2;
	public Time startTime;
	public Time endTime;
	public float strokeWidth = 30.0f;
	public int duration = 0;  // in seconds
	public int color;
    public Paint.Cap strokeCap;
    public Canvas canvas;

	private final Paint arcPaint = new Paint();
	public int startColor = 0xaa111111;
	public int endColor = 0x001a1e85;

	public TimeArc( Canvas canvas ) {
        this.canvas = canvas;
		arcPaint.setStyle( Paint.Style.STROKE );
		arcPaint.setColor( Color.WHITE );
		arcPaint.setStrokeWidth( this.strokeWidth );
		arcPaint.setAntiAlias( true );

	}

    public void place(Canvas canvas) {
        float degrees=0;

        if ( this.canvas != null ) {
            canvas.rotate(degrees - 90, this.centerX, this.centerY);
            Log.d(TAG, "Degrees " + degrees);

            this.drawArc( this.canvas );

        }
    }

    public void drawArc( Canvas canvas ) {

        float startDeg = this.timeToDegrees(this.startTime);
        float sweepDeg = this.secondsToDegrees(this.duration);

        RectF oval = this.getOval();
        canvas.drawArc( oval, startDeg, sweepDeg, false, arcPaint );
    }

	public RectF getOval( ) {
		RectF oval = new RectF( this.centerX - this.radius, 
								this.centerY - this.radius, 
								this.centerX + this.radius, 
								this.centerY + this.radius);
		return oval;
	}

	public void setDuration( int seconds ) {
		this.duration = seconds;
	}
	
	public void setStrokeWidth( float width ) {
		this.strokeWidth = width;
		arcPaint.setStrokeWidth( this.strokeWidth );
	}

    public void setStrokeCap( Paint.Cap style) {
        this.strokeCap = style;
        arcPaint.setStrokeCap( this.strokeCap );
    }

	public void setColor( int color ) {
		this.color = color;
		arcPaint.setColor(color);
	}
	
	public void setHours( int hours ) {
		this.hours=hours;
		this.minutes=hours * 60;
	}
	
	public void setCenterX( float x ) {
		this.centerX=x;
	}

	public void setCenterY( float y ) {
		this.centerY=y;
	}
	
	public void setRadius( float radius ) {
		this.radius=radius;
		this.diameter=this.radius * 2;
	}
	
	public void setStartTime( Time time ) {
		this.startTime = time;
	}

	public void setEndTime( Time time ) {
		this.endTime = time;
	}
	
	public void setStartColor( int color ) {
		this.startColor=color;
	}

	public void setEndColor( int color ) {
		this.endColor=color;
	}
	
	public float timeToDegrees ( Time time ) {

        float degrees = 0;

        if ( time == null ) {
            return degrees;
        }

		float minutes = (float)time.hour * (float)60 + (float)time.minute;
		float ratio = DEGREES / (float)this.minutes;
		
		degrees=minutes * ratio;
		return degrees;
	}
	
	public float secondsToDegrees( int seconds ) {
		float degrees = 0; 
		float minutes = seconds / 60;
		float ratio = DEGREES / (float)this.minutes;
		Log.d(TAG, "Minutes "+minutes+" Ratio "+ratio);
		
		degrees=minutes * ratio;
		return degrees;
	}
}
