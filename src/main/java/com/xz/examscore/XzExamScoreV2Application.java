package com.xz.examscore;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class XzExamScoreV2Application {

    static final Logger LOG = LoggerFactory.getLogger(XzExamScoreV2Application.class);

    static {
        if (!"production".equals(System.getProperty("env"))) {
            System.setProperty("socksProxyHost", "127.0.0.1");
            System.setProperty("socksProxyPort", "2346");
        }
    }

    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(XzExamScoreV2Application.class);

        if ("true".equals(System.getProperty("noweb"))) {
            LOG.warn("Web 组件已被禁用。");
            application.setWebEnvironment(false);
        }

        application.run(args);
    }
}
