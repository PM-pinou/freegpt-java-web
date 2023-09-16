package com.chat.base.bean.vo;

import com.chat.base.bean.constants.Action;
import lombok.Data;
import lombok.EqualsAndHashCode;


@Data
public class SubmitChangeDTO {

	private String taskId;

	private String action;

	private Integer index;

	private String state;

	private String notifyHook;
}
