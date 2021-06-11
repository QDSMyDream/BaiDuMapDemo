package com.szjoin.joinmapmodule.bean;

import android.text.TextUtils;

import com.szjoin.joinmapmodule.utils.JoinMapUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 创建人：MyDream
 * 创建日期：2021/06/10 10:21
 * 类描述：
 */
public class JoinCityBean {
    private String name;
    private String province;
    private String pinyin;
    private String code;
    private String type = "";
    private String latitude;//获取纬度信息
    private String longitude;  //获取经度信息
    private boolean isHot = false;
    private boolean isLocation = false;
    public JoinCityBean(String name, String province, String code) {
        this.name = name;
        this.province = province;
        this.code = code;
    }

    public JoinCityBean(String name, String province, String code, String latitude, String longitude) {
        this.name = name;
        this.province = province;
        this.code = code;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public JoinCityBean(String name, String province, String pinyin, String code) {
        this.name = name;
        this.province = province;
        this.pinyin = pinyin;
        this.code = code;
    }

    public JoinCityBean(String name, String province, String pinyin, String code, String type, String latitude, String longitude, boolean isHot, boolean isLocation) {
        this.name = name;
        this.province = province;
        this.pinyin = pinyin;
        this.code = code;
        this.type = type;
        this.latitude = latitude;
        this.longitude = longitude;
        this.isHot = isHot;
        this.isLocation = isLocation;
    }

    public JoinCityBean(String name, String province, String pinyin, String code, boolean isHot, boolean isLocation) {
        this.name = name;
        this.province = province;
        this.pinyin = pinyin;
        this.code = code;
        this.isHot = isHot;
        this.isLocation = isLocation;
    }

    public JoinCityBean(String name, String province, String pinyin, String code, String latitude, String longitude) {
        this.name = name;
        this.province = province;
        this.pinyin = pinyin;
        this.code = code;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getPinyin() {
        return pinyin;
    }

    public void setPinyin(String pinyin) {
        this.pinyin = pinyin;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public boolean isHot() {
        return isHot;
    }

    public void setHot(boolean hot) {
        isHot = hot;
    }

    public boolean isLocation() {
        return isLocation;
    }

    public void setLocation(boolean location) {
        isLocation = location;
    }

    public void setHot() {
        isHot = true;
        setType(JoinMapUtils.JOIN_CITY_FLAG_HOT);
    }


    public String getType() {
        return type;
    }

    public JoinCityBean setType(String type) {
        this.type = type;
        return this;
    }

    @Override
    public String toString() {
        return "JoinCityBean{" +
                "name='" + name + '\'' +
                ", province='" + province + '\'' +
                ", pinyin='" + pinyin + '\'' +
                ", code='" + code + '\'' +
                ", type='" + type + '\'' +
                ", latitude='" + latitude + '\'' +
                ", longitude='" + longitude + '\'' +
                ", isHot=" + isHot +
                ", isLocation=" + isLocation +
                '}';
    }

    /***
     * 获取悬浮栏文本，（#、定位、热门 需要特殊处理）
     * @return
     */
    public String getSection() {
        if (TextUtils.isEmpty(pinyin)) {
            return "#";
        } else {
            String c = pinyin.substring(0, 1);
            Pattern p = Pattern.compile("[a-zA-Z]");
            Matcher m = p.matcher(c);
            if (m.matches()) {
                return c.toUpperCase();
            }
            //在添加定位和热门数据时设置的section就是‘定’、’热‘开头
            else if (TextUtils.equals(c, "定") || TextUtils.equals(c, "热"))
                return pinyin;
            else if (isHot) {
                return pinyin;
            } else
                return "#";
        }
    }


}
