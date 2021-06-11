package com.szjoin.joinmapmodule.map;

import android.content.Context;
import android.util.Log;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.Poi;
import com.szjoin.joinmapmodule.bean.JoinCityBean;


/**
 * 创建人：MyDream
 * 创建日期：2021/06/08 15:55
 * 类描述：
 */
public class BaiduMapLocation {
    private static BaiduMapLocation baiduMapLocation = null;
    public LocationClient mLocationClient = null;
    public Context context;
    public JoinCityBean baiduLocationCity;
    //注册监听函数
    public LocationClientOption option;
    public MyLocationListener myLocationListener;

    public BaiduMapLocation() {

    }

    public static BaiduMapLocation getInstance() {
        if (baiduMapLocation == null) {
            baiduMapLocation = new BaiduMapLocation();
        }
        return baiduMapLocation;
    }

    public void initBaiduMap(Context context, MapLocationReceiver mapLocationReceiver) {
        this.context = context;
        mLocationClient = new LocationClient(context);
        myLocationListener = new MyLocationListener(mapLocationReceiver);
        //声明LocationClient类
        mLocationClient.registerLocationListener(myLocationListener);
        initBaiduMapOption();


    }

    private void initBaiduMapOption() {
        //注册监听函数
        option = new LocationClientOption();

        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
//可选，设置定位模式，默认高精度
//LocationMode.Hight_Accuracy：高精度；
//LocationMode. Battery_Saving：低功耗；
//LocationMode. Device_Sensors：仅使用设备；

        option.setCoorType("bd09ll");
//可选，设置返回经纬度坐标类型，默认GCJ02
//GCJ02：国测局坐标；
//BD09ll：百度经纬度坐标；
//BD09：百度墨卡托坐标；
//海外地区定位，无需设置坐标类型，统一返回WGS84类型坐标

        option.setScanSpan(10000);
//可选，设置发起定位请求的间隔，int类型，单位ms
//如果设置为0，则代表单次定位，即仅定位一次，默认为0
//如果设置非0，需设置1000ms以上才有效

        option.setOpenGps(true);
//可选，设置是否使用gps，默认false
//使用高精度和仅用设备两种定位模式的，参数必须设置为true

        option.setLocationNotify(true);
//可选，设置是否当GPS有效时按照1S/1次频率输出GPS结果，默认false
//        option.setIgnoreKillProcess(false);
////可选，定位SDK内部是一个service，并放到了独立进程。
////设置是否在stop的时候杀死这个进程，默认（建议）不杀死，即setIgnoreKillProcess(true)

        option.SetIgnoreCacheException(false);
//可选，设置是否收集Crash信息，默认收集，即参数为false

        option.setWifiCacheTimeOut(5 * 60 * 1000);
//可选，V7.2版本新增能力
//如果设置了该接口，首次启动定位时，会先判断当前Wi-Fi是否超出有效期，若超出有效期，会先重新扫描Wi-Fi，然后定位

        option.setEnableSimulateGps(false);
//可选，设置是否需要过滤GPS仿真结果，默认需要，即参数为false

        option.setNeedNewVersionRgc(true);
//可选，设置是否需要最新版本的地址信息。默认需要，即参数为true

        option.setIsNeedAddress(true);

        mLocationClient.setLocOption(option);
//mLocationClient为第二步初始化过的LocationClient对象
//需将配置好的LocationClientOption对象，通过setLocOption方法传递给LocationClient对象使用
//更多LocationClientOption的配置，请参照类参考中LocationClientOption类的详细说明


    }

    /**
     * 停止定位
     */
    public void stopReceiver() {
        if (mLocationClient != null) {
            mLocationClient.unRegisterLocationListener(myLocationListener);
            mLocationClient.stop();
        }

    }

    /**
     * 停止定位
     */
    public void stopReceiver(BDAbstractLocationListener bdAbstractLocationListener) {
        if (mLocationClient != null) {
            mLocationClient.unRegisterLocationListener(bdAbstractLocationListener);
            mLocationClient.stop();
        }
    }


    /**
     * 开始定位
     */
    public void startReceiver(BDAbstractLocationListener bdAbstractLocationListener) {
        if (mLocationClient != null && !mLocationClient.isStarted()) {
            mLocationClient.registerLocationListener(bdAbstractLocationListener);
            mLocationClient.start();
        }
    }

    /**
     * 开始定位
     */
    public void startReceiver() {
        if (mLocationClient != null && !mLocationClient.isStarted()) {
            mLocationClient.registerLocationListener(myLocationListener);
            mLocationClient.start();
        }
    }

    public MyLocationListener getLocationListener() {
        return myLocationListener;
    }

    /**
     * 打印地址信息
     *
     * @param location
     */
    public static void printLocationInfo(BDLocation location) {
        if (null != location && location.getLocType() != BDLocation.TypeServerError) {
            StringBuilder sb = new StringBuilder(256);
            sb.append("time : ");
            /**
             * 时间也可以使用systemClock.elapsedRealtime()方法 获取的是自从开机以来，每次回调的时间；
             * location.getTime() 是指服务端出本次结果的时间，如果位置不发生变化，则时间不变
             */
            sb.append(location.getTime());
            sb.append("\nlocType : ");// 定位类型
            sb.append(location.getLocType());
            sb.append("\nlocType description : ");// *****对应的定位类型说明*****
            sb.append(location.getLocTypeDescription());
            sb.append("\nlatitude : ");// 纬度
            sb.append(location.getLatitude());
            sb.append("\nlontitude : ");// 经度
            sb.append(location.getLongitude());
            sb.append("\nradius : ");// 半径
            sb.append(location.getRadius());
            sb.append("\nCountryCode : ");// 国家码
            sb.append(location.getCountryCode());
            sb.append("\nCountry : ");// 国家名称
            sb.append(location.getCountry());
            sb.append("\ncitycode : ");// 城市编码
            sb.append(location.getCityCode());
            sb.append("\ncity : ");// 城市
            sb.append(location.getCity());
            sb.append("\nDistrict : ");// 区
            sb.append(location.getDistrict());
            sb.append("\nStreet : ");// 街道
            sb.append(location.getStreet());
            sb.append("\naddr : ");// 地址信息
            sb.append(location.getAddrStr());
            sb.append("\nUserIndoorState: ");// *****返回用户室内外判断结果*****
            sb.append(location.getUserIndoorState());
            sb.append("\nDirection(not all devices have value): ");
            sb.append(location.getDirection());// 方向
            sb.append("\nlocationdescribe: ");
            sb.append(location.getLocationDescribe());// 位置语义化信息
            sb.append("\nPoi: ");// POI信息
            if (location.getPoiList() != null && !location.getPoiList().isEmpty()) {
                for (int i = 0; i < location.getPoiList().size(); i++) {
                    Poi poi = location.getPoiList().get(i);
                    sb.append(poi.getName() + ";");
                }
            }
            if (location.getLocType() == BDLocation.TypeGpsLocation) {// GPS定位结果
                sb.append("\nspeed : ");
                sb.append(location.getSpeed());// 速度 单位：km/h
                sb.append("\nsatellite : ");
                sb.append(location.getSatelliteNumber());// 卫星数目
                sb.append("\nheight : ");
                sb.append(location.getAltitude());// 海拔高度 单位：米
                sb.append("\ngps status : ");
                sb.append(location.getGpsAccuracyStatus());// *****gps质量判断*****
                sb.append("\ndescribe : ");
                sb.append("gps定位成功");
            } else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {// 网络定位结果
                // 运营商信息
                if (location.hasAltitude()) {// *****如果有海拔高度*****
                    sb.append("\nheight : ");
                    sb.append(location.getAltitude());// 单位：米
                }
                sb.append("\noperationers : ");// 运营商信息
                sb.append(location.getOperators());
                sb.append("\ndescribe : ");
                sb.append("网络定位成功");
            } else if (location.getLocType() == BDLocation.TypeOffLineLocation) {// 离线定位结果
                sb.append("\ndescribe : ");
                sb.append("离线定位成功，离线定位结果也是有效的");
            } else if (location.getLocType() == BDLocation.TypeServerError) {
                sb.append("\ndescribe : ");
                sb.append("服务端网络定位失败，可以反馈IMEI号和大体定位时间到loc-bugs@baidu.com，会有人追查原因");
            } else if (location.getLocType() == BDLocation.TypeNetWorkException) {
                sb.append("\ndescribe : ");
                sb.append("网络不同导致定位失败，请检查网络是否通畅");
            } else if (location.getLocType() == BDLocation.TypeCriteriaException) {
                sb.append("\ndescribe : ");
                sb.append("无法获取有效定位依据导致定位失败，一般是由于手机的原因，处于飞行模式下一般会造成这种结果，可以试着重启手机");
            }
            Log.e("BaiduMapLocation", "printLocationInfo: " + sb.toString());
        }
    }

    /**
     * 当收到定位信息
     *
     * @param bdLocation
     * @return
     */
    public JoinCityBean onReceiveLocation(BDLocation bdLocation) {
        if (baiduLocationCity == null || bdLocation.getCity() != null) {
            baiduLocationCity = new JoinCityBean(bdLocation.getCity(), bdLocation.getProvince(), bdLocation.getCityCode(), bdLocation.getLatitude() + "", bdLocation.getLongitude() + "");
        }
        return baiduLocationCity;
    }
}
