
package com.example.jacky.http.request;



import com.example.jacky.http.model.HttpMethod;
import com.example.jacky.http.request.base.BodyRequest;

import okhttp3.Request;
import okhttp3.RequestBody;


public class DeleteRequest<T> extends BodyRequest<T, DeleteRequest<T>> {

    public DeleteRequest(String url) {
        super(url);
    }

    @Override
    public HttpMethod getMethod() {
        return HttpMethod.DELETE;
    }

    @Override
    public Request generateRequest(RequestBody requestBody) {
        Request.Builder requestBuilder = generateRequestBuilder(requestBody);
        return requestBuilder.delete(requestBody).url(url).tag(tag).build();
    }
}
