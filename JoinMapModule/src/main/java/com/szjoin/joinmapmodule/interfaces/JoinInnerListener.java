package com.szjoin.joinmapmodule.interfaces;

import com.szjoin.joinmapmodule.bean.JoinCityBean;

public interface JoinInnerListener {
    /**
     * 取消
     */
    void dismiss(int position, JoinCityBean data);

    /**
     * 选择
     */
    void onclick(int position, JoinCityBean city);

    void locate();


}
