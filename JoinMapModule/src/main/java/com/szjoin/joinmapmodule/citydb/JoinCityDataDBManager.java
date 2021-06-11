package com.szjoin.joinmapmodule.citydb;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import androidx.fragment.app.FragmentActivity;

import com.github.promeg.pinyinhelper.Pinyin;
import com.szjoin.joinmapmodule.bean.JoinCityBean;
import com.szjoin.joinmapmodule.interfaces.IJoinCityDataDb;
import com.szjoin.joinmapmodule.utils.JoinMapUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * 创建人：MyDream
 * 创建日期：2021/06/10 10:39
 * 类描述：城市数据库管理类
 */
public class JoinCityDataDBManager implements IJoinCityDataDb {
    private static final int BUFFER_SIZE = 1024;
    private String DB_PATH;
    private Context mContext;


    public JoinCityDataDBManager(Context context) {
        this.mContext = context;
        DB_PATH = File.separator + "data"
                + Environment.getDataDirectory().getAbsolutePath() + File.separator
                + context.getPackageName() + File.separator + "databases" + File.separator;
        copyDBFile();
    }

    private void copyDBFile() {
        File dir = new File(DB_PATH);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        //如果旧版数据库存在，则删除
        File dbV1 = new File(DB_PATH + JoinCityDBConfig.DB_NAME);
        if (dbV1.exists()) {
            dbV1.delete();
        }
        //创建新版本数据库
        File dbFile = new File(DB_PATH + JoinCityDBConfig.LATEST_DB_NAME);
        if (!dbFile.exists()) {
            InputStream is;
            OutputStream os;
            try {
                is = mContext.getResources().getAssets().open(JoinCityDBConfig.LATEST_DB_NAME);
                os = new FileOutputStream(dbFile);
                byte[] buffer = new byte[BUFFER_SIZE];
                int length;
                while ((length = is.read(buffer, 0, buffer.length)) > 0) {
                    os.write(buffer, 0, length);
                }
                os.flush();
                os.close();
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public List<JoinCityBean> getAllCities() {
        SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(DB_PATH + JoinCityDBConfig.LATEST_DB_NAME, null);
        Cursor cursor = db.rawQuery("select * from " + JoinCityDBConfig.TABLE_NAME, null);
        List<JoinCityBean> result = new ArrayList<>();
        JoinCityBean city;
        while (cursor.moveToNext()) {
            String name = cursor.getString(cursor.getColumnIndex(JoinCityDBConfig.COLUMN_C_NAME));
            String province = cursor.getString(cursor.getColumnIndex(JoinCityDBConfig.COLUMN_C_PROVINCE));
            String pinyin = cursor.getString(cursor.getColumnIndex(JoinCityDBConfig.COLUMN_C_PINYIN));
            String code = cursor.getString(cursor.getColumnIndex(JoinCityDBConfig.COLUMN_C_CODE));
            city = new JoinCityBean(name, province, pinyin, code);
            city.setType(JoinMapUtils.JOIN_CITY_FLAG_LIST);
            result.add(city);
        }
        Log.e("zhuxu", "city size is " + result.size());
        cursor.close();
        db.close();
        Collections.sort(result, new CityComparator());
        return result;
    }

    public List<JoinCityBean> searchCity(final String keyword) {
        String sql = "select * from " + JoinCityDBConfig.TABLE_NAME + " where "
                + JoinCityDBConfig.COLUMN_C_NAME + " like ? " + "or "
                + JoinCityDBConfig.COLUMN_C_PINYIN + " like ? ";
        SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(DB_PATH + JoinCityDBConfig.LATEST_DB_NAME, null);
        Cursor cursor = db.rawQuery(sql, new String[]{"%" + keyword + "%", keyword + "%"});

        List<JoinCityBean> result = new ArrayList<>();
        while (cursor.moveToNext()) {
            String name = cursor.getString(cursor.getColumnIndex(JoinCityDBConfig.COLUMN_C_NAME));
            String province = cursor.getString(cursor.getColumnIndex(JoinCityDBConfig.COLUMN_C_PROVINCE));
            String pinyin = cursor.getString(cursor.getColumnIndex(JoinCityDBConfig.COLUMN_C_PINYIN));
            String code = cursor.getString(cursor.getColumnIndex(JoinCityDBConfig.COLUMN_C_CODE));
            JoinCityBean city = new JoinCityBean(name, province, pinyin, code);
            result.add(city);
        }
        cursor.close();
        db.close();
        CityComparator comparator = new CityComparator();
        Collections.sort(result, comparator);
        return result;
    }


    /**
     * sort by a-z
     */
    private class CityComparator implements Comparator<JoinCityBean> {
        @Override
        public int compare(JoinCityBean lhs, JoinCityBean rhs) {
            if (TextUtils.isEmpty(lhs.getPinyin())) {
                lhs.setPinyin(Pinyin.toPinyin(lhs.getName().charAt(0)));
            }
            if (TextUtils.isEmpty(rhs.getPinyin())) {
                rhs.setPinyin(Pinyin.toPinyin(rhs.getName().charAt(0)));
            }
            String a = lhs.getPinyin().substring(0, 1);
            String b = rhs.getPinyin().substring(0, 1);
            return a.compareTo(b);
        }
    }

}
