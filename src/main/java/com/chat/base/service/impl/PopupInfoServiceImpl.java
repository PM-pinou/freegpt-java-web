package com.chat.base.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chat.base.bean.entity.PopupInfo;
import com.chat.base.mapper.PopupInfoMapper;
import com.chat.base.service.IPopupInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 *
 * @author linyous
 * @since 2023-05-10
 */
@Slf4j
@Service
public class PopupInfoServiceImpl extends ServiceImpl<PopupInfoMapper, PopupInfo> implements IPopupInfoService {

    /**
     * 查询最新公告的内容
     * @return
     */
    public Optional<PopupInfo> queryPopupInfo(String source) {
        PopupInfo popupInfo = super.baseMapper.getPopup(source);
        log.info("queryPopupInfo PopupInfo={}",popupInfo);
        if(popupInfo==null){
            return Optional.empty();
        }
        return Optional.of(popupInfo);
    }

}
