package com.xz.services;

import com.xz.XzExamScoreV2ApplicationTests;
import org.bson.Document;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.Assert.*;

/**
 * @author by fengye on 2016/7/28.
 */
public class SchoolServiceTest extends XzExamScoreV2ApplicationTests {

    @Autowired
    SchoolService schoolService;

    @Test
    public void testGetSchoolsByTags() throws Exception {
        List<Document> doc = schoolService.getSchoolsByTags("430300-672a0ed23d9148e5a2a31c8bf1e08e62", "false", "true");
        System.out.println(doc.toString());
        //schoolService.paddingTags("430300-672a0ed23d9148e5a2a31c8bf1e08e62");
    }
}