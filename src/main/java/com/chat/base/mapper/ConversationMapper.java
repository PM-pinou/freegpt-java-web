package com.chat.base.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chat.base.bean.entity.Conversation;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author lixin
 * @since 2023-05-07
 */
public interface ConversationMapper extends BaseMapper<Conversation> {

    @Delete("delete from Conversation where conversation_uid=#{conversationUid}")
    int deleteByUid(@Param("conversationUid") String conversationUid);

}
