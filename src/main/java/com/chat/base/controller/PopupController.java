package com.chat.base.controller;


import com.chat.base.bean.req.PopupReq;
import com.chat.base.handler.PopupManager;
import com.chat.base.utils.ResultVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;



/**
 *
 * @author linyous
 * @since 2023-05-10
 */
@Slf4j
@RestController
public class PopupController extends BaseController{

    @Autowired
    private PopupManager popupManager;

    /**
     * 获取公告内容
     * @return
     */
    @PostMapping("/popupInfo/getPopupInfo")
    public ResultVO getPopupInfo(@RequestBody PopupReq source){
        try {
            return popupManager.queryPopupInfo(source.getSource());
        }catch (Exception e){
            log.error("getPopupInfo error",e);
        }
        return ResultVO.fail("服务器繁忙!请联系作者！");
    }

}

