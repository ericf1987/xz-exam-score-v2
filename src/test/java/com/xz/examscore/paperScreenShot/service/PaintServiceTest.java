package com.xz.examscore.paperScreenShot.service;

import com.xz.examscore.XzExamScoreV2ApplicationTests;
import com.xz.examscore.paperScreenShot.bean.PaperScreenShotBean;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author by fengye on 2017/3/1.
 */
public class PaintServiceTest extends XzExamScoreV2ApplicationTests{

    @Autowired
    PaintService paintService;

    @Autowired
    PaperScreenShotService paperScreenShotService;

    @Test
    public void testSaveScreenShot() throws Exception {
        String projectId = "430300-9cef9f2059ce4a36a40a7a60b07c7e00";
        String schoolId = "dd46843a-0ea9-4d49-a664-7eb1fb869e79";
        String classId = "42ffec58-7d86-4979-9ae0-04e6b5f6771d";
        String subjectId = "001";

        PaperScreenShotBean paperScreenShotBean = paperScreenShotService.packScreenShotTaskBean(projectId, schoolId, classId, subjectId, "100");
        paintService.saveScreenShot(paperScreenShotBean);
    }
}