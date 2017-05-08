package com.xz.examscore.asynccomponents.report.schools;

import com.xz.ajiaedu.common.excel.ExcelWriter;
import com.xz.ajiaedu.common.lang.Result;
import com.xz.ajiaedu.common.ajia.Param;
import com.xz.examscore.api.server.school.SchoolHighSegmentAnalysis;
import com.xz.examscore.asynccomponents.report.SheetGenerator;
import com.xz.examscore.asynccomponents.report.SheetTask;
import com.xz.examscore.bean.ProjectConfig;
import com.xz.examscore.bean.Range;
import com.xz.examscore.bean.Target;
import com.xz.examscore.services.ProjectConfigService;
import com.xz.examscore.services.SchoolService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author by fengye on 2016/6/25.
 * 学校成绩分析-尖子生情况-高分段竞争力分析
 */
@SuppressWarnings("unchecked")
@Component
public class SchoolPaperHScoreSheets extends SheetGenerator {

    @Autowired
    SchoolHighSegmentAnalysis schoolHighSegmentAnalysis;

    @Autowired
    SchoolService schoolService;

    @Autowired
    ProjectConfigService projectConfigService;

    @Override
    protected void generateSheet(String projectId, ExcelWriter excelWriter, SheetTask sheetTask) throws Exception {
        Target target = sheetTask.get("target");
        String subjectId = target.getId().toString();
        Range schoolRange = sheetTask.getRange();
        ProjectConfig projectConfig =  projectConfigService.getProjectConfig(projectId);
        //获取高分段参数
        Param param = new Param().setParameter("projectId", projectId).setParameter("percent", projectConfig.getHighScoreRate())
                .setParameter("schoolId", schoolRange.getId()).setParameter("subjectId", subjectId);
        Result result = schoolHighSegmentAnalysis.execute(param);
        setupHeader(excelWriter, result);
        fillSchoolData(excelWriter, result);
        fillClassData(excelWriter, result);
    }

    private void setupHeader(ExcelWriter excelWriter, Result result) {
        AtomicInteger column = new AtomicInteger(-1);
        List<Map<String, Object>> schools = result.get("schools");
        excelWriter.set(0, column.incrementAndGet(), "学校名称");
        for(Map<String, Object> school : schools){
            excelWriter.set(0, column.incrementAndGet(), school.get("subjectName"));
        }
    }

    private void fillSchoolData(ExcelWriter excelWriter, Result result) {
        AtomicInteger column = new AtomicInteger(-1);
        List<Map<String, Object>> schools = result.get("schools");
        excelWriter.set(1, column.incrementAndGet(), "总体");
        for(Map<String, Object> school : schools){
            excelWriter.set(1, column.incrementAndGet(), school.get("average"));
        }
    }

    private void fillClassData(ExcelWriter excelWriter, Result result) {
        int row = 2;
        AtomicInteger column = new AtomicInteger(-1);
        List<Map<String, Object>> classes = result.get("classes");
        for(Map<String, Object> clazz : classes){
            List<Map<String, Object>> subjects = (List<Map<String, Object>>)clazz.get("subjects");
            excelWriter.set(row, column.incrementAndGet(), clazz.get("className"));
            for(Map<String, Object> subject : subjects){
                excelWriter.set(row, column.incrementAndGet(), subject.get("average"));
            }
            row++;
            column.set(-1);
        }
    }
}
