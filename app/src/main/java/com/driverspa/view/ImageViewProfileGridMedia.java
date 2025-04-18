package com.driverspa.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

public class ImageViewProfileGridMedia extends ImageView {

	public static final double RATIO = 0.75;
	
	public ImageViewProfileGridMedia(Context context) {
		super(context);
	}

	public ImageViewProfileGridMedia(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public ImageViewProfileGridMedia(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override 
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		setMeasuredDimension(getMeasuredWidth(), (int)Math.ceil(getMeasuredWidth() * RATIO) );
	}
	
}
