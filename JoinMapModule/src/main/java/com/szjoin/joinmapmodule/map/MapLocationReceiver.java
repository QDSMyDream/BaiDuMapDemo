package com.szjoin.joinmapmodule.map;

import com.baidu.location.BDLocation;

public interface MapLocationReceiver {
        void onReceiveLocation(BDLocation location);
    }