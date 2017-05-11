package com.xz.examscore.services;

import com.xz.examscore.XzExamScoreV2ApplicationTests;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

/**
 * @author by fengye on 2017/5/11.
 */
public class IdentifyStudentServiceTest extends XzExamScoreV2ApplicationTests {

    @Autowired
    IdentifyStudentService identifyStudentService;

    @Test
    public void testExportStudents() throws Exception {
        //identifyStudentService.createPack("430100-354dce3ac8ef4800a1b57f81a10b8baa", "target/students.txt");

        identifyStudentService.exportStudents("430100-354dce3ac8ef4800a1b57f81a10b8baa", false);
    }
}