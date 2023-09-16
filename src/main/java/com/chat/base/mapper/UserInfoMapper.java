package com.chat.base.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chat.base.bean.entity.UserInfo;
import com.chat.base.bean.entity.UserLog.UserLog;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author lixin
 * @since 2023-05-07
 */
public interface UserInfoMapper extends BaseMapper<UserInfo> {

    @Select("select id,username,account, avatar ,status ,user_level as userLevel from user_info where account=#{account} and password=#{password} and status = 0  limit 1")
    UserInfo queryAccountAndPassword(@Param("account") String account, @Param("password") String password);

    @Select("select id from user_info where account = #{account} limit 1")
    UserInfo queryByAccount(@Param("account")String account);

    @Select("select * from user_info where id = #{id} and status = 0  limit 1")
    UserInfo queryById(@Param("id") Long id);

    /**
     * todo 由于现在量级还未上来 所以暂时一次性都查询出来
     * @return
     */
    @Select("select account from user_info  where  status = 0 ")
    List<String> getAllAccount();

    @Select("select * from user_info ")
    List<UserInfo> getAllUser();


    default IPage<UserInfo> queryEntitiesWithPagination(Page<UserInfo> page, QueryWrapper<UserInfo> queryWrapper) {
        return selectPage(page, queryWrapper);
    }
}
