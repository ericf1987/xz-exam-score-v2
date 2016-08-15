package com.xz.services;

import com.xz.XzExamScoreV2ApplicationTests;
import org.bson.Document;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.Assert.*;

/**
 * @author by fengye on 2016/8/15.
 */
public class SchoolTagServiceTest extends XzExamScoreV2ApplicationTests {

    @Autowired
    SchoolTagService schoolTagService;

    @Test
    public void testFindSchoolIdsByTags() throws Exception {
    }
}