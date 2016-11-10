package com.xz.examscore.asynccomponents.report.schools;

import com.xz.examscore.XzExamScoreV2ApplicationTests;
import com.xz.examscore.bean.Range;
import com.xz.examscore.bean.Target;
import com.xz.examscore.services.AverageService;
import com.xz.examscore.services.OverAverageService;
import com.xz.examscore.services.StudentService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author by fengye on 2016/6/8.
 */
public class SchoolBasicScoreReportTest extends XzExamScoreV2ApplicationTests {

    @Autowired
    SchoolBasicScoreReport schoolBasicScoreReport;

    @Autowired
    StudentService studentService;

    @Autowired
    OverAverageService overAverageService;

    @Autowired
    AverageService averageService;

    @Test
    public void testGetSheetTask() throws Exception {

        Range schoolRange = Range.school("1282f8d2-09c9-4e45-952b-549c1166ecbc");
        Range classRange = Range.clazz("eda45c4e-06da-49d7-ad7b-5d0175e5453e");
        Target subjectTarget = Target.subject("003");
        Target projectTarget = Target.project("431000-a5087c7540004f3a8df0042718c65424");
        String projectId = "431000-a5087c7540004f3a8df0042718c65424";


        schoolBasicScoreReport.generate("431000-a5087c7540004f3a8df0042718c65424", schoolRange, "target/school-basic-score.xlsx");
//        int studentCount = studentService.getStudentCount("430300-672a0ed23d9148e5a2a31c8bf1e08e62", Range.school("11b66fc2-8a76-41c2-a1b3-5011523c7e47"), Target.subject("003"));
        double overAverage = overAverageService.getOverAverage(projectId, classRange, subjectTarget);

        double classAvg = averageService.getAverage(projectId, classRange, subjectTarget);
        double schoolAvg = averageService.getAverage(projectId, schoolRange, projectTarget);
        double overAverage1 = (classAvg - schoolAvg) / schoolAvg;

        System.out.println(overAverage1);
        System.out.println(overAverage);
    }
}