package com.chat.base.service;

import com.alibaba.fastjson2.JSONObject;
import com.chat.base.bean.vo.ChatMessageResultVo;
import com.chat.base.config.ChatBaseGPTProperties;
import com.chat.base.handler.gpt.ChatBaseOpenAiApi;
import com.chat.base.handler.gpt.ChatBaseOpenAiService;
import com.chat.base.handler.model.bean.ModelBaseUrlConstants;
import com.chat.base.interceptor.ChatBaseAuthenticationInterceptor;
import com.chat.base.utils.TokenUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import com.theokanning.openai.completion.chat.ChatCompletionChoice;
import com.theokanning.openai.completion.chat.ChatCompletionChunk;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import io.github.asleepyfish.enums.ChatGPTErrorEnum;
import io.github.asleepyfish.enums.RoleEnum;
import io.github.asleepyfish.exception.ChatGPTException;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.apache.catalina.connector.ClientAbortException;
import retrofit2.Retrofit;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.nio.charset.Charset;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
public class ChatBaseOpenAiProxyService extends ChatBaseOpenAiService {

    // model_config_id
    private Long id;

    private String BASE_URL;
    private static final Random RANDOM = new Random();

    private final ChatBaseGPTProperties chatGPTProperties;

    private final OkHttpClient client;

    /**
     * 第三方的token
     */
    public final String token;

    public ChatBaseOpenAiProxyService(ChatBaseGPTProperties chatGPTProperties,Long id) {

        String baseUrl = chatGPTProperties.getBaseUrl();
        String token = chatGPTProperties.getToken();
        String proxyHost = chatGPTProperties.getProxyHost();
        int proxyPort = chatGPTProperties.getProxyPort();
        this.client = defaultClient(token, proxyHost , proxyPort,baseUrl);

        super.api = buildApi(chatGPTProperties.getChatModel(),baseUrl,client);
        super.executorService = client.dispatcher().executorService();
        this.chatGPTProperties = chatGPTProperties;
        this.token = token;
        this.BASE_URL = baseUrl;
        this.id = id;
    }

    public static ChatBaseOpenAiApi buildApi(String model , String baseUrl,OkHttpClient client) {
        ObjectMapper mapper = defaultObjectMapper();
        Retrofit retrofit = defaultRetrofit(model,client, mapper,baseUrl);
        return retrofit.create(ChatBaseOpenAiApi.class);
    }


    public static OkHttpClient defaultClient(String token,String proxyHost, int proxyPort,String baseUrl) {
        if (Strings.isNullOrEmpty(proxyHost)) {
            ModelBaseUrlConstants urlConstants = ModelBaseUrlConstants.getAuthorizationByUrl(baseUrl);
            return new OkHttpClient.Builder()
                    .followRedirects(true)
                    .followSslRedirects(true)
                    .addInterceptor(new ChatBaseAuthenticationInterceptor(urlConstants.getPrefix()+token,urlConstants.getAuthorization()))
                    .connectionPool(new ConnectionPool(100, 60, TimeUnit.SECONDS))
                    .readTimeout(10, TimeUnit.SECONDS)
                    .build();
        }
        // Create proxy object
        Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyHost, proxyPort));
        return defaultClient(token,baseUrl).newBuilder().proxy(proxy).build();
    }

    public static OkHttpClient defaultClient(String token,String baseUrl) {
        ModelBaseUrlConstants urlConstants = ModelBaseUrlConstants.getAuthorizationByUrl(baseUrl);
        return new OkHttpClient.Builder()
                .addInterceptor(new ChatBaseAuthenticationInterceptor(urlConstants.getPrefix()+token,urlConstants.getAuthorization()))
                .connectionPool(new ConnectionPool(100, 60, TimeUnit.SECONDS))
                .readTimeout(10, TimeUnit.SECONDS)
                .build();
    }


    private ChatCompletionChunk getChunk(String chunk){
        try {
            return JSONObject.parseObject(chunk, ChatCompletionChunk.class);
        }catch (Exception e){
            log.error("getChunk chunk="+chunk+", exception= {}",e);
        }
        return null;
    }


    public ChatMessageResultVo createStreamChatCompletion(ChatCompletionRequest chatCompletionRequest, OutputStream os, String userToken) {
        chatCompletionRequest.setStream(true);
        chatCompletionRequest.setN(1);

        ChatMessage prompt = chatCompletionRequest.getMessages().get(chatCompletionRequest.getMessages().size()-1);
        int promptTokenNumber = TokenUtil.countTokenMessages(chatCompletionRequest.getMessages(), chatCompletionRequest.getModel());
        List<ChatCompletionChunk> chunks = new ArrayList<>();
        for (int i = 0; i < chatGPTProperties.getRetries(); i++) {
            try {
                // avoid frequently request, random sleep 0.5s~0.7s
                if (i > 0) {
                    randomSleep();
                }
                super.streamChatCompletion(chatCompletionRequest).doOnError(Throwable::printStackTrace).blockingForEach(chunk -> {
                    chunk.getChoices().stream().map(choice -> choice.getMessage().getContent())
                            .filter(Objects::nonNull).findFirst().ifPresent(o -> {
                        try {
                            if(os!=null){
                                os.write(o.getBytes(Charset.defaultCharset()));
                                os.flush();
                            }
                        }catch (ClientAbortException e){
                            log.error("gpt ClientAbortException error chunk={}",chunk, e);
                        } catch (Exception e) {
                            log.error("gpt Exception error token={},url={}",token,this.BASE_URL, e);
                            throw new RuntimeException(e);
                        }
                    });
                    chunks.add(chunk);
                });
                // if the last line code is correct, we can simply break the circle
                break;
            } catch (Exception e) {
                String message = e.getMessage();
                log.error("answer failed " + (i + 1) + " times, the error message is: " + message);
                if (i == chatGPTProperties.getRetries() - 1) {
                    e.printStackTrace();
                    throw new ChatGPTException(ChatGPTErrorEnum.FAILED_TO_GENERATE_ANSWER, message);
                }
            }
        }

        ChatMessage chatMessage = new ChatMessage(RoleEnum.ASSISTANT.getRoleName(), chunks.stream()
                .flatMap(chunk -> chunk.getChoices().stream())
                .map(ChatCompletionChoice::getMessage)
                .map(ChatMessage::getContent)
                .filter(Objects::nonNull)
                .collect(Collectors.joining()));
        int relyTokenNumber = TokenUtil.countTokenText(chatMessage.getContent(), chatCompletionRequest.getModel());

        return ChatMessageResultVo.builder()
                .chatContent(chatMessage.getContent())
                .chatMessage(chatMessage)
                .content(prompt.getContent())
                .model(chatCompletionRequest.getModel())
                .user(chatCompletionRequest.getUser())
                .promptTokenNumber(promptTokenNumber)
                .relyTokenNumber(relyTokenNumber)
                .source("chat")
                .systemToken(this.token)
                .userToken(userToken)
                .build();
    }

    /**
     * Get Bill
     *
     * @return Unit: (USD)
     */
    public String billingUsage() {
        BigDecimal totalUsage = BigDecimal.ZERO;
        try {
            LocalDate startDate = LocalDate.of(2022, 1, 1);
            LocalDate endDate = LocalDate.now();
            // the max query bills scope up to 100 days. The interval for each query is defined as 3 months.
            Period threeMonth = Period.ofMonths(3);
            LocalDate nextDate = startDate;
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            while (nextDate.isBefore(endDate)) {
                String left = nextDate.format(formatter);
                nextDate = nextDate.plus(threeMonth);
                String right = nextDate.format(formatter);
                totalUsage = totalUsage.add(new BigDecimal(billingUsage(left, right)));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return totalUsage.toPlainString();
    }

    /**
     * You can query bills for up to 100 days at a time.
     *
     * @param startDate startDate
     * @param endDate   endDate
     * @return Unit: (USD)
     */
    public String billingUsage(String startDate, String endDate) {
        HttpUrl.Builder urlBuildr = HttpUrl.parse(this.BASE_URL + "/v1/dashboard/billing/usage").newBuilder();
        urlBuildr.addQueryParameter("start_date", startDate);
        urlBuildr.addQueryParameter("end_date", endDate);
        String url = urlBuildr.build().toString();
        Request request = new Request.Builder()
                .url(url)
                .build();
        String billingUsage = "0";
        for (int i = 0; i < chatGPTProperties.getRetries(); i++) {
            try (Response response = client.newCall(request).execute()) {
                if (i > 0) {
                    randomSleep();
                }
                String resStr = response.body().string();
                JSONObject resJson = JSONObject.parseObject(resStr);
                String cents = resJson.get("total_usage").toString();
                billingUsage = new BigDecimal(cents).divide(new BigDecimal("100")).toPlainString();
                break;
            } catch (Exception e) {
                log.error("query billingUsage failed " + (i + 1) + " times, the error message is: " + e.getMessage());
                if (i == chatGPTProperties.getRetries() - 1) {
                    e.printStackTrace();
                    throw new ChatGPTException(ChatGPTErrorEnum.QUERY_BILLINGUSAGE_ERROR, e.getMessage());
                }
            }
        }
        return billingUsage;
    }

    private void randomSleep() throws InterruptedException {
        Thread.sleep(500 + RANDOM.nextInt(200));
    }

    private static boolean checkTokenUsage(String message) {
        return message.contains("This model's maximum context length is");
    }


    private BufferedImage getImageFromBase64(String base64) throws IOException {
        byte[] imageBytes = Base64.getDecoder().decode(base64.getBytes());
        try (ByteArrayInputStream bis = new ByteArrayInputStream(imageBytes)) {
            return ImageIO.read(bis);
        }
    }

    public Long getId() {
        return id;
    }

    public String getBASE_URL() {
        return BASE_URL;
    }

    public String getToken() {
        return token;
    }
}
