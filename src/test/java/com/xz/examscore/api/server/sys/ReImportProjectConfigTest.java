package com.xz.examscore.api.server.sys;

import com.xz.examscore.XzExamScoreV2ApplicationTests;
import com.xz.examscore.api.Param;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author by fengye on 2016/11/10.
 */
public class ReImportProjectConfigTest extends XzExamScoreV2ApplicationTests {
    @Autowired
    ReImportProjectConfig reImportProjectConfig;

    @Test
    public void testExecute() throws Exception {
        Param param = new Param().setParameter("projectId", "430100-bdfa63b0070b431fbc5308c0d362e74f");
        reImportProjectConfig.execute(param);
    }
}