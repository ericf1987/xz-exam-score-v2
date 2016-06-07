package com.xz.report.schools;

import com.xz.ajiaedu.common.excel.ExcelWriter;
import com.xz.ajiaedu.common.lang.Result;
import com.xz.api.Param;
import com.xz.api.server.school.SchoolScoreSegment;
import com.xz.bean.Range;
import com.xz.bean.Target;
import com.xz.report.SheetGenerator;
import com.xz.report.SheetTask;
import com.xz.services.SchoolService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author by fengye on 2016/6/7.
 */
@Component
public class SchoolBasicScoreSegmentSheet extends SheetGenerator {
    @Autowired
    SchoolScoreSegment schoolScoreSegment;

    @Autowired
    SchoolService schoolService;

    @Override
    protected void generateSheet(String projectId, ExcelWriter excelWriter, SheetTask sheetTask) throws Exception {
        Target target = sheetTask.get("target");
        String subjectId = target.match(Target.PROJECT) ? null : target.getId().toString();
        Range schoolRange = sheetTask.getRange();
        Param param = new Param().setParameter("projectId", projectId).
                setParameter("subjectId", subjectId).
                setParameter("schoolIds", schoolRange.getId());

        Result result = schoolScoreSegment.execute(param);

        //System.out.println("学校分数段统计-->" + result.getData());
        setupHeader(excelWriter, result.get("schools"));
        //fillSchoolData(result.get("schools"),excelWriter);
        fillClassData(result.getList("classes", null),excelWriter);
    }

    private void setupHeader(ExcelWriter excelWriter, List<Map<String, Object>> schools) {
        AtomicInteger column = new AtomicInteger(-1);
        excelWriter.set(0, column.incrementAndGet(), "班级名称");
        for (Map<String, Object> school : schools){
            excelWriter.set(0, column.incrementAndGet(), school.get("title"));
        }
    }

/*    private void fillSchoolData(List<Map<String, Object>> schools, ExcelWriter excelWriter) {
    }*/

    private void fillClassData(List<Map<String, Object>> classes, ExcelWriter excelWriter) {
        int row = 2;
        for(Map<String, Object> clazz : classes){
            fillRows(clazz, excelWriter, row);
            row++;
        }
    }

    private void fillRows(Map<String, Object> clazz, ExcelWriter excelWriter, int row) {
        AtomicInteger column = new AtomicInteger(-1);
        excelWriter.set(row, column.incrementAndGet(), clazz.get("className"));
        for(Map<String, Object> scoreSegment : (List<Map<String, Object>>)clazz.get("scoreSegments")) {
            excelWriter.set(row, column.incrementAndGet(), scoreSegment.get("countRate"));
        }
    }


}
