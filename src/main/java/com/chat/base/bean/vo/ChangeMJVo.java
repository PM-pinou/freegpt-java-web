package com.chat.base.bean.vo;

import com.chat.base.bean.constants.Action;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class ChangeMJVo {
	/**
	 * state: 自定义参数, task中保留.
	 */
	private String state;
	/**
	 * 动作: IMAGINE\UPSCALE\VARIATION\RESET.
	 */
	@NotEmpty(message = "action不能为空")
	private String action;
	/**
	 * 任务ID: action 为 UPSCALE\VARIATION\RESET 必传.
	 */
	@NotEmpty(message = "taskId不能为空")
	private String taskId;
	/**
	 * index: action 为 UPSCALE\VARIATION 必传.
	 */
	private Integer index = 0;
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
