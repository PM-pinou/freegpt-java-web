package com.chat.base.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chat.base.bean.entity.PopupInfo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 *
 * @author linyous
 * @since 2023-05-10
 */
public interface PopupInfoMapper extends BaseMapper<PopupInfo> {

    @Select("select id,title,content, createTime, popupLocation,isShow from popup_info where popupLocation=#{source} order by createTime desc limit 1")
    PopupInfo getPopup(@Param("source")String source);
}
