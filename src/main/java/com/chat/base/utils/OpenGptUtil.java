package com.chat.base.utils;

/**
 * @author huyd
 * @date 2023/5/2 7:43 PM
 */

import com.alibaba.fastjson.JSONObject;
import com.chat.base.bean.dto.SubmitMJDto;
import com.chat.base.bean.gpt.drawImageRes;
import com.chat.base.bean.vo.SubmitChangeDTO;
import com.chat.base.bean.vo.SubmitDTO;
import com.chat.base.bean.vo.SubmitMJVo;
import com.chat.base.interceptor.ChatBaseAuthenticationInterceptor;
import com.google.common.collect.Lists;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class OpenGptUtil {
    private static OkHttpClient client;



    static {
        client = new OkHttpClient.Builder()
                .connectionPool(new ConnectionPool(20, 5, TimeUnit.SECONDS))
                .readTimeout(10, TimeUnit.MILLISECONDS)
                .build();
    }

    public static void stramChatV2() {
        try {
            ChatMessage message = new ChatMessage();
            message.setContent("香辣嗦螺怎么做好吃");
            message.setRole("system");

            ChatCompletionRequest request = new ChatCompletionRequest();
            request.setMessages(Lists.newArrayList(message));
            request.setUser("123");
            request.setModel("gpt-3.5-turbo");

            // 指定接口地址
            String req = "http://localhost:8088/api/stream?request=%s";
            URL url = new URL(String.format(req, URLEncoder.encode(JSONObject.toJSONString(request), "UTF-8")));

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Accept", "text/event-stream");

            InputStream inputStream = connection.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
            reader.close();
            connection.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void postStreamChat(String prompt, String userId, OutputStream os) {
        long startTime = System.currentTimeMillis();
        try {
            String url = "http://www.wandou.online/api/plus/sendmsg";
            String message = prompt;

            OkHttpClient client = new OkHttpClient();

            Request request = new Request.Builder()
                    .url(url + "?message=" + message)
                    .addHeader("Accept", "text/event-stream")
                    .addHeader("Accept-Language", "zh-CN,zh;q=0.9")
                    .addHeader("Connection", "keep-alive")
                    .addHeader("Origin", "http://localhost")
                    .addHeader("Referer", "http://localhost/")
                    .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) like Gecko) Chrome/113.0.0.0.0 Safari/537.36")
                    .addHeader("requestid", "WD701e53d683_"+userId)
                    .addHeader("sessionnum", "0")
                    .build();
            Response response = client.newCall(request).execute();
            InputStream inputStream = response.body().byteStream();
            byte[] bytes = new byte[1024];
            int len = 0;
            int i = 0;
            while ((len = inputStream.read(bytes)) != -1) {
                String s = new String(bytes, 0, len);
                if(s.contains("content")){
                    String[] split = s.split(".*content\":\"");
                    os.write(split[1].split("\"}.*")[0].replace("\\n","").getBytes());
                    os.flush();
                }
                if(s.contains("DONE")){
                    inputStream.close();
                    break;
                }
            }
            inputStream.close();
            os.close();
            response.close();
        } catch (Exception e) {
            log.info("cost={}",System.currentTimeMillis()-startTime);
        }
    }

    public static String createMjImageTask(SubmitDTO submitDTO){
        try {
            OkHttpClient client = new OkHttpClient();

            MediaType mediaType = MediaType.parse("application/json");
            RequestBody body = RequestBody.create(mediaType, JSONObject.toJSONString(submitDTO));

            Request request = new Request.Builder()
                    .url("http://43.153.112.145:8088/mj/submit/imagine")
                    .post(body)
                    .addHeader("content-type", "application/json")
                    .addHeader("cache-control", "no-cache")
                    .build();
            Response response = client.newCall(request).execute();
            return response.body().string();
        }catch (Exception e){
            log.error("createMjImageTask error ",e);
        }
        return  null;
    }

    public static String getMjImageByTaskId(String taskId){
        try {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(String.format("http://43.153.112.145:8088/mj/task/%s/fetch", taskId))
                    .addHeader("content-type", "application/json")
                    .addHeader("cache-control", "no-cache")
                    .build();
            Response response = client.newCall(request).execute();
            return response.body().string();
        }catch (Exception e){
            log.error("createMjImageTask error ",e);
        }
        return  null;
    }

    public static String getChangeMjImageByTaskId(SubmitChangeDTO submitDTO){
        try {
            OkHttpClient client = new OkHttpClient();

            MediaType mediaType = MediaType.parse("application/json");
            RequestBody body = RequestBody.create(mediaType, JSONObject.toJSONString(submitDTO));

            Request request = new Request.Builder()
                    .url("http://43.153.112.145:8088/mj/submit/change")
                    .post(body)
                    .addHeader("content-type", "application/json")
                    .addHeader("cache-control", "no-cache")
                    .build();
            Response response = client.newCall(request).execute();
            return response.body().string();
        }catch (Exception e){
            log.error("createMjImageTask error ",e);
        }
        return  null;
    }


    public  static String createImageTask(SubmitMJVo submitMJVo,String mjTokenPath) {
        try {
            OkHttpClient client = new OkHttpClient();
            MediaType mediaType = MediaType.parse("application/json");
            SubmitMJDto submitMJDto = new SubmitMJDto();
            submitMJDto.setPrompt(submitMJVo.getPrompt().trim());
            submitMJDto.setNotifyHook(submitMJVo.getNotifyHook());
            if (StringUtils.isNotBlank(submitMJVo.getFileName())){
                submitMJDto.setBase64Array(Collections.singletonList(submitMJVo.getFileName()));
            }
            submitMJDto.setState(submitMJVo.getState());
            RequestBody body = RequestBody.create(mediaType, JSONObject.toJSONString(submitMJDto));

            Request request = new Request.Builder()
                    .url("https://api.mctools.online/mj/submit/imagine")
                    .post(body)
                    .addHeader("content-type", "application/json")
                    .addHeader("cache-control", "no-cache")
                    .addHeader("Authorization",mjTokenPath)
                    .build();
            Response response = client.newCall(request).execute();
            return response.body().string();
        }catch (Exception e){
            log.error("createMjImageTask error ",e);
        }
        return  null;
    }


    public static String getMjImageResultByTaskId(String taskId,String mjTokenPath){
        try {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(String.format("https://api.mctools.online/mj/task/%s/fetch", taskId))
                    .addHeader("content-type", "application/json")
                    .addHeader("cache-control", "no-cache")
                    .addHeader("Authorization",mjTokenPath)
                    .build();
            Response response = client.newCall(request).execute();
            return response.body().string();
        }catch (Exception e){
            log.error("createMjImageTask error ",e);
        }
        return  null;
    }


    public static String getChangeMjImageResultByTaskId(SubmitChangeDTO submitDTO,String mjTokenPath){
        try {
            OkHttpClient client = new OkHttpClient();

            MediaType mediaType = MediaType.parse("application/json");
            RequestBody body = RequestBody.create(mediaType, JSONObject.toJSONString(submitDTO));

            Request request = new Request.Builder()
                    .url("https://api.mctools.online/mj/submit/change")
                    .post(body)
                    .addHeader("content-type", "application/json")
                    .addHeader("cache-control", "no-cache")
                    .addHeader("Authorization",mjTokenPath)
                    .build();
            Response response = client.newCall(request).execute();
            return response.body().string();
        }catch (Exception e){
            log.error("createMjImageTask error ",e);
        }
        return  null;
    }


    public static void drawTaskResult(drawImageRes prompt, OutputStream os) {
        try {

            // 指定接口地址
            String req = "https://mst.ai/stableDiffusion/api/web/draw/drawTaskResult";
            URL url = new URL(req);

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("gpt-token", "OldSanGiuNBPlus666");
            connection.setDoOutput(true);
            setBodyParameter(JSONObject.toJSONString(prompt), connection);

            InputStream inputStream = connection.getInputStream();
            byte[] bytes = new byte[1024];
            int len = 0;
            while ((len = inputStream.read(bytes)) != -1) {
                os.write(bytes, 0, len);
                os.flush();
            }
            connection.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    public static void setBodyParameter(String str, HttpURLConnection conn) throws IOException {
        try (DataOutputStream out = new DataOutputStream(conn.getOutputStream())) {
            out.write(str.getBytes(StandardCharsets.UTF_8));
            out.flush();
        } catch (Exception e) {
            System.out.println("setBodyParameter error:" + e);
        }
    }


    public static void main(String[] args) throws IOException, InterruptedException {
        OkHttpClient client = new OkHttpClient.Builder()
                .followRedirects(true)
                .followSslRedirects(true)
                .addInterceptor(new ChatBaseAuthenticationInterceptor("VpS7vVrHX223473T", "Authorization"))
                .connectionPool(new ConnectionPool(500, 60, TimeUnit.SECONDS))
                .readTimeout(10, TimeUnit.SECONDS)
                .build();
        AtomicInteger count = new AtomicInteger(0);
        while (true){
            for (int i=0;i<50;i++){
                new Thread(()->{
                    Request.Builder req = new Request.Builder().url("https://www.ai-yuxin.space/fastapi/api/chat");
                    req.addHeader("Accept-Language", "zh-CN,zh;q=0.9");
                    String body = "{\n" +
                            "    \"msg\": [\n" +
                            "        {\n" +
                            "                \"content\": \"你好，你最近咋样 \",\n" +
                            "                \"role\": \"user\"\n" +
                            "                }\n" +
                            "    ],\n" +
                            "     \"model\":\"gpt-3.5-turbo\",\n" +
                            "     \"user_id\": \"168427\",\n" +
                            "     \"token\": \"VpS7vVrHX223473T\"\n" +
                            "}";
                    RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), body);
                    req.post(requestBody);
                    Response response = null;
                    try {
                        response = client.newCall(req.build()).execute();
                        String result = response.body() == null ? null : response.body().string();
                        response.close();
                        System.out.println(count.getAndIncrement());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }).start();
            }
            Thread.sleep(3000);
        }
    }


    public static void streamChat(String message, OutputStream os) {
        try {
            // 指定接口地址
            String req = "https://www.ai-yuxin.space/fastapi/api/chat";
            URL url = new URL(req);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Accept", "text/event-stream");
            connection.setRequestProperty("gpt-token", "OldSanGiuNBPlus666");


            InputStream inputStream = connection.getInputStream();
            byte[] bytes = new byte[1024];
            int len = 0;
            while ((len = inputStream.read(bytes)) != -1) {
                os.write(bytes, 0, len);
                os.flush();
            }
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                os.write(line.getBytes(Charset.defaultCharset()));
                os.flush();
            }
            reader.close();
            os.close();
            inputStream.close();
            connection.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
