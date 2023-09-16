package com.chat.base.bean.vo;

import com.chat.base.bean.gpt.ChatBaseSSE;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.theokanning.openai.OpenAiError;
import com.theokanning.openai.OpenAiHttpException;
import com.theokanning.openai.service.OpenAiService;
import io.reactivex.FlowableEmitter;
import lombok.extern.slf4j.Slf4j;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.HttpException;
import retrofit2.Response;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

@Slf4j
public class ApiResponseBodyCallback implements Callback<ResponseBody> {
    private static final ObjectMapper mapper = OpenAiService.defaultObjectMapper();

    private FlowableEmitter<ChatBaseSSE> emitter;
    private boolean emitDone;

    public ApiResponseBodyCallback(FlowableEmitter<ChatBaseSSE> emitter, boolean emitDone) {
        this.emitter = emitter;
        this.emitDone = emitDone;
    }

    @Override
    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
        BufferedReader reader = null;

        try {
            if (!response.isSuccessful()) {
                HttpException e = new HttpException(response);
                ResponseBody errorBody = response.errorBody();

                if (errorBody == null) {
                    throw e;
                } else {
                    OpenAiError error = mapper.readValue(
                            errorBody.string(),
                            OpenAiError.class
                    );
                    throw new OpenAiHttpException(error, e, e.code());
                }
            }

            InputStream in = response.body().byteStream();
            reader = new BufferedReader(new InputStreamReader(in));
            String line;
            ChatBaseSSE sse = null;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("data:")) {
                    String data = line.substring(5).trim();
                    sse = new ChatBaseSSE(data);
                    if (sse.isDone()) {
                        if (emitDone) {
                            emitter.onNext(sse);
                        }
                        break;
                    }
                    emitter.onNext(sse);
                }
            }
            emitter.onComplete();
        } catch (Throwable t) {
            onFailure(call, t);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    log.error("onResponse error",e);
                }
            }
        }
    }

    @Override
    public void onFailure(Call<ResponseBody> call, Throwable t) {
        emitter.onError(t);
    }
}
