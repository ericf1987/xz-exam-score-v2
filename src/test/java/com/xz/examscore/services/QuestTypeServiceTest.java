package com.xz.examscore.services;

import com.xz.ajiaedu.common.beans.dic.QuestType;
import com.xz.examscore.XzExamScoreV2ApplicationTests;
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
        List<QuestType> questTypeList = questTypeService.getQuestTypeList(
                "430300-672a0ed23d9148e5a2a31c8bf1e08e62", "001");
        questTypeList.forEach(System.out::println);
    }
}