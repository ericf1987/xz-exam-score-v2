package com.xz.services;

import com.xz.XzExamScoreV2ApplicationTests;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * (description)
 * created at 16/06/11
 *
 * @author yiding_he
 */
public class QuestTypeServiceTest extends XzExamScoreV2ApplicationTests {

    @Autowired
    QuestTypeService questTypeService;

    @Test
    public void testGetQuestTypeList() throws Exception {
        List<String> questTypeList = questTypeService.getQuestTypeList(PROJECT_ID);
        System.out.println(questTypeList);
    }
}