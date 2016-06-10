package com.xz.report.classes;

import com.xz.ajiaedu.common.excel.ExcelWriter;
import com.xz.api.server.classes.ClassRankAnalysis;
import com.xz.report.SheetGenerator;
import com.xz.report.SheetTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author by fengye on 2016/6/8.
 */
@Component
public class ClassBasicScoreSheet extends SheetGenerator {

    @Override
    protected void generateSheet(String projectId, ExcelWriter excelWriter, SheetTask sheetTask) throws Exception {

    }
}
