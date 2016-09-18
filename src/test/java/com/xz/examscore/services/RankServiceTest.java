package com.xz.examscore.services;

import com.xz.examscore.XzExamScoreV2ApplicationTests;
import com.xz.examscore.bean.Range;
import com.xz.examscore.bean.Target;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * (description)
 * created at 16/05/13
 *
 * @author yiding_he
 */
public class RankServiceTest extends XzExamScoreV2ApplicationTests {

    @Autowired
    RankService rankService;

    @Autowired
    StudentService studentService;

    @Autowired
    ScoreService scoreService;

    @Test
    public void testGetRank() throws Exception {
        System.out.println("rank of 99: " +
                rankService.getRank(XT_PROJECT_ID, Range.school("SCHOOL_007"), Target.subject("003"), 99));
    }

    @Test
    public void testGetRank2() throws Exception {
        String projectId = "430200-89c9dc7481cd47a69d85af3f0808e0c4";
        Range range = Range.school("7e34fa5e-9023-4ad4-b4fa-fe4e3d7d1b52");
        Target target = Target.quest("573c49e62d560287556b8a76");

        int rank = rankService.getRank(projectId,
                range, target, "c740e974-c281-4c19-9f1b-82103c691563");

        int studentCount = studentService.getStudentCount(projectId, range, target);

        System.out.println(rank + ", " + studentCount);
    }

    @Test
    public void testGetRankLevel() throws Exception {
        System.out.println(
                rankService.getRankLevel(
                        XT_PROJECT_ID, Range.school("SCHOOL_008"), Target.subject("002"), "SCHOOL_008_CLASS_08_01"));
    }

    @Test
    public void testGetRankLevel1() throws Exception {
        String projectId = "430100-e7bd093d92d844819c7eda8b641ab6ee";
        String classId = "4a3336b6-4239-4e72-b613-cb3469b3def7";
        String subjectId = "001";
        String studentId = "fb599639-d37e-4372-bc97-e0977603c2a2";
        int count = studentService.getStudentCount(projectId, Range.clazz(classId));
        int rank = rankService.getRank(projectId, Range.clazz(classId), Target.subject(subjectId), studentId);
        System.out.println("学生总人数-->" + count + "排名-->" + rank);
    }

    @Test
    public void testRankScore() throws Exception {
        String projectId = "430600-95e565c247574dd3b935ae9912c8eca5";
        String classId = "649e603f-1e27-43bb-89ca-5970efb76710";
        String schoolId = "d1bf6d54-1e2e-40b3-b3df-fda8069e4389";
        String subjectId = "001";
        double score = rankService.getRankScore(projectId, Range.clazz(classId), Target.subject(subjectId), 10);
        System.out.println(score);
    }
}