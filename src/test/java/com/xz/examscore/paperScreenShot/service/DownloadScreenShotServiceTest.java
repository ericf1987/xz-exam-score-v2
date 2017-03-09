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
    public void testGenerateDownloadPath() throws Exception {
        String projectId = "430300-9cef9f2059ce4a36a40a7a60b07c7e00";
        String schoolId = "002e02d6-c036-4780-85d4-e54e3f1fbf9f";
        String[] classIds = new String[]{
                "5e43902d-954d-4483-a431-8931a5d7e2bf"//,"671d7704-11b9-47b6-9958-3900fe38217d","1359d983-8344-4596-b76f-9077dcf66551"
        };
        String[] subjectIds = new String[]{"001", "004", "008"};
        Map<String, Object> map = downloadScreenShotService.generateDownloadPath(projectId, schoolId, classIds, subjectIds);
        System.out.println(
                "文件路径列表：" + map.toString()
        );
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
        Map<String, Object> map = downloadScreenShotService.downloadGeneratedPaperScreenShot2(projectId, schoolId, classIds);
        System.out.println(map.toString());
    }

}