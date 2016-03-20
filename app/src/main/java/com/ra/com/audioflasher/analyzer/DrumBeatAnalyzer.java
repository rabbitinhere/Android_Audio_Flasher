package com.ra.com.audioflasher.analyzer;

import android.util.Log;

import com.ra.com.audioflasher.bean.UnSteadyBean;

import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Created by Administrator on 2015/10/2.
 */
public class DrumBeatAnalyzer implements Analyzer {
    String TAG = DrumBeatAnalyzer.class.getSimpleName();
    private int unSteadySum = 0;
    private LinkedList<Long> rhythmPool;
    private LinkedList<Integer> steadyPoolLargeRange;
    private LinkedList<Integer> steadyPoolSmallRange;
    private long lastAddToSteadyPoolTime = 0;
    private long lastSmallRangeFlash = 0;
    private static final int DEFAULT = 0;
    private int unSteadyLevel = DEFAULT; //may be change to arraylist
    private final int RHYTHM_POOL_SIZE = 3;
    private final int STEADY_POOL_LARGE_RANGE_SIZE = 20;
    private final int STEADY_POOL_SMALL_RANGE_SIZE = 5;


    public DrumBeatAnalyzer() {
        rhythmPool = new LinkedList<Long>();
        steadyPoolLargeRange = new LinkedList<Integer>();
        steadyPoolSmallRange = new LinkedList<Integer>();
    }

    @Override
    public UnSteadyBean analyzer(byte[] bytes) {
        UnSteadyBean res;
        res = unSteadyValue(bytes);
        Log.d(TAG, "unSteady--->" + res);
        return res;
    }

    private boolean unSteadyValueEnough_Avg(byte[] bytes) {
        int sum = 0;
        int step = bytes.length / 20;
        int border = bytes.length - step;
        int sumPartNum = (border - 1) / step;
        for (int i = 0; i < border; i += step) {
            int distance = (Math.abs(bytes[i + step]) - Math.abs(bytes[i]));
            sum += Math.abs(distance);
        }
        int avgDis = sum / sumPartNum;
//        Log.d(TAG, "steady one--->" + avgDis);
        if (System.currentTimeMillis() > lastAddToSteadyPoolTime + 1000) //make steadyPoolLargeRange contain long time
        {
            if (steadyPoolLargeRange.size() < STEADY_POOL_LARGE_RANGE_SIZE) {
                steadyPoolLargeRange.addLast(avgDis);
                lastAddToSteadyPoolTime = System.currentTimeMillis();
            } else {
                steadyPoolLargeRange.removeFirst();
                steadyPoolLargeRange.addLast(avgDis);
                lastAddToSteadyPoolTime = System.currentTimeMillis();
            }
            Log.d(TAG, "add~~~~ once" + steadyPoolLargeRange.size());
        }
        int avgSteadyValue = avgSteadyPool(steadyPoolLargeRange);
//        Log.d(TAG, "steady all avg--->" + avgSteadyValue);
        if (avgDis > avgSteadyValue * 1.6) {
            return true;
        } else {
            return false;
        }
    }

    private boolean unSteadyValueEnough_ToPre(byte[] bytes) {
        boolean res;
        int sum = 0;
        int step = bytes.length / 20;
        int border = bytes.length - step;
        int sumPartNum = (border - 1) / step;
        for (int i = 0; i < border; i += step) {
            int distance = (Math.abs(bytes[i + step]) - Math.abs(bytes[i]));
            sum += Math.abs(distance);
        }
        int avgDis = sum / sumPartNum;

        int avgSteadyValue = avgSteadyPool(steadyPoolSmallRange);
        Log.d(TAG, "smallSteadyAvg:"+ avgSteadyValue + "curSteadyValue:" + avgDis);
        long curTime = System.currentTimeMillis();
        if (avgDis > avgSteadyValue * 1.01  && curTime>lastSmallRangeFlash+300) {
            if(unSteadyLevel >= 1)
            {
                res = true;
                lastSmallRangeFlash = curTime;
            }else
            {
                res = false;
            }
            unSteadyLevel ++;
        } else {
            unSteadyLevel = DEFAULT;
            res = false;
        }
        if (steadyPoolSmallRange.size() < STEADY_POOL_SMALL_RANGE_SIZE) {
            steadyPoolSmallRange.addLast(avgDis);
        } else {
            steadyPoolSmallRange.removeFirst();
            steadyPoolSmallRange.addLast(avgDis);
        }
        return res;
    }

    private UnSteadyBean unSteadyValue(byte[] bytes) {

        boolean res;
        int sum = 0;
        int step = bytes.length / 20;   //一小段有多少个数   20段
        int sumPartNum = 20;
        for (int i = 0; i < bytes.length-step; i += step) {
            int distance = (Math.abs(bytes[i + step]) - Math.abs(bytes[i]));//一段的两端相减的差
            sum += Math.abs(distance);
        }
        int unSteadyValue = sum / (sumPartNum-1);//平均每段两端的差

        if (steadyPoolLargeRange.size() < STEADY_POOL_LARGE_RANGE_SIZE) {
            steadyPoolLargeRange.addLast(unSteadyValue);
        } else {
            steadyPoolLargeRange.removeFirst();
            steadyPoolLargeRange.addLast(unSteadyValue);
        }

        int avgSteadyValue = avgSteadyPool(steadyPoolLargeRange);//算出avgDis的平均值

        UnSteadyBean bean = new UnSteadyBean(unSteadyValue,avgSteadyValue);
        return bean;
    }



    private int avgSteadyPool(LinkedList<Integer> steadyPool) {
        if (steadyPool == null || steadyPool.size() == 0) {
            return 0;
        }
        int steadySum = 0;
        for (int i = 0; i < steadyPool.size(); i++) {
            Integer steadyValue = steadyPool.get(i);
            steadySum += steadyValue;
        }
        return steadySum / steadyPool.size();
    }

    /**
     * Error Function
     *
     * @return
     */
    private boolean onRightTime() {
        if (rhythmPool.size() < RHYTHM_POOL_SIZE) {
            rhythmPool.addLast(System.currentTimeMillis());
            return true;

        } else {
            rhythmPool.removeFirst();
            rhythmPool.addLast(System.currentTimeMillis());
        }
        ArrayList<Long> timeDisList = new ArrayList<Long>();
        for (int i = 0; i < rhythmPool.size() - 1; i++) {
            Long dis = rhythmPool.get(i + 1) - rhythmPool.get(i);
            timeDisList.add(dis);
        }

        long disAve = 0;
        long disSum = 0;
        for (int i = 0; i < timeDisList.size() - 1; i++) {
            disSum += timeDisList.get(i);
        }
        disAve = disSum / timeDisList.size();
        if (disAve > 30) {
            return true;
        } else {
            return false;
        }
    }
}
