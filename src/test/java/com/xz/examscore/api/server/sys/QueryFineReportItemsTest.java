package com.xz.examscore.api.server.sys;

import com.xz.ajiaedu.common.lang.Result;
import com.xz.examscore.XzExamScoreV2ApplicationTests;
import com.xz.examscore.api.Param;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

/**
 * @author by fengye on 2017/4/26.
 */
public class QueryFineReportItemsTest extends XzExamScoreV2ApplicationTests {

    @Autowired
    QueryFineReportItems queryFineReportItems;

    @Test
    public void testExecute() throws Exception {
        Param param = new Param();
        Result result = queryFineReportItems.execute(param);
        System.out.println(result.getData());
    }
}