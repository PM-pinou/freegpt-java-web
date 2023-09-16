package com.chat.base.mj;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.chat.base.RunnerTest;
import com.chat.base.bean.constants.Action;
import com.chat.base.bean.vo.SubmitChangeDTO;
import com.chat.base.bean.vo.SubmitDTO;
import com.chat.base.utils.OpenGptUtil;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.stream.Collectors;

public class MjTest extends RunnerTest {

    @Test
    public void mjCreateImg(){
        SubmitDTO submitDTO = new SubmitDTO();
        submitDTO.setAction(Action.IMAGINE);
        submitDTO.setPrompt("一只蓝色可爱的带有科技感的猫");
        String mjImageTask = OpenGptUtil.createMjImageTask(submitDTO);
        JSONObject.parseObject("");
        System.out.println(mjImageTask);
    }

    @Test
    public void getImgUrl(){
        String mjImageByTaskId = OpenGptUtil.getMjImageByTaskId("5485210706134607");
        if(mjImageByTaskId!=null){
            JSONObject jsonObject = JSONObject.parseObject(mjImageByTaskId);
            String imageUrl = jsonObject.getString("imageUrl");
            System.out.println(imageUrl);
        }
        System.out.println(mjImageByTaskId);
    }

    @Test
    public void changeImgUrl(){
        SubmitChangeDTO submitDTO = new SubmitChangeDTO();
        submitDTO.setIndex(2);
        submitDTO.setTaskId("2174807297713417");
        submitDTO.setAction(Action.UPSCALE.name());
        String mjImageByTaskId = OpenGptUtil.getChangeMjImageByTaskId(submitDTO);
        if(mjImageByTaskId!=null){
            JSONObject jsonObject = JSONObject.parseObject(mjImageByTaskId);
            String imageUrl = jsonObject.getString("imageUrl");
            System.out.println(imageUrl);
        }
        System.out.println(mjImageByTaskId);
    }

    @Test
    public void searchApi() throws IOException {
        URL url = new URL("https://www.googleapis.com/customsearch/v1?key="+"AIzaSyCFN6HM0dn8brgZCTFZbN510iu8h8tz-zA"+ "&cx="+ "852e9b6f4bc5a4856" +"&q=What is the date in China today"+"&alt=json"+"&start="+0+"&num="+5);
        HttpURLConnection conn2 = (HttpURLConnection) url.openConnection();
        conn2.setRequestMethod("GET");
        conn2.setRequestProperty("Accept", "application/json");
        BufferedReader br = new BufferedReader(new InputStreamReader(
                (conn2.getInputStream())));
        String collect = br.lines().collect(Collectors.joining());
        JSONObject jsonObject = JSONObject.parseObject(collect);
        JSONArray items = jsonObject.getJSONArray("items");
        for (Object item : items) {
            JSONObject itemObject = JSONObject.parseObject(item.toString());
            System.out.println(itemObject.getString("snippet"));
        }
        System.out.println();
    }


    public static void main(String[] args) {
        JSONObject jsonObject = JSONObject.parseObject("{\"code\":1,\"description\":\"成功\",\"result\":\"4926064381498243\"}");
        System.out.println(jsonObject.getString("result"));
    }
}
