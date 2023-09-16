package com.chat.base.handler;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chat.base.bean.constants.CommonConstant;
import com.chat.base.bean.constants.ConsumptionConstant;
import com.chat.base.bean.constants.OpEnum;
import com.chat.base.bean.entity.ConsumptionRecords;
import com.chat.base.bean.entity.Coupon;
import com.chat.base.bean.entity.CouponOrderSnapshoot;
import com.chat.base.bean.req.CouponAddReq;
import com.chat.base.bean.req.CouponDelReq;
import com.chat.base.bean.req.CouponReq;
import com.chat.base.bean.req.CouponUseReq;
import com.chat.base.bean.vo.CacheGptApiTokenVo;
import com.chat.base.bean.vo.CacheUserInfoVo;
import com.chat.base.bean.vo.CouponVo;
import com.chat.base.service.impl.ConsumptionRecordsServiceImpl;
import com.chat.base.service.impl.CouponOrderSnapshootServiceImpl;
import com.chat.base.service.impl.CouponServiceImpl;
import com.chat.base.utils.AmountUtil;
import com.chat.base.utils.ResultVO;
import com.chat.base.utils.SessionUser;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Component
@Slf4j
public class CouponManager {
    @Autowired
    private CouponServiceImpl couponService;
    @Autowired
    private CouponOrderSnapshootServiceImpl couponOrderSnapshootService;
    @Autowired
    private ConsumptionRecordsServiceImpl consumptionRecordsService;

    @Autowired
    private UserManager userManager;

    @Transactional
    public ResultVO<Coupon> createCoupon(CouponAddReq req){
        CacheUserInfoVo cacheUserInfoVo = SessionUser.get();
        // todo 如果做集群模型需要做 分布式锁
        synchronized(cacheUserInfoVo.getClass()) {
            CacheGptApiTokenVo gptApiTokenVo = cacheUserInfoVo.getGptApiTokenVo();
            //  操作用户余额的校验
            //  1. 用户是否有缓存
            //  2. 兑换金额大于 0.2$
            //  3. 兑换金额小于用户余额
            if (Objects.isNull(gptApiTokenVo) || req.getCouponAmount() < AmountUtil.DEFAULT_AMOUNT || gptApiTokenVo.getBalance() < req.getCouponAmount()){
                return ResultVO.fail("用户额度不够！");
            }
            // 2、兑换码的生成
            String couponCode = generateCode();
            Coupon coupon = new Coupon();
            coupon.setCouponNo(couponCode);
            coupon.setCouponAmount(req.getCouponAmount());
            coupon.setAccount(cacheUserInfoVo.getAccount());
            coupon.setCreateTime(LocalDateTime.now());
            boolean saveResult = couponService.save(coupon);
            //  3、兑换卷消费记录生成
            ConsumptionRecords consumptionRecords = new ConsumptionRecords();
            consumptionRecords.setOp(OpEnum.COUPUN_CREATE.getDesc());
            consumptionRecords.setCost(req.getCouponAmount().toString());
            consumptionRecords.setCostBefore(String.valueOf(gptApiTokenVo.getBalance()));
            consumptionRecords.setCostAfter(String.valueOf(gptApiTokenVo.getBalance() - req.getCouponAmount()));
            consumptionRecords.setBizId(couponCode);
            consumptionRecords.setCreateTime(LocalDateTime.now());
            consumptionRecords.setType(ConsumptionConstant.COUPON_CREATE);
            consumptionRecords.setUserToken(gptApiTokenVo.getToken());
            boolean consumptionResult = consumptionRecordsService.save(consumptionRecords);
            CouponOrderSnapshoot orderSnapshoot = new CouponOrderSnapshoot();
            orderSnapshoot.setCouponNo(couponCode);
            orderSnapshoot.setOrderId(consumptionRecords.getId());
            boolean couponOrderResult = couponOrderSnapshootService.save(orderSnapshoot);

            if(!saveResult || !consumptionResult || !couponOrderResult){
                log.error("createCoupon error saveResult={},consumptionResult={},couponOrderResult={}",saveResult,consumptionResult,couponOrderResult);
                // 有一个没保存成功,抛出异常,事务回滚
                throw new RuntimeException("rock RuntimeException");
            }
            // 4、当前用户余额的扣除
            Boolean decreaseCachaBalanceResult = userManager.decreaseCacheBalance(gptApiTokenVo, req.getCouponAmount());
            SessionUser.setSessionUserInfo(cacheUserInfoVo);
            log.info("createCoupon success saveResult={},consumptionResult={},couponOrderResult={},decreaseCachaBalanceResult={}",saveResult,consumptionResult,couponOrderResult,decreaseCachaBalanceResult);
            return ResultVO.success(coupon);
        }
    }

    @Transactional
    public Boolean delCoupon(CouponDelReq req){

        CacheUserInfoVo cacheUserInfoVo = SessionUser.get();
        if(req.getAccount().equals(cacheUserInfoVo.getAccount()) || SessionUser.isAdmin()){
            Coupon coupon = couponService.queryCouponByCouponNo(req.getCouponNo());
            if(Objects.isNull(coupon)) return false;

            //判断当前修改人是不是兑换卷生成人 或者管理员  todo 待做分布式锁
            synchronized(cacheUserInfoVo.getClass()) {
                CacheGptApiTokenVo gptApiTokenVo = cacheUserInfoVo.getGptApiTokenVo();
                // 更新兑换卷状态
                coupon.setStatus(-1);
                coupon.setUpdateTime(LocalDateTime.now());
                Boolean updateResult = couponService.updateById(coupon);

                // 增加逆向消费记录
                ConsumptionRecords consumptionRecords = new ConsumptionRecords();
                consumptionRecords.setOp(OpEnum.COUPUNRETURN.getDesc());
                consumptionRecords.setCost(coupon.getCouponAmount().toString());
                consumptionRecords.setCostBefore(String.valueOf(gptApiTokenVo.getBalance()));
                consumptionRecords.setCostAfter(String.valueOf(gptApiTokenVo.getBalance() + coupon.getCouponAmount()));
                consumptionRecords.setBizId(req.getCouponNo());
                consumptionRecords.setType(ConsumptionConstant.COUPON_CANCEL);
                consumptionRecords.setUserToken(gptApiTokenVo.getToken());
                consumptionRecords.setCreateTime(LocalDateTime.now());
                Boolean consumptionResult = consumptionRecordsService.save(consumptionRecords);

                // 根据兑换卷码 删除 兑换卷-订单快照表
                Boolean delResult = couponOrderSnapshootService.delCouponOrderSnapshoot(req.getCouponNo());

                if(!updateResult || !consumptionResult || !delResult){
                    log.error("delCoupon error updateResult={},consumptionResult={},delResult={}",updateResult,consumptionResult,delResult);
                    // 有一个没保存成功,抛出异常,事务回滚
                    throw new RuntimeException("rock RuntimeException");
                }
                // 4、当前用户余额增加
                Boolean increaseCachaBalanceResult = userManager.increaseCachaBalance(gptApiTokenVo, coupon.getCouponAmount());
                if(!increaseCachaBalanceResult){
                    throw new RuntimeException("用户余额新增失败！");
                }
                SessionUser.setSessionUserInfo(cacheUserInfoVo);
                log.info("delCoupon success updateResult={},consumptionResult={},delResult={},increaseCachaBalanceResult={}",updateResult,consumptionResult,delResult,increaseCachaBalanceResult);
                return true;
            }
        }

        return false;
    }


    @Transactional
    public Integer useCoupon(CouponUseReq req){
        String couponNo = req.getCouponNo().trim();
        CacheUserInfoVo userInfoVo = SessionUser.get();
        if(Objects.isNull(userInfoVo)){
            return -1;
        }
        // 判断兑换码是否是可使用状态
        Coupon coupon = couponService.queryCouponByCouponNo(couponNo);
        if (Objects.isNull(coupon)){
            return 0;
        }
        // todo 如果集群模型待分布式锁
        synchronized(userInfoVo.getClass()) {{
            // 使用兑换码, 兑换码表状态的关系更新
            coupon.setUseAccount(userInfoVo.getAccount());
            Boolean updateCouponStatus = couponService.updateCouponByCouponNo(couponNo, coupon);
            // 兑换卷订单中间表记录
            Boolean updateSnapshootResult = couponOrderSnapshootService.updateCouponOrderSnapshoot(coupon.getCouponNo());

            // 增加消费记录
            ConsumptionRecords consumptionRecords = new ConsumptionRecords();
            consumptionRecords.setCost(coupon.getCouponAmount().toString());
            consumptionRecords.setOp(OpEnum.COUPUN.getDesc());
            consumptionRecords.setCostBefore(String.valueOf(userInfoVo.getGptApiTokenVo().getBalance()));
            consumptionRecords.setCostAfter(String.valueOf(userInfoVo.getGptApiTokenVo().getBalance() + coupon.getCouponAmount()));
            consumptionRecords.setBizId(req.getCouponNo());
            consumptionRecords.setType(ConsumptionConstant.COUPON_USE);
            consumptionRecords.setUserToken(userInfoVo.getGptApiTokenVo().getToken());
            consumptionRecords.setCreateTime(LocalDateTime.now());
            Boolean consumptionResult = consumptionRecordsService.save(consumptionRecords);

            if(!updateCouponStatus || !updateSnapshootResult || !consumptionResult){
                log.error("useCoupon error updateCouponStatus={},updateSnapshootResult={},consumptionResult={}",updateCouponStatus,updateSnapshootResult,consumptionResult);
                // 有一个没保存成功,抛出异常,事务回滚
                throw new RuntimeException("rollBack RuntimeException");
            }
            // 使用用户余额的增加
            Boolean increased = userManager.increaseCachaBalance(userInfoVo.getGptApiTokenVo(), coupon.getCouponAmount());
            SessionUser.setSessionUserInfo(userInfoVo);
            log.info("useCoupon success updateCouponStatus={},updateSnapshootResult={},increased={}",updateCouponStatus,updateSnapshootResult,increased);
            return 1;
        }}
    }

    /**
     * 生成卷码
     * @return
     */
    private static String generateCode() {
        return "blueCat-"+UUID.randomUUID().toString().replace("-", "").substring(0, 32);
    }



    public  IPage<CouponVo> queryCouponLists(CouponReq req) {
            Coupon coupon = new Coupon();
            if (StringUtils.isNotBlank(req.getCouponNo())){
                coupon.setCouponNo(req.getCouponNo().trim());
            }
            coupon.setStatus(req.getStatus());
            if (StringUtils.isNotBlank(req.getAccount())) {
                coupon.setAccount(req.getAccount().trim());
            }
            if (StringUtils.isNotBlank(req.getUseAccount())) {
                coupon.setUseAccount(req.getUseAccount().trim());
            }
            coupon.setCouponAmount(req.getCouponAmount());
            QueryWrapper<Coupon> wrapper = new QueryWrapper<>();
            wrapper.setEntity(coupon);
            wrapper.orderByDesc("create_time");
            IPage<Coupon> couponIPage = couponService.queryEntitiesWithPagination(req.getPage(), req.getPageSize(), wrapper);
            return dtoToVo(couponIPage);
    }


    private IPage<CouponVo> dtoToVo(IPage<Coupon> couponIPage){
        IPage<CouponVo> couponVoIPage = new Page<>();
        List<Coupon> records = couponIPage.getRecords();
        List<CouponVo> couponVos = new ArrayList<>();
        records.stream().forEach(coupon -> {
            CouponVo couponVo = new CouponVo();
            couponVo.setCouponAmount(String.valueOf((float) coupon.getCouponAmount() / CommonConstant.multipleAmount.longValue()));
            couponVo.setCouponNo(coupon.getCouponNo());
            couponVo.setUseEndTime(coupon.getUseEndTime());
            couponVo.setCreateTime(coupon.getCreateTime());
            couponVo.setUpdateTime(coupon.getUpdateTime());
            couponVo.setStatus(coupon.getStatus());
            couponVo.setAccount(coupon.getAccount());
            couponVo.setUseAccount(coupon.getUseAccount());
            couponVos.add(couponVo);
        });
        couponVoIPage.setRecords(couponVos);
        couponVoIPage.setPages(couponIPage.getPages());
        couponVoIPage.setTotal(couponIPage.getTotal());
        couponVoIPage.setSize(couponIPage.getSize());
        couponVoIPage.setCurrent(couponIPage.getCurrent());
        return couponVoIPage;
    }
}
