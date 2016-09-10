package com.xz.examscore.services;

import com.xz.examscore.XzExamScoreV2ApplicationTests;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

import static org.junit.Assert.*;

/**
 * @author by fengye on 2016/9/10.
 */
public class ReportItemServiceTest extends XzExamScoreV2ApplicationTests {
    @Autowired
    ReportItemService reportItemService;

    @Test
    public void testQuerySchoolReportItems() throws Exception {
        Map<String, Object> param = reportItemService.querySchoolReportItems("430600-855904ddd05243d2a6eeb76832e7b61e");
        System.out.println(param.get("school").getClass());
        System.out.println(param.get("school").toString());
    }
}