package com.ra.com.audioflasher.analyzer;

import com.ra.com.audioflasher.bean.UnSteadyBean;

/**
 * Created by Administrator on 2015/10/2.
 */
public interface Analyzer {
    public UnSteadyBean analyzer(byte[] bytes);
}
