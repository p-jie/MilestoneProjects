
package com.example.jacky.http.request;



import com.example.jacky.http.model.HttpMethod;
import com.example.jacky.http.request.base.NoBodyRequest;

import okhttp3.Request;
import okhttp3.RequestBody;

public class GetRequest<T> extends NoBodyRequest<T, GetRequest<T>> {

    public GetRequest(String url) {
        super(url);
    }

    @Override
    public HttpMethod getMethod() {
        return HttpMethod.GET;
    }

    @Override
    public Request generateRequest(RequestBody requestBody) {
        Request.Builder requestBuilder = generateRequestBuilder(requestBody);
        return requestBuilder.get().url(url).tag(tag).build();
    }
}
