package com.xz.examscore.asynccomponents.aggrtask.impl;

import com.mongodb.client.MongoDatabase;
import com.xz.ajiaedu.common.lang.Value;
import com.xz.examscore.XzExamScoreV2ApplicationTests;
import com.xz.examscore.asynccomponents.aggrtask.AggrTaskMessage;
import com.xz.examscore.bean.Range;
import com.xz.examscore.bean.Target;
import com.xz.examscore.services.ScoreService;
import com.xz.examscore.services.StudentService;
import com.xz.examscore.services.TargetService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * (description)
 * created at 16/05/13
 *
 * @author yiding_he
 */
public class MinMaxTaskTest extends XzExamScoreV2ApplicationTests {

    @Autowired
    MinMaxTask minMaxTask;

    @Autowired
    ScoreService scoreService;

    @Autowired
    StudentService studentService;

    @Autowired
    TargetService targetService;

    @Test
    public void testRunTask() throws Exception {
        minMaxTask.runTask(
                new AggrTaskMessage(XT_PROJECT_ID, "aggr1", "minmax")
                        .setRange(Range.SCHOOL, "SCHOOL_005")
                        .setTarget(Target.SUBJECT, "001"));
    }

    @Test
    public void test1() throws Exception {
        String projectId = "430100-553137a1e78741149104526aaa84393e";
        Target target = Target.subject("008");
        Range range = Range.school("d00faaa0-8a9b-45c4-ae16-ea2688353cd0");
        String subjectId = targetService.getTargetSubjectId(projectId, target);

        // 查询考生列表
        List<String> studentIds = studentService.getStudentIds(projectId, subjectId, range);

        if (studentIds.isEmpty()) {

        } else {
            // 查询每个考生的分数，得出最高分最低分
            Value<Double> min = Value.of((double) Integer.MAX_VALUE), max = Value.of(0d);
            queryMinMax(projectId, target, studentIds, min, max);

            // 保存最高分最低分
            System.out.println("最高分-->" + max.get());
            System.out.println("最低分-->" + min.get());
        }
    }

    private void queryMinMax(
            String projectId, Target target, List<String> studentIds, Value<Double> min, Value<Double> max) {

        for (int i = 0; i < studentIds.size(); i++) {
            double totalScore = scoreService.getScore(projectId, new Range(Range.STUDENT, studentIds.get(i)), target);

            if(i == 0){
                min.set(totalScore);
                max.set(totalScore);
            }

            if (totalScore < min.get()) {
                min.set(totalScore);
            } else if (totalScore > max.get()) {
                max.set(totalScore);
            }
        }
    }
}