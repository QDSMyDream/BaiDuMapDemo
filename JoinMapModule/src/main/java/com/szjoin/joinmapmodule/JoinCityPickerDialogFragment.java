package com.szjoin.joinmapmodule;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StyleRes;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.github.promeg.pinyinhelper.Pinyin;
import com.szjoin.joinmapmodule.adapter.JoinCityListAdapter;
import com.szjoin.joinmapmodule.bean.JoinCityBean;
import com.szjoin.joinmapmodule.bean.JoinCityHotBean;
import com.szjoin.joinmapmodule.bean.JoinLocatedCityBean;
import com.szjoin.joinmapmodule.citydb.JoinCityDataDBManager;
import com.szjoin.joinmapmodule.config.JoinCityPickerConfig;
import com.szjoin.joinmapmodule.decoration.JoinDividerItemDecoration;
import com.szjoin.joinmapmodule.decoration.JoinSectionItemDecoration;
import com.szjoin.joinmapmodule.interfaces.JoinInnerListener;
import com.szjoin.joinmapmodule.interfaces.JoinOnPickListener;
import com.szjoin.joinmapmodule.interfaces.JoinSearchActionInterface;
import com.szjoin.joinmapmodule.map.LocationService;
import com.szjoin.joinmapmodule.map.MapLocationReceiver;
import com.szjoin.joinmapmodule.map.MyLocationListener;
import com.szjoin.joinmapmodule.utils.JoinChineseSortUtil;
import com.szjoin.joinmapmodule.utils.JoinCityState;
import com.szjoin.joinmapmodule.utils.JoinMapUtils;
import com.szjoin.joinmapmodule.utils.ScreenUtil;
import com.szjoin.joinmapmodule.view.JoinCitySidebar;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * ????????????MyDream
 * ???????????????2021/06/10 10:05
 * ????????????????????????????????????
 */
public class JoinCityPickerDialogFragment extends DialogFragment implements TextWatcher, JoinInnerListener, View.OnClickListener, JoinCitySidebar.OnIndexTouchedChangedListener, MapLocationReceiver {
    private String TAG = getClass().getSimpleName();

    private LinearLayout cpSearchView;
    private EditText cpSearchBox;
    private ImageView cpClearAll;
    private TextView cpCancel;
    private RecyclerView cpCityRecyclerview;
    private TextView cpOverlay;
    private LinearLayout cpEmptyView;
    private ImageView cpNoResultIcon;
    private TextView cpNoResultText;
    private JoinCitySidebar cpSideIndexBar;

    private int mAnimStyle = R.style.DefaultCityPickerAnimation;//??????

    private View mContentView;

    private JoinLocatedCityBean joinLocatedCityBean;//????????????

    private List<JoinCityBean> mResults;

    private List<JoinCityBean> joinCityAllBeans;//??????????????????

    private List<JoinCityBean> joinCityCustomBeans;//???????????????

    private List<JoinCityHotBean> joinCityHotBeans;//??????????????????

    private List<JoinCityHotBean> joinCustomBeans;//?????????????????????

    private boolean enableAnim = false;//??????????????????

    private JoinCityPickerConfig joinCityPickerConfig;//??????

    private int locateState;//????????????

    private JoinCityDataDBManager joinCityDataDBManager;

    private JoinSearchActionInterface searchActionInterface;//????????????

    private JoinCityListAdapter joinCityListAdapter;//?????????

    private String iconTxt = "???";

    private JoinOnPickListener joinOnPickListener;

    private int height;
    private int width;
//    private LocationClient mClient;
    private MyLocationListener myLocationListener;

    @SuppressLint("ResourceType")
    public void setAnimationStyle(@StyleRes int resId) {
        this.mAnimStyle = resId <= 0 ? mAnimStyle : resId;
    }

    public void setIconTxt(String iconTxt) {
        this.iconTxt = iconTxt;
    }


    /**
     * ????????????
     *
     * @param enable ????????????????????????
     */
    public static JoinCityPickerDialogFragment newInstance(boolean enable, JoinCityPickerConfig config) {
        final JoinCityPickerDialogFragment fragment = new JoinCityPickerDialogFragment();
        Bundle args = new Bundle();
        args.putBoolean(JoinMapUtils.JOIN_CITY_DIALOG_IS_ANIM, enable);
        args.putSerializable(JoinMapUtils.JOIN_CITY_DIALOG_CONFIG, config);
        fragment.setArguments(args);
        return fragment;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mContentView = inflater.inflate(R.layout.cp_dialog_city_picker, container, false);
        return mContentView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initFindView();
        initViews();
        initData();
        initLocation();
    }

    private void initLocation() {
        myLocationListener = new MyLocationListener(this);
//        mClient = new LocationClient(getActivity().getApplicationContext());
//        mClient.setLocOption(LocationService.get().getDefaultLocationClientOption());
//        mClient.registerLocationListener(myLocationListener);
        LocationService.start(myLocationListener);
    }

    private void initFindView() {
        cpSearchView = (LinearLayout) mContentView.findViewById(R.id.cp_search_view);
        cpSearchBox = (EditText) mContentView.findViewById(R.id.cp_search_box);
        cpClearAll = (ImageView) mContentView.findViewById(R.id.cp_clear_all);
        cpCancel = (TextView) mContentView.findViewById(R.id.cp_cancel);
        cpCityRecyclerview = (RecyclerView) mContentView.findViewById(R.id.cp_city_recyclerview);
        cpOverlay = (TextView) mContentView.findViewById(R.id.cp_overlay);
        cpEmptyView = (LinearLayout) mContentView.findViewById(R.id.cp_empty_view);
        cpNoResultIcon = (ImageView) mContentView.findViewById(R.id.cp_no_result_icon);
        cpNoResultText = (TextView) mContentView.findViewById(R.id.cp_no_result_text);
        cpSideIndexBar = (JoinCitySidebar) mContentView.findViewById(R.id.cp_side_index_bar);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NORMAL, R.style.CityPickerStyle);
    }


    /**
     * ??????????????????
     */
    public void setLocatedCity(JoinLocatedCityBean joinLocatedCityBean) {
        this.joinLocatedCityBean = joinLocatedCityBean;
    }


    /**
     * ??????????????????
     */
    public void setHotCities(List<JoinCityHotBean> joinCustomBeans) {
        if (joinCustomBeans != null && !joinCustomBeans.isEmpty()) {
            this.joinCityHotBeans = joinCustomBeans;
        }
    }

    /**
     * ???????????????
     */
    public void setCustomModelList(List<JoinCityHotBean> data) {
        if (data != null && !data.isEmpty()) {
            this.joinCustomBeans = data;
        }
    }


    private void initViews() {
        Bundle args = getArguments();
        if (args != null) {
            enableAnim = args.getBoolean(JoinMapUtils.JOIN_CITY_DIALOG_IS_ANIM);
            joinCityPickerConfig = (JoinCityPickerConfig) args.getSerializable(JoinMapUtils.JOIN_CITY_DIALOG_CONFIG);
        }
        //?????????????????????
        if (joinCityHotBeans == null || joinCityHotBeans.isEmpty()) {
            // ?????????????????????????????????
            joinCityHotBeans = new ArrayList<>();
            joinCityHotBeans.add(new JoinCityHotBean("??????", "??????", "101010100").setType(JoinMapUtils.JOIN_CITY_FLAG_HOT));
            joinCityHotBeans.add(new JoinCityHotBean("??????", "??????", "101020100").setType(JoinMapUtils.JOIN_CITY_FLAG_HOT));
            joinCityHotBeans.add(new JoinCityHotBean("??????", "??????", "101280601").setType(JoinMapUtils.JOIN_CITY_FLAG_HOT));
            joinCityHotBeans.add(new JoinCityHotBean("??????", "??????", "101280101").setType(JoinMapUtils.JOIN_CITY_FLAG_HOT));
            joinCityHotBeans.add(new JoinCityHotBean("??????", "??????", "101210101").setType(JoinMapUtils.JOIN_CITY_FLAG_HOT));
            joinCityHotBeans.add(new JoinCityHotBean("??????", "??????", "101030100").setType(JoinMapUtils.JOIN_CITY_FLAG_HOT));
            joinCityHotBeans.add(new JoinCityHotBean("??????", "??????", "101190101").setType(JoinMapUtils.JOIN_CITY_FLAG_HOT));
            joinCityHotBeans.add(new JoinCityHotBean("??????", "??????", "101200101").setType(JoinMapUtils.JOIN_CITY_FLAG_HOT));
            joinCityHotBeans.add(new JoinCityHotBean("??????", "??????", "101270101").setType(JoinMapUtils.JOIN_CITY_FLAG_HOT));
            joinCityHotBeans.add(new JoinCityHotBean("??????", "??????", "101270101").setType(JoinMapUtils.JOIN_CITY_FLAG_HOT));
        }
        //????????????????????????????????????????????????????????????
        if (joinLocatedCityBean == null) {
            joinLocatedCityBean = new JoinLocatedCityBean(getString(R.string.cp_locating), "??????", "0").setType(JoinMapUtils.JOIN_CITY_FLAG_LOCATION);
            locateState = JoinCityState.LOCATING;
        } else {
            locateState = JoinCityState.SUCCESS;
        }

        // ???????????????????????????????????????
        if (!joinCityPickerConfig.isUseCustomData()) {
            joinCityDataDBManager = new JoinCityDataDBManager(getActivity());
            joinCityAllBeans = joinCityDataDBManager.getAllCities();
        } else {
            joinCityAllBeans = joinCityCustomBeans;
        }

        // ?????????????????????
        initPinyinData(joinCityAllBeans);

        // ??????????????????
        if (joinCityPickerConfig.isShowHotCities()) {
            JoinCityHotBean hotCity = new JoinCityHotBean(joinCityPickerConfig.hasSetStrHotCities() ? joinCityPickerConfig.getStrHotCities() : "????????????", "??????", "0");
            hotCity.setHot();
            joinCityAllBeans.add(0, hotCity);
        }

        // ???????????????????????????
        if (joinCityPickerConfig.isUseCustomModel()) {
            JoinCityHotBean hotCity = new JoinCityHotBean(joinCityPickerConfig.hasSetStrCustomModel() ? joinCityPickerConfig.getStrCustomModelTitle() : "??????????????????",
                    "??????", "0");
            joinCityAllBeans.add(0, hotCity);
        }
        // ??????????????????
        if (joinCityPickerConfig.isShowLocation()) {
            joinCityAllBeans.add(0, joinLocatedCityBean);
        }
        joinCityPickerConfig.setStrHotCities("????????????");

        mResults = joinCityAllBeans;
    }


    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        String keyword = s.toString();
        if (TextUtils.isEmpty(keyword)) {
            cpClearAll.setVisibility(View.GONE);
            cpEmptyView.setVisibility(View.GONE);
            mResults = joinCityAllBeans;
            ((JoinSectionItemDecoration) (cpCityRecyclerview.getItemDecorationAt(0))).setData(mResults);
            joinCityListAdapter.updateData(mResults);
        } else {
            cpClearAll.setVisibility(View.VISIBLE);
            if (searchActionInterface != null) {
                searchActionInterface.search(keyword);
            } else {
                //?????????????????????
                mResults = joinCityDataDBManager.searchCity(keyword);
                ((JoinSectionItemDecoration) (cpCityRecyclerview.getItemDecorationAt(0))).setData(mResults);
                if (mResults == null || mResults.isEmpty()) {
                    cpEmptyView.setVisibility(View.VISIBLE);
                } else {
                    cpEmptyView.setVisibility(View.GONE);
                    joinCityListAdapter.updateData(mResults);
                }
            }
        }
        cpCityRecyclerview.scrollToPosition(0);
    }


    private void initData() {
        cpCityRecyclerview = mContentView.findViewById(R.id.cp_city_recyclerview);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        cpCityRecyclerview.setLayoutManager(mLayoutManager);
        cpCityRecyclerview.setHasFixedSize(true);
        cpCityRecyclerview.addItemDecoration(new JoinSectionItemDecoration(getActivity(), joinCityAllBeans), 0);
        cpCityRecyclerview.addItemDecoration(new JoinDividerItemDecoration(getActivity(), joinCityAllBeans), 1);
        joinCityListAdapter = new JoinCityListAdapter(getActivity(), joinCityAllBeans, joinCityHotBeans, joinCustomBeans, locateState);
        joinCityListAdapter.autoLocate(true);
        joinCityListAdapter.setInnerListener(this);
        joinCityListAdapter.setLayoutManager(mLayoutManager);
        joinCityListAdapter.setIconTxt(iconTxt);
        joinCityListAdapter.setConfig(joinCityPickerConfig);

        cpCityRecyclerview.setAdapter(joinCityListAdapter);
        cpCityRecyclerview.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                //?????????????????????????????????
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    joinCityListAdapter.refreshLocationItem();
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            }
        });

        cpEmptyView = mContentView.findViewById(R.id.cp_empty_view);
        cpOverlay = mContentView.findViewById(R.id.cp_overlay);

        cpSideIndexBar = mContentView.findViewById(R.id.cp_side_index_bar);
        cpSideIndexBar.setCutomLittleTitle(joinCityPickerConfig.getStrLittleCustomModel());
        cpSideIndexBar.setNavigationBarHeight(ScreenUtil.getNavigationBarHeight(getActivity()));
        cpSideIndexBar.setOverlayTextView(cpOverlay)
                .setOnIndexChangedListener(this);

        cpSearchBox = mContentView.findViewById(R.id.cp_search_box);
        cpSearchBox.addTextChangedListener(this);

        cpCancel = mContentView.findViewById(R.id.cp_cancel);
        cpClearAll = mContentView.findViewById(R.id.cp_clear_all);
        cpCancel.setOnClickListener(this);
        cpClearAll.setOnClickListener(this);


    }

    private void initPinyinData(List<JoinCityBean> list) {
        for (JoinCityBean city : list) {
            if (TextUtils.isEmpty(city.getPinyin())) {
                // ?????????????????????
                city.setPinyin(Pinyin.toPinyin(city.getName().charAt(0)));
            }
        }
        // ??????
        JoinChineseSortUtil.sortList(list);
        joinCityAllBeans = list;
    }


    /**
     * ??????????????????
     *
     * @param _searchActionInterface
     */
    public void searchInterface(JoinSearchActionInterface _searchActionInterface) {
        this.searchActionInterface = _searchActionInterface;

    }


    @Override
    public void dismiss(int position, JoinCityBean data) {
        dismiss();
        if (joinOnPickListener != null) {
            joinOnPickListener.onPick(position, data);
        }

    }

    @Override
    public void onclick(int position, JoinCityBean city) {
        dismiss();
        if (joinOnPickListener != null) {
            joinOnPickListener.onPick(position, city);
        }

    }

    @Override
    public void locate() {
        Log.e(TAG, "locate: ");

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.cp_cancel) {
            dismiss();
            if (joinOnPickListener != null) {
                joinOnPickListener.onCancel();
            }
        } else if (id == R.id.cp_clear_all) {
            cpSearchBox.setText("");
        }
    }

    @Override
    public void onIndexChanged(String index, int position) {
        //??????RecyclerView???????????????
        joinCityListAdapter.scrollToSection(index);
    }

    public void locationChanged(JoinLocatedCityBean location, int state) {
        joinCityListAdapter.updateLocateState(location, state);
    }

    /**
     * ????????????
     */
    public void updateResult(List<JoinCityBean> _mResults) {
        for (JoinCityBean city : _mResults) {
            if (TextUtils.isEmpty(city.getPinyin())) {
                // ?????????????????????
                city.setPinyin(Pinyin.toPinyin(city.getName().charAt(0)));
            }
        }
        // ??????
        JoinChineseSortUtil.sortList(_mResults);

        ((JoinSectionItemDecoration) (cpCityRecyclerview.getItemDecorationAt(0))).setData(_mResults);
        if (_mResults == null || _mResults.isEmpty()) {
            cpEmptyView.setVisibility(View.VISIBLE);
        } else {
            cpEmptyView.setVisibility(View.GONE);
            joinCityListAdapter.updateData(_mResults);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    if (joinOnPickListener != null) {
                        joinOnPickListener.onCancel();
                    }
                }
                return false;
            }
        });

        measure();

        Window window = dialog.getWindow();
        if (window != null) {
            window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
            window.setGravity(Gravity.BOTTOM);
            window.setLayout(width, height - ScreenUtil.getStatusBarHeight(getActivity()));
            if (enableAnim) {
                window.setWindowAnimations(mAnimStyle);
            }
        }
    }

    //????????????
    private void measure() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            DisplayMetrics dm = new DisplayMetrics();
            getActivity().getWindowManager().getDefaultDisplay().getRealMetrics(dm);
            height = dm.heightPixels;
            width = dm.widthPixels;
        } else {
            DisplayMetrics dm = getResources().getDisplayMetrics();
            height = dm.heightPixels;
            width = dm.widthPixels;
        }
    }

    public void setOnPickListener(JoinOnPickListener listener) {
        this.joinOnPickListener = listener;
    }

    public void setCustomData(ArrayList<JoinCityBean> custom_listdata) {
        this.joinCityCustomBeans = custom_listdata;

    }

    @Override
    public void onReceiveLocation(BDLocation bdLocation) {
        Log.e(TAG, "onReceiveLocation: " + bdLocation.getCity());
        if (!TextUtils.isEmpty(bdLocation.getCity())) {
            locationChanged(new JoinLocatedCityBean(bdLocation.getCity(),
                    bdLocation.getProvince(), bdLocation.getCityCode(),
                    bdLocation.getLatitude() + "", bdLocation.getLongitude() + ""), JoinCityState.SUCCESS

            );
            LocationService.stop(myLocationListener);
        }


    }
}
