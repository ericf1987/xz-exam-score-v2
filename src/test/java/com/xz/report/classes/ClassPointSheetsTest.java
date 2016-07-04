package com.xz.report.classes;

import com.xz.XzExamScoreV2ApplicationTests;
import com.xz.bean.Range;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

/**
 * @author by fengye on 2016/6/30.
 */
public class ClassPointSheetsTest extends XzExamScoreV2ApplicationTests{
    @Autowired
    ClassPointReport classPointReport;

    @Test
    public void testGenerateSheet() throws Exception {
        classPointReport.generate("430200-b73f03af1d74484f84f1aa93f583caaa", Range.clazz("0c738247-b62c-4c90-9016-1cc1163fd0b1"), "target/class-point.xlsx");
    }
}