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
    public void testPrepareQuestTypeList() throws Exception {
        prepareDataService.prepareQuestTypeList(PROJECT_ID);
    }

    @Test
    public void testPrepareStudentList() throws Exception {
        String project = "430500-6539f2f49f74411a8a1beb232a0cedf1";
        prepareDataService.prepareStudentList(project);
    }

    @Test
    public void testPrepareFixQuestOptions() throws Exception {
        prepareDataService.prepareFixQuestOptions(PROJECT_ID);
    }
}