package com.jilgen.yourface;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.SweepGradient;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.text.TextPaint;
import android.text.format.Time;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * TODO: document your custom view class.
 */
public class Ticker extends View {

	long unit = 1000;
	float tick = 360 / unit;
	final static String TAG =  "Ticker";
	public int hours = 12;
	public int minutes = hours * 60;
	final static float DEGREES = 360;
	final float scale = getResources().getDisplayMetrics().density;
	public float radius = 150 * scale;
	public float centerX = 150 * scale;
	public float centerY = 150 * scale;
	public float diameter = radius * 2;

	float[] positions = {0,1};
	private final Paint tickerPaint = new Paint();
	public int startColor = 0xaa111111;
	public int endColor = 0x001a1e85;
	
	public Ticker(Context context) {
		super(context);
		tickerPaint.setAntiAlias(true);
		tickerPaint.setStrokeCap(Paint.Cap.ROUND);
		tickerPaint.setStrokeWidth(3);
		tickerPaint.setStyle(Paint.Style.FILL);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		Time time = new Time();
		time.setToNow();
		Log.d(TAG, "Now: "+time.hour+" "+time.minute);
		Log.d( TAG, "Scale "+scale);
				
		float degrees = timeToDegrees(time);

		canvas.rotate(degrees - 90, this.centerX, this.centerY);
		Log.d(TAG, "Degrees " + degrees);

		RectF oval = this.getOval();
		canvas.drawArc(oval, 0, 360, true, tickerPaint);
		
		int[] colors = {this.startColor, this.endColor };
		SweepGradient gradient = new SweepGradient(this.centerX, this.centerY, colors, positions);
		tickerPaint.setShader(gradient);
		
		super.onDraw(canvas);

	}
	
	public RectF getOval( ) {
		RectF oval = new RectF( this.centerX - this.radius, 
								this.centerY - this.radius, 
								this.centerX + this.radius, 
								this.centerY + this.radius);
		return oval;
	}
	
	public void setHours( int hours ) {
		this.hours=hours;
		this.minutes=hours * 60;
	}
	
	public void setCenterX( float x ) {
		this.centerX=x * scale;
	}

	public void setCenterY( float y ) {
		this.centerY=y * scale;
	}
	
	public void setRadius( float radius ) {
		this.radius=radius * this.scale;
		this.diameter=this.radius * 2;
	}
	
	public void setStartColor( int color ) {
		this.startColor=color;
	}

	public void setEndColor( int color ) {
		this.endColor=color;
	}
	
	public float timeToDegrees ( Time time ) {
		float degrees = 0; 
		float minutes = (float)time.hour * (float)60 + (float)time.minute;
		float ratio = DEGREES / (float)this.minutes;
		Log.d(TAG, "Minutes "+minutes+" Ratio "+ratio);
		
		degrees=minutes * ratio;
		return degrees;
	}
}
