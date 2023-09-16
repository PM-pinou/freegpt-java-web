package com.chat.base.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chat.base.bean.entity.Coupon;
import com.chat.base.bean.req.CouponReq;
import com.chat.base.mapper.CouponMapper;
import com.chat.base.service.ICouponService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author liuzilin
 * @since 2023-08-13
 */
@Service
public class CouponServiceImpl extends ServiceImpl<CouponMapper, Coupon> implements ICouponService {

    @Autowired
    private CouponMapper couponMapper;
    public Coupon queryCouponByCouponNo(String couponNo){
        Coupon coupon = new Coupon();
        coupon.setCouponNo(couponNo);
        coupon.setStatus(1);
        QueryWrapper<Coupon> wrapper = new QueryWrapper<>();
        wrapper.setEntity(coupon);
       return couponMapper.selectOne(wrapper);
    }

    public IPage<Coupon> queryEntitiesWithPagination(int current, int size, QueryWrapper<Coupon> queryWrapper) {
        Page<Coupon> page = new Page<>(current, size,20);
        return couponMapper.queryEntitiesWithPagination(page, queryWrapper);
    }



    public Boolean updateCouponByCouponNo(String couponNo,Coupon coupon){
        coupon.setCouponNo(couponNo);
        coupon.setUpdateTime(LocalDateTime.now());
        coupon.setStatus(0);
        return couponMapper.updateById(coupon) == 1;
    }
}
