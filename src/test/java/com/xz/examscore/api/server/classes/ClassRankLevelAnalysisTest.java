package com.xz.examscore.api.server.classes;

import com.xz.ajiaedu.common.lang.Result;
import com.xz.examscore.XzExamScoreV2ApplicationTests;
import com.xz.ajiaedu.common.ajia.Param;
import com.xz.examscore.services.StudentService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author by fengye on 2016/7/21.
 */
public class ClassRankLevelAnalysisTest extends XzExamScoreV2ApplicationTests{
    @Autowired
    ClassRankLevelAnalysis classRankLevelAnalysis;

    @Autowired
    StudentService studentService;

    @Test
    public void testExecute() throws Exception {
        Param param = new Param()
                .setParameter("projectId", "430100-e7bd093d92d844819c7eda8b641ab6ee")
                .setParameter("classId", "048eb56f-a105-4992-8228-0e436c9e4670");
        Result result = classRankLevelAnalysis.execute(param);
        System.out.println(result.getData());
/*        List<Document> studentDoc = studentService.getStudentList("430100-e7bd093d92d844819c7eda8b641ab6ee", Range.clazz("048eb56f-a105-4992-8228-0e436c9e4670"));
        System.out.println(studentDoc.toString());*/
    }
}