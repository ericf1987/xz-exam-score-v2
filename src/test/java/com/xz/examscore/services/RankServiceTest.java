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
    ProvinceService provinceService;

    @Autowired
    StudentCompetitiveService studentCompetitiveService;

    @Test
    public void testGetRank() throws Exception {
        System.out.println("rank of 99: " +
                rankService.getRank(XT_PROJECT_ID, Range.school("SCHOOL_007"), Target.subject("003"), 99));
    }

    @Test
    public void testGetRankByStudentId() throws Exception {
        String projectId = "430200-83943be3c36f43a8aab0b545e66dbe3d";
        Range schoolRange = Range.school("7e34fa5e-9023-4ad4-b4fa-fe4e3d7d1b52");
        Range classRange = Range.clazz("fbc457d7-865c-4599-86e3-baf857f7a75d");
        Target target = Target.subject("002");

        int rank = rankService.getRank(projectId,
                classRange, target, "c6864656-f372-4ffa-91ba-50bcb56dad1a");

        int studentCount = studentService.getStudentCount(projectId, classRange, target);
        System.out.println(rank + ", " + studentCount);
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
        String projectId = "430300-29c4d40d93bf41a5a82baffe7e714dd9";
        Range provinceRange = Range.province(provinceService.getProjectProvince(projectId));
        Target projectTarget = Target.project(projectId);
        double score = rankService.getRankScore(projectId, provinceRange, projectTarget, 15);
        System.out.println(score);
    }
}