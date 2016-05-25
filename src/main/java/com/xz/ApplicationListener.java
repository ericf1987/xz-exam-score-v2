package com.xz;

import com.xz.api.server.ServerConsole;
import com.xz.context.App;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * spring上下文监听类
 *
 * @author zhaorenwu
 */
@SpringBootApplication
public class ApplicationListener {

    @Bean
    public ServletContextListener servletContextListener() {
        return new ServletContextListener() {

            @Override
            public void contextInitialized(ServletContextEvent event) {
                WebApplicationContext webApplicationContext =
                        WebApplicationContextUtils.getRequiredWebApplicationContext(event.getServletContext());
                App.setApplicationContext(webApplicationContext);
                ServerConsole.start();
            }

            @Override
            public void contextDestroyed(ServletContextEvent event) {}
        };
    }
}
