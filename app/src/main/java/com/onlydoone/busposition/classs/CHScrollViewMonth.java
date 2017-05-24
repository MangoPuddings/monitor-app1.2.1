package com.onlydoone.busposition.classs;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.HorizontalScrollView;

import com.onlydoone.busposition.app.monitor.Mileage;

import com.onlydoone.busposition.fragment.miles.Month;

/**
 * Created by zhaohui on 2017/2/24.
 */

public class CHScrollViewMonth extends HorizontalScrollView {
    Month ff =  null;

    public CHScrollViewMonth(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        Mileage f =  (Mileage)context;
        ff = f.month;
    }


    public CHScrollViewMonth(Context context, AttributeSet attrs) {
        super(context, attrs);
        Mileage f =  (Mileage)context;
        ff = f.month;
    }

    public CHScrollViewMonth(Context context) {
        super(context);
        Mileage f =  (Mileage)context;
        ff = f.month;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        //进行触摸赋值
        ff.mTouchView = this;
        return super.onTouchEvent(ev);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        //当当前的CHSCrollView被触摸时，滑动其它
        if(ff.mTouchView == this) {
            ff.onScrollChanged(l, t, oldl, oldt);
        }else{
            super.onScrollChanged(l, t, oldl, oldt);
        }
    }
}
