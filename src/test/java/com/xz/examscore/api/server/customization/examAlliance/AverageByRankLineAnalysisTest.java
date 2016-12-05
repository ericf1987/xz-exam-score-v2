package com.xz.examscore.api.server.customization.examAlliance;

import com.xz.ajiaedu.common.lang.Result;
import com.xz.examscore.XzExamScoreV2ApplicationTests;
import com.xz.examscore.api.Param;
import com.xz.examscore.bean.Range;
import com.xz.examscore.bean.Target;
import com.xz.examscore.services.ProvinceService;
import com.xz.examscore.services.StudentService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author by fengye on 2016/12/5.
 */
public class AverageByRankLineAnalysisTest extends XzExamScoreV2ApplicationTests {

    @Autowired
    AverageByRankLineAnalysis averageByRankLineAnalysis;

    @Autowired
    StudentService studentService;

    @Autowired
    ProvinceService provinceService;

    @Test
    public void testExecute() throws Exception {
        String projectId = "430300-672a0ed23d9148e5a2a31c8bf1e08e62";
        Range provinceRange = Range.province(provinceService.getProjectProvince(projectId));
        Param param = new Param().setParameter("projectId", "430300-672a0ed23d9148e5a2a31c8bf1e08e62")
                .setParameter("rankSegment", "0.1");
        Result result = averageByRankLineAnalysis.execute(param);
        int studentCount = studentService.getStudentCount(projectId, provinceRange, Target.project(projectId));
        System.out.println(studentCount);
        System.out.println(result.getData());
    }
}