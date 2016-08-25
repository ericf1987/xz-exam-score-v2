package com.xz.examscore.services;

import com.xz.examscore.XzExamScoreV2ApplicationTests;
import org.bson.Document;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

/**
 * ${描述}
 *
 * @author zhaorenwu
 */
public class AbilityLevelServiceTest extends XzExamScoreV2ApplicationTests {

    @Autowired
    AbilityLevelService abilityLevelService;

    @Test
    public void testQueryAbilityLevels() throws Exception {
        Map<String, Document> abilityLevels = abilityLevelService.queryAbilityLevels("3", "001");
        System.out.println(abilityLevels);
    }

    @Test
    public void testQueryAbilityLevelName() throws Exception {
        String levelName = abilityLevelService.queryAbilityLevelName("3", "001", "A");
        System.out.println(levelName);
    }
}