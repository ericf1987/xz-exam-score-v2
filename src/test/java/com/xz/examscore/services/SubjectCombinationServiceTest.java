package com.xz.examscore.services;

import com.xz.examscore.XzExamScoreV2ApplicationTests;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;

/**
 * @author by fengye on 2016/10/18.
 */
public class SubjectCombinationServiceTest extends XzExamScoreV2ApplicationTests {
    @Autowired
    SubjectCombinationService subjectCombinationService;

    @Test
    public void testGetAllSubjectCombinations() throws Exception {
        String projectId = "430200-01ef739fb0074d489f39e62a9be64629";
        System.out.println(subjectCombinationService.getAllSubjectCombinations(projectId));
    }

    @Test
    public void testGetSubjectCombinationName() throws Exception {
        String name = subjectCombinationService.getSubjectCombinationName("007008009");
        System.out.println(name);
    }

    @Test
    public void testSaveProjectSubjectCombinations() throws Exception {
        String projectId = "430100-2e63dd5efb014a0b9a0505ad36ce8f90";
        subjectCombinationService.saveProjectSubjectCombinations(projectId, Arrays.asList("004005006"));
    }
}