package com.chat.base.interceptor;

import com.alibaba.fastjson.JSONObject;
import com.chat.base.bean.annotation.VisitLimit;
import com.chat.base.bean.common.BaseCodeEnum;
import com.chat.base.bean.constants.CommonConstant;
import com.chat.base.bean.constants.LimitEnum;
import com.chat.base.bean.vo.CacheUserInfoVo;
import com.chat.base.handler.InterceptRecordManager;
import com.chat.base.utils.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Component
public class VisitLimitInterceptor implements HandlerInterceptor {

    @Autowired
    private InterceptRecordManager interceptRecordManager;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        try {
            //放行OPTIONS请求
            String method = request.getMethod();
            if("OPTIONS".equals(method)){
                return true;
            }
            String requestURI = request.getRequestURI();
            String ip = HttpUtil.getIpAddress();
            CacheUserInfoVo cacheUserInfoVo = SessionUser.get();
            boolean isIn = RiskControlUtil.get(ip);
            if(isIn){
                //命中风控命中，将被禁止访问所有功能
                responseResult(response, BaseCodeEnum.BACKLIST.getMsg());
                return false;
            }
            if(!(handler instanceof HandlerMethod)){
                return true;
            }

            VisitLimit visitLimit = getVisitLimitAnnotation((HandlerMethod) handler);
            // 当用户没有登录的时候，进行ip限访问次数
            if(visitLimit!=null){
                if(visitLimit.scope()==CommonConstant.NO_LOGIN_SCOPE){
                    //该注解只在不登录的情况下起作用，如果当前用户是登录状态，则不生效
                    if(cacheUserInfoVo!=null){
                        return true;
                    }
                }

                LimitEnum[] limitEnums = visitLimit.value();
                for (LimitEnum limitEnum : limitEnums) {
                    if(CommonConstant.LIMIT_IP.equals(limitEnum.getLimit())){

                        if(StringUtils.isNoneEmpty(ip)){
                            String key = requestURI+":"+ip;
                            AtomicInteger atomicInteger = initLimitCount(key);
                            if(atomicInteger.get()>limitEnum.getNumber()){
                                interceptRecordManager.addRecord(ip,null,requestURI,"未登录用户限制访问次数");
                                responseResult(response, BaseCodeEnum.IP_LIMIT_MSG.getMsg());
                                return false;
                            }
                        }
                        break;
                    }
                }
            }
            return true;
        }catch (Exception e){
            log.error("LoginInterceptor error",e);
            responseResult(response, BaseCodeEnum.SERVER_BUSY.getMsg());
            return false;
        }
    }

    private void responseResult(HttpServletResponse response, String msg) throws Exception {
        response.setCharacterEncoding("utf-8");
        response.setContentType("application/json; charset=utf-8");
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.getWriter().write(JSONObject.toJSONString(ResultVO.fail(msg)));
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
    }

    private VisitLimit getVisitLimitAnnotation(HandlerMethod handler) {
        VisitLimit visitLimit = handler.getMethod().getAnnotation(VisitLimit.class);
        if (visitLimit == null) {
            visitLimit = handler.getBeanType().getAnnotation(VisitLimit.class);
        }
        return visitLimit;
    }


    private AtomicInteger initLimitCount(String ip){
        AtomicInteger ipLimit = IpCacheUtil.getIfPresent(ip);
        if(ipLimit==null){
            AtomicInteger integer = new AtomicInteger(1);
            IpCacheUtil.put(ip,integer);
            return integer;
        }
        ipLimit.getAndIncrement();
        return ipLimit;
    }

}
