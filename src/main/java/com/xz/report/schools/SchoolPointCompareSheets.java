package com.xz.report.schools;

import com.xz.ajiaedu.common.excel.ExcelWriter;
import com.xz.ajiaedu.common.lang.Result;
import com.xz.api.Param;
import com.xz.api.server.school.SchoolPointCompare;
import com.xz.bean.Range;
import com.xz.bean.Target;
import com.xz.report.SheetGenerator;
import com.xz.report.SheetTask;
import com.xz.services.SchoolService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author by fengye on 2016/7/1.
 */
@Component
public class SchoolPointCompareSheets extends SheetGenerator {
    @Autowired
    SchoolPointCompare schoolPointCompare;
    @Autowired
    SchoolService schoolService;

    @Override
    protected void generateSheet(String projectId, ExcelWriter excelWriter, SheetTask sheetTask) throws Exception {
        Target target = sheetTask.get("target");
        String pointId = target.getId().toString();
        Range schoolRange = sheetTask.getRange();
        Param param = new Param().
                setParameter("projectId", projectId).
                setParameter("pointId", pointId).
                setParameter("schoolId", schoolRange.getId());
        Result result = schoolPointCompare.execute(param);
        System.out.println("学校知识点对比-->" + result.getData());
    }
}
