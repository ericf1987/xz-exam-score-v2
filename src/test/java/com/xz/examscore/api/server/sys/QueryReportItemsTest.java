package com.xz.examscore.api.server.sys;

import com.xz.examscore.XzExamScoreV2ApplicationTests;
import com.xz.examscore.api.Param;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

/**
 * @author by fengye on 2016/9/5.
 */
public class QueryReportItemsTest extends XzExamScoreV2ApplicationTests {
    @Autowired
    QueryReportItems queryReportItems;

    @Test
    public void testExecute() throws Exception {
        String projectId = "430100-e7bd093d92d844819c7eda8b641ab6ee";
        Param param = new Param().setParameter("projectId", projectId);
        System.out.println(queryReportItems.execute(param).getData());
    }
}