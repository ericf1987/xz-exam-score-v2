package com.xz.examscore.asynccomponents.report.biz.classes;

import com.xz.ajiaedu.common.lang.Result;
import com.xz.examscore.XzExamScoreV2ApplicationTests;
import com.xz.ajiaedu.common.ajia.Param;
import com.xz.examscore.services.QuestService;
import org.bson.Document;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;
import java.util.List;

/**
 * @author by fengye on 2017/2/15.
 */
public class ClassPointAbilityLevelBizTest extends XzExamScoreV2ApplicationTests {

    @Autowired
    ClassPointAbilityLevelBiz classPointAbilityLevelBiz;

    @Autowired
    QuestService questService;

    @Test
    public void testExecute() throws Exception {
        String projectId = "430100-dd3013ab961946fb8a3668e5ccc475b6";
        String classId = "21c44641-dabd-4e98-b2fa-eef94a9d8ffc";
        String subjectId = "007";
        Param param = new Param().setParameter("projectId", projectId)
                .setParameter("classId", classId)
                .setParameter("subjectId", subjectId);
        Result result = classPointAbilityLevelBiz.execute(param);
        System.out.println(result.getData());
    }

    @Test
    public void testFindQuest() throws Exception {
        String projectId = "430100-dd3013ab961946fb8a3668e5ccc475b6";
        String pointId = "1023816";
        String level = "C";
        String questId = "58fdc7e62d560287557b72ef";

        List<Document> quests = Collections.singletonList(questService.findQuest(projectId, questId));

        List<Document> quests1 = classPointAbilityLevelBiz.findQuests(pointId, level, quests);
        System.out.println(quests1.toString());
    }
}