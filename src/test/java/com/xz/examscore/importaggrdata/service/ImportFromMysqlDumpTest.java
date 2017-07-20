package com.xz.examscore.importaggrdata.service;

import com.xz.ajiaedu.common.io.ZipFileReader;
import com.xz.examscore.XzExamScoreV2ApplicationTests;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.util.zip.ZipFile;

import static org.junit.Assert.*;

/**
 * @author by fengye on 2017/7/18.
 */
public class ImportFromMysqlDumpTest extends XzExamScoreV2ApplicationTests {

    @Autowired
    ImportFromMysqlDump importFromMysqlDump;

    public static final String PROJECT_ID = "430200-13e01c025ac24c6497d916551b3ae7a6";

    @Test
    public void testImportData() throws Exception {

    }

    @Test
    public void testDoImportProcess() throws Exception {
        File file = new File("F://aggregation-data-archives//3658941c-723c-491a-ad20-2a001de17885.zip");

        ZipFileReader reader = new ZipFileReader(file);

        importFromMysqlDump.doImportProcess(PROJECT_ID, reader);
    }
}