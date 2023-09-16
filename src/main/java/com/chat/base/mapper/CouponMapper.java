package com.chat.base.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chat.base.bean.entity.Coupon;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author liuzilin
 * @since 2023-08-13
 */
public interface CouponMapper extends BaseMapper<Coupon> {

    default IPage<Coupon> queryEntitiesWithPagination(Page<Coupon> page, QueryWrapper<Coupon> queryWrapper) {
        return selectPage(page, queryWrapper);
    }
}
