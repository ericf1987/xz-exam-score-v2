package com.xz.examscore.api.server.paperScreenShot;

import com.xz.ajiaedu.common.ajia.Param;
import com.xz.ajiaedu.common.lang.Result;
import com.xz.examscore.XzExamScoreV2ApplicationTests;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;

import static org.junit.Assert.*;

/**
 * @author by fengye on 2017/5/20.
 */
public class PaperImgItfTest extends XzExamScoreV2ApplicationTests {

    @Autowired
    PaperImgItf paperImgItf;

    @Test
    public void testExecute() throws Exception {
        Param param = new Param().setParameter("projectId", "430000-5f0a50b99441454599f2fa3bbe424485")
                .setParameter("schoolId", "e7740712-beb1-44cb-8049-145e2b6d06cb")
                .setParameter("classId", "e026a268-e0b9-4f3c-9865-3a784d84b732")
                .setParameter("subjectId", "001")
                .setParameter("studentId", "00a6bd6d-4400-41f2-87cd-45fa3a335876")
                .setParameter("isPositive", "true");

        Result execute = paperImgItf.execute(param);
        System.out.println(execute.getData().toString());
    }
}