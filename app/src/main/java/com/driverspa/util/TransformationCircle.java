package com.driverspa.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;

public class TransformationCircle implements com.squareup.picasso.Transformation {

	Context context;	
	public TransformationCircle() {
	}

	public TransformationCircle(Context context) {
		this.context = context;		
	}

	@Override
	public Bitmap transform(final Bitmap source) {		
		final Paint paint = new Paint();
		paint.setAntiAlias(true);
		paint.setShader(new BitmapShader(source, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP));
		float radius = source.getWidth();
		float margin = 0f; 
		if(context != null) margin = Functions.dipToPixels(context, 5);
		Bitmap output = Bitmap.createBitmap(source.getWidth(), source.getHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(output);		
		canvas.drawRoundRect(new RectF(margin, margin, source.getWidth() - margin, source.getHeight() - margin), radius, radius, paint);
		
		 if (source != output) {
				source.recycle();
		 }		 
		
		return output;
	}

	@Override
	public String key() {
		return "circled";
	}
	
}