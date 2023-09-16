package com.chat.base.handler.model;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class MjDrawProcessorFactory implements ApplicationContextAware {

    private static List<MjDrawProcessor> mjDrawProcessors = new ArrayList<>();

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        mjDrawProcessors.addAll(applicationContext.getBeansOfType(MjDrawProcessor.class).values());
    }


    public static MjDrawProcessor getMjDrawProcessorByBaseUrl(String baseUrl){
        for (MjDrawProcessor mjDrawProcessor : mjDrawProcessors) {
            if(mjDrawProcessor.match(null,baseUrl)){
                return mjDrawProcessor;
            }
        }
        return null;
    }


}
