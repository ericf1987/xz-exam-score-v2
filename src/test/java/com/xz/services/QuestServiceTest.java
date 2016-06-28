package com.xz.services;

import com.xz.XzExamScoreV2ApplicationTests;
import org.bson.Document;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * (description)
 * created at 16/05/11
 *
 * @author yiding_he
 */
public class QuestServiceTest extends XzExamScoreV2ApplicationTests {

    @Autowired
    QuestService questService;

    @Test
    public void testFindQuest() throws Exception {

    }

    @Test
    public void testGetQuests() throws Exception {
        String project = "430100-8d805ef37b2f4bc7ad9808a9a109dc22";
        List<Document> quests = questService.getQuests(project);
        System.out.println(quests.size());
        System.out.println(quests.get(0).toJson());
    }

    @Test
    public void testGetQuestsByPointLevel() throws Exception {
        List<Document> quests = questService.getQuests(UNION_PROJECT_ID, "1022505", "C");
        quests.forEach(System.out::println);
    }
}