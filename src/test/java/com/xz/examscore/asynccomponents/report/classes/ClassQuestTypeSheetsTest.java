package com.xz.examscore.asynccomponents.report.classes;

import com.xz.examscore.XzExamScoreV2ApplicationTests;
import com.xz.examscore.bean.Range;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author by fengye on 2016/6/27.
 */
public class ClassQuestTypeSheetsTest extends XzExamScoreV2ApplicationTests{
    @Autowired
    ClassQuestTypeReport classQuestTypeReport;

    @Test
    public void testGenerateSheet() throws Exception {
        long begin = System.currentTimeMillis();
        classQuestTypeReport.generate("430100-194d9c9dd59d4145ae94bb66a06434d0", Range.clazz("33af690e-a3a2-41e5-b689-0fff6ebb315e"), "target/class-quest-type-data.xlsx");
        long end = System.currentTimeMillis();
        System.out.println("生成报表耗时：" + (end - begin));
    }
}