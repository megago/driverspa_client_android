package com.driverspa.view;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

public class RobotoLight extends androidx.appcompat.widget.AppCompatTextView {

	public RobotoLight(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		createFont();
	}

	public RobotoLight(Context context, AttributeSet attrs) {
		super(context, attrs);
		createFont();
	}

	public RobotoLight(Context context) {
		super(context);
		createFont();
	}

	public void createFont() {
		Typeface font = Typeface.createFromAsset(getContext().getAssets(), "roboto_light.ttf");
		setTypeface(font);
	}
	
}