package com.chat.base.config;

import lombok.Data;

@Data
public class ChatBaseGPTProperties {

    /**
     * OpenAi token string "sk-XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX"
     */
    private String token;

    /**
     * The name of the model to use.
     * Required if specifying a fine tuned model or if using the new v1/completions endpoint.
     */
    private String model = "text-davinci-003";

    /**
     * chatModel which use by createChatCompletion
     */
    private String chatModel = "gpt-3.5-turbo";

    /**
     * Timeout retries
     */
    private int retries = 1;

    /**
     * proxyHost
     */
    private String proxyHost;

    /**
     * proxyPort
     */
    private int proxyPort;

    /**
     * sessionExpirationTime
     */
    private Integer sessionExpirationTime;

    private String baseUrl;

}
