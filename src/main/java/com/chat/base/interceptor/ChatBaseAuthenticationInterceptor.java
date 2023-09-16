package com.chat.base.interceptor;

import com.google.gson.Gson;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

public class ChatBaseAuthenticationInterceptor implements Interceptor {

    private final String token;

    private final String headerName;

    public ChatBaseAuthenticationInterceptor(String token,String headerName) {
        this.token = token;
        this.headerName = headerName;
    }

    public Response intercept(Chain chain) throws IOException {

        Request request = chain.request().newBuilder().header(headerName, this.token).build();
        return chain.proceed(request);
    }

    public static void main(String[] args) {
        String str = "{\"choices\":[{\"index\":0,\"message\":{\"content\":\"助\"}}],\"created\":1692036294,\"id\":\"chatcmpl-7nWDua6TJhXaMIEfyXtjRYSrcMARg\",\"model\":\"gpt-3.5-turbo-0613\",\"object\":\"chat.completion.chunk\"}{\"choices\":[{\"finish_reason\":\"stop\",\"index\":0,\"message\":{}}],\"created\":1692036294,\"id\":\"chatcmpl-7nWDua6TJhXaMIEfyXtjRYSrcMARg\",\"model\":\"gpt-3.5-turbo-0613\",\"object\":\"chat.completion.chunk\"}   java中如何将这种格式的字符串转化为json格式";
        Gson gson = new Gson();
        String json = gson.toJson(str);
        System.out.println(json);
    }
}
