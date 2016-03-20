package com.ra.com.audioflasher.bean;

/**
 * Created by Administrator on 2015/10/31.
 */
public class UnSteadyBean {
    public int unSteady;
    public int unSteadyAVG;

    public UnSteadyBean(int unSteady, int unSteadyAVG) {
        this.unSteady = unSteady;
        this.unSteadyAVG = unSteadyAVG;
    }

    @Override
    public String toString() {
        return "UnSteadyBean{" +
                "unSteady=" + unSteady +
                ", unSteadyAVG=" + unSteadyAVG +
                '}';
    }
}
