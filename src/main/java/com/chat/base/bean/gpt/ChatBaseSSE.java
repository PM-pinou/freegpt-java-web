package com.chat.base.bean.gpt;

public class ChatBaseSSE {

    private static final String DONE_DATA = "[DONE]";

    private final String data;

    public ChatBaseSSE(String data){
        this.data = data;
    }

    public String getData(){
        return this.data;
    }

    public boolean isDone(){
        return DONE_DATA.equalsIgnoreCase(this.data);
    }

    public static boolean isDone(String data){
        return DONE_DATA.equalsIgnoreCase(data);
    }

}
