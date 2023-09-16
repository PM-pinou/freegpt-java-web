package com.chat.base.handler.billing;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class ModelBillingFactory implements ApplicationContextAware {

    private final static Map<String,ModelBillingService> MODEL_BILLING_SERVICE_MAP = new ConcurrentHashMap<>();

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        Map<String, ModelBillingService> beans = applicationContext.getBeansOfType(ModelBillingService.class);
        if(beans.size() <= 0){
            log.error("ModelBillingFactory init error beans is empty");
            return;
        }

        beans.values().forEach(e->{
            String[] billingModels = e.billingModel();
            if(billingModels!=null && billingModels.length>0){
                for (String billingModel : billingModels) {
                    MODEL_BILLING_SERVICE_MAP.put(billingModel,e);
                }
            }
        });
        log.info("ModelBillingFactory init ok beans.size={}",MODEL_BILLING_SERVICE_MAP.size());
    }

    public static ModelBillingService getModelBillingService(String model){
        return MODEL_BILLING_SERVICE_MAP.getOrDefault(model,null);
    }


}
