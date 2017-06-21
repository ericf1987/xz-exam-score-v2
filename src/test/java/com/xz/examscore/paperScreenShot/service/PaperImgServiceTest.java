package com.xz.examscore.paperScreenShot.service;

import com.xz.examscore.XzExamScoreV2ApplicationTests;
import com.xz.examscore.paperScreenShot.bean.PaperScreenShotBean;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * @author by fengye on 2017/5/18.
 */
public class PaperImgServiceTest extends XzExamScoreV2ApplicationTests {

    @Autowired
    PaperScreenShotService paperScreenShotService;

    @Autowired
    PaperImgService paperImgService;

    @Test
    public void testGeneratePaperZone() throws Exception {
        String projectId = "430600-d248e561aefc425b9971f2a26d267478";
        String schoolId = "5266e03d-bc8a-4c6e-b2e0-45b6bad9357f";
        String classId = "38a55f5c-090d-414d-bffd-997327207754";
        String subjectId = "003";
        PaperScreenShotBean paperScreenShotBean = paperScreenShotService.packScreenShotTaskBean(projectId, schoolId, classId, subjectId, "100");
        System.out.println(paperScreenShotBean.getStudentCardSlices().toString());
        List<Map<String, Object>> list = paperImgService.generatePaperZone(paperScreenShotBean, null);
        System.out.println(list.toString());
    }

    @Test
    public void testGetOnePage() throws Exception {

    }

    @Test
    public void testConvertImgToString() throws Exception {
        BufferedImage bufferedImage = ImageIO.read(new File("F:\\paper\\screenshot\\430300-9cef9f2059ce4a36a40a7a60b07c7e00\\dd46843a-0ea9-4d49-a664-7eb1fb869e79\\42ffec58-7d86-4979-9ae0-04e6b5f6771d\\001\\0c0f6203-cea2-4a11-8d59-05f56ac52197_positive.png"));
        String imgString = paperImgService.convertImgToString(bufferedImage, "png");
        System.out.println(imgString);
        System.out.println(imgString.length());
    }
}