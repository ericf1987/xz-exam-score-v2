package com.xz.api.controllers;

import com.xz.ajiaedu.common.lang.Result;
import com.xz.api.annotation.*;
import com.xz.api.server.ServerConsole;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 接口列表/接口明细
 *
 * @author zhaorenwu
 */
@RequestMapping(value = "/apiconsole")
@RestController
public class FunctionController {

    /**
     * 接口列表
     */
    @RequestMapping(value="/functions", method = RequestMethod.GET)
    public Result functions() {
        List<Map<String, String>> functions = new ArrayList<>();

        Map<String, Function> functionMap = ServerConsole.getAllFunctions();
        for (String functionName : functionMap.keySet()) {
            Map<String, String> map = new HashMap<>();
            map.put("functionName", functionName);
            map.put("functionDesc", functionMap.get(functionName).description());

            functions.add(map);
        }

        return Result.success().set("functions", functions);
    }

    /**
     * 指定接口明细
     */
    @RequestMapping(value="/functionInfo/{functionName}", method = RequestMethod.GET)
    public Result functions(@PathVariable("functionName") String functionName) {
        Map<String, Object> functionInfo = new HashMap<>();

        Function function = ServerConsole.getFunctionByName(functionName);
        if (function == null) {
            return Result.fail("未知的接口(" + functionName + ")");
        }

        functionInfo.put("functionName", functionName);
        functionInfo.put("functionDesc", function.description());
        functionInfo.put("params", parseParameters(function.parameters()));
        functionInfo.put("result", parseResult(function.result()));
        return Result.success().set("functionInfo", functionInfo);
    }

    /**
     * 获取IP地址
     */
    @RequestMapping(value="/getIp", method = RequestMethod.GET)
    public Result getIp(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");

        if (StringUtils.isNotEmpty(ip)) {
            return Result.success().set("ip", ipHandler(ip)) ;
        }

        ip = request.getHeader("X-Real-IP");
        if (StringUtils.isNotEmpty(ip)) {
            return Result.success().set("ip", ipHandler(ip)) ;
        }

        return Result.success().set("ip", ipHandler(request.getRemoteAddr()));
    }

    // 对Ip地址进行一些格式化处理，防止出现类似这样的情况：10.44.123.238, 61.158.153.53
    private String ipHandler(String initIp) {

        if (StringUtils.isEmpty(initIp)) {
            return initIp;
        }

        if (initIp.contains(",")) {
            return initIp.split(",")[1];
        } else {
            return initIp;
        }
    }

    private Map<String, Object> parseResult(ResultInfo resultInfo) {
        Map<String, Object> result = new HashMap<>();
        result.put("success", resultInfo.success());

        List<FuncProperty> funcProperties = new ArrayList<>();
        Property[] properties = resultInfo.properties();
        for (Property property : properties) {
            funcProperties.add(new FuncProperty(property));
        }
        result.put("properties", funcProperties);

        List<FuncListProperty> funcListProperties = new ArrayList<>();
        ListProperty[] listProperties = resultInfo.listProperties();
        for (ListProperty listProperty : listProperties) {
            funcListProperties.add(new FuncListProperty(listProperty));
        }
        result.put("listProperties", funcListProperties);

        return result;
    }

    private List<FuncParameter> parseParameters(Parameter[] parameters) {
        List<FuncParameter> params = new ArrayList<>();
        for (Parameter parameter : parameters) {
            params.add(new FuncParameter(parameter));
        }

        return params;
    }

    /////////////////////////////////////////////////////////////////////

    @SuppressWarnings("unused")
    public class FuncParameter {

        // 参数名
        private String name;

        // 参数类型
        private Type type;

        // 参数描述
        private String description;

        // 是否必须。如果 required 为 true，则 defaultValue 不起作用。
        private boolean required;

        // 缺省值
        private String defaultValue;

        public FuncParameter(Parameter parameter) {
            this.name = parameter.name();
            this.type = parameter.type();
            this.description = parameter.description();
            this.required = parameter.required();
            this.defaultValue = parameter.defaultValue();
        }

        public String getName() {
            return name;
        }

        public Type getType() {
            return type;
        }

        public String getDescription() {
            return description;
        }

        public boolean isRequired() {
            return required;
        }

        public String getDefaultValue() {
            return defaultValue;
        }
    }

    /////////////////////////////////////////////////////////////////////

    public class FuncProperty {

        // 属性名
        private String name;

        // 属性类型
        private Type type;

        // 描述
        private String description;

        public FuncProperty(Property property) {
            this.name = property.name();
            this.type = property.type();
            this.description = property.description();
        }

        public String getName() {
            return name;
        }

        public Type getType() {
            return type;
        }

        public String getDescription() {
            return description;
        }
    }

    //////////////////////////////////////////////////////////////////

    @SuppressWarnings("unused")
    public class FuncListProperty {

        // 返回值名称
        private String name;

        // 描述
        private String description;

        // 列表元素的属性
        private FuncProperty[] properties;

        public FuncListProperty(ListProperty listProperty) {
            this.name = listProperty.name();
            this.description = listProperty.description();

            Property[] properties = listProperty.properties();

            if (properties != null && properties.length > 0) {
                int length = properties.length;
                FuncProperty[] funcProperties = new FuncProperty[length];

                for (int i = 0; i < length; i++) {

                    FuncProperty funcProperty = new FuncProperty(properties[i]);
                    funcProperties[i] = funcProperty;
                }

                this.properties = funcProperties;
            }
        }

        public String getName() {
            return name;
        }

        public String getDescription() {
            return description;
        }

        public FuncProperty[] getProperties() {
            return properties;
        }
    }
}
