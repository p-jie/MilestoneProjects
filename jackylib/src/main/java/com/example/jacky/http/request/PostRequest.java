
package com.example.jacky.http.request;


import com.example.jacky.http.model.HttpMethod;
import com.example.jacky.http.request.base.BodyRequest;

import okhttp3.Request;
import okhttp3.RequestBody;


public class PostRequest<T> extends BodyRequest<T, PostRequest<T>> {

    public PostRequest(String url) {
        super(url);
    }

    @Override
    public HttpMethod getMethod() {
        return HttpMethod.POST;
    }

    @Override
    public Request generateRequest(RequestBody requestBody) {
        Request.Builder requestBuilder = generateRequestBuilder(requestBody);
        return requestBuilder.post(requestBody).url(url).tag(tag).build();
    }
}
