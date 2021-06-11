package com.szjoin.joinmapmodule.bean;


import com.szjoin.joinmapmodule.utils.JoinMapUtils;

public class JoinLocatedCityBean extends JoinCityBean {

    public JoinLocatedCityBean(String name, String province, String code) {
        super(name, province, "定位城市", code);
        setType(JoinMapUtils.JOIN_CITY_FLAG_LOCATION);
    }

    @Override
    public JoinLocatedCityBean setType(String type) {
        super.setType(type);
        return this;
    }

    public JoinLocatedCityBean(String name, String province, String code, String latitude, String longitude) {
        super(name, province, "定位城市", code, latitude, longitude);
        setType(JoinMapUtils.JOIN_CITY_FLAG_LOCATION);
    }
}
