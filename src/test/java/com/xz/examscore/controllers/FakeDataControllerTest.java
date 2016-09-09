package com.xz.examscore.controllers;

import com.xz.examscore.XzExamScoreV2ApplicationTests;
import org.bson.Document;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * (description)
 * created at 16/09/08
 *
 * @author yiding_he
 */
public class FakeDataControllerTest extends XzExamScoreV2ApplicationTests {

    @Autowired
    FakeDataController controller;

    @Test
    public void queryPoints() throws Exception {
        List<Document> points = controller.queryPoints();
    }

}