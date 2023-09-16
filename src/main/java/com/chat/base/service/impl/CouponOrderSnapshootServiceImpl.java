package com.chat.base.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chat.base.bean.entity.CouponOrderSnapshoot;
import com.chat.base.mapper.CouponOrderSnapshootMapper;
import com.chat.base.service.ICouponOrderSnapshootService;
import com.chat.base.utils.SessionUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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
public class CouponOrderSnapshootServiceImpl extends ServiceImpl<CouponOrderSnapshootMapper, CouponOrderSnapshoot> implements ICouponOrderSnapshootService {

    @Autowired
    private CouponOrderSnapshootMapper mapper;

    public Boolean delCouponOrderSnapshoot(String couponNo){
        CouponOrderSnapshoot couponOrderSnapshoot = new CouponOrderSnapshoot();
        couponOrderSnapshoot.setCouponNo(couponNo);
        QueryWrapper<CouponOrderSnapshoot> wrapper = new QueryWrapper<>();
        wrapper.setEntity(couponOrderSnapshoot);
        return mapper.delete(wrapper) >= 1;
    }


    public Boolean updateCouponOrderSnapshoot(String couponNo){
        CouponOrderSnapshoot entity = new CouponOrderSnapshoot();
        entity.setCouponNo(couponNo);
        QueryWrapper<CouponOrderSnapshoot> wrapper = new QueryWrapper<>();
        wrapper.setEntity(entity);
        CouponOrderSnapshoot orderSnapshoot = mapper.selectOne(wrapper);
        if(Objects.isNull(orderSnapshoot)){
            return false;
        }
        orderSnapshoot.setUserId(SessionUser.getUserId());
        orderSnapshoot.setUseTime(LocalDateTime.now());
        int result = mapper.updateById(orderSnapshoot);
        return  result == 1;
    }
}
