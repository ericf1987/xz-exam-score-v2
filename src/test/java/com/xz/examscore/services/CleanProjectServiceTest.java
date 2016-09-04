package com.xz.examscore.services;

import com.xz.examscore.XzExamScoreV2ApplicationTests;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;
import java.util.List;

/**
 * @author by fengye on 2016/9/3.
 */
public class CleanProjectServiceTest extends XzExamScoreV2ApplicationTests {
    @Autowired
    CleanProjectService cleanProjectService;

    @Test
    public void testDoCleanData() throws Exception {
        String projectId = "430100-cb04005aa5ae460fae6b9d87df797066";
        cleanProjectService.doCleanSchedule(projectId);
    }

    @Test
    public void testGetAllCollections() throws Exception {
        List<String> list = cleanProjectService.getAllCollections();
        Collections.sort(list, (String s1, String s2) -> s1.compareTo(s2));
        System.out.println(list.toString());
    }
}