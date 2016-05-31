package com.xz.report;

import com.xz.ajiaedu.common.excel.ExcelWriter;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.InputStream;
import java.util.List;

/**
 * (description)
 * created at 16/05/30
 *
 * @author yiding_he
 */
public abstract class ReportGenerator {

    @Autowired
    SheetManager sheetManager;

    /**
     * 生成并保存报表文件
     *
     * @param projectId 项目ID
     * @param savePath  保存路径
     */
    public void generate(String projectId, String savePath) {
        List<SheetTask> sheetTasks = getSheetTasks(projectId);

        InputStream stream = getClass().getResourceAsStream("report/templates/default.xlsx");
        ExcelWriter excelWriter = new ExcelWriter(stream);
        excelWriter.clearSheets();

        for (SheetTask sheetTask : sheetTasks) {
            excelWriter.openOrCreateSheet(sheetTask.getTitle());
            SheetGenerator sheetGenerator = sheetManager.getSheetGenerator(sheetTask.getGeneratorClass());
            if (sheetGenerator != null) {
                sheetGenerator.generate(projectId, excelWriter, sheetTask);
            }
        }

        excelWriter.save(savePath);
    }

    /**
     * 规划这个 Excel 文件有多少个 Sheet，为每个 Sheet 创建一个 SheetTask 对象
     *
     * @param projectId 项目ID
     *
     * @return SheetTask 列表
     */
    protected abstract List<SheetTask> getSheetTasks(String projectId);
}
