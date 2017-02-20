package com.xz.examscore.asynccomponents.report.biz.classes;

import com.xz.ajiaedu.common.lang.Result;
import com.xz.examscore.XzExamScoreV2ApplicationTests;
import com.xz.examscore.api.Param;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author by fengye on 2017/2/9.
 */
public class ClassPointBizTest extends XzExamScoreV2ApplicationTests {

    @Autowired
    ClassPointBiz classPointBiz;

    @Test
    public void testExecute() throws Exception {
        Param param = new Param().setParameter("projectId", "FAKE_PROJ_1486524671547_0")
                .setParameter("classId", "CLASS_1486524671577_2")
                .setParameter("subjectId", "001");

        long begin = System.currentTimeMillis();
        Result result = classPointBiz.execute(param);
        long end = System.currentTimeMillis();
        System.out.println(end - begin);
        System.out.println(result.getData());
    }
}