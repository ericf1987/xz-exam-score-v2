package com.xz.examscore.services;

import com.xz.examscore.XzExamScoreV2ApplicationTests;
import com.xz.examscore.bean.PointLevel;
import com.xz.examscore.bean.Range;
import com.xz.examscore.bean.Target;
import com.xz.examscore.util.Mongo;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * (description)
 * created at 16/05/13
 *
 * @author yiding_he
 */
public class ScoreServiceTest extends XzExamScoreV2ApplicationTests {

    public static final String PROJECT = "430100-a05db0d05ad14010a5c782cd31c0283f";

    @Autowired
    ScoreService scoreService;

    @Test
    public void testGetTotalScore() throws Exception {
        double score = scoreService.getScore("430200-b73f03af1d74484f84f1aa93f583caaa",
                Range.school("200f3928-a8bd-48c4-a2f4-322e9ffe3700"),
                Target.point("1025605"));

        System.out.println(score);
    }

    @Test
    public void testGetQuestCorrectCount() throws Exception {
        int count = scoreService.getQuestCorrentCount(
                "430200-89c9dc7481cd47a69d85af3f0808e0c4",
                "57403a032d560287556b90d4",
                Range.school("7e34fa5e-9023-4ad4-b4fa-fe4e3d7d1b52")
        );

        System.out.println(count);
    }

    @Test
    public void testGetSubjectLevelTotalScore() throws Exception {
        Range range = Range.clazz("a1895cd9-d82c-4b12-a698-164fb5ceb1f3");
        double totalScore = scoreService.getScore(PROJECT, range, Target.subjectLevel("003", "B"));
        System.out.println(totalScore);
    }

    @Test
    public void testAddTotalScore() throws Exception {
        String projectId = "430300-672a0ed23d9148e5a2a31c8bf1e08e62";
        Range range = Range.clazz("747f3d0f-e108-4cc8-95ea-f0293b7cfc41");
        PointLevel pointLevel = new PointLevel("1019056", "A");
        Target target = Target.pointLevel(pointLevel);
        int modifiedCount = scoreService.addTotalScore(projectId, range, target, 1.1);
        System.out.println(modifiedCount);
    }

    @Test
    public void testGetScore() throws Exception {
        String projectId = "430100-2c641a3e36ff492aa535da7fb4cf28cf";
        Range range = Range.clazz("27bb692f-a179-41b1-a57f-ab51ee42b71d");
        Target target = Target.subjectCombination("007008009");
        System.out.println(projectId + "|" + range.toString() + "|" + target.toString());
        System.out.println(Mongo.target2Doc(target).toString());
        double d = scoreService.getScore(projectId, range, target);
        System.out.println(d);
    }

    @Test
    public void testGetCountByScore() throws Exception {
        String projectId = "430300-672a0ed23d9148e5a2a31c8bf1e08e62";
        Range range = Range.school("c99a630b-d8e6-4758-b27d-4b062f9fec0a");
        Target target = Target.project(projectId);
        int count = scoreService.getCountByScore(projectId, range, target, 800);
        System.out.println(count);
    }
}