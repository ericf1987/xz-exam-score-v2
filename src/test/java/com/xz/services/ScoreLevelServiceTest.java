package com.xz.services;

import com.xz.XzExamScoreV2ApplicationTests;
import com.xz.bean.Range;
import com.xz.bean.Target;
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
                PROJECT_ID,
                Range.province("430000"), Target.project(PROJECT_ID));

        for (Document document : documents) {
            System.out.println(document.toJson());
        }
    }
}