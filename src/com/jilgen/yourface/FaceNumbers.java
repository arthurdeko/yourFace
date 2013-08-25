package com.jilgen.yourface;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

/**
 * TODO: document your custom view class.
 */
public class FaceNumbers extends View {

	final static int HOURS = 24;
	final Paint textPaint = new Paint();
	
	public FaceNumbers(Context context) {
		super(context);

	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);


	}
}
