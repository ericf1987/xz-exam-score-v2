package com.xz.report.schools;

import com.xz.ajiaedu.common.excel.ExcelWriter;
import com.xz.ajiaedu.common.lang.Result;
import com.xz.api.Param;
import com.xz.api.server.school.SchoolTopStudentStat;
import com.xz.report.SheetGenerator;
import com.xz.report.SheetTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author by fengye on 2016/6/19.
 */
@Component
public class SchoolTopStudentSubjectSheets extends SheetGenerator {
    @Autowired
    SchoolTopStudentStat schoolTopStudentStat;

    public static final String[] RANKSEGMENTS = new String[]{
            "1", "50"
    };
    @Override
    protected void generateSheet(String projectId, ExcelWriter excelWriter, SheetTask sheetTask) throws Exception {
        Param param = new Param().setParameter("projectId", projectId).setParameter("rankSegment", RANKSEGMENTS);
        Result result = schoolTopStudentStat.execute(param);
        System.out.println("学校尖子生试题分析统计-->" + result.getData());
    }
}
