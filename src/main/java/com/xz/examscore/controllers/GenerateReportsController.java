package com.xz.examscore.controllers;

import com.xz.ajiaedu.common.lang.Result;
import com.xz.examscore.services.ExamAllianceReportService;
import com.xz.examscore.services.ReportService;
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

    @ResponseBody
    @RequestMapping(value = "all")
    public Result generateReports(@RequestParam("project") String projectId) {

        reportService.generateReports(projectId, true, false);

        return Result.success();
    }

}
