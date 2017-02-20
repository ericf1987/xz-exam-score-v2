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
        String projectId = "430600-db9cff031e12437aab42de6fdbccbac6";
        String classId = "4c8a3944-1b51-445f-8948-061747f13d74";
        String subjectId = "001";
        Param param = new Param().setParameter("projectId", projectId)
                .setParameter("classId", classId)
                .setParameter("subjectId", subjectId);
        Result result = classPointAbilityLevelBiz.execute(param);
        System.out.println(result.getData());
    }
}