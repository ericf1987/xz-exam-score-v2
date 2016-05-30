package com.xz.report;

import com.xz.ajiaedu.common.excel.ExcelWriter;
import com.xz.report.sheet.SheetGenerator;
import com.xz.report.sheet.SheetInfo;
import com.xz.report.sheet.SheetManager;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.io.InputStream;

/**
 * (description)
 * created at 16/05/30
 *
 * @author yiding_he
 */
public abstract class ReportGenerator {

    @Autowired
    ReportManager reportManager;

    @Autowired
    SheetManager sheetManager;

    @PostConstruct
    public void init() {
        reportManager.registerReport(this);
    }

    /**
     * 生成并保存报表文件
     *
     * @param projectId 项目ID
     * @param savePath  保存路径
     */
    public void generate(String projectId, String savePath) {
        if (!this.getClass().isAnnotationPresent(ReportInfo.class)) {
            return;
        }

        ReportInfo reportInfo = this.getClass().getAnnotation(ReportInfo.class);
        SheetInfo[] sheetInfos = reportInfo.sheets();

        InputStream stream = getClass().getResourceAsStream("report-templates/default.xlsx");
        ExcelWriter excelWriter = new ExcelWriter(stream);
        excelWriter.clearSheets();

        for (SheetInfo sheetInfo : sheetInfos) {
            excelWriter.openOrCreateSheet(sheetInfo.name());
            SheetGenerator sheetGenerator = sheetManager.getSheetGenerator(sheetInfo.type());
            if (sheetGenerator != null) {
                sheetGenerator.generate(projectId, excelWriter);
            }
        }

        excelWriter.save(savePath);
    }

    public ReportInfo getInfo() {
        return this.getClass().getAnnotation(ReportInfo.class);
    }
}
