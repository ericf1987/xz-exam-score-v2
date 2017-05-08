package com.xz.examscore.api.server.school.compare;

import com.xz.examscore.XzExamScoreV2ApplicationTests;
import com.xz.ajiaedu.common.ajia.Param;
import com.xz.examscore.services.ProjectService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author by fengye on 2016/7/22.
 */
public class SchoolAverageCompareAnalysisTest extends XzExamScoreV2ApplicationTests{

    @Autowired
    SchoolAverageCompareAnalysis schoolAverageCompareAnalysis;

    @Autowired
    ProjectService projectService;

    @Test
    public void testExecute() throws Exception {
/*
        List<Document> projectList = projectService.listProjectsByRange(Range.school("200f3928-a8bd-48c4-a2f4-322e9ffe3700"));
        projectList = projectList.stream().filter(projectDoc -> null != projectDoc && !projectDoc.isEmpty()).collect(Collectors.toList());

        System.out.println(projectList.toString());*/


        Param param = new Param()
                .setParameter("projectId", "430100-e7bd093d92d844819c7eda8b641ab6ee")
                .setParameter("schoolId", "d00faaa0-8a9b-45c4-ae16-ea2688353cd0");
        System.out.println(schoolAverageCompareAnalysis.execute(param).getData());
    }
}