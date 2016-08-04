package com.xz.api.server.classes;

import com.xz.XzExamScoreV2ApplicationTests;
import com.xz.api.Param;
import com.xz.bean.Range;
import com.xz.services.StudentService;
import org.bson.Document;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

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
        classRankLevelAnalysis.execute(param);
/*        List<Document> studentDoc = studentService.getStudentList("430100-e7bd093d92d844819c7eda8b641ab6ee", Range.clazz("048eb56f-a105-4992-8228-0e436c9e4670"));
        System.out.println(studentDoc.toString());*/
    }
}