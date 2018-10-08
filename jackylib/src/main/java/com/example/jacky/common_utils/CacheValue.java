package com.example.jacky.common_utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.jacky.base.JackBaseApplication;

/**
 * @author jacky
 * @date 2018/6/13
 * @description
 */

public class CacheValue {
    private static String SHAREDNANE = "dp_cache";

    private static SharedPreferences mSpf = JackBaseApplication.getInstance().getTopActivity().getSharedPreferences(SHAREDNANE, Context.MODE_PRIVATE);

    /**
     * 保存数据到SharedPreferences缓存
     *
     * @param name;
     * @param value
     */
    public static void setCacheValue(String name, Object value) {
        if (mSpf == null) {
            mSpf = JackBaseApplication.getInstance().getTopActivity().getSharedPreferences(SHAREDNANE, Context.MODE_PRIVATE);
        }
        SharedPreferences.Editor spEdit = mSpf.edit();
        if (value instanceof String) {
            spEdit.putString(name, String.valueOf(value));
        } else if (value instanceof Integer) {
            spEdit.putInt(name, ((Integer) value).intValue());
        } else if (value instanceof Float) {
            spEdit.putFloat(name, ((Float) value).floatValue());
        } else if (value instanceof Long) {
            spEdit.putLong(name, ((Long) value).longValue());
        } else if (value instanceof Boolean) {
            spEdit.putBoolean(name, ((Boolean) value).booleanValue());
        }
        spEdit.commit();
    }

    /**
     * 获取SharedPreferences缓存数据
     *
     * @param name
     * @param defValue
     * @return
     */
    public static String getCacheStringValue(String name, String defValue) {
        if (mSpf == null) {
            mSpf = JackBaseApplication.getInstance().getTopActivity().getSharedPreferences(SHAREDNANE, Context.MODE_PRIVATE);
        }
        return mSpf.getString(name, defValue);
    }

    /**
     * 获取SharedPreferences缓存数据
     *
     * @param name
     * @param defValue
     * @return
     */
    public static int getCacheIntValue(String name, int defValue) {
        if (mSpf == null) {
            mSpf = JackBaseApplication.getInstance().getTopActivity().getSharedPreferences(SHAREDNANE, Context.MODE_PRIVATE);
        }
        return mSpf.getInt(name, defValue);
    }

    /**
     * 获取SharedPreferences缓存数据
     *
     * @param name
     * @param defValue
     * @return
     */
    public static long getCacheLongValue(String name, long defValue) {
        if (mSpf == null) {
            mSpf = JackBaseApplication.getInstance().getTopActivity().getSharedPreferences(SHAREDNANE, Context.MODE_PRIVATE);
        }
        return mSpf.getLong(name, defValue);
    }

    /**
     * 获取SharedPreferences缓存数据
     *
     * @param name
     * @param defValue
     * @return
     */
    public static boolean getCacheBooleanValue(String name, boolean defValue) {
        if (mSpf == null) {
            mSpf = JackBaseApplication.getInstance().getTopActivity().getSharedPreferences(SHAREDNANE, Context.MODE_PRIVATE);
        }
        return mSpf.getBoolean(name, defValue);
    }

    /**
     * 删除SharedPreferences缓存数据
     */
    public static void ClearValue() {
        if (mSpf == null) {
            mSpf = JackBaseApplication.getInstance().getTopActivity().getSharedPreferences(SHAREDNANE, Context.MODE_PRIVATE);
        }
        mSpf.edit().clear().commit();
    }
}
