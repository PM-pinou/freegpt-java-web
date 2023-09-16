package com.chat.base.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chat.base.bean.entity.ChatHistory;
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
public interface ChatHistoryMapper extends BaseMapper<ChatHistory> {

    @Delete("delete from chat_history where conversation_uid =#{conversationUid}")
    int deleteByconversationId(@Param("conversationUid") String conversationUid);

}
