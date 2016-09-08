package com.xz.examscore.services;

import com.xz.examscore.XzExamScoreV2ApplicationTests;
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
        String studyStage = projectService.findProjectStudyStage("430300-672a0ed23d9148e5a2a31c8bf1e08e62");
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
        Range schoolRange = Range.school("11b66fc2-8a76-41c2-a1b3-5011523c7e47");
        System.out.println(projectService.listProjectsByRange(schoolRange).toString());
    }
}