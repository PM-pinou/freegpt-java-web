package com.chat.base.bean.vo;

import com.chat.base.bean.entity.PromptModel;
import lombok.Data;

import java.util.List;

@Data
public class PromptModelVo {

    private String type;

    private List<PromptModel> promptModels;
}
