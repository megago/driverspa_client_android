package com.driverspa.view;


import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

public class GridViewNonScrolling extends GridView {

	public GridViewNonScrolling(Context context) {
		super(context);
		init();
	}

	public GridViewNonScrolling(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public GridViewNonScrolling(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		init();
	}
	
	private void init() {
		
	}
	
//	@Override
//    public boolean onInterceptTouchEvent(MotionEvent ev) {
//        final int action = ev.getAction();
//        switch (action)
//        {
//            case MotionEvent.ACTION_DOWN:
//                    LogUtil.i("VerticalScrollview", "onInterceptTouchEvent: DOWN super false" );
//                    super.onTouchEvent(ev);
//                    break;
//
//            case MotionEvent.ACTION_MOVE:
//                    return false; // redirect MotionEvents to ourself
//
//            case MotionEvent.ACTION_CANCEL:
//            	LogUtil.i("VerticalScrollview", "onInterceptTouchEvent: CANCEL super false" );
//                    super.onTouchEvent(ev);
//                    break;
//
//            case MotionEvent.ACTION_UP:
//            	LogUtil.i("VerticalScrollview", "onInterceptTouchEvent: UP super false" );
//                    return false;
//
//            default: 
//            	LogUtil.i("VerticalScrollview", "onInterceptTouchEvent: " + action ); break;
//        }
//
//        return false;
//    }

}
