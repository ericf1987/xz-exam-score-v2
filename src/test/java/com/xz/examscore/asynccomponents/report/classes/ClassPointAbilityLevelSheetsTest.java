package com.xz.examscore.asynccomponents.report.classes;

import com.xz.examscore.XzExamScoreV2ApplicationTests;
import com.xz.examscore.bean.Range;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author by fengye on 2017/2/14.
 */
public class ClassPointAbilityLevelSheetsTest extends XzExamScoreV2ApplicationTests {

    @Autowired
    ClassPointAbilityLevelReport classPointAbilityLevelReport;

    @Test
    public void testGenerateSheet() throws Exception {
        long begin = System.currentTimeMillis();
        classPointAbilityLevelReport.generate("430100-dd3013ab961946fb8a3668e5ccc475b6",
                Range.clazz("21c44641-dabd-4e98-b2fa-eef94a9d8ffc"), "target/class-point-ability-level.xlsx");
        long end = System.currentTimeMillis();
        System.out.println(end - begin);
    }
}