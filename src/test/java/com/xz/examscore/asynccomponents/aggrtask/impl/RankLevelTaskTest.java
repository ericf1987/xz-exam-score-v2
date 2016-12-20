package com.xz.examscore.asynccomponents.aggrtask.impl;

import com.xz.examscore.XzExamScoreV2ApplicationTests;
import com.xz.examscore.asynccomponents.aggrtask.AggrTaskMessage;
import com.xz.examscore.bean.Range;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * (description)
 * created at 16/05/16
 *
 * @author yiding_he
 */
public class RankLevelTaskTest extends XzExamScoreV2ApplicationTests {

    @Autowired
    RankLevelTask rankLevelTask;

    @Test
    public void testRunTask() throws Exception {
        String student = "1a4f6f49-e230-447f-b476-29e65b463bac";
        String projectId = "430600-2404b0cc131c472dbbd13085385f5ee0";
        AggrTaskMessage taskInfo = new AggrTaskMessage(projectId, "aaa", "rank_level").setRange(Range.student(student));

        rankLevelTask.runTask(taskInfo);
    }
}