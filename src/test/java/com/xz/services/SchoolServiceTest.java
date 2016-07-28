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
        List<Document> doc = schoolService.getSchoolsByTags("430500-6539f2f49f74411a8a1beb232a0cedf1", "false", "true");
        System.out.println(doc.toString());
    }
}