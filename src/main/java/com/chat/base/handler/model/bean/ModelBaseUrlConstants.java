package com.chat.base.handler.model.bean;

public enum ModelBaseUrlConstants {
    MJ_NEKO_API ( "https://api.mctools.online/","Authorization",""),
    GPT_API ( "","Authorization","Bearer "),
    MJ_STRATMATE_API ( "https://mj-api.starxmate.com/","mj-api-secret","");
    private String prefix;
    private String url;
    private String authorization;

    ModelBaseUrlConstants(String url, String authorization,String prefix) {
        this.url = url;
        this.authorization = authorization;
        this.prefix = prefix;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getAuthorization() {
        return authorization;
    }

    public void setAuthorization(String authorization) {
        this.authorization = authorization;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public static ModelBaseUrlConstants getAuthorizationByUrl(String url){
       for (ModelBaseUrlConstants value : ModelBaseUrlConstants.values()) {
           if (value.getUrl().contains(url)) {
               return value;
           }
       }
       // 如果找不到,则返回默认的
       return GPT_API;
    }
}
