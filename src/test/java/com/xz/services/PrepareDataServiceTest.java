package com.xz.services;

import com.xz.XzExamScoreV2ApplicationTests;
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
        String project = "430100-e7bd093d92d844819c7eda8b641ab6ee";
        prepareDataService.prepareStudentList(project);
    }

    @Test
    public void testPrepareFixQuestOptions() throws Exception {
        prepareDataService.prepareFixQuestOptions(XT_PROJECT_ID);
    }
}