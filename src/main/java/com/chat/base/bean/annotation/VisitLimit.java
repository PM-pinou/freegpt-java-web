package com.chat.base.bean.annotation;

import com.chat.base.bean.constants.CommonConstant;
import com.chat.base.bean.constants.LimitEnum;

import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface VisitLimit {
	LimitEnum[] value() default {};

	/**
	 * 作用域 默认为1，当为1的时候 在用户登录状态下生效，2的时候不生效
	 * @return
	 */
	int scope() default CommonConstant.ALL_SCOPE;
}
