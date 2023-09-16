package com.chat.base.config;

import com.chat.base.handler.GptModelConfigManager;
import com.chat.base.handler.PromptRecordManager;
import com.chat.base.handler.gpt.OpenAiProxyServiceFactory;
import com.chat.base.interceptor.AdminInterceptor;
import com.chat.base.interceptor.LoginInterceptor;
import com.chat.base.interceptor.VisitLimitInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.PostConstruct;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private GptModelConfigManager gptModelConfigManager;

    @Autowired
    private PromptRecordManager promptRecordManager;

    /**
     * 初始化一些参数
     */
    @PostConstruct
    public void initTokenService(){
        OpenAiProxyServiceFactory.initGptModelConfig(gptModelConfigManager);
    }

    @Autowired
    private LoginInterceptor loginInterceptor;

    @Autowired
    private VisitLimitInterceptor visitLimitInterceptor;


    @Autowired
    private AdminInterceptor adminInterceptor;


    /**
     * 支持跨域
     *
     * @param registry
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowCredentials(true)
                .allowedHeaders("*")
                .allowedOrigins("*")
                .allowedMethods("*");
    }

    // 临时放开 回话限制
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginInterceptor).addPathPatterns("/**")
                .excludePathPatterns(
                        "/chat/createTask","/chat/drawTaskResult",
                        "/prompt/getPromptGroup","/popupInfo/getPopupInfo",
                        "/userInfo/login","/userInfo/register","/check/text",
                        "/chat/streamChatWithWeb/api/chat",
                        "/v1/chat/completions"
                )
                .order(-1);
        registry.addInterceptor(visitLimitInterceptor).addPathPatterns("/**")
                .order(1);

        // todo等待完善之后再打开此配置
//        registry.addInterceptor(adminInterceptor).addPathPatterns("/admin/**")
//                .order(2);
    }
}

