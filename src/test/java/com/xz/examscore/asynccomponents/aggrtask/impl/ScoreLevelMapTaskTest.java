package com.xz.examscore.asynccomponents.aggrtask.impl;

import com.xz.examscore.XzExamScoreV2ApplicationTests;
import com.xz.examscore.asynccomponents.aggrtask.AggrTaskMessage;
import com.xz.examscore.bean.Range;
import com.xz.examscore.bean.Target;
import com.xz.examscore.services.ScoreLevelService;
import com.xz.examscore.services.StudentService;
import org.bson.Document;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigurationPackage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static com.xz.ajiaedu.common.mongo.MongoUtils.doc;
import static org.junit.Assert.*;

/**
 * @author by fengye on 2016/9/7.
 */
public class ScoreLevelMapTaskTest extends XzExamScoreV2ApplicationTests {

    @Autowired
    ScoreLevelMapTask scoreLevelMapTask;

    @Test
    public void testRunTask() throws Exception {
        String projectId = "430600-d248e561aefc425b9971f2a26d267478";
        Range range = Range.clazz("38a55f5c-090d-414d-bffd-997327207754");
//        Target target = Target.subject("003");
        Target projectTarget = Target.project(projectId);
        AggrTaskMessage aggrTaskMessage = new AggrTaskMessage(projectId, "100", "score_level_map");
        aggrTaskMessage.setRange(range);
        aggrTaskMessage.setTarget(projectTarget);
        scoreLevelMapTask.runTask(aggrTaskMessage);
    }
}