package com.xz.examscore.services;

import com.xz.examscore.XzExamScoreV2ApplicationTests;
import com.xz.examscore.bean.Range;
import com.xz.examscore.bean.Target;
import org.bson.Document;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * (description)
 * created at 16/05/16
 *
 * @author yiding_he
 */
public class AverageServiceTest extends XzExamScoreV2ApplicationTests {

    @Autowired
    AverageService averageService;

    @Autowired
    PointService pointService;

    @Test
    public void testGetAverage() throws Exception {
        double average = averageService.getAverage(UNION_PROJECT_ID,
                Range.school("0835e05b-4d01-4944-9a0a-b8a77f201933"), Target.subjectLevel("001", "A"));

        System.out.println(average);
    }

    @Test
    public void testGetAverage2() throws Exception {
        String project = "430200-b73f03af1d74484f84f1aa93f583caaa";
        // Range range = Range.province("430000");
        Range range = Range.school("200f3928-a8bd-48c4-a2f4-322e9ffe3700");
        Target target = Target.point("1025548");
        double average = averageService.getAverage(project, range, target);
        System.out.println(average);
    }

    @Test
    public void testGetAverage3() throws Exception {
        String projectId = "430100-e7bd093d92d844819c7eda8b641ab6ee";
        String subjectId = "001";
        Range clazzRange = Range.clazz("0bc7b0a4-adfc-4cb2-8324-863b976ab543");
        List<String> pointIds = pointService.getPoints(projectId, subjectId).stream().map(p -> p.getId()).collect(Collectors.toList());
        System.out.println(pointIds.size());
        System.out.println(pointIds.toString());
        long begin = System.currentTimeMillis();
        ArrayList<Document> averageByTargetIds = averageService.getAverageByTargetIds(projectId, clazzRange, pointIds);
        System.out.println(averageByTargetIds.size());
        long end = System.currentTimeMillis();
        System.out.println(end - begin);

    }
}