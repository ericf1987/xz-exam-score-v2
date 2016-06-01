package com.xz.report;

import com.xz.ajiaedu.common.excel.ExcelWriter;

/**
 * 生成报表文件中的单个 Sheet
 *
 * @author yiding_he
 */
public abstract class SheetGenerator {

    /**
     * 生成 sheet
     *
     * @param projectId   项目ID
     * @param excelWriter Excel 写入对象
     */
    public void generate(String projectId, ExcelWriter excelWriter, SheetTask sheetTask) throws Exception {
        generateSheet(projectId, excelWriter, sheetTask);
    }

    protected abstract void generateSheet(
            String projectId, ExcelWriter excelWriter, SheetTask sheetTask) throws Exception;
}
