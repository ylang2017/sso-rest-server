package com.example.demo.component;

import com.alibaba.fastjson.JSONObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * 自定义请求信息
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class HttpRequestDetail {
    private String requestURL;
    private String requestMethod;
    private Map<String,Object> requestParams;

    public String toString(){
        JSONObject jsonObject = (JSONObject) JSONObject.toJSON(this);
        return jsonObject.toJSONString();
    }
}
