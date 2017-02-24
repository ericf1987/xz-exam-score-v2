package com.xz.examscore.controllers;

import com.xz.ajiaedu.common.lang.Result;
import com.xz.examscore.scanner.ScannerDBService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author by fengye on 2017/2/24.
 */
@Controller
public class ImportStudentCardSlice {

    @Autowired
    ScannerDBService scannerDBService;

    /**
     * 从 Scanner 数据库导入学生试卷留痕到 统计 数据库
     *
     * @param project 项目ID
     */
    @RequestMapping(value = "/import-student-card-slice", method = RequestMethod.POST)
    @ResponseBody
    public Result importStudentCardSlice(@RequestParam("project") String project) {
        scannerDBService.importStudentCardSlice(project);
        return Result.success();
    }
}
