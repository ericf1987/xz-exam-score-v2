package com.xz.examscore.asynccomponents.report.schools;

import com.xz.ajiaedu.common.excel.ExcelWriter;
import com.xz.ajiaedu.common.lang.Result;
import com.xz.examscore.api.Param;
import com.xz.examscore.api.server.school.SchoolAbilityLevelAnalysis;
import com.xz.examscore.asynccomponents.report.SheetGenerator;
import com.xz.examscore.asynccomponents.report.SheetTask;
import com.xz.examscore.bean.Range;
import com.xz.examscore.bean.Target;
import com.xz.examscore.services.SchoolService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author by fengye on 2017/3/20.
 */
@Component
public class SchoolAbilityLevelSheets extends SheetGenerator {

    @Autowired
    SchoolAbilityLevelAnalysis schoolAbilityLevelAnalysis;

    @Autowired
    SchoolService schoolService;

    @Override
    protected void generateSheet(String projectId, ExcelWriter excelWriter, SheetTask sheetTask) throws Exception {
        Target target = sheetTask.get("target");
        String subjectId = target.getId().toString();
        Range schoolRange = sheetTask.getRange();
        Param param = new Param().setParameter("projectId", projectId)
                .setParameter("subjectId", subjectId)
                .setParameter("schoolId", schoolRange.getId());
        Result result = schoolAbilityLevelAnalysis.execute(param);
        setHeader(excelWriter, result);
        fillSchoolData(excelWriter, result);
        fillClassData(excelWriter, result);
    }

    private void setHeader(ExcelWriter excelWriter, Result result) {
        AtomicInteger column = new AtomicInteger(-1);
        List<Map<String, Object>> schools = result.get("schools");
        excelWriter.set(0, column.incrementAndGet(), "班级");
        for(Map<String, Object> pointstat : schools){
            excelWriter.set(0, column.incrementAndGet(), pointstat.get("levelName"));
        }
    }

    private void fillSchoolData(ExcelWriter excelWriter, Result result) {
        AtomicInteger column = new AtomicInteger(-1);
        List<Map<String, Object>> classes = result.get("schools");
        excelWriter.set(1, column.incrementAndGet(), "本校");
        for(Map<String, Object> pointstat : classes){
            excelWriter.set(1, column.incrementAndGet(), pointstat.get("score"));
        }
    }

    private void fillClassData(ExcelWriter excelWriter, Result result) {
        int row = 2;
        AtomicInteger column = new AtomicInteger(-1);
        List<Map<String, Object>> classes = result.get("classes");
        for(Map<String, Object> clazz : classes){
            List<Map<String, Object>> pointStats = (List<Map<String, Object>>)clazz.get("levelStats");
            excelWriter.set(row, column.incrementAndGet(), clazz.get("className"));
            for(Map<String, Object> pointStat : pointStats) {
                excelWriter.set(row, column.incrementAndGet(), pointStat.get("score"));
            }
            row++;
            column.set(-1);
        }
    }
}
