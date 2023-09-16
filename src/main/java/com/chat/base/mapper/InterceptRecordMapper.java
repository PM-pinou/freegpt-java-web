package com.chat.base.mapper;

import com.chat.base.bean.entity.InterceptRecord.InterceptRecord;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chat.base.bean.vo.InterceptRecordVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author lixin
 * @since 2023-05-31
 */
@Mapper
public interface InterceptRecordMapper extends BaseMapper<InterceptRecord> {

    @Select("select count(1) as number,ip,source from intercept_record where source = #{source} and create_time >= DATE_SUB(NOW(), INTERVAL #{time} MINUTE) GROUP BY source ,ip HAVING number>= #{number} ")
    List<InterceptRecordVo> getIpBySource(@Param("source") String source, @Param("number") Integer number,@Param("time") Integer time);
}
