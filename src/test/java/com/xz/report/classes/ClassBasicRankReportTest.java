package com.xz.report.classes;

import com.xz.XzExamScoreV2ApplicationTests;
import com.xz.bean.Range;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

/**
 * @author by fengye on 2016/6/8.
 */
public class ClassBasicRankReportTest extends XzExamScoreV2ApplicationTests {
    @Autowired
    ClassBasicRankReport classBasicRankReport;

    @Test
    public void testGetSheetTasks() throws Exception {
        classBasicRankReport.generate("430200-8a9be9fc2e1842a4b9b4894eee1f5f73", Range.clazz("e08222d5-25c9-4915-94ea-7d9758705f20"), "target/class-basic-rank.xlsx");
    }
}