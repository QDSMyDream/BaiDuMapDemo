package com.szjoin.joinmapmodule;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.StyleRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.baidu.location.LocationClient;
import com.szjoin.joinmapmodule.bean.JoinCityBean;
import com.szjoin.joinmapmodule.bean.JoinCityHotBean;
import com.szjoin.joinmapmodule.bean.JoinLocatedCityBean;
import com.szjoin.joinmapmodule.config.JoinCityPickerConfig;
import com.szjoin.joinmapmodule.interfaces.JoinOnPickListener;
import com.szjoin.joinmapmodule.interfaces.JoinSearchActionInterface;
import com.szjoin.joinmapmodule.map.MyLocationListener;
import com.szjoin.joinmapmodule.utils.JoinCityState;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;


/**
 * 创建人：MyDream
 * 创建日期：2021/06/10 09:56
 * 类描述：城市选择器   默认集成百度地图定位
 */
public class JoinCityPicker  {
    private final String TAG = getClass().getSimpleName();

    private WeakReference<FragmentActivity> mContext;

    private WeakReference<Fragment> mFragment;

    private WeakReference<FragmentManager> mFragmentManager;

    private Context context;

    private boolean enableAnim;

    //动画
    private int mAnimStyle;

    private JoinLocatedCityBean mLocation;
    private List<JoinCityHotBean> mHotCities;
    private List<JoinCityHotBean> mCustomModelData;
    private JoinOnPickListener mOnPickListener;
    // 新增的参数
    private JoinCityPickerConfig cityPickerConfig = new JoinCityPickerConfig();
    private ArrayList<JoinCityBean> custom_listdata;

    private MyLocationListener myLocationListener;
    private JoinSearchActionInterface searchActionInterface;//搜索回调接口

    private LocationClient mClient;


    private JoinCityPicker() {

    }

    private JoinCityPicker(Fragment fragment) {
        this(fragment.getActivity(), fragment);
        mFragmentManager = new WeakReference<>(fragment.getChildFragmentManager());
    }

    private JoinCityPicker(FragmentActivity activity) {
        this(activity, null);
        mFragmentManager = new WeakReference<>(activity.getSupportFragmentManager());
    }

    private JoinCityPicker(FragmentActivity activity, Fragment fragment) {
        context = activity.getApplicationContext();
        mContext = new WeakReference<>(activity);
        mFragment = new WeakReference<>(fragment);
//        cityPickerConfig = new CityPickerConfig();
    }

    public static JoinCityPicker getInstance(Fragment fragment) {
        return new JoinCityPicker(fragment);
    }

    public static JoinCityPicker getInstance(FragmentActivity activity) {
        return new JoinCityPicker(activity);
    }


    /**
     * 设置动画效果
     *
     * @param animStyle
     * @return
     */
    public JoinCityPicker setAnimationStyle(@StyleRes int animStyle) {
        this.mAnimStyle = animStyle;
        return this;
    }


    /**
     * 设置是否使用自定义数据模块
     *
     * @param enable           是否开启自定义数据模块
     * @param title            自定义数据模块的标题
     * @param titleLittle      右侧导航栏的显示(最多二字)
     * @param mCustomModelData 自定义数据模块的列表数据
     * @return
     */
    public JoinCityPicker setCustomModel(boolean enable, String title, String titleLittle, List<JoinCityHotBean> mCustomModelData) {
        cityPickerConfig.setUseCustomModel(enable);
        if (!enable) {
            return this;
        }
        this.mCustomModelData = mCustomModelData;
        cityPickerConfig.setStrCustomModelTitle(TextUtils.isEmpty(title) ? "自定义模块" : title);
        cityPickerConfig.setStrLittleCustomModel(TextUtils.isEmpty(title) ? "自定" : titleLittle);
        return this;
    }

    public JoinCityPicker setCustomModel(boolean enable) {
        cityPickerConfig.setUseCustomModel(enable);
        return this;
    }

    /**
     * 设置自定义模块内容
     *
     * @param location 定位信息
     * @return
     */
    public JoinCityPicker setLocatedCity(boolean enable, JoinLocatedCityBean location) {
        cityPickerConfig.setShowLocation(enable);
        if (!enable) {
            return this;
        }
        this.mLocation = location;
        return this;
    }

    /**
     * 设置自定义模块内容
     *
     * @return
     */
    public JoinCityPicker setLocatedCity(boolean enable) {
        cityPickerConfig.setShowLocation(enable);

        return this;
    }


    /**
     * 设置热门城市信息
     *
     * @param enable 是否启用热门城市
     * @param data   热门城市数据,如需使用默认则设为null
     * @return
     */
    public JoinCityPicker setHotCities(boolean enable, List<JoinCityHotBean> data) {
        cityPickerConfig.setShowHotCities(enable);
        if (!enable) {
            return this;
        }
        this.mHotCities = data;
        return this;
    }

    public JoinCityPicker setHotCities(boolean enable) {
        cityPickerConfig.setShowHotCities(enable);
        return this;
    }


    /**
     * 设置热门城市部分内容
     *
     * @param title   设置“热门城市”标题
     * @param iconTxt 设置“热门城市”模块下的标记内容
     * @return
     */
    public JoinCityPicker setHotModel(String title, String iconTxt) {
        if (title == null || TextUtils.isEmpty(title)) {
            cityPickerConfig.setStrHotCities("热门城市");
        } else {
            cityPickerConfig.setStrHotCities(title);
        }
        if (iconTxt == null || TextUtils.isEmpty(iconTxt)) {
            cityPickerConfig.setStrHotCitiesIcon("热");
        } else {
            cityPickerConfig.setStrHotCitiesIcon(iconTxt);
        }
        return this;
    }

    /**
     * 自定义配置文件
     *
     * @param config
     * @return
     */
    public JoinCityPicker setConfig(boolean enable, JoinCityPickerConfig config) {
        this.cityPickerConfig = config;
        return this;
    }

    /**
     * 自定义数据
     *
     * @param enable   是否启用自定义数据，启用自定义数据将会代替现有数据库数据
     * @param listdata 自定义数据
     * @return
     */
    public JoinCityPicker setCustomData(boolean enable, ArrayList<JoinCityBean> listdata) {
        cityPickerConfig.setUseCustomData(enable);
        if (!enable) {
            return this;
        }
        this.custom_listdata = listdata;
        return this;
    }

    public JoinCityPicker setCustomData(boolean enable) {
        cityPickerConfig.setUseCustomData(enable);
        return this;
    }

    /**
     * 启用动画效果，默认为false
     *
     * @param enable    是否开启动画
     * @param animStyle 动画效果 如果enable为true且animStyle为0，则自动加载默认动画
     * @return
     */
    public JoinCityPicker setAnimation(boolean enable, int animStyle) {
        this.enableAnim = enable;
        if (!enable) {
            return this;
        }
        if (enableAnim && animStyle == 0) {
            setAnimationStyle(R.style.DefaultCityPickerAnimation);
        } else {
            setAnimationStyle(animStyle);
        }
        return this;
    }

    public JoinCityPicker setAnimation(boolean enable) {
        this.enableAnim = enable;
        if (enableAnim) {
            setAnimationStyle(R.style.DefaultCityPickerAnimation);
        }
        return this;
    }

    /**
     * 设置选择结果的监听器
     *
     * @param listener
     * @return
     */
    public JoinCityPicker setOnPickListener(JoinOnPickListener listener) {
        this.mOnPickListener = listener;
        return this;
    }


    /**
     * 设置搜索回调
     */
    public JoinCityPicker searchInterface(JoinSearchActionInterface _searchActionInterface) {
        Log.e("zhuxu", " set searchInterface is 1 " + (_searchActionInterface == null));
        searchActionInterface = _searchActionInterface;
        return this;
    }


    public void showDialog() {


        FragmentTransaction ft = mFragmentManager.get().beginTransaction();
        final Fragment prev = mFragmentManager.get().findFragmentByTag(TAG);
        if (prev != null) {
            ft.remove(prev).commit();
            ft = mFragmentManager.get().beginTransaction();
        }
        ft.addToBackStack(null);
        final JoinCityPickerDialogFragment joinCityPickerDialogFragment = JoinCityPickerDialogFragment.newInstance(enableAnim, cityPickerConfig);
        joinCityPickerDialogFragment.setLocatedCity(mLocation);
        joinCityPickerDialogFragment.setHotCities(mHotCities);
        joinCityPickerDialogFragment.setCustomModelList(mCustomModelData);
        joinCityPickerDialogFragment.setAnimationStyle(mAnimStyle);
        joinCityPickerDialogFragment.setIconTxt(cityPickerConfig.getStrHotCitiesIcon());
        joinCityPickerDialogFragment.setOnPickListener(mOnPickListener);
        joinCityPickerDialogFragment.searchInterface(searchActionInterface);
        if (cityPickerConfig.isUseCustomData()) {
            joinCityPickerDialogFragment.setCustomData(custom_listdata);
        }
//        if (cityPickerConfig.isUseCustomHotData()) {
//            cityPickerFragment.setCustomHotData(custom_hot_listdata);
//        }
        joinCityPickerDialogFragment.show(ft, TAG);



    }

    /**
     * 定位完成
     *
     * @param location
     * @param state
     */
    public void locateComplete(JoinLocatedCityBean location, @JoinCityState.State int state) {
        JoinCityPickerDialogFragment fragment = (JoinCityPickerDialogFragment) mFragmentManager.get().findFragmentByTag(TAG);
        if (fragment != null) {
            fragment.locationChanged(location, state);
        }
    }

    /**
     * 更新数据
     */
    public void updateResult(List<JoinCityBean> _mResults) {
        JoinCityPickerDialogFragment fragment = (JoinCityPickerDialogFragment) mFragmentManager.get().findFragmentByTag(TAG);
        if (fragment != null) {
            fragment.updateResult(_mResults);
        }
    }





}
