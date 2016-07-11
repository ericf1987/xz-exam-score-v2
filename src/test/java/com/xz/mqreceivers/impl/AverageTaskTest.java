package com.xz.mqreceivers.impl;

import com.xz.XzExamScoreV2ApplicationTests;
import com.xz.bean.Range;
import com.xz.bean.Target;
import com.xz.mqreceivers.AggrTask;
import org.bson.Document;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * (description)
 * created at 16/05/16
 *
 * @author yiding_he
 */
public class AverageTaskTest extends XzExamScoreV2ApplicationTests {

    @Autowired
    AverageTask averageTask;

    @Test
    public void testRunTask() throws Exception {
        averageTask.runTask(new AggrTask("430200-8a9be9fc2e1842a4b9b4894eee1f5f73", "1111", "average")
                .setTarget(Target.subject("001")).setRange(Range.school("200f3928-a8bd-48c4-a2f4-322e9ffe3700")));
    }

    @Test
    public void testCalculateAverage() throws Exception {
        String project = "430200-b73f03af1d74484f84f1aa93f583caaa";
        Document range = new Document("name", "school").append("id", "200f3928-a8bd-48c4-a2f4-322e9ffe3700");
        Document target = new Document("name", "point").append("id", "1025616");
        double average = averageTask.calculateAverate(project, range, target, 2718d, new Document());
        System.out.println(average);
    }
}