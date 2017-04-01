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
        String projectId = "430300-f39dc20ba0044b8b9cb302d7910c87c4";
        List<ReportTask> list = reportManager.createReportGenerators(projectId, false);
        System.out.println(list.toString());
    }
}