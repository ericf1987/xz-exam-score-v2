package com.xz.examscore.asynccomponents.report.customization;

import com.xz.ajiaedu.common.excel.ExcelWriter;
import com.xz.ajiaedu.common.lang.Result;
import com.xz.ajiaedu.common.ajia.Param;
import com.xz.examscore.api.server.customization.ClassSbjCbnCompare;
import com.xz.examscore.asynccomponents.report.SheetGenerator;
import com.xz.examscore.asynccomponents.report.SheetTask;
import com.xz.examscore.bean.Target;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author by fengye on 2016/10/20.
 */
@SuppressWarnings("unchecked")
@Component
public class ClassSbjCbnSheets extends SheetGenerator {

    @Autowired
    ClassSbjCbnCompare classSbjCbnCompare;

    @Override
    protected void generateSheet(String projectId, ExcelWriter excelWriter, SheetTask sheetTask) throws Exception {
        Target target = sheetTask.get("target");
        Param param = new Param().setParameter("projectId", projectId)
                .setParameter("subjectCombinationId", target.getId().toString());
        Result result = classSbjCbnCompare.execute(param);
        List<Map<String, Object>> list = result.get("classes");
        setHeader(excelWriter);
        fillData(excelWriter, list);
    }

    private void setHeader(ExcelWriter excelWriter) {
        AtomicInteger column = new AtomicInteger(-1);
        excelWriter.set(0, column.incrementAndGet(), "班级");
        excelWriter.set(0, column.incrementAndGet(), "综合平均分");
        excelWriter.set(0, column.incrementAndGet(), "学校排名");
        excelWriter.set(0, column.incrementAndGet(), "联考排名");
    }

    private void fillData(ExcelWriter excelWriter, List<Map<String, Object>> list) {
        AtomicInteger column = new AtomicInteger(-1);
        int row = 1;
        for(Map<String, Object> map : list){
            excelWriter.set(row, column.incrementAndGet(), map.get("className"));
            excelWriter.set(row, column.incrementAndGet(), map.get("average"));
            excelWriter.set(row, column.incrementAndGet(), map.get("schoolRank"));
            excelWriter.set(row, column.incrementAndGet(), map.get("projectRank"));
            row++;
            column.set(-1);
        }
    }
}
