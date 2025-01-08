package com.driverspa.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Button;

public class ButtonSquared extends Button {

	public ButtonSquared(Context context) {
		super(context);
	}

	public ButtonSquared(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public ButtonSquared(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
	
	@Override 
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		setMeasuredDimension(getMeasuredWidth(), getMeasuredWidth());
	}

}
