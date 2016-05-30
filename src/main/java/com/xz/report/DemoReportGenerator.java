package com.xz.report;

import com.xz.report.sheet.DemoSheetGenerator;
import com.xz.report.sheet.SheetInfo;
import org.springframework.stereotype.Component;

@ReportInfo(category = "总体报表", fileName = "测试报表.xlsx", sheets = {
        @SheetInfo(name = "你好", type = DemoSheetGenerator.class)
})
@Component
public class DemoReportGenerator extends ReportGenerator {

}
