package com.xz.services;

import com.xz.XzExamScoreV2ApplicationTests;
import com.xz.ajiaedu.common.beans.dic.QuestType;
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
        List<QuestType> questTypeList = questTypeService.getQuestTypeList(PROJECT_ID);
        questTypeList.forEach(System.out::println);
    }
}