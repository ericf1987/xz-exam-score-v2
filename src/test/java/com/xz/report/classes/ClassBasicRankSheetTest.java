package com.xz.report.classes;

import com.xz.XzExamScoreV2ApplicationTests;
import com.xz.ajiaedu.common.lang.Result;
import com.xz.api.Param;
import com.xz.api.server.classes.ClassBasicDataAnalysis;
import com.xz.api.server.classes.ClassRankAnalysis;
import com.xz.bean.Range;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

/**
 * @author by fengye on 2016/8/1.
 */
public class ClassBasicRankSheetTest extends XzExamScoreV2ApplicationTests {
    @Autowired
    ClassBasicRankReport classBasicRankReport;

    @Test
    public void testExecute() throws Exception {
        classBasicRankReport.generate("430100-2df3f3ad199042c39c5f4b69f5dc7840", Range.clazz("a1895cd9-d82c-4b12-a698-164fb5ceb1f3"), "target/class-score-rank.xlsx");
    }
}