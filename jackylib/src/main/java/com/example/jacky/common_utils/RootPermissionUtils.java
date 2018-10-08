package com.example.jacky.common_utils;

/**
 * @Description:主要功能:判断手机是否具有root权限工具类
 * @version: 1.0.0
 */

import java.io.File;

public class RootPermissionUtils {
    /**
     * 根据/system/bin/或/system/xbin目录下是否存在su文件判断是否已ROOT
     * @return true：已ROOT
     */
    public static boolean isRoot() {
        try {
            return new File("/system/bin/su").exists() || new File("/system/xbin/su").exists();
        } catch (Exception e) {
            return false;
        }
    }

}
