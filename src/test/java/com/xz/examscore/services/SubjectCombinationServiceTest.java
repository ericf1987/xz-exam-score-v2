package com.xz.examscore.services;

import com.xz.examscore.XzExamScoreV2ApplicationTests;
import com.xz.examscore.bean.SubjectCombination;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;

import static org.junit.Assert.*;

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
        String projectId = "430100-2c641a3e36ff492aa535da7fb4cf28cf";
        subjectCombinationService.saveProjectSubjectCombinations(projectId, Arrays.asList("007008009"));
    }
}