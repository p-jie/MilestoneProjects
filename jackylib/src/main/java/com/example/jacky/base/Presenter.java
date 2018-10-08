package com.example.jacky.base;

import android.app.Activity;
import android.view.View;

/**
 * @author jacky
 * @date 2018/4/8
 * @description Activity或Fragment implements Presenter
 */

public interface Presenter {
    static final String INTENT_TITLE = "INTENT_TITLE";
    static final String INTENT_ID = "INTENT_ID";
    static final String INTENT_TYPE = "INTENT_TYPE";
    static final String INTENT_PHONE = "INTENT_PHONE";
    static final String INTENT_PASSWORD = "INTENT_PASSWORD";
    static final String INTENT_VERIFY = "INTENT_VERIFY";
    static final String INTENT_USER_ID = "INTENT_USER_ID";
    static final String RESULT_DATA = "RESULT_DATA";
    static final String ACTION_EXIT_APP = "ACTION_EXIT_APP";

    /**
     * UI显示方法(操作UI，但不存在数据获取或处理代码，也不存在事件监听代码)
     * @must Activity-在子类onCreate方法内初始化View(setContentView)后调用；Fragment-在子类onCreateView方法内初始化View后调用
     */
    void initView();
    /**
     * Data数据方法(存在数据获取或处理代码，但不存在事件监听代码)
     * @must Activity-在子类onCreate方法内初始化View(setContentView)后调用；Fragment-在子类onCreateView方法内初始化View后调用
     */
    void initData();
    /**
     * Event事件方法(只要存在事件监听代码就是)
     * @must Activity-在子类onCreate方法内初始化View(setContentView)后调用；Fragment-在子类onCreateView方法内初始化View后调用
     */
    void initEvent();


    /**
     * 是否存活(已启动且未被销毁)
     */
    boolean isAlive();
    /**
     * 是否在运行
     */
    boolean isRunning();

    /**获取Activity
     * @must 在非抽象Activity中 return this;
     */
    public Activity getActivity();//无public导致有时自动生成的getActivity方法会缺少public且对此报错

    /**返回按钮被点击
     * *Activity的返回按钮和底部弹窗的取消按钮几乎是必备，正好原生支持反射；而其它比如Fragment极少用到，也不支持反射
     * @param v
     */
    public void onReturnClick(View v);

    /**前进按钮被点击
     * *Activity常用导航栏右边按钮，而且底部弹窗BottomWindow的确定按钮是必备；而其它比如Fragment极少用到，也不支持反射
     * @param v
     */
    public void onForwardClick(View v);
}
