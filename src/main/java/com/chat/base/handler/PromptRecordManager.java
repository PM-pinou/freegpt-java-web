package com.chat.base.handler;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chat.base.bean.constants.ModelPriceEnum;
import com.chat.base.bean.entity.PromptRecord;
import com.chat.base.bean.req.PromptRecordReq;
import com.chat.base.bean.req.PromptRecordUserReq;
import com.chat.base.bean.vo.ChatMessageResultVo;
import com.chat.base.bean.vo.PromptRecordResult;
import com.chat.base.bean.vo.PromptRecordVo;
import com.chat.base.service.impl.PromptRecordServiceImpl;
import com.chat.base.utils.AmountUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Component
public class PromptRecordManager {

    @Autowired
    private PromptRecordServiceImpl promptRecordService;


    /**
     * 异步添加prompt记录
     * @param vo
     */
    public void asyncAddPromptRecord(ChatMessageResultVo vo){
        if(vo==null){
            return;
        }
        ModelPriceEnum modelPrice = ModelPriceEnum.getModelPrice(vo.getModel());
        Long cost = modelPrice.getInPrice()*vo.getPromptTokenNumber() + modelPrice.getOutPrice()*vo.getRelyTokenNumber();
        try {
            ThreadPoolManager.promptRecordPool.execute(
                    ()->{
                        String relyText = vo.getChatContent();
                        String prompt = vo.getContent();

                        PromptRecord entity = new PromptRecord();
                        entity.setCreateTime(LocalDateTime.now());
                        entity.setSource(vo.getSource());
                        entity.setServiceType(vo.getModel());
                        entity.setConversationId(vo.getUser());
                        entity.setToken(vo.getUserToken());
                        entity.setRelyText(relyText!=null && relyText.length() > 500 ? relyText.substring(0, 500) : relyText);
                        entity.setPromptToken(vo.getPromptTokenNumber());
                        entity.setRelyToken(vo.getRelyTokenNumber());
                        entity.setPrompt(prompt!=null && prompt.length() > 500 ? prompt.substring(0, 500) : prompt);
                        entity.setCost(cost);
                        entity.setSourceToken(vo.getSystemToken());
                        boolean result = promptRecordService.save(entity);
                        log.info("asyncAdd result={}",result);
                    }
            );
        }catch (Exception e){
            log.error("asyncAdd error vo={}",vo,e);
        }
    }



    public IPage<PromptRecord> queryTodayPromptRecord(){
        try {
            QueryWrapper<PromptRecord> queryWrapper = new QueryWrapper<>();
            queryWrapper.between("create_time",LocalDateTime.of(LocalDate.now(), LocalTime.MIN), LocalDateTime.now());
            return promptRecordService.queryEntitiesWithPagination(1,20, queryWrapper);
        }catch (Exception e){
            log.error("queryPromptRecord error ");
        }
        return new Page<>();
    }

    /**
     * c端用户查询接口，有些信息不能带到前端去 ，修改这个方法的时候稍微注意一下
     *
     * @param req
     * @return
     */
    public PromptRecordResult queUserPromptRecord(PromptRecordUserReq req){
        PromptRecordReq recordReq = new PromptRecordReq();
        PromptRecordResult result = new PromptRecordResult();
        IPage<PromptRecordVo> recordIPageVo = new Page<>();
        // 将用户请求转化为管理请求的实体bean
        BeanUtils.copyProperties(req,recordReq);

        IPage<PromptRecord> recordIPage = queryPromptRecord(recordReq);
        recordIPageVo.setTotal(recordIPage.getTotal());
        recordIPageVo.setRecords(recordIPage.getRecords().stream().map(PromptRecordVo::entityUserToVo).collect(Collectors.toList()));
        Long allCost = promptRecordService.getBaseMapper().getAllCost(recordReq);
        result.setAllCost(AmountUtil.getUserAmount(allCost));
        result.setPage(recordIPageVo);
        return result;
    }


    public IPage<PromptRecord> queryPromptRecord(PromptRecordReq req){
        try {
            QueryWrapper<PromptRecord> queryWrapper = new QueryWrapper<>();
            PromptRecord record = new PromptRecord();
            record.setConversationId(req.getConversationId());
            record.setServiceType(req.getServiceType());
            record.setToken(req.getToken());
            record.setSourceToken(req.getSourceToken());
            record.setSource(req.getSource());
            if(Objects.nonNull(req.getStartTime()) && Objects.nonNull(req.getEndTime())){
                queryWrapper.between("create_time",req.getStartTime(),req.getEndTime());
            }
            queryWrapper.setEntity(record);
            queryWrapper.select("id","cost","conversation_id","source_token","service_type","token","prompt_token","rely_token","create_time","source");
            queryWrapper.orderByDesc("create_time");
            return promptRecordService.queryEntitiesWithPagination(req.getPage(),req.getPageSize(), queryWrapper);
        }catch (Exception e){
            log.error("queryPromptRecord error ");
        }
        return new Page<>();
    }

    public static void main(String[] args) {
        ModelPriceEnum modelPrice = ModelPriceEnum.getModelPrice("gpt-4");
        Long cost = modelPrice.getInPrice()*335 + modelPrice.getOutPrice()*161;
        System.out.println(cost);
    }

}
