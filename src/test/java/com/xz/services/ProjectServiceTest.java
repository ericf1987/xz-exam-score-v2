package com.xz.services;

import com.xz.XzExamScoreV2ApplicationTests;
import org.bson.Document;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.Assert.*;

/**
 * ${描述}
 *
 * @author zhaorenwu
 */
public class ProjectServiceTest extends XzExamScoreV2ApplicationTests {

    @Autowired
    ProjectService projectService;

    @Test
    public void testQuerySchoolProjects() throws Exception {
        List<Document> documents = projectService.querySchoolProjects("11b66fc2-8a76-41c2-a1b3-5011523c7e47", "2016-06");
        for (Document document : documents) {
            System.out.println(document);
        }
    }
}