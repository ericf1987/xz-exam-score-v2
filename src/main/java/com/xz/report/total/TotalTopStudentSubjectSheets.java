package com.xz.report.total;

import com.xz.ajiaedu.common.excel.ExcelWriter;
import com.xz.ajiaedu.common.lang.Result;
import com.xz.api.Param;
import com.xz.api.server.project.ProjectTopStudentQuestTypeStat;
import com.xz.report.SheetGenerator;
import com.xz.report.SheetTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author by fengye on 2016/6/19.
 */
@Component
public class TotalTopStudentSubjectSheets extends SheetGenerator {

    @Autowired
    ProjectTopStudentQuestTypeStat projectTopStudentQuestTypeStat;

    public static final String[] RANKSEGMENTS = new String[]{
            "1", "50"
    };

    @Override
    protected void generateSheet(String projectId, ExcelWriter excelWriter, SheetTask sheetTask) throws Exception {
        Param param = new Param().setParameter("projectId", projectId).setParameter("rankSegment", RANKSEGMENTS);
        Result result = projectTopStudentQuestTypeStat.execute(param);
        setupHeader(excelWriter);
        fillData(excelWriter, result.get("topStudents"));
    }

    private void setupHeader(ExcelWriter excelWriter) {
        AtomicInteger column = new AtomicInteger(-1);
        excelWriter.set(0, column.incrementAndGet(), "尖子生");
        excelWriter.set(0, column.incrementAndGet(), "所属学校");
        excelWriter.set(0, column.incrementAndGet(), "总体排名");
    }

    private void fillData(ExcelWriter excelWriter, Object topStudents) {
        //// TODO: 2016/6/23
    }
}
