
package com.example.jacky.http.request;



import com.example.jacky.http.model.HttpMethod;
import com.example.jacky.http.request.base.NoBodyRequest;

import okhttp3.Request;
import okhttp3.RequestBody;


public class TraceRequest<T> extends NoBodyRequest<T, TraceRequest<T>> {

    public TraceRequest(String url) {
        super(url);
    }

    @Override
    public HttpMethod getMethod() {
        return HttpMethod.TRACE;
    }

    @Override
    public Request generateRequest(RequestBody requestBody) {
        Request.Builder requestBuilder = generateRequestBuilder(requestBody);
        return requestBuilder.method("TRACE", requestBody).url(url).tag(tag).build();
    }
}
