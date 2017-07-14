package com.xz.examscore.services;

import com.xz.examscore.XzExamScoreV2ApplicationTests;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

/**
 * @author by fengye on 2017/7/14.
 */
public class SubjectServiceTest extends XzExamScoreV2ApplicationTests {

    @Autowired
    SubjectService subjectService;

    public static final String PROJECT_ID = "431100-b1a6af6a102a4ba794e95bbd0dd9b5d9";

    public static final String STUDENT_ID = "bd890246-a34c-4fa1-ae07-c44602ae4262";

    @Test
    public void testQueryAbsentSubject() throws Exception {
        List<Map<String, String>> list = subjectService.queryAbsentSubject(PROJECT_ID, STUDENT_ID);
        System.out.println(list.toString());
    }
}