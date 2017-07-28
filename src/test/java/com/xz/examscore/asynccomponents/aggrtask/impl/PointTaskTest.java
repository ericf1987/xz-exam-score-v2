package com.xz.examscore.asynccomponents.aggrtask.impl;

import com.xz.ajiaedu.common.lang.DoubleCounterMap;
import com.xz.examscore.XzExamScoreV2ApplicationTests;
import com.xz.examscore.asynccomponents.aggrtask.AggrTaskMessage;
import com.xz.examscore.bean.PointLevel;
import com.xz.examscore.bean.Range;
import com.xz.examscore.bean.SubjectLevel;
import com.xz.examscore.bean.Target;
import com.xz.examscore.services.PointService;
import com.xz.examscore.services.StudentService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author by fengye on 2016/7/30.
 */
public class PointTaskTest extends XzExamScoreV2ApplicationTests {

    public static final String PROJECT = "430300-672a0ed23d9148e5a2a31c8bf1e08e62";

    @Autowired
    StudentService studentService;

    @Autowired
    PointService pointService;

    @Autowired
    PointTask task;

    public static final String PROJECT_ID = "430000-6c4add56e5fb42b09f9de5387dfa59c0";
    public static final String STUDENT_ID = "0003520a-e436-4569-8a2b-bcfde02003c4";

    @Test
    public void testRunTask() throws Exception {
        task.runTask(
                new AggrTaskMessage(PROJECT, "1", Target.POINT)
                        .setRange(Range.student("09db6684-ba7c-435d-a430-70992c40fd0d")));
    }

    @Test
    public void testDoPointTaskDistribute() throws Exception {
        DoubleCounterMap<String> pointScores = new DoubleCounterMap<>();
        DoubleCounterMap<SubjectLevel> subjectLevelScores = new DoubleCounterMap<>();
        DoubleCounterMap<PointLevel> pointLevelScores = new DoubleCounterMap<>();

        task.doPointTaskDistribute(PROJECT_ID, "001", STUDENT_ID, pointScores, subjectLevelScores, pointLevelScores);
    }
}