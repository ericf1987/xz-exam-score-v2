package com.xz.examscore.api.server.sys;

import com.xz.ajiaedu.common.lang.Result;
import com.xz.examscore.XzExamScoreV2ApplicationTests;
import com.xz.examscore.api.Param;
import com.xz.examscore.bean.ProjectStatus;
import com.xz.examscore.services.ProjectService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author by fengye on 2016/8/7.
 */
public class QueryProjectProgessTest extends XzExamScoreV2ApplicationTests {

    @Autowired
    ProjectService projectService;

    @Autowired
    QueryProjectProgess queryProjectProgess;

    @Test
    public void testExecute() throws Exception {
        String projectId = "430100-2df3f3ad199042c39c5f4b69f5dc7840";
        ProjectStatus status = projectService.getProjectStatus(projectId);
        System.out.println(status.name().equals("AggregationCompleted"));
        Param param = new Param().setParameter("projectId", projectId);
        Result result = queryProjectProgess.execute(param);
        System.out.println(result.getData());
    }
}