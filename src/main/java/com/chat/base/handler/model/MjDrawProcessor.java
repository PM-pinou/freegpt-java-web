package com.chat.base.handler.model;

import com.chat.base.bean.vo.ChangeMJVo;
import com.chat.base.bean.vo.SubmitChangeDTO;
import com.chat.base.bean.vo.SubmitMJVo;
import com.chat.base.handler.model.bean.QueryDrawModelResult;

public interface MjDrawProcessor {

    /**
     * 创建绘画任务
     * @return
     */
    String createDrawTask(SubmitMJVo submitMJVo,String modeId);


    /**
     * 更给绘画
     * @return
     */
    String changeDraw(SubmitChangeDTO submitChangeDTO, String modelId);

    /**
     * 查询会话
     * @param taskId
     * @return
     */
    QueryDrawModelResult getMjImageResultByTaskId(String taskId, String modelId);


    /**
     *
     * @param model
     * @param baseUrl
     * @return
     */
    boolean match(String model,String baseUrl);


    /**
     * 越小的越优先
     * @return
     */
    default int order(){
        return -1;
    }
}
