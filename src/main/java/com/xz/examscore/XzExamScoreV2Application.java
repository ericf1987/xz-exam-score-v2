package com.xz.examscore;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class XzExamScoreV2Application {

    static {
        if (!"production".equals(System.getProperty("env"))) {
            System.setProperty("socksProxyHost", "127.0.0.1");
            System.setProperty("socksProxyPort", "2346");
        }
    }

    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(XzExamScoreV2Application.class);

        if ("true".equals(System.getProperty("noweb"))) {
            application.setWebEnvironment(false);
        }

        application.run(args);
    }
}
