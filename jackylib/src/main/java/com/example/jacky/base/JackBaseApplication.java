package com.example.jacky.base;

import android.app.Activity;
import android.app.Application;
import android.support.multidex.MultiDexApplication;

import com.example.jacky.http.JackHttp;
import com.example.jacky.http.cache.CacheEntity;
import com.example.jacky.http.cache.CacheMode;
import com.example.jacky.http.cookie.CookieJarImpl;
import com.example.jacky.http.cookie.store.DBCookieStore;
import com.example.jacky.http.https.HttpsUtils;
import com.example.jacky.http.interceptor.HttpLoggingInterceptor;
import com.example.jacky.http.model.HttpHeaders;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import okhttp3.OkHttpClient;

/**
 * Created by pjj on 2018/4/2.
 */

public class JackBaseApplication extends MultiDexApplication {
    private static JackBaseApplication instance;
    /**
     * 维护Activity 的list
     */
    private static List<Activity> mActivity = Collections
            .synchronizedList(new LinkedList<Activity>());
    @Override
    public void onCreate() {
        super.onCreate();
        initHttp();
    }



    public static synchronized JackBaseApplication getInstance() {
        if (instance == null) {
            instance = new JackBaseApplication();
        }
        return instance;
    }

    private void initHttp() {

        HttpHeaders headers = new HttpHeaders();
        //headers.put("commonHeaderKey1", "commonHeaderValue1");    //header不支持中文，不允许有特殊字符
        // headers.put("commonHeaderKey2", "commonHeaderValue2");

        //----------------------------------------------------------------------------------------//

        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        //log相关
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor("JackHttp");
        loggingInterceptor.setPrintLevel(HttpLoggingInterceptor.Level.BODY);        //log打印级别，决定了log显示的详细程度
        loggingInterceptor.setColorLevel(Level.INFO);                               //log颜色级别，决定了log在控制台显示的颜色
        builder.addInterceptor(loggingInterceptor);                                 //添加OkGo默认debug日志


        //超时时间设置，默认60秒
        builder.readTimeout(JackHttp.DEFAULT_MILLISECONDS, TimeUnit.MILLISECONDS);      //全局的读取超时时间
        builder.writeTimeout(JackHttp.DEFAULT_MILLISECONDS, TimeUnit.MILLISECONDS);     //全局的写入超时时间
        builder.connectTimeout(JackHttp.DEFAULT_MILLISECONDS, TimeUnit.MILLISECONDS);   //全局的连接超时时间

        //自动管理cookie（或者叫session的保持），以下几种任选其一就行
        //builder.cookieJar(new CookieJarImpl(new SPCookieStore(this)));            //使用sp保持cookie，如果cookie不过期，则一直有效
        builder.cookieJar(new CookieJarImpl(new DBCookieStore(this)));              //使用数据库保持cookie，如果cookie不过期，则一直有效
        //builder.cookieJar(new CookieJarImpl(new MemoryCookieStore()));            //使用内存保持cookie，app退出后，cookie消失

        //https相关设置，以下几种方案根据需要自己设置
        //方法一：信任所有证书,不安全有风险
        HttpsUtils.SSLParams sslParams1 = HttpsUtils.getSslSocketFactory();
        //方法二：自定义信任规则，校验服务端证书
        //HttpsUtils.SSLParams sslParams2 = HttpsUtils.getSslSocketFactory(new SafeTrustManager());
        //方法三：使用预埋证书，校验服务端证书（自签名证书）
        //HttpsUtils.SSLParams sslParams3 = HttpsUtils.getSslSocketFactory(getAssets().open("srca.cer"));
        //方法四：使用bks证书和密码管理客户端证书（双向认证），使用预埋证书，校验服务端证书（自签名证书）
        //HttpsUtils.SSLParams sslParams4 = HttpsUtils.getSslSocketFactory(getAssets().open("xxx.bks"), "123456", getAssets().open("yyy.cer"));
        builder.sslSocketFactory(sslParams1.sSLSocketFactory, sslParams1.trustManager);
        //配置https的域名匹配规则，详细看demo的初始化介绍，不需要就不要加入，使用不当会导致https握手失败
        //builder.hostnameVerifier(new SafeHostnameVerifier());

        // 其他统一的配置
        JackHttp.getInstance().init(this)                        //必须调用初始化
                .setOkHttpClient(builder.build())               //建议设置OkHttpClient，不设置会使用默认的
                .setCacheMode(CacheMode.NO_CACHE)               //全局统一缓存模式，默认不使用缓存，可以不传
                .setCacheTime(CacheEntity.CACHE_NEVER_EXPIRE)   //全局统一缓存时间，默认永不过期，可以不传
                .setRetryCount(3)                               //全局统一超时重连次数，默认为三次，那么最差的情况会请求4次(一次原始请求，三次重连请求)，不需要可以设置为0
                .addCommonHeaders(headers);                     //全局公共头
        // .addCommonParams(params);                       //全局公共参数
    }


    /**
     * 添加一个activity到管理里
     */
    public void pushActivity(Activity activity) {
        mActivity.add(activity);
    }

    /**
     * 删除一个activity在管理里
     */
    public void popActivity(Activity activity) {
        mActivity.remove(activity);
    }


    /**
     * get current Activity 获取当前Activity（栈中最后一个压入的）
     */
    public static Activity currentActivity() {
        if (mActivity == null || mActivity.isEmpty()) {
            return null;
        }
        Activity activity = mActivity.get(mActivity.size() - 1);
        return activity;
    }

    /**
     * 结束当前Activity（栈中最后一个压入的）
     */
    public static void finishCurrentActivity() {
        if (mActivity == null || mActivity.isEmpty()) {
            return;
        }
        Activity activity = mActivity.get(mActivity.size() - 1);
        finishActivity(activity);
    }

    /**
     * 结束指定的Activity
     */
    public static void finishActivity(Activity activity) {
        if (mActivity == null || mActivity.isEmpty()) {
            return;
        }
        if (activity != null) {
            mActivity.remove(activity);
            activity.finish();
            activity = null;
        }
    }

    /**
     * 结束指定类名的Activity
     */
    public static void finishActivity(Class<?> cls) {
        if (mActivity == null || mActivity.isEmpty()) {
            return;
        }
        for (Activity activity : mActivity) {
            if (activity.getClass().equals(cls)) {
                finishActivity(activity);
            }
        }
    }

    /**
     * 指定结束前几个Activity
     *
     * @param topNum
     */
    public static void finishTopNumActivity(int topNum) {
        if (mActivity == null || mActivity.isEmpty()) {
            return;
        }
        if (mActivity.size() < topNum) {
            return;
        }
        for (int i = mActivity.size() - 1; i >= 0; i--) {
            if (topNum > 0) {
                finishActivity(mActivity.get(i));
                topNum--;
            }
        }
    }

    /**
     * 按照指定类名找到activity
     */
    public static Activity findActivity(Class<?> cls) {
        Activity targetActivity = null;
        if (mActivity != null) {
            for (Activity activity : mActivity) {
                if (activity.getClass().equals(cls)) {
                    targetActivity = activity;
                    break;
                }
            }
        }
        return targetActivity;
    }

    /**
     * 获取当前最顶部activity的实例
     */
    public Activity getTopActivity() {
        Activity mBaseActivity = null;
        synchronized (mActivity) {
            final int size = mActivity.size() - 1;
            if (size < 0) {
                return null;
            }
            if (!mActivity.get(size).isFinishing()) {
                mBaseActivity = mActivity.get(size);
            } else {
                if (size == 0) {
                    mBaseActivity = mActivity.get(0);
                } else {
                    mBaseActivity = mActivity.get(size - 1);
                }

            }

        }
        return mBaseActivity;

    }

    /**
     * 获取当前最顶部的acitivity 名字
     */
    public String getTopActivityName() {
        Activity mBaseActivity = null;
        synchronized (mActivity) {
            final int size = mActivity.size() - 1;
            if (size < 0) {
                return null;
            }
            mBaseActivity = mActivity.get(size);
        }
        return mBaseActivity.getClass().getName();
    }

    /**
     * 结束所有Activity
     */
    public static void finishAllActivity() {
        if (mActivity == null) {
            return;
        }
        for (Activity activity : mActivity) {
            activity.finish();
        }
        mActivity.clear();
    }

    /**
     * 退出应用程序
     */
    public static void appExit() {
        try {
            finishAllActivity();
            android.os.Process.killProcess(android.os.Process.myPid());
        } catch (Exception e) {
        }
    }

}
