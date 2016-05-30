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
                "430200-89c9dc7481cd47a69d85af3f0808e0c4",
                Range.clazz("e6e04686-f06e-42a8-b504-d55ed5ddd898"), Target.subject("003"));

        for (Document document : documents) {
            System.out.println(document.toJson());
        }
    }
}