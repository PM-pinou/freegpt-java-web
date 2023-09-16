package com.chat.base.bean.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class SubmitMJDto {
	/**
	 * state: 自定义参数, task中保留.
	 */
	private String state;
	/**
	 * prompt: action 为 IMAGINE 必传.
	 */
	private String prompt;
	/**
	 * 图片文件名称
	 */
	private List<String> base64Array;
	/**
	 * notifyHook of caller
	 */
	private String notifyHook;
}
