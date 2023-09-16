package com.chat.base.handler;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.chat.base.bean.entity.LoginUserInfo;
import com.chat.base.bean.entity.PromptRecord;
import com.chat.base.bean.entity.UserInfo;
import com.chat.base.bean.entity.UserLog.UserLog;
import com.chat.base.bean.req.UserLogReq;
import com.chat.base.bean.req.UserQueryReq;
import com.chat.base.bean.vo.CacheUserInfoVo;
import com.chat.base.bean.vo.UserDataInfoVo;
import com.chat.base.service.impl.LoginUserInfoServiceImpl;
import com.chat.base.service.impl.UserLogServiceImpl;
import com.chat.base.utils.CacheUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentMap;

@Slf4j
@Component
public class UserLogManager {

    @Autowired
    private UserLogServiceImpl userLogService;

    @Autowired
    private UserManager userHandler;

    @Autowired
    private PromptRecordManager promptRecordManager;

    @Autowired
    private DrawTaskInfoManager drawTaskInfoManager;


    @Autowired
    private LoginUserInfoServiceImpl loginUserInfoService;

    public void addUserLog(String appName,String biz,Integer op,String ip,String browserName){
        ThreadPoolManager.discernRecordPool.execute(()->{
            try {
                UserLog log = new UserLog();
                log.setBiz(biz);
                log.setOp(op);
                log.setAppName(appName);
                log.setIp(ip);
                log.setBrowserName(browserName);
                log.setCreateTime(LocalDateTime.now());
                log.setCreateUser(String.valueOf(biz));
                userLogService.save(log);
            }catch (Exception e){
                log.error("addSystemLog error appName={} biz={},op={}",appName,biz,op,e);
            }
        });


    }

    public IPage<UserLog> queryUserLog(UserLogReq req){
            try {
                UserLog userLog = new UserLog();
                userLog.setBiz(req.getBiz());
                userLog.setBrowserName(req.getBrowserName());
                userLog.setOp(req.getOp());
                QueryWrapper<UserLog> queryWrapper = new QueryWrapper<>();
                queryWrapper.setEntity(userLog);
                if(Objects.nonNull(req.getStartTime()) && Objects.nonNull(req.getEndTime())){
                    queryWrapper.between("create_time",req.getStartTime(),req.getEndTime());
                }
                queryWrapper.orderByDesc("create_time");
                return userLogService.queryEntitiesWithPagination(req.getPage(), req.getPageSize(), queryWrapper);
            }catch (Exception e){
                log.error("addSystemLog error appName={} biz={},op={}",req.getAppName(),req.getBiz(),req.getOp(),e);
            }
            return null;
    }

    public List<LoginUserInfo> queryOnlineUserInfo(){
       return loginUserInfoService.queryOnlineUserInfo();
    }

    public List<UserDataInfoVo> queryUserDataInfo(){
        List<UserDataInfoVo> userDataInfoVos = new ArrayList<>();

        ConcurrentMap<String, CacheUserInfoVo> cacheUserInfo = CacheUtil.getAllCacheUserInfo();

        UserDataInfoVo onlineUserInfo = new UserDataInfoVo();
        onlineUserInfo.setName("在线人数");
        onlineUserInfo.setData(cacheUserInfo.size());

        UserLogReq userLogReq = new UserLogReq();
        userLogReq.setOp(1);
        userLogReq.setStartTime(LocalDateTime.of(LocalDate.now(), LocalTime.MIN));
        userLogReq.setEndTime(LocalDateTime.now());
        IPage<UserLog> userLogIPage = queryUserLog(userLogReq);
        UserDataInfoVo userLogRegister = new UserDataInfoVo();
        userLogRegister.setName("今天登录人数");
        userLogRegister.setData((int) userLogIPage.getTotal());

        userLogReq.setOp(2);
        IPage<UserLog> userLogIPage2 = queryUserLog(userLogReq);

        UserDataInfoVo userLogLogin = new UserDataInfoVo();
        userLogLogin.setName("今天注册人数");
        userLogLogin.setData((int) userLogIPage2.getTotal());

        UserQueryReq userQueryReq = new UserQueryReq();
        IPage<UserInfo> userInfoIPage = userHandler.queryUserInfo(userQueryReq);

        UserDataInfoVo totalUser = new UserDataInfoVo();
        totalUser.setName("总用户人数");
        totalUser.setData((int) userInfoIPage.getTotal());

        IPage<PromptRecord> promptRecordIPage = promptRecordManager.queryTodayPromptRecord();

        UserDataInfoVo todayPrompts = new UserDataInfoVo();
        todayPrompts.setName("今天总提问数");
        todayPrompts.setData((int) promptRecordIPage.getTotal());

        UserDataInfoVo totalLoginUser = new UserDataInfoVo();
        totalLoginUser.setName("总登录人数");
        totalLoginUser.setData(5748);

        int todayTaskCount = drawTaskInfoManager.getTodayTaskCount();
        UserDataInfoVo totalDrawUser = new UserDataInfoVo();
        totalDrawUser.setName("今日绘画人数");
        totalDrawUser.setData(todayTaskCount);

        userDataInfoVos.add(userLogRegister);
        userDataInfoVos.add(userLogLogin);
        userDataInfoVos.add(totalUser);
        userDataInfoVos.add(onlineUserInfo);
        userDataInfoVos.add(todayPrompts);
        userDataInfoVos.add(totalLoginUser);
        userDataInfoVos.add(totalDrawUser);
        return userDataInfoVos;
    }
}
