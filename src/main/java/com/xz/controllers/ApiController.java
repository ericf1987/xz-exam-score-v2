package com.xz.controllers;

import com.alibaba.fastjson.JSON;
import com.xz.ajiaedu.common.lang.Result;
import com.xz.api.Param;
import com.xz.api.annotation.Function;
import com.xz.api.server.Server;
import com.xz.api.server.ServerConsole;
import com.xz.api.utils.ParamUtils;
import com.xz.api.utils.ThrowableUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * API接口
 *
 * @author zhaorenwu
 */
@RestController
public class ApiController {

    public static final Logger LOG = LoggerFactory.getLogger(ApiController.class);

    /**
     * 验证参数和请求，访问服务
     */
    @RequestMapping(value = "/api/{server}", method = RequestMethod.GET)
    public Result api(String p, @PathVariable("server") String server) {

        // 验证服务
        Server serverObj = ServerConsole.getServer(server);
        if (serverObj == null) {
            return Result.fail("(未知的接口'" + server + "')");
        }

        // 解析与验证参数
        Param param;
        Function function = ServerConsole.getFunctionByName(server);
        try {
            param = ParamUtils.decipherParam(function, p);
            LOG.info("Request: " + server + "/" + param);
        } catch (Exception e) {
            LOG.error("", e);
            return Result.fail(e.getMessage());
        }

        // 处理
        Result result;
        try {
            result = serverObj.execute(param);
        } catch (Exception e) {
            LOG.error("", e);
            return Result.fail(ThrowableUtils.toString(e));
        }

        LOG.debug(server + " result: " + JSON.toJSONString(result));
        return result;
    }
}
