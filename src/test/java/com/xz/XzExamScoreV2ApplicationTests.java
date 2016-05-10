package com.xz;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = XzExamScoreV2Application.class)
@WebAppConfiguration
public class XzExamScoreV2ApplicationTests {

    static {
        System.setProperty("socksProxyHost", "127.0.0.1");
        System.setProperty("socksProxyPort", "2346");
    }

    @Before
    public void start() throws Exception {
        System.out.println("\n------------------------------------------\n");
    }

    @AfterClass
    public static void finish() throws Exception {
        Thread.sleep(1000);
    }
}
