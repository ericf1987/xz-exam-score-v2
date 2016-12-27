package com.xz.examscore.services;

import com.xz.examscore.XzExamScoreV2ApplicationTests;
import com.xz.examscore.bean.Range;
import com.xz.examscore.bean.Target;
import org.bson.Document;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author by fengye on 2016/10/28.
 */
public class CollegeEntryLevelServiceTest extends XzExamScoreV2ApplicationTests {

    @Autowired
    CollegeEntryLevelService collegeEntryLevelService;

    @Autowired
    StudentService studentService;

    @Autowired
    ProvinceService provinceService;

    @Test
    public void testGetEntryLevelStudentCount() throws Exception {
        String projectId = "430300-672a0ed23d9148e5a2a31c8bf1e08e62";
        Range schoolRange = Range.school("9ea1472e-9f8e-4b48-b00c-8bde3288cc80");
        System.out.println(collegeEntryLevelService.getEntryLevelStudentRankSegment(projectId, schoolRange).toString());
    }

    @Test
    public void testGetEntryLevelScoreLine() throws Exception {
        String projectId = "430300-672a0ed23d9148e5a2a31c8bf1e08e62";
        Range range = Range.province(provinceService.getProjectProvince(projectId));
        Target target = Target.project(projectId);
        int studentCount = studentService.getStudentCount(projectId,
                range, target);
        System.out.println(collegeEntryLevelService.getEntryLevelScoreLine(projectId, range, target, studentCount));
    }

    @Test
    public void testGetEntryLevelDoc() throws Exception {
        String projectId = "430500-ea90a33d908c40aba5907bd97b838d61";
        List<Document> entryLevelDoc = collegeEntryLevelService.getEntryLevelDoc(projectId);
        entryLevelDoc.forEach(doc -> System.out.println(doc.toString()));
        Collections.sort(entryLevelDoc, (Document doc1, Document doc2) -> {
            Double s1 = doc1.getDouble("score");
            Double s2 = doc2.getDouble("score");
            return s2.compareTo(s1);
        });
        entryLevelDoc.forEach(doc -> System.out.println(doc.toString()));
    }

    @Test
    public void testGetEntryLevelStudent() throws Exception {
        String projectId = "430200-3e67c524f149491597279ef6ae31baef";
        Range provinceRange = Range.province(provinceService.getProjectProvince(projectId));
        Target target = Target.project(projectId);
        double entryLevelTotalScore = collegeEntryLevelService.getEntryLevelTotalScore(projectId, provinceRange, target, "");
        System.out.println(entryLevelTotalScore);
    }

    @Test
    public void testGetEntryLevelStudentRankSegment() throws Exception {

    }

    @Test
    public void testGetEntryLevelTotalScore() throws Exception {

    }

    @Test
    public void testGetEntryLevelKey() throws Exception {

    }

    @Test
    public void testGetEntryLevelStudentByKey() throws Exception {
        String projectId = "430500-ea90a33d908c40aba5907bd97b838d61";
        String province = provinceService.getProjectProvince(projectId);
        Range provinceRange = Range.province(province);
        Target target = Target.project(projectId);
        int size = collegeEntryLevelService.getEntryLevelStudentByKey(projectId, provinceRange, target, "ONE").size();
        int count = collegeEntryLevelService.getEntryLevelStudentCount(projectId, provinceRange, target, "ONE");
        System.out.println(size + ":" + count);
    }

    @Test
    public void testGetEntryKeyDesc() throws Exception {

    }
}