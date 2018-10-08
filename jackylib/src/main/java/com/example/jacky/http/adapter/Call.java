
package com.example.jacky.http.adapter;


import com.example.jacky.http.callback.Callback;
import com.example.jacky.http.model.Response;
import com.example.jacky.http.request.base.Request;

public interface Call<T> {
    /** 同步执行 */
    Response<T> execute() throws Exception;

    /** 异步回调执行 */
    void execute(Callback<T> callback);

    /** 是否已经执行 */
    boolean isExecuted();

    /** 取消 */
    void cancel();

    /** 是否取消 */
    boolean isCanceled();

    Call<T> clone();

    Request getRequest();
}
