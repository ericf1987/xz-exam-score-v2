package com.xz.examscore.services;

import com.xz.examscore.XzExamScoreV2ApplicationTests;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * (description)
 * created at 16/06/12
 *
 * @author yiding_he
 */
public class PrepareDataServiceTest extends XzExamScoreV2ApplicationTests {

    @Autowired
    PrepareDataService prepareDataService;

    @Test
    public void testPrepare() throws Exception {

    }

    @Test
    public void testPrepareStudentList() throws Exception {
        String project = "433100-fef19389d6ce4b1f99847ab96d2cfeba";
        prepareDataService.prepareStudentList(project);
    }

    @Test
    public void testPrepareFixQuestOptions() throws Exception {
        prepareDataService.prepareFixQuestOptions(XT_PROJECT_ID);
    }
}