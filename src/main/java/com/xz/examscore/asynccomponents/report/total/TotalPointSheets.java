package com.xz.examscore.asynccomponents.report.total;

import com.xz.ajiaedu.common.excel.ExcelWriter;
import com.xz.ajiaedu.common.lang.Result;
import com.xz.ajiaedu.common.ajia.Param;
import com.xz.examscore.api.server.project.ProjectPointAnalysis;
import com.xz.examscore.asynccomponents.report.SheetGenerator;
import com.xz.examscore.asynccomponents.report.SheetTask;
import com.xz.examscore.bean.Target;
import com.xz.examscore.services.SchoolService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * @author by fengye on 2017/3/20.
 */
@Component
public class TotalPointSheets extends SheetGenerator {

    @Autowired
    SchoolService schoolService;

    @Autowired
    ProjectPointAnalysis projectPointAnalysis;

    @Override
    protected void generateSheet(String projectId, ExcelWriter excelWriter, SheetTask sheetTask) throws Exception {
        Target target = sheetTask.get("target");
        String subjectId = target.getId().toString();
        List<String> schoolIds = schoolService.getProjectSchools(projectId).stream().map(s -> s.getString("school")).collect(Collectors.toList());
        Param param = new Param().setParameter("projectId", projectId)
                .setParameter("subjectId", subjectId)
                .setParameter("schoolIds", schoolIds.toArray(new String[schoolIds.size()]));

        Result result = projectPointAnalysis.execute(param);
        setupHeader(excelWriter, result);
        fillProvinceData(excelWriter, result);
        fillSchoolData(excelWriter, result);
    }

    private void setupHeader(ExcelWriter excelWriter, Result result) {
        AtomicInteger column = new AtomicInteger(-1);
        List<Map<String, Object>> schools = result.get("province");
        excelWriter.set(0, column.incrementAndGet(), "学校");
        for(Map<String, Object> pointstat : schools){
            excelWriter.set(0, column.incrementAndGet(), pointstat.get("pointName"));
        }
    }

    private void fillProvinceData(ExcelWriter excelWriter, Result result) {
        AtomicInteger column = new AtomicInteger(-1);
        List<Map<String, Object>> classes = result.get("province");
        excelWriter.set(1, column.incrementAndGet(), "总体");
        for(Map<String, Object> pointstat : classes){
            excelWriter.set(1, column.incrementAndGet(), pointstat.get("score"));
        }
    }

    private void fillSchoolData(ExcelWriter excelWriter, Result result) {
        int row = 2;
        AtomicInteger column = new AtomicInteger(-1);
        List<Map<String, Object>> schools = result.get("schools");
        for(Map<String, Object> school : schools){
            List<Map<String, Object>> pointStats = (List<Map<String, Object>>)school.get("pointStats");
            excelWriter.set(row, column.incrementAndGet(), school.get("schoolName"));
            for(Map<String, Object> pointStat : pointStats) {
                excelWriter.set(row, column.incrementAndGet(), pointStat.get("score"));
            }
            row++;
            column.set(-1);
        }
    }
}
