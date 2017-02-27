package com.xz.examscore.services;

import com.xz.examscore.XzExamScoreV2ApplicationTests;
import org.apache.commons.collections.MapUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author by fengye on 2016/9/10.
 */
public class ReportItemServiceTest extends XzExamScoreV2ApplicationTests {
    @Autowired
    ReportItemService reportItemService;

    @Test
    public void testQuerySchoolReportItems() throws Exception {
        Map<String, Object> param = reportItemService.querySchoolReportItems("430500-a0b22e7797c5421bbb1eb99320599eeb", "true");
        System.out.println(param.get("province").getClass());
        System.out.println(param.get("province").toString());
    }
}