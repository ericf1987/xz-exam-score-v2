package com.xz.examscore.asynccomponents.report.customization.examAlliance;

import com.xz.ajiaedu.common.excel.ExcelWriter;
import com.xz.ajiaedu.common.lang.Result;
import com.xz.examscore.api.Param;
import com.xz.examscore.api.server.customization.examAlliance.SchoolSubjectBasicInfoAnalysis;
import com.xz.examscore.asynccomponents.report.SheetGenerator;
import com.xz.examscore.asynccomponents.report.SheetTask;
import com.xz.examscore.bean.Target;
import com.xz.examscore.util.DoubleUtils;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author by fengye on 2016/12/27.
 */
@Component
public class SchoolSubjectBasicInfoSheets extends SheetGenerator {

    @Autowired
    SchoolSubjectBasicInfoAnalysis schoolSubjectBasicInfoAnalysis;

    @Override
    protected void generateSheet(String projectId, ExcelWriter excelWriter, SheetTask sheetTask) throws Exception {
        Target target = sheetTask.get("target");
        String subjectId = target.match(Target.PROJECT) ? null : target.getId().toString();
        Param param = new Param().setParameter("projectId", projectId).setParameter("subjectId", subjectId);
        Result result = schoolSubjectBasicInfoAnalysis.execute(param);
        List<Map<String, Object>> schoolData = result.get("schoolSubjectBasicInfo");
        setHeader(excelWriter);
        fillSchoolData(excelWriter, schoolData);
    }

    private void setHeader(ExcelWriter excelWriter) {
        AtomicInteger column = new AtomicInteger(-1);
        excelWriter.set(0, column.incrementAndGet(), "学校名称");
        excelWriter.set(0, column.incrementAndGet(), "最高分");
        excelWriter.set(0, column.incrementAndGet(), "平均分");
        excelWriter.set(0, column.incrementAndGet(), "平均分排名");
        excelWriter.set(0, column.incrementAndGet(), "得分率");
    }

    private void fillSchoolData(ExcelWriter excelWriter, List<Map<String, Object>> schoolData) {
        int row = 1;
        AtomicInteger column = new AtomicInteger(-1);
        for(Map<String, Object> schoolMap : schoolData){
            excelWriter.set(row, column.incrementAndGet(), schoolMap.get("schoolName"));
            excelWriter.set(row, column.incrementAndGet(), schoolMap.get("max"));
            excelWriter.set(row, column.incrementAndGet(), DoubleUtils.round(MapUtils.getDouble(schoolMap, "average")));
            excelWriter.set(row, column.incrementAndGet(), schoolMap.get("rank"));
            excelWriter.set(row, column.incrementAndGet(), schoolMap.get("scoreRate"));
            row++;
            column.set(-1);
        }
    }
}
