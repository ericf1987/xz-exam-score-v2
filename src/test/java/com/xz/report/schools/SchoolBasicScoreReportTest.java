package com.xz.report.schools;

import com.xz.XzExamScoreV2ApplicationTests;
import com.xz.bean.Range;
import com.xz.bean.Target;
import com.xz.services.AverageService;
import com.xz.services.OverAverageService;
import com.xz.services.StudentService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

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

        Range schoolRange = Range.school("11b66fc2-8a76-41c2-a1b3-5011523c7e47");
        Range classRange = Range.clazz("01d354ca-8177-493a-8373-736a3f15c961");
        Target subjectTarget = Target.subject("003");
        Target projectTarget = Target.project("430300-672a0ed23d9148e5a2a31c8bf1e08e62");
        String projectId = "430300-672a0ed23d9148e5a2a31c8bf1e08e62";


        //schoolBasicScoreReport.generate("430300-672a0ed23d9148e5a2a31c8bf1e08e62", Range.school("11b66fc2-8a76-41c2-a1b3-5011523c7e47"), "target/school-basic-score.xlsx");
//        int studentCount = studentService.getStudentCount("430300-672a0ed23d9148e5a2a31c8bf1e08e62", Range.school("11b66fc2-8a76-41c2-a1b3-5011523c7e47"), Target.subject("003"));
        double overAverage = overAverageService.getOverAverage(projectId, classRange, subjectTarget);

        double classAvg = averageService.getAverage(projectId, classRange, subjectTarget);
        double schoolAvg = averageService.getAverage(projectId, schoolRange, projectTarget);
        double overAverage1 = (classAvg - schoolAvg) / schoolAvg;

        System.out.println(overAverage1);
        System.out.println(overAverage);
    }
}