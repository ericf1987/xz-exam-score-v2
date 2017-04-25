package com.xz.examscore.asynccomponents.report.biz.classes;

import com.xz.ajiaedu.common.lang.Result;
import com.xz.examscore.XzExamScoreV2ApplicationTests;
import com.xz.examscore.api.Param;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

/**
 * @author by fengye on 2017/2/15.
 */
public class ClassPointAbilityLevelBizTest extends XzExamScoreV2ApplicationTests {

    @Autowired
    ClassPointAbilityLevelBiz classPointAbilityLevelBiz;

    @Test
    public void testExecute() throws Exception {
        String projectId = "430100-dd3013ab961946fb8a3668e5ccc475b6";
        String classId = "21c44641-dabd-4e98-b2fa-eef94a9d8ffc";
        String subjectId = "008";
        Param param = new Param().setParameter("projectId", projectId)
                .setParameter("classId", classId)
                .setParameter("subjectId", subjectId);
        Result result = classPointAbilityLevelBiz.execute(param);
        System.out.println(result.getData());
    }
}