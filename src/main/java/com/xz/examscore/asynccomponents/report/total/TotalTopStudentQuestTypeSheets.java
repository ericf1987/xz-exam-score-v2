package com.xz.examscore.asynccomponents.report.total;

import com.xz.ajiaedu.common.excel.ExcelWriter;
import com.xz.ajiaedu.common.lang.Result;
import com.xz.ajiaedu.common.ajia.Param;
import com.xz.examscore.api.server.project.ProjectTopStudentQuestTypeStat;
import com.xz.examscore.asynccomponents.report.SheetGenerator;
import com.xz.examscore.asynccomponents.report.SheetTask;
import com.xz.examscore.bean.Target;
import com.xz.examscore.services.SchoolService;
import com.xz.examscore.services.TopStudentListService;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author by fengye on 2016/6/19.
 * 总体成绩分析-尖子生情况-试卷情况分析
 */
@Component
@SuppressWarnings("unchecked")
public class TotalTopStudentQuestTypeSheets extends SheetGenerator {

    @Autowired
    ProjectTopStudentQuestTypeStat projectTopStudentQuestTypeStat;

    @Autowired
    SchoolService schoolService;

    @Autowired
    TopStudentListService topStudentListService;

    static final Logger LOG = LoggerFactory.getLogger(TotalTopStudentQuestTypeSheets.class);

    @Override
    protected void generateSheet(String projectId, ExcelWriter excelWriter, SheetTask sheetTask) throws Exception {
        Target subjectTarget = sheetTask.get("target");
        Document doc = topStudentListService.getTopStudentLastOne(projectId, sheetTask.getRange(), sheetTask.getTarget());
        if(null == doc || doc.isEmpty()){
            LOG.error("找不到尖子生试卷题型信息, project={}, range={}, target={}", projectId, sheetTask.getRange(), sheetTask.getTarget());
            return;
        }
        String[] rankSegment = new String[]{"1", doc.get("rank").toString()};
        Param param = new Param().setParameter("projectId", projectId)
                .setParameter("subjectId", subjectTarget.getId().toString())
                .setParameter("rankSegment", rankSegment);
        Result result = projectTopStudentQuestTypeStat.execute(param);
        setupHeader(excelWriter, result);
        fillData(excelWriter, result);
    }

    private void setupHeader(ExcelWriter excelWriter, Result result) {
        AtomicInteger column = new AtomicInteger(-1);
        excelWriter.set(0, column.incrementAndGet(), "考号");
        excelWriter.set(0, column.incrementAndGet(), "学生考号");
        excelWriter.set(0, column.incrementAndGet(), "尖子生");
        excelWriter.set(0, column.incrementAndGet(), "所属学校");
        excelWriter.set(0, column.incrementAndGet(), "总体排名");
        List<Map<String, Object>> totals = result.get("totals");
        for(Map<String, Object> total : totals){
            excelWriter.set(0, column.incrementAndGet(), total.get("name"));
        }
    }

    private void fillData(ExcelWriter excelWriter, Result result) {
        int row = 1;
        AtomicInteger column = new AtomicInteger(-1);
        List<Map<String, Object>> topStudents = result.get("topStudents");
        for(Map<String, Object> topStudent : topStudents){
            excelWriter.set(row, column.incrementAndGet(), topStudent.get("examNo"));
            excelWriter.set(row, column.incrementAndGet(), topStudent.get("customExamNo"));
            excelWriter.set(row, column.incrementAndGet(), topStudent.get("name"));
            excelWriter.set(row, column.incrementAndGet(), topStudent.get("schoolName"));
            excelWriter.set(row, column.incrementAndGet(), topStudent.get("rank"));
            List<Map<String, Object>> questTypes = (List<Map<String, Object>>)topStudent.get("questTypes");
            for (Map<String, Object> questType : questTypes){
                //excelWriter.set(row, column.incrementAndGet(), DoubleUtils.toPercent(Double.parseDouble(questType.get("scoreRate").toString())));
                excelWriter.set(row, column.incrementAndGet(), questType.get("score"));
            }
            row++;
            column.set(-1);
        }
    }
}
