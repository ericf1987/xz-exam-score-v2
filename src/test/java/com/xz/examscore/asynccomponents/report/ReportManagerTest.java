package com.xz.examscore.asynccomponents.report;

import com.xz.examscore.XzExamScoreV2ApplicationTests;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * (description)
 * created at 16/06/06
 *
 * @author yiding_heString
 */
public class ReportManagerTest extends XzExamScoreV2ApplicationTests {

    @Autowired
    ReportManager reportManager;

    @Test
    public void testGenerateReports() throws Exception {
        String projectId = "430100-cb04005aa5ae460fae6b9d87df797066";
        List<ReportTask> list = reportManager.createReportGenerators(projectId);
        System.out.println(list.toString());
    }
}