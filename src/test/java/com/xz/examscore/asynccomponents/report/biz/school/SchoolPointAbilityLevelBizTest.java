package com.xz.examscore.asynccomponents.report.biz.school;

import com.xz.ajiaedu.common.lang.Result;
import com.xz.examscore.XzExamScoreV2ApplicationTests;
import com.xz.examscore.api.Param;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author by fengye on 2017/2/13.
 */
public class SchoolPointAbilityLevelBizTest extends XzExamScoreV2ApplicationTests {

    @Autowired
    SchoolPointAbilityLevelBiz schoolPointAbilityLevelBiz;

    @Test
    public void testExecute() throws Exception {
        Param param = new Param().setParameter("projectId", "430100-e7bd093d92d844819c7eda8b641ab6ee")
                .setParameter("schoolId", "d00faaa0-8a9b-45c4-ae16-ea2688353cd0")
                .setParameter("subjectId", "001");
        long begin = System.currentTimeMillis();
        Result result = schoolPointAbilityLevelBiz.execute(param);
        long end = System.currentTimeMillis();
        System.out.println(result.getData());
        System.out.println(end - begin);
    }
}