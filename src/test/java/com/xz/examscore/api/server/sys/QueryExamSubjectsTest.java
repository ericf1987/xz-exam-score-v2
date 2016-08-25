package com.xz.examscore.api.server.sys;

import com.xz.ajiaedu.common.lang.Result;
import com.xz.examscore.XzExamScoreV2ApplicationTests;
import com.xz.examscore.api.Param;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author by fengye on 2016/8/5.
 */
public class QueryExamSubjectsTest extends XzExamScoreV2ApplicationTests {

    String projectId = "430300-672a0ed23d9148e5a2a31c8bf1e08e62";

    String schoolId = "11b66fc2-8a76-41c2-a1b3-5011523c7e47";

    @Autowired
    QueryExamSubjects queryExamSubjects;

    @Test
    public void testExecute() throws Exception {
        Param param = new Param().setParameter("projectId", projectId).setParameter("schoolId", schoolId);
        long begin = System.currentTimeMillis();
        System.out.println("开始调用接口-->" + begin);
        Result result = queryExamSubjects.execute(param);
        long end = System.currentTimeMillis();
        System.out.println("调用接口完成-->" + end);
        System.out.println("用时-->" + (end - begin));
        System.out.println(result.getData());
    }
}