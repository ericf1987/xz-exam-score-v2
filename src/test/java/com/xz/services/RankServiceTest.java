package com.xz.services;

import com.xz.XzExamScoreV2ApplicationTests;
import com.xz.bean.Range;
import com.xz.bean.Target;
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
}