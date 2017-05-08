package com.xz.examscore.api.server.sys;

import com.xz.ajiaedu.common.lang.Result;
import com.xz.examscore.XzExamScoreV2ApplicationTests;
import com.xz.examscore.api.Param;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

/**
 * @author by fengye on 2017/5/8.
 */
public class QueryProjectPublishStatusTest extends XzExamScoreV2ApplicationTests{

    @Autowired
    QueryProjectPublishStatus queryProjectPublishStatus;

    @Test
    public void testExecute() throws Exception {
        Param param = new Param().setParameter("projectId", "430700-bfa36b52d29a4361978a0926b719f865");
        Result execute = queryProjectPublishStatus.execute(param);
        System.out.println(execute.getData());

    }
}