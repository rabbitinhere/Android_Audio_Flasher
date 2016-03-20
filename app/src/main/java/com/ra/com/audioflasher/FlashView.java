package com.ra.com.audioflasher;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.ra.com.audioflasher.bean.UnSteadyBean;

/**
 * Created by Administrator on 2015/10/1.
 */
public class FlashView extends View {

    private static final String TAG = FlashView.class.getSimpleName();
    private boolean mShowDrumBeat;
    private boolean mShowSing;
    private UnSteadyBean unSteadyBean;

    public FlashView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public FlashView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FlashView(Context context) {
        super(context);
    }

    public void updateView(UnSteadyBean unSteadyBean, boolean drumBeat, boolean sing) {
        this.unSteadyBean = unSteadyBean;
        mShowDrumBeat = drumBeat;
        mShowSing = sing;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
//        if(mShowDrumBeat == true)
//        {
//            canvas.drawARGB(255,255,255,255);
//        }else
//        {
//            canvas.drawARGB(255,0,0,0);
//        }

        int trans = 0;
        if (unSteadyBean == null) {
            return;
        }
//        Log.d(TAG,"value-->"+unSteadyBean.unSteady+"    avg-->"+unSteadyBean.unSteadyAVG);
        if (unSteadyBean.unSteadyAVG * 2 == 0) {
            return;
        } else {
            float p = (float)unSteadyBean.unSteady/((float)unSteadyBean.unSteadyAVG * 2);
            trans = (int)(255*p);


            //让光线更显著
//            if(unSteadyBean.unSteady>(unSteadyBean.unSteadyAVG*1.3f))
//            {
//                trans *= 1.9;
//            }else if(unSteadyBean.unSteady<(unSteadyBean.unSteadyAVG*1.3f))
//            {
//                trans *= (1/1.9);
//            }
            Log.d(TAG,"trans-->"+trans);


            //保护
            if(trans > 255)
            {
                trans = 255;
            }else if(trans <0)
            {
                trans = 0;
            }
            canvas.drawARGB(trans,255,255,255);
        }
    }
}
