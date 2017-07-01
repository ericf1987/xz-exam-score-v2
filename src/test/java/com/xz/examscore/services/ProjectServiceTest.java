package com.xz.examscore.services;

import com.xz.examscore.XzExamScoreV2ApplicationTests;
import com.xz.examscore.bean.AggregationStatus;
import com.xz.examscore.bean.ProjectStatus;
import com.xz.examscore.bean.Range;
import org.bson.Document;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

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
        List<Document> documents = projectService.querySchoolProjects(null, null, "5266e03d-bc8a-4c6e-b2e0-45b6bad9357f", null);
        for (Document document : documents) {
            System.out.println(document);
        }
    }

    @Test
    public void testFindProjectStudyStage() throws Exception {
        String studyStage = projectService.findProjectStudyStage("430200-13e01c025ac24c6497d916551b3ae7a6");
        System.out.println(studyStage);
    }

    @Test
    public void testListProjects() throws Exception {
        List<String> projectIds = projectService.listProjectIds();
        projectIds.forEach(System.out::println);
    }

    @Test
    public void testSetProjectStatus() throws Exception {
        List<String> projectIds = projectService.listProjectIds();
        projectIds.forEach(projectId ->
                projectService.setProjectStatus(projectId, ProjectStatus.AggregationCompleted));
    }

    @Test
    public void testFindProject() throws Exception {
        Document doc = projectService.findProject("430200-b73f03af1d74484f84f1aa93f583caaa");
        System.out.println(doc.toString());
    }

    @Test
    public void testListProjectsByRange(){
        Document doc = projectService.findProject("");
        Range schoolRange = Range.school("11b66fc2-8a76-41c2-a1b3-5011523c7e47");
        System.out.println(projectService.listProjectsByRange(schoolRange, doc.getString("category")).toString());
    }

    @Test
    public void testsetAggregationStatus(){
        projectService.setAggregationStatus("433100-148ec5544f7b4764851c3a8976945a2f", AggregationStatus.Terminated);
    }
}