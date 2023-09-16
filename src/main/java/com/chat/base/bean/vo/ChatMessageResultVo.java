package com.chat.base.bean.vo;

import com.theokanning.openai.completion.chat.ChatMessage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatMessageResultVo {

    /**
     * 聊天记录
     */
    private ChatMessage chatMessage;
    private String content;// 提示词
    private String chatContent; //gpt返回内容
    private String userToken; // // 用户在系统的token
    private String systemToken; // 渠道token
    private String source;// //聊天的来源
    private int promptTokenNumber;// 提示词消耗的token 数
    private int relyTokenNumber; // 返回内容消耗的token数
    private String model; //模型接口
    private String user;

}
