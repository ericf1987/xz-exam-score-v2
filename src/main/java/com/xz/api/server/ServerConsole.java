package com.xz.api.server;

import com.xz.AppException;
import com.xz.api.annotation.Function;
import com.xz.context.App;
import com.xz.api.utils.ClassUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 服务控制台
 *
 * @author zhaorenwu
 */
public class ServerConsole {

    static final Logger log = LoggerFactory.getLogger(ServerConsole.class);

    public static Map<String, Function> SERVER_FUNCTION_MAP = new HashMap<>();

    public static Map<String, Server> SERVER_MAP = new HashMap<>();

    public static final String[] packageName = new String[]{
        "com.xz.api.server.sys"
    };

    public static void start() throws AppException {
        try {

            //获取包下的类
            List<Class<Server>> classLists = ClassUtils.findClasses(Server.class, packageName);

            for (Class<Server> clazz : classLists) {
                log.info(clazz.getName());

                // 不列出接口和抽象类
                if (clazz.isInterface() || Modifier.isAbstract(clazz.getModifiers())) {
                    continue;
                }

                //获取类的注解对象
                Function function = Class.forName(clazz.getName()).getAnnotation(Function.class);
                SERVER_FUNCTION_MAP.put(clazz.getSimpleName(), function);
                SERVER_MAP.put(clazz.getSimpleName(), App.getBean(clazz));
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new AppException("server api start is Exception", e.fillInStackTrace());
        }
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
