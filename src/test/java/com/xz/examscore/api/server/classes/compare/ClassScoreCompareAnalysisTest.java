package com.xz.examscore.api.server.classes.compare;

import com.xz.ajiaedu.common.lang.Result;
import com.xz.examscore.XzExamScoreV2ApplicationTests;
import com.xz.examscore.api.Param;
import com.xz.examscore.bean.Range;
import com.xz.examscore.services.ProjectService;
import org.bson.Document;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @author by fengye on 2016/7/26.
 */
public class ClassScoreCompareAnalysisTest extends XzExamScoreV2ApplicationTests {

    @Autowired
    ClassScoreCompareAnalysis classScoreCompareAnalysis;

    @Autowired
    ProjectService projectService;

    @Test
    public void testExecute() throws Exception {
        Param param = new Param()
                .setParameter("projectId", "433100-fef19389d6ce4b1f99847ab96d2cfeba")
                .setParameter("subjectId", "001")
                .setParameter("classId", "f8259b31-7c8b-47ba-90d5-c5c15763660f");
        Result result = classScoreCompareAnalysis.execute(param);
        System.out.println(result.getData());
    }

    @Test
    public void test1() throws Exception {
        Document doc = projectService.findProject("");
        List<Document> list = projectService.listProjectsByRange(Range.clazz("f8259b31-7c8b-47ba-90d5-c5c15763660f"), doc.getString("category"));
        System.out.println(list.toString());
    }
}