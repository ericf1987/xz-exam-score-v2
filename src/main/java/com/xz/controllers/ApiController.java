package com.xz.controllers;

import com.alibaba.fastjson.JSON;
import com.xz.ajiaedu.common.lang.Result;
import com.xz.ajiaedu.common.lang.StringUtil;
import com.xz.api.ApiException;
import com.xz.api.Param;
import com.xz.api.annotation.Function;
import com.xz.api.annotation.Parameter;
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

import java.util.ArrayList;
import java.util.List;

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
    @RequestMapping(value="/api/{server}", method = RequestMethod.GET)
    public Result api(String p, @PathVariable("server") String server) {

        // 验证服务
        Server serverObj = ServerConsole.getServer(server);
        if (serverObj == null) {
            return Result.fail("(未知的接口'" + server + "')");
        }

        // 解析参数
        Param param;
        try {
            param = ParamUtils.decipherParam(p);
        } catch (Exception e) {
            return Result.fail("parameters('" + p + "') is error");
        }

        // 验证与初始化参数
        Function function = ServerConsole.getFunctionByName(server);
        try {
            validateParam(function, param);
        } catch (Exception e) {
            LOG.error(e.getMessage());
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

        LOG.info(server + " return :" + JSON.toJSONString(result));
        return result;
    }

    private void validateParam(Function function, Param param) {
        List<String> emptyParams = new ArrayList<>();

        Parameter[] parameters = function.parameters();
        for (Parameter parameter : parameters) {
            String name = parameter.name();
            boolean required = parameter.required();
            String defaultValue = parameter.defaultValue();

            String paramValue = param.getString(name);
            if (StringUtil.isNotBlank(paramValue)) {
                continue;
            }

            if (required) {
                emptyParams.add(name);
            }

            if (!required && StringUtil.isNotBlank(defaultValue)) {
                param.setParameter(name, defaultValue);
            }
        }

        if (emptyParams.size() > 0) {
            throw new ApiException("参数不能为空：" + emptyParams);
        }
    }
}
