package com.sterlite.pcc.serverstatus;

import okhttp3.Response;

import java.io.IOException;
import java.net.URLDecoder;

public class HttpResponse {

    public final Response response;
    private String responseBody;

    public static HttpResponse create(Response response) throws IOException {
        return new HttpResponse(response, URLDecoder.decode(response.body().string(),"UTF-8"));
    }

    public HttpResponse(Response response, String responseBody) {
        this.response = response;
        this.responseBody = responseBody;
    }

    public String responseBody() {
        return responseBody;
    }

    public String message() {
        return response.message();
    }

    public boolean isSuccessful() {
        return response.isSuccessful();
    }


    @Override
    public String toString() {
        return response.toString();
    }
}
