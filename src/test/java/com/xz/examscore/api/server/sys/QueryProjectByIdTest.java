package com.xz.examscore.api.server.sys;

import com.xz.ajiaedu.common.ajia.Param;
import com.xz.ajiaedu.common.lang.Result;
import com.xz.examscore.XzExamScoreV2ApplicationTests;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

/**
 * @author by fengye on 2017/6/19.
 */
public class QueryProjectByIdTest extends XzExamScoreV2ApplicationTests {

    @Autowired
    QueryProjectById queryProjectById;

    @Test
    public void testExecute() throws Exception {
        Param param = new Param().setParameter("projectId", "430100-661af70cb9e94f18964b27971772bb1b");
        Result result = queryProjectById.execute(param);
        System.out.println(result.getData().toString());
    }
}