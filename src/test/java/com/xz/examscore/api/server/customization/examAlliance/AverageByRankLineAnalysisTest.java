package com.xz.examscore.api.server.customization.examAlliance;

import com.xz.ajiaedu.common.lang.Result;
import com.xz.examscore.XzExamScoreV2ApplicationTests;
import com.xz.ajiaedu.common.ajia.Param;
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
        String projectId = "430100-6da6fefff9b74e67917950567b368910";
        Range provinceRange = Range.province(provinceService.getProjectProvince(projectId));
        Param param = new Param().setParameter("projectId", "430100-6da6fefff9b74e67917950567b368910")
                .setParameter("rankSegment", "0.9");
        Result result = averageByRankLineAnalysis.execute(param);
        int studentCount = studentService.getStudentCount(projectId, provinceRange, Target.project(projectId));
        System.out.println(studentCount);
        System.out.println(result.getData());
    }
}