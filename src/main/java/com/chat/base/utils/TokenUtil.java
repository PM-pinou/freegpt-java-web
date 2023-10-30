package com.chat.base.utils;

import com.chat.base.bean.constants.ModelPriceEnum;
import com.knuddels.jtokkit.Encodings;
import com.knuddels.jtokkit.api.Encoding;
import com.knuddels.jtokkit.api.EncodingRegistry;
import com.knuddels.jtokkit.api.ModelType;
import com.theokanning.openai.completion.chat.ChatMessage;
import io.github.asleepyfish.enums.RoleEnum;
import lombok.extern.slf4j.Slf4j;

import java.util.*;


@Slf4j
public class TokenUtil {


    private final static EncodingRegistry registry = Encodings.newDefaultEncodingRegistry();
    private static final Map<String, Encoding> modelMap = new HashMap<>();
    static {
        for (ModelType modelType : ModelType.values()) {
            modelMap.put(modelType.getName(), registry.getEncodingForModel(modelType));
        }
        modelMap.put(ModelPriceEnum.GPT_3_TURBO_0301.getModel(), registry.getEncodingForModel(ModelType.GPT_3_5_TURBO));
        modelMap.put(ModelPriceEnum.GPT_4_32K.getModel(), registry.getEncodingForModel(ModelType.GPT_4));
        modelMap.put(ModelPriceEnum.GPT_4_32K_0314.getModel(), registry.getEncodingForModel(ModelType.GPT_4));
        modelMap.put(ModelPriceEnum.GPT_4_0314.getModel(), registry.getEncodingForModel(ModelType.GPT_4));
        modelMap.put(ModelPriceEnum.GPT_4_0613.getModel(), registry.getEncodingForModel(ModelType.GPT_4));
        modelMap.put(ModelPriceEnum.NET_GPT_3_TURBO_16K.getModel(), registry.getEncodingForModel(ModelType.GPT_4));
    }

    /**
     * https://github.com/songquanpeng/one-api/blob/main/common/model-ratio.go  模型计费
     *
     * @param args
     */
    public static void main(String[] args) {
        LinkedList<ChatMessage> chatMessages = new LinkedList<ChatMessage>();
        ChatMessage chatMessage1 = new ChatMessage();
        chatMessage1.setContent("你好");
        chatMessage1.setRole(RoleEnum.USER.getRoleName());
        ChatMessage chatMessage2 = new ChatMessage();
        chatMessage2.setContent("你好!有什么我可以帮助你的吗？");
        chatMessage2.setRole(RoleEnum.ASSISTANT.getRoleName());
        ChatMessage chatMessage3 = new ChatMessage();
        chatMessage3.setContent("你好!有什么我可以帮助你的吗？");
        chatMessage3.setRole(RoleEnum.USER.getRoleName());

        ChatMessage chatMessage4 = new ChatMessage();
        chatMessage4.setContent("谢谢你的询问，作为一个人工智能助手，我没有实际需求，但非常感谢你的关心。如果你有任何问题或需要帮助，请随时告诉我，我会尽力为你提供帮助。");
        chatMessage4.setRole(RoleEnum.ASSISTANT.getRoleName());

        ChatMessage chatMessage5 = new ChatMessage();
        chatMessage5.setContent("你好!有什么我可以帮助你的吗？");
        chatMessage5.setRole(RoleEnum.USER.getRoleName());

        chatMessages.add(chatMessage1);
        chatMessages.add(chatMessage2);
        chatMessages.add(chatMessage3);
        chatMessages.add(chatMessage4);
        chatMessages.add(chatMessage5);
        int token = TokenUtil.countTokenMessages(chatMessages, ModelPriceEnum.GPT_3_TURBO.getModel());
        System.out.println(token);
        int tokens = TokenUtil.countTokenText("谢谢你的善意，但我是一个人工智能助手，没有实际需求。不过，如果你有任何问题，无论是关于学习、工作、娱乐还是其他方面，我都会尽力为你提供帮助和回答。请随时告诉我你需要什么！", ModelPriceEnum.GPT_3_TURBO.getModel());
        System.out.println(tokens);
    }




    public static int countTokenText(String text,String model){
        Encoding encodingForModel = modelMap.get(model);
        if(encodingForModel==null){
            log.error("countTokenText error 当前模型不能没计算token model={} ", model);
            return 0;
        }
        return getTokenNum(encodingForModel,text);
    }


    public static int countTokenMessages(Collection<ChatMessage> chatMessages,String model){
        Encoding encodingForModel = modelMap.get(model);
        if(encodingForModel==null){
            log.error("countTokenMessages error 当前模型不能没计算token model={} ", model);
            return 0;
        }
        int tokensPerMessage;
        int tokensPerName;
        if ( "gpt-3.5-turbo-0301".equals(model)) {
            tokensPerMessage = 4;
            tokensPerName = -1; // If there's a name, the role is omitted
        } else {
            tokensPerMessage = 3;
            tokensPerName = 1;
        }
        int tokenNum = 0;
        for (ChatMessage chatMessage : chatMessages) {
            tokenNum += tokensPerMessage;
            tokenNum += getTokenNum(encodingForModel, chatMessage.getContent());
            tokenNum += getTokenNum(encodingForModel, chatMessage.getRole());
        }
        tokenNum += 3; // Every reply is primed with <|start|>assistant<|message|>
        return tokenNum;
    }


    public static int getTokenNum(Encoding tiktoken, String text){
        List<Integer> encode = tiktoken.encode(text);
        return encode.size();
    }

}
