package com.driverspa.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

import com.squareup.picasso.Picasso.LoadedFrom;
import com.squareup.picasso.Target;

public class CheckedTextViewPicassoTarget extends androidx.appcompat.widget.AppCompatCheckedTextView implements Target {

	public CheckedTextViewPicassoTarget(Context context) {
		super(context);
	}
	
	public CheckedTextViewPicassoTarget(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
	}

	public CheckedTextViewPicassoTarget(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	public void onBitmapFailed(Drawable errorDrawable) {
	}

	@Override
	public void onBitmapLoaded(Bitmap bitmap, LoadedFrom from) {
		this.setCompoundDrawablesWithIntrinsicBounds(
				new BitmapDrawable(getResources(), bitmap),
				null,
				null,
				null);
	}

	@Override
	public void onPrepareLoad(Drawable placeholder) {
	}

}
