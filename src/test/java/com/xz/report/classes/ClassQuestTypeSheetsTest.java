package com.xz.report.classes;

import com.xz.XzExamScoreV2ApplicationTests;
import com.xz.bean.Range;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

/**
 * @author by fengye on 2016/6/27.
 */
public class ClassQuestTypeSheetsTest extends XzExamScoreV2ApplicationTests{
    @Autowired
    ClassQuestTypeReport classQuestTypeReport;

    @Test
    public void testGenerateSheet() throws Exception {
        classQuestTypeReport.generate("430300-672a0ed23d9148e5a2a31c8bf1e08e62", Range.clazz("0bdc71cf-7443-474f-9ba9-1d15c91a5561"), "target/class-quest-type-data.xlsx");
    }
}