package com.tokkor.news60words;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.viewpager.widget.ViewPager;

public class VerticalViewPager extends ViewPager {

    public VerticalViewPager(Context context) {
        super(context);
        init();
    }

    public VerticalViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        // Set the custom page transformer to enable vertical swiping
        setPageTransformer(true, new VerticalPageTransformer());
        // Set the orientation to make swipe vertically
        setOverScrollMode(OVER_SCROLL_NEVER);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        boolean intercepted = super.onInterceptTouchEvent(swapXY(ev));
        swapXY(ev); // Return the touch coordinates back to normal
        return intercepted;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return super.onTouchEvent(swapXY(ev));
    }

    // Swap the X and Y coordinates of the touch event to achieve vertical scrolling
    private MotionEvent swapXY(MotionEvent ev) {
        float x = ev.getX();
        float y = ev.getY();
        ev.setLocation(y, x);
        return ev;
    }

    private class VerticalPageTransformer implements PageTransformer {
        @Override
        public void transformPage(View view, float position) {
            if (position < -1) { // [-Infinity,-1)
                // This page is way off-screen to the left.
                view.setAlpha(0);
            } else if (position <= 1) { // [-1,1]
                view.setAlpha(1);

                // Counteract the default slide transition
                view.setTranslationX(view.getWidth() * -position);

                // Set the vertical swipe effect
                float yPosition = position * view.getHeight();
                view.setTranslationY(yPosition);
            } else { // (1,+Infinity]
                // This page is way off-screen to the right.
                view.setAlpha(0);
            }
        }
    }
}
