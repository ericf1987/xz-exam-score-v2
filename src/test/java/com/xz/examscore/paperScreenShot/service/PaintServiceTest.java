package com.xz.examscore.paperScreenShot.service;

import com.xz.examscore.XzExamScoreV2ApplicationTests;
import com.xz.examscore.paperScreenShot.bean.PaperScreenShotBean;
import com.xz.examscore.paperScreenShot.bean.SubjectiveQuestZone;
import com.xz.examscore.paperScreenShot.utils.PaintUtils;
import com.xz.examscore.services.SubjectService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;

/**
 * @author by fengye on 2017/3/1.
 */
public class PaintServiceTest extends XzExamScoreV2ApplicationTests {

    @Autowired
    PaintService paintService;
    
    @Autowired
    SubjectService subjectService;

    @Autowired
    PaperScreenShotService paperScreenShotService;

    @Test
    public void testSaveScreenShot() throws Exception {
        /*String projectId = "430300-9cef9f2059ce4a36a40a7a60b07c7e00";
        String schoolId = "dd46843a-0ea9-4d49-a664-7eb1fb869e79";
        String classId = "42ffec58-7d86-4979-9ae0-04e6b5f6771d";
        String subjectId = "006";*/

        Map<String, Object> map = new HashMap<>();
        Map<String, Object> rankInClass = new HashMap<>();
        rankInClass.put("subjective", Arrays.asList("7", "11", "12", "20"));
        rankInClass.put("objective", true);
        rankInClass.put("total", true);

        Map<String, Object> rankInSchool = new HashMap<>();
        rankInSchool.put("subjective", Arrays.asList("7", "13", "15", "20"));
        rankInSchool.put("objective", false);
        rankInSchool.put("total", false);

        Map<String, Object> rankInProvince = new HashMap<>();
        rankInProvince.put("subjective", Arrays.asList("16", "17", "19", "20"));
        rankInProvince.put("objective", true);
        rankInProvince.put("total", true);

        Map<String, Object> subjectMap = new HashMap<>();
        subjectMap.put("rankInClass", rankInClass);
        subjectMap.put("rankInSchool", rankInSchool);
        subjectMap.put("rankInProvince", rankInProvince);

        String projectId = "430900-8f11fe8dbac842a3805d45e05eb31095";
        String schoolId = "d0d78c80-b6ae-4908-80dd-fd78efc01479";
        String classId = "a1ee55a0-a62d-4337-b50e-2e43e0423597";
//        String subjectId = "001";
        List<String> subjects = subjectService.querySubjects(projectId);
        for(String subjectId : subjects){
            map.put(subjectId, subjectMap);
            PaperScreenShotBean paperScreenShotBean = paperScreenShotService.packScreenShotTaskBean(projectId, schoolId, classId, subjectId, "100");
            paintService.saveScreenShot(paperScreenShotBean, map);
        }
    }

    @Test
    public void testpaintRects() throws Exception {
        String savePath = "F:/paper/test4";
        String img_positive = "http://znxunzhi-marking-pic.oss-cn-shenzhen.aliyuncs.com/430300-9cef9f2059ce4a36a40a7a60b07c7e00/21454f2c-c3c7-4dcc-8a62-eef2164c07d1/001/01/paperImage/310160034_positive.png";
        String img_reverse = "http://znxunzhi-marking-pic.oss-cn-shenzhen.aliyuncs.com/430300-9cef9f2059ce4a36a40a7a60b07c7e00/21454f2c-c3c7-4dcc-8a62-eef2164c07d1/001/01/paperImage/310160034_reverse.png";
        List<SubjectiveQuestZone> subjectiveQuestZones = new ArrayList<>();
        SubjectiveQuestZone r1 = new SubjectiveQuestZone();
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

        SubjectiveQuestZone r2 = new SubjectiveQuestZone();
        r2.setQuestNo("13");
        r2.setCoordinateX(167.0);
        r2.setCoordinateY(388.0);
        r2.setFullScore(4.0);
        r2.setScore(3.0);
        r2.setHeight(269.0);
        r2.setWidth(753.0);
        r2.setPageIndex(0);
        r2.setPaper_positive(img_positive);
        r2.setPaper_reverse(img_reverse);

        subjectiveQuestZones.add(r1);
        subjectiveQuestZones.add(r2);

        paintService.paintPaper(null, null, subjectiveQuestZones, savePath, img_positive, img_reverse);
    }

    @Test
    public void testPaint() throws Exception {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        String[] availableFontFamilyNames = ge.getAvailableFontFamilyNames();
        for (String name : availableFontFamilyNames) {
            System.out.println("可用字体有：" + name);
        }
    }

    @Test
    public void test1() throws Exception {
        String ss = "试卷题号";
        System.out.println(new String(ss.getBytes(), "UTF-8"));
    }

    @Test
    public void test2() throws Exception {
        BufferedImage bufferedImage = PaintUtils.loadImageUrl("http://znxunzhi-marking-pic.oss-cn-shenzhen.aliyuncs.com/430300-c582131e66b64fe38da7d0510c399ec4/c99a630b-d8e6-4758-b27d-4b062f9fec0a/002/18/paperImage/469140955_reverse.png");
        System.out.println(bufferedImage);
    }
}