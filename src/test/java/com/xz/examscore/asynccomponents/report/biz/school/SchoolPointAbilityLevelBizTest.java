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
        Param param = new Param().setParameter("projectId", "430100-dd3013ab961946fb8a3668e5ccc475b6")
                .setParameter("schoolId", "d9bdecc9-0185-4688-90d1-1aaf27e2dcfd")
                .setParameter("subjectId", "007");
        long begin = System.currentTimeMillis();
        Result result = schoolPointAbilityLevelBiz.execute(param);
        long end = System.currentTimeMillis();
        System.out.println(result.getData());
        System.out.println(end - begin);
    }
}