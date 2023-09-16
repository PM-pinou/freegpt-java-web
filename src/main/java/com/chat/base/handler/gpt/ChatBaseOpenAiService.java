package com.chat.base.handler.gpt;

import com.chat.base.bean.gpt.ChatBaseSSE;
import com.chat.base.bean.vo.ApiResponseBodyCallback;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.theokanning.openai.DeleteResult;
import com.theokanning.openai.OpenAiError;
import com.theokanning.openai.OpenAiHttpException;
import com.theokanning.openai.completion.CompletionChunk;
import com.theokanning.openai.completion.CompletionRequest;
import com.theokanning.openai.completion.CompletionResult;
import com.theokanning.openai.completion.chat.ChatCompletionChunk;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatCompletionResult;
import com.theokanning.openai.edit.EditRequest;
import com.theokanning.openai.edit.EditResult;
import com.theokanning.openai.embedding.EmbeddingRequest;
import com.theokanning.openai.embedding.EmbeddingResult;
import com.theokanning.openai.file.File;
import com.theokanning.openai.finetune.FineTuneEvent;
import com.theokanning.openai.finetune.FineTuneRequest;
import com.theokanning.openai.finetune.FineTuneResult;
import com.theokanning.openai.image.CreateImageEditRequest;
import com.theokanning.openai.image.CreateImageRequest;
import com.theokanning.openai.image.CreateImageVariationRequest;
import com.theokanning.openai.image.ImageResult;
import com.theokanning.openai.model.Model;
import com.theokanning.openai.moderation.ModerationRequest;
import com.theokanning.openai.moderation.ModerationResult;
import com.theokanning.openai.service.ResponseBodyCallback;
import com.theokanning.openai.service.SSE;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.Single;
import okhttp3.*;
import org.apache.commons.lang3.StringUtils;
import retrofit2.Call;
import retrofit2.HttpException;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;

public class ChatBaseOpenAiService {

    private static final ObjectMapper mapper = defaultObjectMapper();

    public ChatBaseOpenAiApi api;
    public ExecutorService executorService;


    /**
     * Creates a new OpenAiService that wraps OpenAiApi.
     * The ExecutorService must be the one you get from the client you created the api with
     * otherwise shutdownExecutor() won't work.
     * <p>
     * Use this if you need more customization.
     *
     */
    public ChatBaseOpenAiService() {

    }

    public List<Model> listModels() {
        return execute(api.listModels()).data;
    }

    public Model getModel(String modelId) {
        return execute(api.getModel(modelId));
    }

  public ResponseBody getMjTask(String taskId) {
        return execute(api.getMJTask(taskId));
    }

    public ResponseBody createMjTask(RequestBody body) {
        return execute(api.createMJTask(body));
    }


    public ResponseBody changeMJTask(RequestBody body) {
        return execute(api.changeMJTask(body));
    }


    public CompletionResult createCompletion(CompletionRequest request) {
        return execute(api.createCompletion(request));
    }

    public Flowable<CompletionChunk> streamCompletion(CompletionRequest request) {
        request.setStream(true);
        return stream(api.createCompletionStream(request), CompletionChunk.class);
    }

    public ChatCompletionResult createChatCompletion(ChatCompletionRequest request) {
        return execute(api.createChatCompletion(request));
    }

    public Flowable<ChatCompletionChunk> streamChatCompletion(ChatCompletionRequest request) {
        request.setStream(true);

        return stream(api.createChatCompletionStream(request), ChatCompletionChunk.class);
    }

    public Flowable<String> streamApiChatCompletion(ChatCompletionRequest request) {
        request.setStream(true);

        return apiStream(api.createChatCompletionStream(request), String.class);
    }

    public EditResult createEdit(EditRequest request) {
        return execute(api.createEdit(request));
    }

    public EmbeddingResult createEmbeddings(EmbeddingRequest request) {
        return execute(api.createEmbeddings(request));
    }

    public List<File> listFiles() {
        return execute(api.listFiles()).data;
    }

    public File uploadFile(String purpose, String filepath) {
        java.io.File file = new java.io.File(filepath);
        RequestBody purposeBody = RequestBody.create(okhttp3.MultipartBody.FORM, purpose);
        RequestBody fileBody = RequestBody.create(MediaType.parse("text"), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("file", filepath, fileBody);

        return execute(api.uploadFile(purposeBody, body));
    }

    public DeleteResult deleteFile(String fileId) {
        return execute(api.deleteFile(fileId));
    }

    public File retrieveFile(String fileId) {
        return execute(api.retrieveFile(fileId));
    }

    public FineTuneResult createFineTune(FineTuneRequest request) {
        return execute(api.createFineTune(request));
    }

    public CompletionResult createFineTuneCompletion(CompletionRequest request) {
        return execute(api.createFineTuneCompletion(request));
    }

    public List<FineTuneResult> listFineTunes() {
        return execute(api.listFineTunes()).data;
    }

    public FineTuneResult retrieveFineTune(String fineTuneId) {
        return execute(api.retrieveFineTune(fineTuneId));
    }

    public FineTuneResult cancelFineTune(String fineTuneId) {
        return execute(api.cancelFineTune(fineTuneId));
    }

    public List<FineTuneEvent> listFineTuneEvents(String fineTuneId) {
        return execute(api.listFineTuneEvents(fineTuneId)).data;
    }

    public DeleteResult deleteFineTune(String fineTuneId) {
        return execute(api.deleteFineTune(fineTuneId));
    }

    public ImageResult createImage(CreateImageRequest request) {
        return execute(api.createImage(request));
    }

    public ImageResult createImageEdit(CreateImageEditRequest request, String imagePath, String maskPath) {
        java.io.File image = new java.io.File(imagePath);
        java.io.File mask = null;
        if (maskPath != null) {
            mask = new java.io.File(maskPath);
        }
        return createImageEdit(request, image, mask);
    }

    public ImageResult createImageEdit(CreateImageEditRequest request, java.io.File image, java.io.File mask) {
        RequestBody imageBody = RequestBody.create(MediaType.parse("image"), image);

        MultipartBody.Builder builder = new MultipartBody.Builder()
                .setType(MediaType.get("multipart/form-data"))
                .addFormDataPart("prompt", request.getPrompt())
                .addFormDataPart("size", request.getSize())
                .addFormDataPart("response_format", request.getResponseFormat())
                .addFormDataPart("image", "image", imageBody);

        if (request.getN() != null) {
            builder.addFormDataPart("n", request.getN().toString());
        }

        if (mask != null) {
            RequestBody maskBody = RequestBody.create(MediaType.parse("image"), mask);
            builder.addFormDataPart("mask", "mask", maskBody);
        }

        return execute(api.createImageEdit(builder.build()));
    }

    public ImageResult createImageVariation(CreateImageVariationRequest request, String imagePath) {
        java.io.File image = new java.io.File(imagePath);
        return createImageVariation(request, image);
    }

    public ImageResult createImageVariation(CreateImageVariationRequest request, java.io.File image) {
        RequestBody imageBody = RequestBody.create(MediaType.parse("image"), image);

        MultipartBody.Builder builder = new MultipartBody.Builder()
                .setType(MediaType.get("multipart/form-data"))
                .addFormDataPart("size", request.getSize())
                .addFormDataPart("response_format", request.getResponseFormat())
                .addFormDataPart("image", "image", imageBody);

        if (request.getN() != null) {
            builder.addFormDataPart("n", request.getN().toString());
        }

        return execute(api.createImageVariation(builder.build()));
    }

    public ModerationResult createModeration(ModerationRequest request) {
        return execute(api.createModeration(request));
    }

    /**
     * Calls the Open AI api, returns the response, and parses error messages if the request fails
     */
    public static <T> T execute(Single<T> apiCall) {
        try {
            return apiCall.blockingGet();
        } catch (HttpException e) {
            try {
                if (e.response() == null || e.response().errorBody() == null) {
                    throw e;
                }
                String errorBody = e.response().errorBody().string();

                OpenAiError error = mapper.readValue(errorBody, OpenAiError.class);
                throw new OpenAiHttpException(error, e, e.code());
            } catch (IOException ex) {
                // couldn't parse OpenAI error
                throw e;
            }
        }
    }

    /**
     * Calls the Open AI api and returns a Flowable of SSE for streaming
     * omitting the last message.
     *
     * @param apiCall The api call
     */
    public static Flowable<SSE> stream(Call<ResponseBody> apiCall) {
        return stream(apiCall, false);
    }


    public static Flowable<ChatBaseSSE> apiStream(Call<ResponseBody> apiCall) {
        return apiStream(apiCall, false);
    }

    /**
     * Calls the Open AI api and returns a Flowable of SSE for streaming.
     *
     * @param apiCall  The api call
     *
     * @param emitDone If true the last message ([DONE]) is emitted
     */
    public static Flowable<SSE> stream(Call<ResponseBody> apiCall, boolean emitDone) {
        return Flowable.create(emitter -> apiCall.enqueue(new ResponseBodyCallback(emitter, emitDone)), BackpressureStrategy.BUFFER);
    }

    public static Flowable<ChatBaseSSE> apiStream(Call<ResponseBody> apiCall, boolean emitDone) {
        return Flowable.create(emitter -> apiCall.enqueue(new ApiResponseBodyCallback(emitter, emitDone)), BackpressureStrategy.BUFFER);
    }

    /**
     * Calls the Open AI api and returns a Flowable of type T for streaming
     * omitting the last message.
     *
     * @param apiCall The api call
     * @param cl      Class of type T to return
     */
    public static <T> Flowable<T> stream(Call<ResponseBody> apiCall, Class<T> cl) {
        return stream(apiCall).map(sse -> mapper.readValue(sse.getData(), cl));
    }

    public static <T> Flowable<T> apiStream(Call<ResponseBody> apiCall, Class<T> cl) {
        return apiStream(apiCall).map(sse -> (T)sse.getData());
    }

    /**
     * Shuts down the OkHttp ExecutorService.
     * The default behaviour of OkHttp's ExecutorService (ConnectionPool)
     * is to shut down after an idle timeout of 60s.
     * Call this method to shut down the ExecutorService immediately.
     */
    public void shutdownExecutor() {
        Objects.requireNonNull(this.executorService, "executorService must be set in order to shut down");
        this.executorService.shutdown();
    }

    public static ObjectMapper defaultObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
        return mapper;
    }


    public static Retrofit defaultRetrofit(String model , OkHttpClient client, ObjectMapper mapper,String basUrl) {
        if (StringUtils.isNotBlank(model) && model.equals("Midjourney")){
            return new Retrofit.Builder()
                    .baseUrl(basUrl)
                    .client(client)
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(ChatBaseJacksonConverterFactory.create(mapper))
                    .build();
        } else{
            return new Retrofit.Builder()
                    .baseUrl(basUrl)
                    .client(client)
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(ChatBaseJacksonConverterFactory.create(mapper))
                    .build();
        }
    }
}
