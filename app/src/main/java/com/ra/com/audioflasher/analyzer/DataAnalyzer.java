package com.ra.com.audioflasher.analyzer;

import android.util.Log;

import com.ra.com.audioflasher.FlashView;
import com.ra.com.audioflasher.bean.UnSteadyBean;

/**
 * Created by Administrator on 2015/10/1.
 */
public class DataAnalyzer {

    private FlashView mFlashView;
    private boolean mShowDrumBeat;
    private boolean mShowSing;
    private UnSteadyBean unSteadyBean;
    private String TAG = DataAnalyzer.class.getSimpleName();
    private Analyzer analyzer;

    public DataAnalyzer() {
        analyzer = new DrumBeatAnalyzer();
    }

    public void linkFlashView(FlashView flashView)
    {
        mFlashView = flashView;
    }

    public boolean analyzeWave(byte[] bytes)
    {
        unSteadyBean = analyzer.analyzer(bytes);
//        if(analyzer.analyzer(bytes))
//        {
//            mShowDrumBeat = true;
//        }else
//        {
//            mShowDrumBeat = false;
//        }
        return true;
    }



//    public FlashType analyzeFFT(byte[] bytes)
//    {
//        return FlashType.DRUMBEAT;
//    }

    public void updateView()
    {
        if(mFlashView!=null)
        {
            mFlashView.updateView(unSteadyBean,mShowDrumBeat,mShowSing);
        }
    }

}
