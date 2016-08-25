package com.xz.examscore.services;

import com.xz.examscore.XzExamScoreV2ApplicationTests;
import com.xz.examscore.bean.Range;
import com.xz.examscore.bean.Target;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * ${描述}
 *
 * @author zhaorenwu
 */
public class TopAverageServiceTest extends XzExamScoreV2ApplicationTests {

    @Autowired
    TopAverageService topAverageService;

    @Test
    public void testGetTopAverage() throws Exception {
        double topAverage = topAverageService.getTopAverage(XT_PROJECT_ID, Target.subject("001"),
                Range.clazz("0031ebb0-8653-4ad6-aa28-51ca88b99328"), 0.3);
        System.out.println(topAverage);
    }
}