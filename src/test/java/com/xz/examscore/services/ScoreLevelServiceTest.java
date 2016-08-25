package com.xz.examscore.services;

import com.xz.examscore.XzExamScoreV2ApplicationTests;
import com.xz.examscore.bean.Range;
import com.xz.examscore.bean.Target;
import org.bson.Document;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * (description)
 * created at 16/05/30
 *
 * @author yiding_he
 */
public class ScoreLevelServiceTest extends XzExamScoreV2ApplicationTests {

    @Autowired
    ScoreLevelService scoreLevelService;

    @Test
    public void testGetScoreLevelRate() throws Exception {
        List<Document> documents = scoreLevelService.getScoreLevelRate(
                XT_PROJECT_ID,
                Range.province("430000"), Target.project(XT_PROJECT_ID));

        for (Document document : documents) {
            System.out.println(document.toJson());
        }
    }
}