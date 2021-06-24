package com.szjoin.joinmapmodule.map;

import android.util.Log;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;

import java.util.HashMap;
import java.util.Map;

public class MyLocationListener extends BDAbstractLocationListener {
    private String TAG = getClass().getSimpleName();
    private MapLocationReceiver baiduLocationReceiver;
    private Map<Integer, String> map = new HashMap<>();


    public MyLocationListener(MapLocationReceiver baiduLocationReceiver) {
        this.baiduLocationReceiver = baiduLocationReceiver;
        initErrorCode();
    }


    @Override
    public void onReceiveLocation(BDLocation location) {
        Log.e(TAG, "onReceiveLocation: " );
        if (logErrorCode(location.getLocType())) {
            baiduLocationReceiver.onReceiveLocation(location);
        }
//        String name = location.getCity();
//        double latitude = location.getLatitude();    //获取纬度信息
//        double longitude = location.getLongitude();    //获取经度信息
//        float radius = location.getRadius();    //获取定位精度，默认值为0.0f
//        String coorType = location.getCoorType();
//        //获取经纬度坐标类型，以LocationClientOption中设置过的坐标类型为准
//        int errorCode = location.getLocType();
//        logErrorCode(errorCode);
//        //获取定位类型、定位错误返回码，具体信息可参照类参考中BDLocation类中的说明
//        Log.e(TAG, "onReceiveLocation: 获取纬度信息" + latitude);
//        Log.e(TAG, "onReceiveLocation: 获取经度信息" + longitude);
//        Log.e(TAG, "onReceiveLocation: 获取定位精度" + radius);
//        Log.e(TAG, "onReceiveLocation: 获取经纬度坐标类型" + coorType);
//        Log.e(TAG, "onReceiveLocation: 获取定位类型、定位错误返回码" + errorCode);
    }


    private void initErrorCode() {
        map.put(61, "  GPS定位成功");
        map.put(62, "  定位失败 无法获取有效定位依据，请检查运营商网络或者WiFi网络是否正常开启，尝试重新请求定位");
        map.put(63, "  没有成功向服务器发起请求，请确认当前测试手机网络是否通畅，尝试重新请求定位");
        map.put(66, "  离线定位结果\n" +
                "               : 通过requestOfflineLocaiton调用时对应的返回结果");
        map.put(67, "  离线定位失败");
        map.put(161, "  \t网络定位结果\n" +
                ":网络定位成功");
        map.put(162, "  请求串密文解析失败\n" +
                "       : 一般是由于客户端SO文件加载失败造成，请严格参照开发指南或demo开发，放入对应SO文件");
        map.put(167, "  定位失败，请确认您定位的开关打开状态，是否赋予APP定位权限");
        map.put(505, "  AK不存在或者非法\n" +
                "              :  请按照说明文档重新申请AK");
    }

    private boolean logErrorCode(int code) {
        Log.e(TAG, "LogErrorCode: " + map.get(code));
        if (code == 161 || code == 61) {
            return true;
        }
        return false;
    }
}

