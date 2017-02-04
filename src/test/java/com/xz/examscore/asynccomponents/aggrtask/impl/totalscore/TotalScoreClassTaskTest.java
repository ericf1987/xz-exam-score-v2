package com.xz.examscore.asynccomponents.aggrtask.impl.totalscore;

import com.xz.examscore.XzExamScoreV2ApplicationTests;
import com.xz.examscore.asynccomponents.aggrtask.AggrTaskMessage;
import com.xz.examscore.bean.Target;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * (description)
 * created at 2017/1/20
 *
 * @author yidin
 */
public class TotalScoreClassTaskTest extends XzExamScoreV2ApplicationTests {

    @Autowired
    TotalScoreClassTask totalScoreClassTask;

    @Test
    public void runTask() throws Exception {
        Target target = Target.quest("FAKE_PROJ_1484895547755_0:001:33");
        String projectId = "FAKE_PROJ_1484895547755_0";
        String aggregationId = "1111";
        String type = "total_score_class";

        totalScoreClassTask.runTask(new AggrTaskMessage(
                projectId, aggregationId, type, target
        ));
    }

}