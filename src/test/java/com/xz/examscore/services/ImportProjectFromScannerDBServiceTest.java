package com.xz.examscore.services;

import com.xz.ajiaedu.common.lang.Context;
import com.xz.examscore.XzExamScoreV2ApplicationTests;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

/**
 * @author by fengye on 2017/5/12.
 */
public class ImportProjectFromScannerDBServiceTest extends XzExamScoreV2ApplicationTests {

    @Autowired
    ImportProjectFromScannerDBService importProjectFromScannerDBService;

    public static final String PROJECT_ID = "430100-c47e179bebac47a491d51a1e355c10fd";

    @Test
    public void testGetCollection() throws Exception {
        Context context = new Context();
        importProjectFromScannerDBService.importSchools(PROJECT_ID, context);
    }

    @Test
    public void testImportSchools() throws Exception {

    }

    @Test
    public void testImportClasses() throws Exception {

    }

    @Test
    public void testImportStudents() throws Exception {

    }
}