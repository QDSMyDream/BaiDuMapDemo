package com.szjoin.joinmapmodule.interfaces;

import com.szjoin.joinmapmodule.bean.JoinCityBean;

import java.util.List;

/**
 * 创建人：MyDream
 * 创建日期：2021/06/10 10:39
 * 类描述：声明操作接口
 */
public interface IJoinCityDataDb {
    /**
     * 获取所有城市信息
     *
     * @return 所有城市信息的集合
     */
    List<JoinCityBean> getAllCities();

    /**
     * 搜索城市
     *
     * @param keyword
     * @return 搜索结果
     */
    List<JoinCityBean> searchCity(final String keyword);
}
