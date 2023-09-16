package com.chat.base.handler.gpt;

import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Converter;

import java.io.IOException;

final class ChatBaseJacksonResponseBodyConverter<T> implements Converter<ResponseBody, T> {

    private final ObjectReader adapter;

    ChatBaseJacksonResponseBodyConverter(ObjectReader adapter) {
        this.adapter = adapter;
    }

    @Override
    public T convert(ResponseBody value) throws IOException {
        try {
            return adapter.readValue(value.charStream());
        } finally {
            value.close();
        }
    }

}
