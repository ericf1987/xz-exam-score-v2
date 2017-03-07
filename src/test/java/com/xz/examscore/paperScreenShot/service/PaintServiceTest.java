package com.xz.examscore.paperScreenShot.service;

import com.xz.examscore.XzExamScoreV2ApplicationTests;
import com.xz.examscore.paperScreenShot.bean.PaperScreenShotBean;
import com.xz.examscore.paperScreenShot.bean.Rect;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

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

    @Test
    public void testpaintRects() throws Exception {
        String savePath = "F:/paper/test1";
        String img_positive = "http://znxunzhi-marking-pic.oss-cn-shenzhen.aliyuncs.com/430300-9cef9f2059ce4a36a40a7a60b07c7e00/21454f2c-c3c7-4dcc-8a62-eef2164c07d1/001/01/paperImage/310160034_positive.png";
        String img_reverse = "http://znxunzhi-marking-pic.oss-cn-shenzhen.aliyuncs.com/430300-9cef9f2059ce4a36a40a7a60b07c7e00/21454f2c-c3c7-4dcc-8a62-eef2164c07d1/001/01/paperImage/310160034_reverse.png";
        List<Rect> rects = new ArrayList<>();
        Rect r1 = new Rect();
        r1.setQuestNo("12");
        r1.setCoordinateX(70.0);
        r1.setCoordinateY(1127.0);
        r1.setFullScore(4.0);
        r1.setScore(2.0);
        r1.setWidth(748.0);
        r1.setHeight(212.0);
        r1.setPageIndex(0);
        r1.setPaper_positive(img_positive);
        r1.setPaper_reverse(img_reverse);

        Rect r2 = new Rect();
        r2.setQuestNo("13");
        r2.setCoordinateX(1111167.0);
        r2.setCoordinateY(111111388.0);
        r2.setFullScore(4.0);
        r2.setScore(3.0);
        r2.setHeight(269.0);
        r2.setWidth(753.0);
        r2.setPageIndex(0);
        r2.setPaper_positive(img_positive);
        r2.setPaper_reverse(img_reverse);

        rects.add(r1);
        rects.add(r2);

        paintService.paintRects(rects, savePath, img_positive, img_reverse);
    }
}