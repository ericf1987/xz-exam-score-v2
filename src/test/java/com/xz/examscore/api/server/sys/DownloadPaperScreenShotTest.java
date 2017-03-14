package com.xz.examscore.api.server.sys;

import com.xz.ajiaedu.common.lang.Result;
import com.xz.examscore.XzExamScoreV2ApplicationTests;
import com.xz.examscore.api.Param;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author by fengye on 2017/3/8.
 */
public class DownloadPaperScreenShotTest extends XzExamScoreV2ApplicationTests {

    @Autowired
    DownloadPaperScreenShot downloadPaperScreenShot;

    @Test
    public void testExecute() throws Exception {
        String projectId = "430300-9cef9f2059ce4a36a40a7a60b07c7e00";
        String schoolId = "dd46843a-0ea9-4d49-a664-7eb1fb869e79";
        String[] classIds = new String[]{
                "42ffec58-7d86-4979-9ae0-04e6b5f6771d"
        };
        String[] subjectIds = new String[]{
                "001", "001-002-003-007008009"
        };

        Param param = new Param().setParameter("projectId", projectId)
                .setParameter("schoolId", schoolId)
                .setParameter("classIds", classIds)
                .setParameter("subjectIds", subjectIds);

        Result result = downloadPaperScreenShot.execute(param);
        System.out.println(result.getData());
    }

    @Test
    public void test2() throws Exception {
        String projectId = "430300-9cef9f2059ce4a36a40a7a60b07c7e00";
        String schoolId = "dd46843a-0ea9-4d49-a664-7eb1fb869e79";
        String[] classIds = new String[]{
                "42ffec58-7d86-4979-9ae0-04e6b5f6771d"
        };
        Param param = new Param().setParameter("projectId", projectId)
                .setParameter("schoolId", schoolId)
                .setParameter("classIds", classIds);
        Result result = downloadPaperScreenShot.execute(param);
        System.out.println(result.getData());
    }

    @Test
    public void test3() throws Exception {
        String projectId = "430300-9cef9f2059ce4a36a40a7a60b07c7e00";
        String schoolId = "dd46843a-0ea9-4d49-a664-7eb1fb869e79";
        Param param = new Param().setParameter("projectId", projectId)
                .setParameter("schoolId", schoolId);
        Result result = downloadPaperScreenShot.execute(param);
        System.out.println(result.getData());
    }

}