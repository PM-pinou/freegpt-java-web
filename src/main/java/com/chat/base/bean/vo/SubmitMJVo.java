package com.chat.base.bean.vo;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class SubmitMJVo {
	/**
	 * eq:
	 * state: 自定义参数, task中保留.
	 */
	private String state;
	/**
	 * prompt: action 为 IMAGINE 必传.
	 */
	@NotEmpty(message = "prompt不能为空")
	private String prompt;

	/**
	 * 图片文件名称
	 */
	private String fileName;

	/**
	 * notifyHook of caller
	 */
	private String notifyHook;
	/**
	 * 模型,默认Midjourney
	 */
	@NotEmpty(message = "model不能为空")
	private String model = "Midjourney";
}
