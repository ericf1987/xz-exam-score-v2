package com.xz.examscore.services;

import com.xz.ajiaedu.common.lang.Result;
import com.xz.examscore.XzExamScoreV2ApplicationTests;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

/**
 * @author by fengye on 2017/1/3.
 */
public class ExamAllianceReportServiceTest extends XzExamScoreV2ApplicationTests {

    @Autowired
    ExamAllianceReportService examAllianceReportService;

    @Test
    public void testDownloadReports() throws Exception {
        Result result = examAllianceReportService.downloadReports("430200-3e67c524f149491597279ef6ae31baef");
        System.out.println(result.getData());
    }
}