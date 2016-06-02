package com.xz.intclient;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.xz.ajiaedu.common.cryption.AESUtils;
import com.xz.ajiaedu.common.cryption.Base64;
import com.xz.ajiaedu.common.http.HttpRequest;
import com.xz.ajiaedu.common.lang.Result;
import com.xz.api.Param;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

/**
 * (description)
 * created at 16/06/01
 *
 * @author yiding_he
 */
@Component
public class InterfaceClient {

    public static final String DEVICE_ID = UUID.randomUUID().toString();

    @Value("${interface.url}")
    private String interfaceUrl;

    @Value("${interface.key}")
    private String interfaceKey;

    /**
     * 调用 interface 接口
     *
     * @param functionName 接口名称
     * @param param        参数
     *
     * @return 返回值
     */
    public Result request(String functionName, Param param) {
        try {
            HttpRequest httpRequest = createHttpRequest(functionName, param);
            JSONObject response = JSON.parseObject(httpRequest.request());
            return parseResponse(response);
        } catch (IOException e) {
            throw new InterfaceException(e);
        }
    }

    private Result parseResponse(JSONObject response) {
        Result result = new Result();
        result.setResultCode(response.getInteger("code"));
        result.setMessage(response.getString("message"));
        result.setData(response.getJSONObject("data"));
        return result;
    }

    private HttpRequest createHttpRequest(String functionName, Param param) {
        String timeStamp = String.valueOf(System.currentTimeMillis());
        String check = Base64.encodeBytes(AESUtils.encode128(timeStamp.getBytes(), interfaceKey.getBytes()));

        HttpRequest httpRequest = new HttpRequest(this.interfaceUrl + functionName);
        httpRequest.setParameter("timestamp", timeStamp);
        httpRequest.setParameter("gateway_id", DEVICE_ID);
        httpRequest.setParameter("check", check);

        Map<String, String[]> parameters = param.getParameters();
        for (String key : parameters.keySet()) {
            httpRequest.setParameter(key, parameters.get(key));
        }
        return httpRequest;
    }
}
