package com.xz.examscore.paperScreenShot.service;

import com.xz.examscore.XzExamScoreV2ApplicationTests;
import com.xz.examscore.paperScreenShot.bean.PaperScreenShotBean;
import com.xz.examscore.services.SubjectService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

/**
 * @author by fengye on 2017/3/1.
 */
public class PaperScreenShotServiceTest extends XzExamScoreV2ApplicationTests{

    @Autowired
    PaperScreenShotService paperScreenShotService;

    @Autowired
    SubjectService subjectService;

    @Autowired
    PaintService paintService;

    @Autowired
    PaperScreenShotConfigService paperScreenShotConfigService;

    @Test
    public void testStartPaperScreenShotTask() throws Exception {
        String projectId = "430300-9cef9f2059ce4a36a40a7a60b07c7e00";
        paperScreenShotService.startPaperScreenShotTask(projectId);
    }

    @Test
    public void testdispatchOneClassTask() throws Exception {
        String projectId = "430900-8f11fe8dbac842a3805d45e05eb31095";
        String schoolId = "d0d78c80-b6ae-4908-80dd-fd78efc01479";
        String classId = "a1ee55a0-a62d-4337-b50e-2e43e0423597";
        List<String> subjects = subjectService.querySubjects(projectId);
        Map<String, Object> configFromCMS = paperScreenShotConfigService.getConfigFromCMS(projectId);

        paperScreenShotService.dispatchOneClassTask(projectId, schoolId, classId, subjects, configFromCMS);
    }

    @Test
    public void testpackScreenShotTaskBean() throws Exception {
        String projectId = "430300-c582131e66b64fe38da7d0510c399ec4";
        String schoolId = "15e70531-5ac0-475d-a2da-2fc04242ac75";
        String classId = "18e035e5-63e9-47bf-9d88-759129e429db";
        String subjectId = "009";
        PaperScreenShotBean paperScreenShotBean = paperScreenShotService.packScreenShotTaskBean(projectId, schoolId, classId, subjectId, "100");
        System.out.println(paperScreenShotBean.getStudentCardSlices().toString());
        paintService.saveScreenShot(paperScreenShotBean, null);
    }

}