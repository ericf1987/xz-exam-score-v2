package com.xz.examscore.api.server.customization;

import com.xz.ajiaedu.common.lang.Result;
import com.xz.examscore.XzExamScoreV2ApplicationTests;
import com.xz.examscore.api.Param;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * @author by fengye on 2017/4/11.
 */
public class StudentProjectRankAndScoreTest extends XzExamScoreV2ApplicationTests {

    @Autowired
    StudentProjectRankAndScore studentProjectRankAndScore;

    @Test
    public void testExecute() throws Exception {
        String projectId = "430300-c582131e66b64fe38da7d0510c399ec4";
        Param param = new Param().setParameter("projectId", projectId);
        Result result = studentProjectRankAndScore.execute(param);
        Map<String, Object> data = result.getData();
        System.out.println(data.get("students").toString());
        List students = (List) data.get("students");
        System.out.println(students.size());
    }
}