package com.chat.base.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chat.base.bean.entity.PromptRecord;
import com.chat.base.bean.req.PromptRecordReq;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author lixin
 * @since 2023-05-17
 */
@Mapper
public interface PromptRecordMapper extends BaseMapper<PromptRecord> {

    default IPage<PromptRecord> queryEntitiesWithPagination(Page<PromptRecord> page, QueryWrapper<PromptRecord> queryWrapper) {
        return selectPage(page, queryWrapper);
    }

    @Select({
            "<script>" +
            " SELECT sum(cost) FROM prompt_record WHERE rely_token > 0  " +
            "<if test=\"conversationId != null\">" +
            "    AND conversation_id = #{conversationId}" +
            "  </if>" +
            "  <if test=\"source != null\">" +
            "    AND source = #{source}" +
            "  </if>" +
            "  <if test=\"token != null\">" +
            "    AND token = #{token}" +
            "  </if>" +
            "  <if test=\"serviceType != null\">" +
            "    AND service_type = #{serviceType}" +
            "  </if>" +
            "  <if test=\"startTime != null\">" +
            "     <![CDATA[ and create_time >= #{startTime} ]]>" +
            "  </if>" +
            "  <if test=\"endTime != null\">" +
            "     <![CDATA[ and create_time <= #{endTime} ]]>" +
            "  </if>"+
            "</script>"
            })
    Long getAllCost(PromptRecordReq req);

}
