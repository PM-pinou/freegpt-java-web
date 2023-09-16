package com.chat.base.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chat.base.bean.entity.UserInfo;
import com.chat.base.bean.entity.UserLog.UserLog;
import com.chat.base.mapper.UserInfoMapper;
import com.chat.base.service.IUserInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author lixin
 * @since 2023-05-07
 */
@Slf4j
@Service
public class UserInfoServiceImpl extends ServiceImpl<UserInfoMapper, UserInfo> implements IUserInfoService {

    /**
     * 根据账号和密码查询用户信息
     * @param account
     * @param password
     * @return
     */
    public Optional<UserInfo> queryUserByAccountAndPassword(String account, String password) {
        UserInfo userInfo = super.baseMapper.queryAccountAndPassword(account, password);
        log.info("login account={},userInfo={}",account,userInfo);
        if(userInfo==null){
            return Optional.empty();
        }
        return Optional.of(userInfo);
    }

    /**
     * 检测账号是否已经存在了
     * @param account
     * @return
     */
    public boolean checkExistAccount(String account){
        UserInfo userInfo = super.baseMapper.queryByAccount(account);
        log.info("checkExistAccount userInfo={}",userInfo);
        return userInfo!=null;
    }


    /**
     * 查询账号
     * @param account
     * @return
     */
    public UserInfo queryUserInfoByAccount(String account){
        UserInfo userInfo = super.baseMapper.queryByAccount(account);
        log.info("checkExistAccount userInfo={}",userInfo);
        return userInfo;
    }

    /**
     * 查询账号
     * @return
     */
    public UserInfo queryUserInfoById(Long id){
        UserInfo userInfo = super.baseMapper.queryById(id);
        log.info("queryUserInfoById userInfo={}",userInfo);
        return userInfo;
    }


    /**
     * 添加用户
     * @param userInfo
     * @return
     */
    public int addUser(UserInfo userInfo){
        return this.baseMapper.insert(userInfo);
    }

    public IPage<UserInfo> queryEntitiesWithPagination(int current, int size, QueryWrapper<UserInfo> queryWrapper) {
        Page<UserInfo> page = new Page<>(current, size,20);
        return this.baseMapper.queryEntitiesWithPagination(page, queryWrapper);
    }

}
