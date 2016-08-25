package com.xz.examscore.services;

import com.xz.examscore.XzExamScoreV2ApplicationTests;
import com.xz.examscore.asynccomponents.aggrtask.AggrTaskInfo;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * (description)
 * created at 16/05/12
 *
 * @author yiding_he
 */
public class AggregationRoundServiceTest extends XzExamScoreV2ApplicationTests {

    public static final String AGGR_ID = "aggr1";

    @Autowired
    AggregationRoundService aggregationRoundService;

    @Test
    public void testRounds() throws Exception {
        AggrTaskInfo task = new AggrTaskInfo(XT_PROJECT_ID, AGGR_ID, "total");
        aggregationRoundService.pushTask(task);

        new Thread(() -> {
            try {
                Thread.sleep(2000);
                aggregationRoundService.taskFinished(task);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();

        aggregationRoundService.waitForRoundCompletion(AGGR_ID);
        System.out.println("All task finished.");
    }
}