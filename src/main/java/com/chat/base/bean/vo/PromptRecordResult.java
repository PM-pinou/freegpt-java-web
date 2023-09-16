package com.chat.base.bean.vo;

import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class PromptRecordResult implements Serializable {

    /**
     * 共消耗的额度
     */
    private String allCost;

    private IPage<PromptRecordVo> page;
}
