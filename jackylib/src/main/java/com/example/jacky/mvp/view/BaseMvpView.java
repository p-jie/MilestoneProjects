package com.example.jacky.mvp.view;

/**
 * @author jacky
 * @date 2017/11/17
 * @description 所有View层接口的基类
 */
public interface BaseMvpView {
    // TODO: 请求成功回调
    void SuccessCallback(Object... data);

    // TODO: 请求失败回调
    void FailCallback(String msg);
}
