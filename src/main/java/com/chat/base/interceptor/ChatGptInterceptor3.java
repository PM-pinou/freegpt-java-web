//package com.chat.base.interceptor;
//
//import com.alibaba.fastjson.JSONObject;
//import com.chat.base.bean.common.BaseCodeEnum;
//import com.chat.base.bean.constants.ModelServiceTypeConstant;
//import com.chat.base.bean.vo.CacheUserInfoVo;
//import com.chat.base.bean.vo.UserLevelAccessVo;
//import com.chat.base.handler.InterceptRecordManager;
//import com.chat.base.handler.access.UserLevelVisitFactory;
//import com.chat.base.utils.CacheUtil;
//import com.chat.base.utils.SessionUser;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//import org.springframework.web.servlet.HandlerInterceptor;
//import org.springframework.web.servlet.ModelAndView;
//
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import java.time.LocalDateTime;
//
//
///**
// * chat gpt3模型的拦截类
// */
//@Slf4j
////@Component
//public class ChatGptInterceptor3 implements HandlerInterceptor {
//    @Autowired
//    private InterceptRecordManager interceptRecordManager;
//
//    @Override
//    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
//        try {
//            String method = request.getMethod();
//            if("OPTIONS".equals(method)){
//                return true;
//            }
//            String requestURI = request.getRequestURI();
//            CacheUserInfoVo cacheUserInfoVo = SessionUser.get();
//            if(cacheUserInfoVo!=null){
//                boolean access = UserLevelVisitFactory.access(cacheUserInfoVo.getId(), ModelServiceTypeConstant.CHAT_GPT_MODEL3);
//                if(!access){
//                    interceptRecordManager.addRecord(null,cacheUserInfoVo.getId(),requestURI,"用户访问过快，被限流规则拦截");
////                    responseResult(response, BaseCodeEnum.NO_VISITS_NUMBER.getMsg());
//                    responseResult(response, BaseCodeEnum.VISIT_BUZY.getMsg());
//                    return false;
//                }
//            }
//            return true;
//        }catch (Exception e){
//            log.error("ChatGptInterceptor3 error",e);
//            responseResult(response, BaseCodeEnum.SERVER_BUSY.getMsg());
//            return false;
//        }
//    }
//
//    private void responseResult(HttpServletResponse response, String msg) throws Exception {
//        response.setCharacterEncoding("utf-8");
//        response.setContentType("application/json; charset=utf-8");
//        response.setHeader("Access-Control-Allow-Origin", "*");
//        response.getWriter().write(msg);
//    }
//
//    @Override
//    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
//
//    }
//
//    /**
//     * chat-gpt3正常回答完了，就会回调到这里
//     * @param request
//     * @param response
//     * @param handler
//     * @param ex
//     * @throws Exception
//     */
//    @Override
//    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
//        // todo 现在暂时放开次数限制
////        CacheUserInfoVo cacheUserInfoVo = SessionUser.get();
////        if(cacheUserInfoVo==null){
////            return;
////        }
////        UserLevelAccessVo userLevelAccessVo = cacheUserInfoVo.getUserLevelAccessVoMap().get(ModelServiceTypeConstant.CHAT_GPT_MODEL3);
////        if(userLevelAccessVo!=null){
//////            userLevelAccessVo.getUseNumber().addAndGet(-1);
////            userLevelAccessVo.setLastVisitDate(LocalDateTime.now());
////            CacheUtil.put(String.valueOf(cacheUserInfoVo.getId()), JSONObject.toJSONString(cacheUserInfoVo));
////        }
//    }
//}
