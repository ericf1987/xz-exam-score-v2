package com.xz.examscore.services;

import com.alibaba.fastjson.JSONArray;
import com.xz.ajiaedu.common.lang.Context;
import com.xz.examscore.XzExamScoreV2ApplicationTests;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * (description)
 * created at 16/07/08
 *
 * @author yiding_he
 */
public class ImportProjectServiceTest extends XzExamScoreV2ApplicationTests {

    @Autowired
    ImportProjectService importProjectService;

    @Test
    public void testImportProjectQuest() throws Exception {
        importProjectService.importProject("430300-f529e0f0236d49559b0c27acbbb255ed", false);
    }

    @Test
    public void testImportReportConfig() throws Exception {
        Context context = new Context();
        importProjectService.importProjectReportConfig("430100-98abd83a67524a7daca4b531db0742ee", context);
        System.out.println(context.toString());
    }

    @Test
    public void testImportProjectInfo() throws Exception {
        importProjectService.importProjectInfo("430100-553137a1e78741149104526aaa84393e", new Context());
    }

    @Test
    public void testimportProject() throws Exception{
        importProjectService.importProject("430300-f529e0f0236d49559b0c27acbbb255ed", true);
    }

}