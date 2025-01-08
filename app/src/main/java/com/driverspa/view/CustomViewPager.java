package com.driverspa.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by Yerzhan Tanatov on 22/12/15.
 */
public class CustomViewPager extends ViewPager {

    public CustomViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected boolean canScroll(View v, boolean checkV, int dx, int x, int y) {
        return true;
//        if(v != this && v instanceof ViewPager) {
//            int currentItem = ((ViewPager) v).getCurrentItem();
//            int countItem = ((ViewPager) v).getAdapter().getCount();
//            if((currentItem==(countItem-1) && dx<0) || (currentItem==0 && dx>0)){
//                return false;
//            }
//            return true;
//        }
//        return super.canScroll(v, checkV, dx, x, y);
    }
    @Override
    public void setCurrentItem(int item, boolean smoothScroll) {
        super.setCurrentItem(item, false);
    }

    @Override
    public void setCurrentItem(int item) {
        super.setCurrentItem(item, false);
    }
}