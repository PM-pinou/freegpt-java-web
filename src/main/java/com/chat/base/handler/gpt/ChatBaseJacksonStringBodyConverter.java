package com.chat.base.handler.gpt;

import com.fasterxml.jackson.databind.ObjectReader;
import okhttp3.ResponseBody;
import org.jetbrains.annotations.Nullable;
import retrofit2.Converter;

import java.io.IOException;

final class ChatBaseJacksonStringBodyConverter<T> implements Converter<String, T> {

    private final ObjectReader adapter;

    ChatBaseJacksonStringBodyConverter(ObjectReader adapter) {
        this.adapter = adapter;
    }

    @Nullable
    @Override
    public T convert(String s) throws IOException {
        return adapter.readValue(s);
    }
}
