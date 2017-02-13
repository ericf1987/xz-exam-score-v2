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
        Param param = new Param().setParameter("projectId", "430100-194d9c9dd59d4145ae94bb66a06434d0")
                .setParameter("classId", "33af690e-a3a2-41e5-b689-0fff6ebb315e")
                .setParameter("subjectId", "001");
        long begin = System.currentTimeMillis();
        Result result = schoolPointAbilityLevelBiz.execute(param);
        long end = System.currentTimeMillis();
        System.out.println(result.getData());
        System.out.println(end - begin);
    }
}