package com.xz.examscore.asynccomponents.report.schools.compare;

import com.xz.ajiaedu.common.excel.ExcelWriter;
import com.xz.ajiaedu.common.lang.Result;
import com.xz.examscore.api.Param;
import com.xz.examscore.api.annotation.Function;
import com.xz.examscore.api.annotation.Parameter;
import com.xz.examscore.api.annotation.Type;
import com.xz.examscore.api.server.school.compare.SchoolCompareAnalysis;
import com.xz.examscore.asynccomponents.report.SheetGenerator;
import com.xz.examscore.asynccomponents.report.SheetTask;
import com.xz.examscore.bean.Range;
import com.xz.examscore.bean.Target;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author by fengye on 2016/9/27.
 */
@SuppressWarnings("unchecked")
@Function(description = "学校成绩-对比", parameters = {
        @Parameter(name = "projectId", type = Type.String, description = "考试项目ID", required = true),
        @Parameter(name = "subjectId", type = Type.String, description = "科目ID", required = false),
        @Parameter(name = "schoolId", type = Type.String, description = "学校id", required = true)
})
@Component
public class SchoolCompareSheets extends SheetGenerator {

    @Autowired
    SchoolCompareAnalysis schoolCompareAnalysis;

    public static final String[] SECONDARY_HEADER = new String[]{
            "得分率(平均分)", "优秀率(人数)", "及格率(人数)"
    };

    @Override
    protected void generateSheet(String projectId, ExcelWriter excelWriter, SheetTask sheetTask) throws Exception {
        Target target = sheetTask.get("target");
        String subjectId = target.match(Target.PROJECT) ? null : target.getId().toString();
        Range schoolRange = sheetTask.getRange();
        Param param = new Param().setParameter("projectId", projectId).
                setParameter("subjectId", subjectId).
                setParameter("schoolId", schoolRange.getId());

        Result result = schoolCompareAnalysis.execute(param);

        setHeader(excelWriter, result);
        setSecondaryHeader(excelWriter, result);
        fillSchoolData(excelWriter, result);
        fillClassData(excelWriter, result);
    }

    private void setHeader(ExcelWriter excelWriter, Result result) {
        AtomicInteger column = new AtomicInteger(-1);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String currentDate = format.format(Calendar.getInstance().getTime());
        Map<String, Object> school = result.get("school");
        List<Map<String, Object>> projectList = (List<Map<String, Object>>) school.get("projects");
        excelWriter.set(0, column.incrementAndGet(), "班级名称");
        projectList.forEach(projectMap -> {
            excelWriter.set(0, column.incrementAndGet(),
                    (projectMap.get("startDate") == null ? currentDate : projectMap.get("startDate")).toString() + projectMap.get("projectName").toString());
            column.incrementAndGet();
            column.incrementAndGet();
            excelWriter.mergeCells(0, column.get() - 2, 0, column.get());
        });
    }

    private void setSecondaryHeader(ExcelWriter excelWriter, Result result) {
        AtomicInteger column = new AtomicInteger(-1);
        excelWriter.set(1, column.incrementAndGet(), "对比项");
        Map<String, Object> school = result.get("school");
        List<Map<String, Object>> projectList = (List<Map<String, Object>>) school.get("projects");
        projectList.forEach(projectDoc -> {
            for (String header : SECONDARY_HEADER) {
                excelWriter.set(1, column.incrementAndGet(), header);
            }
        });
    }

    private void fillSchoolData(ExcelWriter excelWriter, Result result) {
        AtomicInteger column = new AtomicInteger(-1);
        int row = 2;
        Map<String, Object> school = result.get("school");
        excelWriter.set(row, column.incrementAndGet(), "本校");
        List<Map<String, Object>> projectList = (List<Map<String, Object>>) school.get("projects");
        projectList.forEach(projectMap -> {
            excelWriter.set(row, column.incrementAndGet(), projectMap.get("scoreRate") + "(" + projectMap.get("average") + ")");
            excelWriter.set(row, column.incrementAndGet(), projectMap.get("excellentRate") + "(" + projectMap.get("excellentCount") + ")");
            excelWriter.set(row, column.incrementAndGet(), projectMap.get("passRate") + "(" + projectMap.get("passCount") + ")");
        });
    }

    private void fillClassData(ExcelWriter excelWriter, Result result) {
        AtomicInteger column = new AtomicInteger(-1);
        int row = 3;
        List<Map<String, Object>> classes = result.get("classes");
        for(Map<String, Object> clazz : classes){
            List<Map<String, Object>> projectList = (List<Map<String, Object>>) clazz.get("projects");
            excelWriter.set(row, column.incrementAndGet(), clazz.get("className"));
            for(Map<String, Object> projectMap : projectList){
                excelWriter.set(row, column.incrementAndGet(), projectMap.get("scoreRate") + "(" + projectMap.get("average") + ")");
                excelWriter.set(row, column.incrementAndGet(), projectMap.get("excellentRate") + "(" + projectMap.get("excellentCount") + ")");
                excelWriter.set(row, column.incrementAndGet(), projectMap.get("passRate") + "(" + projectMap.get("passCount") + ")");
            }
            row++;
            column.set(-1);
        }
    }
}
