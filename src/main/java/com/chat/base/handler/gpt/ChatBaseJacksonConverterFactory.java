package com.chat.base.handler.gpt;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

public class ChatBaseJacksonConverterFactory extends Converter.Factory  {

    /** Create an instance using a default {@link ObjectMapper} instance for conversion. */
    public static ChatBaseJacksonConverterFactory create() {
        return create(new ObjectMapper());
    }

    /** Create an instance using {@code mapper} for conversion. */
    @SuppressWarnings("ConstantConditions") // Guarding public API nullability.
    public static ChatBaseJacksonConverterFactory create(ObjectMapper mapper) {
        if (mapper == null) throw new NullPointerException("mapper == null");
        return new ChatBaseJacksonConverterFactory(mapper);
    }

    private final ObjectMapper mapper;

    private ChatBaseJacksonConverterFactory(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public Converter<ResponseBody, ?> responseBodyConverter(
            Type type, Annotation[] annotations, Retrofit retrofit) {
        JavaType javaType = mapper.getTypeFactory().constructType(type);
        ObjectReader reader = mapper.readerFor(javaType);
        return new ChatBaseJacksonResponseBodyConverter<>(reader);
    }

    @Override
    public Converter<?, RequestBody> requestBodyConverter(
            Type type,
            Annotation[] parameterAnnotations,
            Annotation[] methodAnnotations,
            Retrofit retrofit) {
        JavaType javaType = mapper.getTypeFactory().constructType(type);
        ObjectWriter writer = mapper.writerFor(javaType);
        return new ChatBaseJacksonRequestBodyConverter<>(writer);
    }

}
