package com.xz.context;

import org.springframework.context.ApplicationContext;

/**
 * spring 上下文
 *
 * @author zhaorenwu
 */
public class App {

    protected static ApplicationContext applicationContext;

    public static void setApplicationContext(ApplicationContext applicationContext) {
        App.applicationContext = applicationContext;
    }

    public static <T> T getBean(Class<T> type) {
        return applicationContext.getBean(type);
    }

    public static <T> T getBean(Class<T> type, String name) {
        return applicationContext.getBean(name, type);
    }
}
