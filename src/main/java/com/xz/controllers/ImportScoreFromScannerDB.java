package com.xz.controllers;

import com.xz.ajiaedu.common.lang.Result;
import com.xz.scanner.ScannerDBService;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * (description)
 * created at 16/06/16
 *
 * @author yiding_he
 */
@Controller
public class ImportScoreFromScannerDB {

    @Autowired
    ScannerDBService scannerDBService;

    /**
     * 从 Scanner 数据库导入并计算客观题成绩
     *
     * @param project 项目ID
     */
    @RequestMapping(value = "/import-score-from-scanner-db", method = RequestMethod.POST)
    @ResponseBody
    public Result impotScoreFromScannerDB(@RequestParam("project") String project) {
        Document projectDoc = scannerDBService.findProject(project);
        return Result.success();
    }
}
