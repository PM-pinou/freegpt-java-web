package com.chat.base;

import com.chat.base.handler.gpt.OpenAiProxyServiceFactory;
import com.chat.base.service.ChatBaseOpenAiProxyService;
import org.junit.jupiter.api.Test;


class ChatBaseApplicationTests extends RunnerTest {

    @Test
    public void testGenerateImg() {
//        ChatBaseOpenAiProxyService[] services = OpenAiProxyServiceFactory.getOpenAiProxyServiceArr();
//        services[0].createStreamChatCompletion("你好", "123",System.out );
    }

    @Test
    public void createStreamChatCompletion() {
        System.out.println("tets");
    }


}
