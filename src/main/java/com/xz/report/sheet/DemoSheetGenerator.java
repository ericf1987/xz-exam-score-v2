package com.xz.report.sheet;

import com.xz.ajiaedu.common.excel.ExcelWriter;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.springframework.stereotype.Component;

@Component
public class DemoSheetGenerator extends SheetGenerator {

    @Override
    protected void generateSheet(String projectId, ExcelWriter excelWriter) {
        CellStyle cellStyle = excelWriter.createCellStyle("DEFAULT");
        excelWriter.setDefaultStyleName("DEFAULT");

        Font font = excelWriter.createFont("微软雅黑", 9, false, IndexedColors.BLACK);
        cellStyle.setFont(font);

        excelWriter.set(0, 0, "Hello, ");
        excelWriter.set(0, 1, "world");
    }
}
