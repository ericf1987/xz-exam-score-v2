package com.xz.examscore.services;

import com.xz.examscore.XzExamScoreV2ApplicationTests;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

/**
 * @author by fengye on 2016/11/10.
 */
public class ReportServiceTest extends XzExamScoreV2ApplicationTests {

    @Autowired
    ReportService reportService;

    @Test
    public void testGenerateReports() throws Exception {
        reportService.generateReports("430200-3e67c524f149491597279ef6ae31baef", true, false);
    }
}