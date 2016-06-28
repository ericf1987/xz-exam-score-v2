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

    public static final String PROJECT_ID = "430300-672a0ed23d9148e5a2a31c8bf1e08e62";

    public static final String UNION_PROJECT_ID = "430100-8d805ef37b2f4bc7ad9808a9a109dc22";

    public static final String SCANNER_PROJECT_ID = "430100-e60b1473c0c244cca32b87ef3bd7de1a";

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
