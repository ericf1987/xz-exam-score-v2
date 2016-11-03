package com.xz.examscore.services;

import com.xz.examscore.XzExamScoreV2ApplicationTests;
import com.xz.examscore.bean.Range;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

/**
 * @author by fengye on 2016/10/28.
 */
public class CollegeEntryLevelServiceTest extends XzExamScoreV2ApplicationTests {

    @Autowired
    CollegeEntryLevelService collegeEntryLevelService;

    @Test
    public void testGetEntryLevelStudent() throws Exception {

    }

    @Test
    public void testGetEntryLevelStudentRankSegment() throws Exception {

    }

    @Test
    public void testGetEntryLevel() throws Exception {

    }

    @Test
    public void testGetEntryLevelKey() throws Exception {

    }

    @Test
    public void testGetEntryLevelStudentCount() throws Exception {
        String projectId = "430300-672a0ed23d9148e5a2a31c8bf1e08e62";
        Range schoolRange = Range.school("9ea1472e-9f8e-4b48-b00c-8bde3288cc80");
        System.out.println(collegeEntryLevelService.getEntryLevelStudentRankSegment(projectId, schoolRange).toString());
    }

    @Test
    public void testGetEntryLevelStudentByKey() throws Exception {

    }

    @Test
    public void testGetEntryKeyDesc() throws Exception {

    }
}