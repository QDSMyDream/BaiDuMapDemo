package com.szjoin.joinmapmodule.interfaces;
import com.szjoin.joinmapmodule.bean.JoinCityBean;

public interface JoinOnPickListener {
    void onPick(int position, JoinCityBean data);
    void onLocate();
    void onCancel();
}
