package com.driverspa.view;

/**
 * Created by Yerzhan Tanatov on 18/12/17.
 */
import android.content.Context;
import android.view.MotionEvent;
import android.widget.FrameLayout;

import com.driverspa.BA;
import com.driverspa.util.L;
import com.driverspa.util.otto.CollapseRequestFields;
import com.driverspa.util.otto.ExpandRequestFields;

public class TouchableWrapper extends FrameLayout {

    boolean isClick = true;
    boolean isCollapsed = false;
    float total, xPrec, yPrec;
    public TouchableWrapper(Context context) {
        super(context);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                total = 0;
                xPrec = event.getX();
                yPrec = event.getY();
                isClick = true;
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                if (!isClick) {
                    isCollapsed = false;
                    BA.getEventBus().post(new ExpandRequestFields());
                }
                break;
            case MotionEvent.ACTION_MOVE:
                isClick = false;
                final float dx = event.getX() - xPrec;
                final float dy = event.getY() - yPrec;
                final float dl = (float)Math.sqrt((double)(dx * dx + dy * dy));
                total += dl;
                xPrec = event.getX();
                yPrec = event.getY();
                if(!isCollapsed && total > 80) {
                   BA.getEventBus().post(new CollapseRequestFields());
                   isCollapsed = true;
                }
                break;
            default:
                break;
        }
        return super.dispatchTouchEvent(event);
    }
}