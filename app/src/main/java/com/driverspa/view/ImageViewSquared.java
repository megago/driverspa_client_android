package com.driverspa.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

/** 
 *  An image view which always remains square with respect to its width.
 *	@author - Square Inc.
 */
public class ImageViewSquared extends androidx.appcompat.widget.AppCompatImageView {
	
	public ImageViewSquared(Context context) {
		super(context);
	}

	public ImageViewSquared(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public ImageViewSquared(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		setMeasuredDimension(getMeasuredWidth(), getMeasuredWidth());
	}
	
}

