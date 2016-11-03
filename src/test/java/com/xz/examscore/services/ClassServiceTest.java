package com.xz.examscore.services;

import com.xz.examscore.XzExamScoreV2ApplicationTests;
import org.bson.Document;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.Assert.*;

/**
 * @author by fengye on 2016/11/2.
 */
public class ClassServiceTest extends XzExamScoreV2ApplicationTests {
    @Autowired
    ClassService classService;

    @Test
    public void testListClasses() throws Exception {
        String projectId = "430100-1944e9f7048b48e2b38e35db75be4980";
        String schoolId = "d00faaa0-8a9b-45c4-ae16-ea2688353cd0";
        List<Document> documents = classService.listClasses(projectId, schoolId);
        documents.forEach(document -> System.out.println(document.toString()));
    }
}