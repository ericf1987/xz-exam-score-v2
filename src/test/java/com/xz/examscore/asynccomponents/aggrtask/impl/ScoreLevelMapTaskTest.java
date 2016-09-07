package com.xz.examscore.asynccomponents.aggrtask.impl;

import com.xz.examscore.XzExamScoreV2ApplicationTests;
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
    StudentService studentService;

    @Autowired
    ScoreLevelService scoreLevelService;

    @Test
    public void testRunTask() throws Exception {
        String projectId = "433100-4cf7b0ef86574a1598481ba3e3841e42";
        Range range = Range.school("64a1c8cd-a9b9-4755-a973-e1ce07f3f70a");
        Target target = Target.subject("001");

        List<String> studentList = studentService.getStudentIds(projectId, range, target);
        Map<String, AtomicInteger> counters = new HashMap<>();

        // 计算各得分等级人数
        for (String studentId : studentList) {
            String scoreLevel = scoreLevelService.getScoreLevel(projectId, studentId, target);
            if (!counters.containsKey(scoreLevel)) {
                counters.put(scoreLevel, new AtomicInteger());
            }
            counters.get(scoreLevel).incrementAndGet();
        }

        System.out.println(counters.toString());

        List<Document> scoreLevelRate = new ArrayList<>();
        for (String scoreLevel : counters.keySet()) {
            int levelStudentCount = counters.get(scoreLevel).get();
            double rate = (double) levelStudentCount / studentList.size();
            scoreLevelRate.add(doc("scoreLevel", scoreLevel).append("count", levelStudentCount).append("rate", rate));
        }

        System.out.println(scoreLevelRate.toString());
    }
}