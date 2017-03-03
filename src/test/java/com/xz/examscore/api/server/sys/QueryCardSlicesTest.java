package com.xz.examscore.api.server.sys;

import com.xz.ajiaedu.common.lang.Result;
import com.xz.examscore.XzExamScoreV2ApplicationTests;
import com.xz.examscore.api.Param;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

/**
 * @author by fengye on 2017/2/27.
 */
public class QueryCardSlicesTest extends XzExamScoreV2ApplicationTests{

    @Autowired
    QueryCardSlices queryCardSlices;

    @Test
    public void testExecute() throws Exception {
        String projectId = "430300-9cef9f2059ce4a36a40a7a60b07c7e00";
        String schoolId = "";
        String[] classIds = new String[]{
                "fbbd5232-9d0f-4077-811d-8b432e9c7fa8", "133840ca-3315-41ee-ab97-1a09fde16237"
        };

        String[] subjectIds = new String[]{
                "001", "003"
        };

        Param param = new Param().setParameter("projectId", projectId)
                .setParameter("schoolId", "")
                .setParameter("classIds", classIds)
                .setParameter("subjectIds", subjectIds);

        long begin = System.currentTimeMillis();
        Result result = queryCardSlices.execute(param);
        List<Map<String, Object>> list = (List<Map<String, Object>>)result.get("studentCardSlices");
        System.out.println(list.size());
        System.out.println(list.toString());
        long end = System.currentTimeMillis();
        System.out.println(end - begin);
    }
}