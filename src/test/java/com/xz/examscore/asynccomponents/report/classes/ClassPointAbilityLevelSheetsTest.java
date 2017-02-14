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
        classPointAbilityLevelReport.generate("430100-e7bd093d92d844819c7eda8b641ab6ee", Range.clazz("0bc7b0a4-adfc-4cb2-8324-863b976ab543"), "target/class-point-ability-level-new.xlsx");
        long end = System.currentTimeMillis();
        System.out.println(end - begin);
    }
}