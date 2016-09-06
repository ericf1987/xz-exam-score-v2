package com.xz.examscore.util;

import com.xz.ajiaedu.common.http.HttpRequest;
import com.xz.examscore.XzExamScoreV2ApplicationTests;
import org.junit.Test;

/**
 * @author by fengye on 2016/9/6.
 */
public class AlertOverTest extends XzExamScoreV2ApplicationTests{

    @Test
    public void testAlertOver() throws Exception{
        String url = "https://api.alertover.com/v1/alert";
        String source_dev = "s-b6c6bd5f-3cbd-4479-882f-e8834899";
        String receive_dev = "u-79467086-5bc3-40f3-a742-3b7293de";

        HttpRequest request = new HttpRequest(url);
        request.setParameter("source", source_dev)
                .setParameter("receiver", receive_dev)
                .setParameter("content", "统计有异常，请及时查看")
                .setParameter("title", "统计提醒");
        System.out.println(request.requestPost());

    }
}
