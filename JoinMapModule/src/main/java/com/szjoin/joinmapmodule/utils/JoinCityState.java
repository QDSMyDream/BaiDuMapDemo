package com.szjoin.joinmapmodule.utils;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 创建人：MyDream
 * 创建日期：2021/06/10 10:36
 * 类描述：定位状态
 */
public class JoinCityState {
    public static final int LOCATING = 123;
    public static final int SUCCESS = 132;
    public static final int FAILURE = 321;

    @IntDef({SUCCESS, FAILURE})
    @Retention(RetentionPolicy.SOURCE)
    public @interface State {

    }
}
