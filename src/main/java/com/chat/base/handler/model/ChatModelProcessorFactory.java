package com.chat.base.handler.model;

import com.chat.base.handler.model.processors.GptChatModelProcessor;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;


@Component
public class ChatModelProcessorFactory implements ApplicationContextAware {

    private static List<ChatModelProcessor> chatModelProcessors = new ArrayList<>();

    private static GptChatModelProcessor gptChatModelProcessor;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        chatModelProcessors.addAll(applicationContext.getBeansOfType(ChatModelProcessor.class).values());
        gptChatModelProcessor = applicationContext.getBean(GptChatModelProcessor.class);
    }


    public static ChatModelProcessor getChatModelProcessor(String model){
        for (ChatModelProcessor chatModelProcessor : chatModelProcessors) {
            if(chatModelProcessor.match(model, null)){
                return chatModelProcessor;
            }
        }
        return gptChatModelProcessor;
    }



}
