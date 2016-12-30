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

    @Autowired
    StudentCompetitiveService studentCompetitiveService;

    @Test
    public void testGetRank() throws Exception {
        System.out.println("rank of 99: " +
                rankService.getRank(XT_PROJECT_ID, Range.school("SCHOOL_007"), Target.subject("003"), 99));
    }

    @Test
    public void testGetRank2() throws Exception {
        String projectId = "430200-3e67c524f149491597279ef6ae31baef";
        Range range = Range.province("430000");
        Target target = Target.subjectCombination("004005006");

        int rank = rankService.getRank(projectId,
                range, target, "c8a246f3-cb0e-4eee-a295-b0730b5f2e1e");

        int studentCount = studentService.getStudentCount(projectId, range, target);
        double average = studentCompetitiveService.getAverage(projectId, range, target, rank);
        System.out.println(rank + ", " + studentCount + ", " + average);
    }

    @Test
    public void testGetRankLevel() throws Exception {
        String levelMap = rankService.getRankLevel(
                "430600-2404b0cc131c472dbbd13085385f5ee0", Range.clazz("e86f50b4-cbe6-403c-84d1-8cc668ee0221"),
                Target.subjectCombination("004005006"), "04d2293d-06ed-456d-a3eb-e9060b9a132f"
        );
        System.out.println(levelMap);
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