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
    }

    @Test
    public void test4() throws Exception {
        String projectId = "430100-e7bd093d92d844819c7eda8b641ab6ee";
        String schoolId = "d00faaa0-8a9b-45c4-ae16-ea2688353cd0";
        String[] classIds = new String[]{
                "4a3336b6-4239-4e72-b613-cb3469b3def7", "8dcd679c-1f8c-4c87-b307-44b1bd7c9105"
        };
        Map<String, Object> map = downloadScreenShotService.downloadGeneratedPaperScreenShot(projectId, schoolId, classIds);
        System.out.println(map.toString());
    }

}