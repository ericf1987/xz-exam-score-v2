package com.xz.examscore.asynccomponents.report.customization;

import com.xz.ajiaedu.common.excel.ExcelWriter;
import com.xz.ajiaedu.common.lang.Result;
import com.xz.ajiaedu.common.ajia.Param;
import com.xz.examscore.api.server.customization.AllClassScoreCompare;
import com.xz.examscore.asynccomponents.report.SheetGenerator;
import com.xz.examscore.asynccomponents.report.SheetTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author by fengye on 2016/10/17.
 */
@SuppressWarnings("unchecked")
@Component
public class AllClassScoreCompareSheets extends SheetGenerator {
    @Autowired
    AllClassScoreCompare allClassScoreCompare;

    public static final String[] SECONDARY_HEADER = new String[]{"得分", "排名"};

    @Override
    protected void generateSheet(String projectId, ExcelWriter excelWriter, SheetTask sheetTask) throws Exception {
        Param param = new Param().setParameter("projectId", projectId);
        Result result = allClassScoreCompare.execute(param);
        List<Map<String, Object>> classes = result.get("classes");
        if(classes.isEmpty()){
            return;
        }
        setHeader(excelWriter, classes);
        setSecondaryHeader(excelWriter, classes);
        fillData(excelWriter, classes);
    }

    private void setHeader(ExcelWriter excelWriter, List<Map<String, Object>> classes) {
        AtomicInteger column = new AtomicInteger(-1);
        excelWriter.set(0, column.incrementAndGet(), "学校");
        excelWriter.set(0, column.incrementAndGet(), "班级");
        List<Map<String, Object>> subjectList = (List<Map<String, Object>>)classes.get(0).get("subjects");
        subjectList.forEach(subject -> {
            excelWriter.set(0, column.incrementAndGet(), subject.get("subjectName"));
            column.incrementAndGet();
            excelWriter.mergeCells(0, column.get() - 1, 0, column.get());
        });
        excelWriter.set(0, column.incrementAndGet(), "全科");
        column.incrementAndGet();
        excelWriter.mergeCells(0, column.get() - 1, 0, column.get());
    }

    private void setSecondaryHeader(ExcelWriter excelWriter, List<Map<String, Object>> classes) {
        AtomicInteger column = new AtomicInteger(-1);
        excelWriter.set(1, column.incrementAndGet(), "学校");
        excelWriter.mergeCells(0, column.get(), 1, column.get());
        excelWriter.set(1, column.incrementAndGet(), "班级");
        excelWriter.mergeCells(0, column.get(), 1, column.get());
        List<Map<String, Object>> subjectList = (List<Map<String, Object>>)classes.get(0).get("subjects");
        for(int i = 0;i < subjectList.size(); i++){
            excelWriter.set(1, column.incrementAndGet(), SECONDARY_HEADER[0]);
            excelWriter.set(1, column.incrementAndGet(), SECONDARY_HEADER[1]);
        }
        excelWriter.set(1, column.incrementAndGet(), SECONDARY_HEADER[0]);
        excelWriter.set(1, column.incrementAndGet(), SECONDARY_HEADER[1]);
    }

    private void fillData(ExcelWriter excelWriter, List<Map<String, Object>> classes) {
        AtomicInteger column = new AtomicInteger(-1);
        int row = 2;
        for (Map<String, Object> clazz : classes){
            excelWriter.set(row, column.incrementAndGet(), clazz.get("schoolName"));
            excelWriter.set(row, column.incrementAndGet(), clazz.get("className"));
            List<Map<String, Object>> subjects = (List<Map<String, Object>>)clazz.get("subjects");
            for(Map<String, Object> subject : subjects){
                excelWriter.set(row, column.incrementAndGet(), subject.get("average"));
                excelWriter.set(row, column.incrementAndGet(), subject.get("rank"));
            }
            excelWriter.set(row, column.incrementAndGet(), clazz.get("totalAverage"));
            excelWriter.set(row, column.incrementAndGet(), clazz.get("totalRank"));
            column.set(-1);
            row++;
        }
    }
}
