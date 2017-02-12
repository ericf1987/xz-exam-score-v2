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
        Param param = new Param().setParameter("projectId", "430100-e7bd093d92d844819c7eda8b641ab6ee")
                .setParameter("classId", "0bc7b0a4-adfc-4cb2-8324-863b976ab543")
                .setParameter("subjectId", "001");

        long begin = System.currentTimeMillis();
        Result result = classPointBiz.execute(param);
        long end = System.currentTimeMillis();
        System.out.println(end - begin);
        System.out.println(result.getData());
    }
}