package com.xz.examscore.api.server;

import com.alibaba.fastjson.JSON;
import com.xz.examscore.AppException;
import com.xz.examscore.api.annotation.Function;
import com.xz.examscore.api.utils.PackageUtil;
import com.xz.examscore.context.App;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 服务控制台
 *
 * @author zhaorenwu
 */
public class ServerConsole {

    static final Logger LOG = LoggerFactory.getLogger(ServerConsole.class);

    public static Map<String, Function> SERVER_FUNCTION_MAP = new HashMap<>();

    public static Map<String, Server> SERVER_MAP = new HashMap<>();

    public static final String[] packageName = new String[]{
        "com.xz.examscore.api.server"
    };

    public static void start() throws AppException {
        List<Class<Object>> loadClasses = new ArrayList<>();

        try {

            List<Class<Object>> packageClass = PackageUtil.findPackageClass(packageName);
            for (Class<Object> clazz : packageClass) {

                // 不列出接口和抽象类
                if (clazz.isInterface() || Modifier.isAbstract(clazz.getModifiers())) {
                    continue;
                }

                // 过滤非Server子类
                if (!Server.class.isAssignableFrom(clazz)) {
                    continue;
                }

                //获取类的注解对象
                Function function = clazz.getAnnotation(Function.class);
                String simpleName = clazz.getSimpleName();
                SERVER_FUNCTION_MAP.put(simpleName, function);
                SERVER_MAP.put(simpleName, (Server) App.getBean(clazz));
                loadClasses.add(clazz);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new AppException("server api start is Exception", e.fillInStackTrace());
        }

        LOG.info("load Servers:{}", JSON.toJSONString(loadClasses));
    }

    /**
     * 通过类名获取Server的类
     *
     * @param className 类名
     *
     * @return  Server类
     */
    public static Server getServer(String className) {
        return SERVER_MAP.get(className);
    }

    /**
     * 获取指定function
     */
    public static Function getFunctionByName(String functionName) {
        return SERVER_FUNCTION_MAP.get(functionName);
    }

    /**
     * 获取所有接口列表
     */
    public static Map<String, Function> getAllFunctions() {
        return SERVER_FUNCTION_MAP;
    }
}
