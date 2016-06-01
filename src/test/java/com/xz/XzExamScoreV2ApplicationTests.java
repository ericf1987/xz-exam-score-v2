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

    public static final String PROJECT_ID = "430200-89c9dc7481cd47a69d85af3f0808e0c4";

    static {
        System.setProperty("socksProxyHost", "127.0.0.1");
        System.setProperty("socksProxyPort", "2346");
        System.setProperty("unit_testing", "true");
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
