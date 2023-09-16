package com.chat.base.handler;

import com.chat.base.bean.entity.PopupInfo;
import com.chat.base.service.impl.PopupInfoServiceImpl;
import com.chat.base.utils.ResultVO;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Optional;

@Component
public class PopupManager {

    @Resource
    private PopupInfoServiceImpl popupInfoService;

    /**
     * 获取公告内容
     * @return
     */
    public ResultVO queryPopupInfo(String source){
        Optional<PopupInfo> popupInfo = popupInfoService.queryPopupInfo(source);
        return popupInfo.<ResultVO>map(ResultVO::success).orElseGet(() -> ResultVO.success(new PopupInfo()));
    }

}
