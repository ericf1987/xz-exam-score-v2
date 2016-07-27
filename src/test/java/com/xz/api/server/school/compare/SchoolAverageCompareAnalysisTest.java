package com.xz.api.server.school.compare;

import com.xz.XzExamScoreV2ApplicationTests;
import com.xz.api.Param;
import com.xz.bean.Range;
import com.xz.services.ProjectService;
import org.bson.Document;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.stream.Collectors;

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
                .setParameter("projectId", "430200-b73f03af1d74484f84f1aa93f583caaa")
                .setParameter("schoolId", "200f3928-a8bd-48c4-a2f4-322e9ffe3700")
                .setParameter("subjectId", "001");
        System.out.println(schoolAverageCompareAnalysis.execute(param).getData());
    }
}