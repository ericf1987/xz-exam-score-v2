package com.xz.report.total;

import com.xz.ajiaedu.common.excel.ExcelWriter;
import com.xz.bean.Range;
import com.xz.report.SheetGenerator;
import com.xz.report.SheetTask;
import com.xz.services.RangeService;
import com.xz.services.SchoolService;
import com.xz.services.StudentService;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * (description)
 * created at 16/05/31
 *
 * @author yiding_he
 */
@Component
public class TotalBasicScoreSheet extends SheetGenerator {

    @Autowired
    RangeService rangeService;

    @Autowired
    StudentService studentService;

    @Autowired
    SchoolService schoolService;

    @Override
    protected void generateSheet(String projectId, ExcelWriter excelWriter, SheetTask sheetTask) {
        setupHeader(excelWriter);
        fillProvinceData(projectId, excelWriter);
        fillSchoolData(projectId, excelWriter);
    }

    // 填充学校数据
    private void fillSchoolData(String projectId, ExcelWriter excelWriter) {
        AtomicInteger column;
        List<Range> schoolRanges = rangeService.queryRanges(projectId, Range.SCHOOL);
        int row = 1;
        for (Range schoolRange : schoolRanges) {
            column = new AtomicInteger(-1);
            Document school = schoolService.queryExamSchool(projectId, schoolRange.getId());
            excelWriter.set(row, column.incrementAndGet(), school.getString("name"));
            excelWriter.set(row, column.incrementAndGet(), studentService.getStudentCount(projectId, schoolRange));
            row++;
        }
    }

    // 填充整体数据
    private void fillProvinceData(String projectId, ExcelWriter excelWriter) {
        Range provinceRange = rangeService.queryRanges(projectId, Range.PROVINCE).get(0);
        AtomicInteger column = new AtomicInteger(-1);
        excelWriter.set(1, column.incrementAndGet(), "总体");
        excelWriter.set(1, column.incrementAndGet(), studentService.getStudentCount(projectId, provinceRange));
    }

    // 填充表头
    private void setupHeader(ExcelWriter excelWriter) {
        AtomicInteger column = new AtomicInteger(-1);
        excelWriter.set(0, column.incrementAndGet(), "学校名称");
        excelWriter.set(0, column.incrementAndGet(), "实考人数");
        excelWriter.set(0, column.incrementAndGet(), "最高分");
        excelWriter.set(0, column.incrementAndGet(), "最低分");
        excelWriter.set(0, column.incrementAndGet(), "平均分");
        excelWriter.set(0, column.incrementAndGet(), "标准差");
        excelWriter.set(0, column.incrementAndGet(), "标准差");
        excelWriter.set(0, column.incrementAndGet(), "优率");
        excelWriter.set(0, column.incrementAndGet(), "良率");
        excelWriter.set(0, column.incrementAndGet(), "及格率");
        excelWriter.set(0, column.incrementAndGet(), "不及格率");
        excelWriter.set(0, column.incrementAndGet(), "全科及格率");
        excelWriter.set(0, column.incrementAndGet(), "全科不及格率");
    }
}
