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
 * created at 16/06/13
 *
 * @author yiding_he
 */
public class TopStudentListServiceTest extends XzExamScoreV2ApplicationTests {

    @Autowired
    TopStudentListService topStudentListService;

    @Test
    public void testGetTopStudentList() throws Exception {
        List<Document> list = topStudentListService.getTopStudentList(
                PROJECT_ID,
                Range.school("002e02d6-c036-4780-85d4-e54e3f1fbf9f"),
                Target.project(PROJECT_ID), 1, 10);

        list.forEach(System.out::println);
    }
}