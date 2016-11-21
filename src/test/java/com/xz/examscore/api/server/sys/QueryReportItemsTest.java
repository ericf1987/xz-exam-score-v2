package com.xz.examscore.api.server.sys;

import com.alibaba.fastjson.JSON;
import com.xz.ajiaedu.common.lang.Result;
import com.xz.examscore.XzExamScoreV2ApplicationTests;
import com.xz.examscore.api.Param;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

import static org.junit.Assert.*;

/**
 * @author by fengye on 2016/9/5.
 */
public class QueryReportItemsTest extends XzExamScoreV2ApplicationTests {
    @Autowired
    QueryReportItems queryReportItems;

    @Test
    public void testExecute() throws Exception {
        String projectId = "430500-a0b22e7797c5421bbb1eb99320599eeb";
        Param param = new Param().setParameter("projectId", projectId);
        Map<String, Object> paramMap = queryReportItems.execute(param).getData();
        String json = JSON.toJSONString(paramMap);
        System.out.println(json);
        Result result = JSON.parseObject(json, Result.class);
        System.out.println(result.getData());
    }
}