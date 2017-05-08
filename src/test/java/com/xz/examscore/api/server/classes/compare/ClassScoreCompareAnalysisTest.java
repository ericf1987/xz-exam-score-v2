package com.xz.examscore.api.server.classes.compare;

import com.xz.ajiaedu.common.lang.Result;
import com.xz.examscore.XzExamScoreV2ApplicationTests;
import com.xz.ajiaedu.common.ajia.Param;
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
                .setParameter("projectId", "430100-194d9c9dd59d4145ae94bb66a06434d0")
                .setParameter("subjectId", "")
                .setParameter("classId", "ab183e62-5bf5-4c5f-b9a3-17c003defaca");
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