package com.szjoin.joinmapmodule.bean;

public class JoinCityHotBean extends JoinCityBean {

    public JoinCityHotBean(String name, String province, String code) {
        super(name, province, name, code, true, false);
//        super(name, province, "热门城市", code);
    }

    @Override
    public JoinCityHotBean setType(String type) {
        super.setType(type);
        return this;
    }
}
