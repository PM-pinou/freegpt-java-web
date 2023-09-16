package com.chat.base.record;

import com.chat.base.RunnerTest;
import com.chat.base.handler.PromptRecordManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class promptRecordTest extends RunnerTest {

    @Autowired
    private PromptRecordManager promptRecordManager;

    @Test
    public void add(){
//        promptRecordManager.asyncAdd("test","34",null,null,null,null,null,null,null);
        try {
            Thread.sleep(10000);
        }catch (Exception e){

        }
    }
}
