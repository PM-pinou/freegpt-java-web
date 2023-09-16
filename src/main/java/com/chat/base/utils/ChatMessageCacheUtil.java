package com.chat.base.utils;

import com.chat.base.bean.constants.ModelPriceEnum;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.theokanning.openai.completion.chat.ChatMessage;
import lombok.extern.slf4j.Slf4j;

import java.util.LinkedList;
import java.util.ListIterator;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@Slf4j
public class ChatMessageCacheUtil {


    public static Cache<String, LinkedList<ChatMessage>> cache ;

    static {
        // 初始化 共用的 缓存
        cache  = CacheBuilder.newBuilder().initialCapacity(100000).expireAfterAccess(720, TimeUnit.MINUTES).build();
    }


    public static LinkedList<ChatMessage> getUserChatMessages(String user,Integer contentNumber){
        LinkedList<ChatMessage> contextInfo = new LinkedList<>();

        try {
            contextInfo = ChatMessageCacheUtil.cache.get(user, LinkedList::new);
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        // 添加元素到contextInfo中
        LinkedList<ChatMessage> lastSixElements = new LinkedList<>();
        // 获取后6个元素
        int userContextInfoSize = contextInfo.size();
        int startIndex = userContextInfoSize - contentNumber;
        if (startIndex >= 0) {
            ListIterator<ChatMessage> iterator = contextInfo.listIterator(startIndex);
            while (iterator.hasNext()) {
                lastSixElements.addFirst(iterator.next());
            }
        } else {
            // 如果contextInfo的大小小于6，则获取完整的contextInfo列表
            lastSixElements.addAll(contextInfo);
        }
        return lastSixElements;
    }


    public static void getOkUserChatMessages(LinkedList<ChatMessage> chatMessages,String model){
        ModelPriceEnum modelPrice = ModelPriceEnum.getModelPrice(model);
        Integer maxInTokenNumber = modelPrice.getMaxInTokenNumber();
        for (;;){
            int tokenMessages = TokenUtil.countTokenMessages(chatMessages, model);
            if(maxInTokenNumber>tokenMessages){
                break;
            }
            chatMessages.removeFirst();
        }
    }


    public static void saveChatMessage(String user,ChatMessage chatMessage){
        LinkedList<ChatMessage> chatMessages = new LinkedList<>();
        try {
            chatMessages = cache.get(user, LinkedList::new);
        } catch (ExecutionException e) {
            log.error("exception error e",e);
        }
        chatMessages.add(chatMessage);
    }




}
