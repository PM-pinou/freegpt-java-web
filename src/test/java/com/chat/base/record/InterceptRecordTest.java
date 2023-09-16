package com.chat.base.record;

import com.chat.base.RunnerTest;
import com.chat.base.bean.vo.InterceptRecordVo;
import com.chat.base.handler.InterceptRecordManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class InterceptRecordTest extends RunnerTest {

    @Autowired
    private InterceptRecordManager interceptRecordManager;

    @Test
    public void getIpBySourceTest(){
        List<InterceptRecordVo> ipBySource = interceptRecordManager.getIpBySource("/api/userInfo/register", 2, 40);
        System.out.println(ipBySource);
    }
}
