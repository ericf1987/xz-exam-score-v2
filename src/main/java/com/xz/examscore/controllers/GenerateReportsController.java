package com.xz.examscore.controllers;

import com.xz.ajiaedu.common.lang.Result;
import com.xz.examscore.services.ExamAllianceReportService;
import com.xz.examscore.services.ReportService;
import org.apache.commons.lang.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * (description)
 * created at 16/05/30
 *
 * @author yiding_he
 */
@Controller
@RequestMapping("/report")
public class GenerateReportsController {

    @Autowired
    ReportService reportService;
    
    @Autowired
    ExamAllianceReportService examAllianceReportService;

    @ResponseBody
    @RequestMapping(value = "all")
    public Result generateReports(@RequestParam("project") String projectId) {
        reportService.generateReports(projectId, true, false);
        return Result.success();
    }

    @ResponseBody
    @RequestMapping(value = "one")
    public Result generateOneReport(
            @RequestParam("project") String projectId,
            @RequestParam(value = "isExamAlliance", required = false, defaultValue = "false") String isExamAlliance,
            @RequestParam("category") String category,
            @RequestParam("filename") String filename,
            @RequestParam("rangeName") String rangeName,
            @RequestParam("rangeId") String rangeId,
            @RequestParam("reportGeneratorName") String reportGeneratorName
    ) {
        boolean b = BooleanUtils.toBoolean(Boolean.valueOf(isExamAlliance));
        reportService.generateOneReport(projectId, b, category, filename, rangeName, rangeId, reportGeneratorName);
        return Result.success();
    }

    @ResponseBody
    @RequestMapping(value="examAlliance")
    public Result generateExamAllianceReports(@RequestParam("project") String projectId){
        reportService.generateReports(projectId, true, true);
        return Result.success();
    }

    @ResponseBody
    @RequestMapping(value="downloadReports")
    public Result downloadReports(@RequestParam("project") String projectId){
        return examAllianceReportService.downloadReports(projectId);
    }

}
