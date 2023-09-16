package com.chat.base.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.chat.base.bean.entity.Coupon;
import com.chat.base.bean.req.CouponAddReq;
import com.chat.base.bean.req.CouponDelReq;
import com.chat.base.bean.req.CouponReq;
import com.chat.base.bean.req.CouponUseReq;
import com.chat.base.bean.vo.CouponVo;
import com.chat.base.handler.CouponManager;
import com.chat.base.utils.AmountUtil;
import com.chat.base.utils.ResultVO;
import com.chat.base.utils.SessionUser;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Objects;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author liuzilin
 * @since 2023-08-13
 */
@Slf4j
@RestController
public class CouponController extends BaseController {
    @Autowired
    private CouponManager couponManager;

    /**
     * 创建兑换卷
     * @param req
     * @return
     */
    @RequestMapping("coupon/createCoupon")
    private ResultVO<Coupon> createCoupon(@RequestBody @Valid CouponAddReq req){

        if (req.getCouponAmount() < AmountUtil.DEFAULT_AMOUNT){
            return ResultVO.fail("消费卷金额不小于系统最低额度!");
        }
        try {
            return couponManager.createCoupon(req);
        }catch (Exception e){
            log.error("createCoupon error req={}",req,e);
        }
        return  ResultVO.fail("创建消费卷失败");
    }

    /**
     * 删除兑换卷
     * @param req
     * @return
     */
    @RequestMapping("coupon/delCoupon")
    private ResultVO<Object> delCoupon(@RequestBody @Valid CouponDelReq req){
        String userId = SessionUser.getAccount();
        log.info("delCoupon req={},userId={}",req,userId);
        try {
            Boolean result = couponManager.delCoupon(req);
            if(!result){
                return ResultVO.fail("删除消费卷失败! 请联系作者！");
            }
            return  ResultVO.success();
        }catch (Exception e){
            log.error("delCoupon error req={}",req,e);
            return ResultVO.fail(e.getMessage());
        }
    }

    /**
     * 使用兑换码卷
     * @param req
     * @return
     */
    @RequestMapping("coupon/useCoupon")
    private ResultVO<Object> useCoupon(@RequestBody @Valid CouponUseReq req) {
        Long userId = SessionUser.getUserId();
        log.info("useCoupon req={},userId={}",req,userId);
        try {
            Integer result = couponManager.useCoupon(req);
            if (result == 0) {
                return ResultVO.fail("消费卷无效或已经被使用! 请重新购买！");
            }
            if (result == -1) {
                return ResultVO.fail("请登录后再使用！");
            }
            return ResultVO.success( );
        } catch (Exception e) {
            log.error("useCoupon error req={}",req,e);
            return ResultVO.fail("消费卷使用异常! 请联系作者！");
        }
    }


    @RequestMapping("coupon/queryCoupon")
    private ResultVO<Object> queryCoupon(@RequestBody @Valid CouponReq req) {
        try {
            if(Objects.isNull(req)){
                return ResultVO.fail("req 不能为空！");
            }
            if(StringUtils.isBlank(req.getUseAccount()) && StringUtils.isBlank(req.getAccount())){
                req.setAccount(SessionUser.getAccount());
            }
            IPage<CouponVo> couponVoIPage = couponManager.queryCouponLists(req);
            return ResultVO.success(couponVoIPage);
        } catch (Exception e) {
            return ResultVO.fail("系统异常! 请联系作者！");
        }
    }

}

