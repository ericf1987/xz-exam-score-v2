package com.xz.examscore.paperScreenShot.service;

import com.xz.examscore.XzExamScoreV2ApplicationTests;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

/**
 * @author by fengye on 2017/3/5.
 */
public class DownloadScreenShotServiceTest extends XzExamScoreV2ApplicationTests {

    @Autowired
    DownloadScreenShotService downloadScreenShotService;

    @Autowired
    PaperScreenShotService paperScreenShotService;

    @Test
    public void testDownloadPaperScreenShot() throws Exception {

    }

    @Test
    public void testDownloadPaperScreenShotByClass() throws Exception {
        String projectId = "430300-9cef9f2059ce4a36a40a7a60b07c7e00";
        String schoolId = "dd46843a-0ea9-4d49-a664-7eb1fb869e79";
        List<Map<String, Object>> list = paperScreenShotService.generateClassPaperScreenShot(projectId, schoolId);
        System.out.println(list.toString());
    }

    @Test
    public void test4() throws Exception {
        String projectId = "430300-9cef9f2059ce4a36a40a7a60b07c7e00";
        String schoolId = "dd46843a-0ea9-4d49-a664-7eb1fb869e79";
        String[] classIds = new String[]{
                "42ffec58-7d86-4979-9ae0-04e6b5f6771d"
        };
        Map<String, Object> map = downloadScreenShotService.downloadGeneratedPaperScreenShot(projectId, schoolId, classIds);
        System.out.println(map.toString());
    }

}