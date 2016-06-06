package com.xz.report.schools;

import com.xz.ajiaedu.common.excel.ExcelWriter;
import com.xz.api.server.project.ProjectScoreAnalysis;
import com.xz.bean.Target;
import com.xz.report.SheetGenerator;
import com.xz.report.SheetTask;
import com.xz.services.ClassService;
import com.xz.services.SchoolService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @author by fengye on 2016/6/6.
 */
public class SchoolBasicScoreSheet extends SheetGenerator {
    @Autowired
    ProjectScoreAnalysis projectScoreAnalysis;

    @Autowired
    ClassService classService;

    @Autowired
    SchoolService schoolService;

    @Override
    protected void generateSheet(String projectId, ExcelWriter excelWriter, SheetTask sheetTask) throws Exception {
    }
}
