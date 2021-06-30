package com.szjoin.baidumapdemo;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;


import androidx.fragment.app.FragmentActivity;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;

import com.szjoin.joinmapmodule.JoinCityPicker;
import com.szjoin.joinmapmodule.bean.JoinCityBean;
import com.szjoin.joinmapmodule.interfaces.JoinOnPickListener;
import com.szjoin.joinmapmodule.map.LocationService;
import com.szjoin.joinmapmodule.map.MapLocationReceiver;
import com.szjoin.joinmapmodule.map.MyLocationListener;
import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.runtime.Permission;

import java.util.List;

public class MainActivity extends FragmentActivity implements MapLocationReceiver {
    private String TAG = getClass().getSimpleName();
    private TextView tv1;
    private TextView tv2;
    private TextView tv3;
    private Button selectBtn;
    private MyLocationListener myLocationListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initPermission();
        //        百度定位
        LocationService.get().init(getApplicationContext());
        initView();
    }


    private void initPermission() {
        AndPermission.with(this)
                .runtime()
                .permission(Permission.ACCESS_FINE_LOCATION)
                .onGranted(new Action<List<String>>() {
                    @Override
                    public void onAction(List<String> permissions) {
                        // Storage permission are allowed.
                        Log.e(TAG, "initPermission:onGranted ");
                    }
                })
                .onDenied(new Action<List<String>>() {
                    @Override
                    public void onAction(List<String> permissions) {
                        // Storage permission are not allowed.
                        Log.e(TAG, "initPermission:onDenied ");
                    }
                })
                .start();
    }

    private void initView() {
        tv1 = (TextView) findViewById(R.id.tv1);
        tv2 = (TextView) findViewById(R.id.tv2);
        tv3 = (TextView) findViewById(R.id.tv3);
        selectBtn = (Button) findViewById(R.id.select_btn);
        selectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickCity();
            }
        });
        myLocationListener = new MyLocationListener(this);

        LocationService.start(myLocationListener);
    }

    private void pickCity() {
        JoinCityPicker joinCityPicker = JoinCityPicker.getInstance(this);
        joinCityPicker.setHotCities(true);
        joinCityPicker.setLocatedCity(true);
        joinCityPicker.setOnPickListener(new JoinOnPickListener() {
            @Override
            public void onPick(int position, JoinCityBean data) {
                Log.e(TAG, "onPick: " + data.toString());
                tv1.setText(data.getName());
                tv2.setText(data.getLatitude());
                tv3.setText(data.getLongitude());
            }

            @Override
            public void onLocate() {
                Log.e(TAG, "onLocate: ");
            }

            @Override
            public void onCancel() {
                Log.e(TAG, "onCancel: ");
            }
        }).showDialog();

    }

    @Override
    public void onReceiveLocation(BDLocation location) {
        String name = location.getCity();

        //此处的BDLocation为定位结果信息类，通过它的各种get方法可获取定位相关的全部结果
        //以下只列举部分获取经纬度相关（常用）的结果信息
        //更多结果信息获取说明，请参照类参考中BDLocation类中的说明

        double latitude = location.getLatitude();    //获取纬度信息
        double longitude = location.getLongitude();    //获取经度信息
        tv2.setText(latitude + "");
        tv3.setText(longitude + "");

        float radius = location.getRadius();    //获取定位精度，默认值为0.0f

        String coorType = location.getCoorType();
        //获取经纬度坐标类型，以LocationClientOption中设置过的坐标类型为准

        int errorCode = location.getLocType();
        //获取定位类型、定位错误返回码，具体信息可参照类参考中BDLocation类中的说明
        Log.e(TAG, "onReceiveLocation: 获取纬度信息" + latitude);
        Log.e(TAG, "onReceiveLocation: 获取经度信息" + longitude);
        Log.e(TAG, "onReceiveLocation: 获取定位精度" + radius);
        Log.e(TAG, "onReceiveLocation: 获取经纬度坐标类型" + coorType);

        if (name != null && !name.equals("")) {
            tv1.setText(name == null ? "null" : name);
            LocationService.stop(myLocationListener);
        }

    }


    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {


    }
}