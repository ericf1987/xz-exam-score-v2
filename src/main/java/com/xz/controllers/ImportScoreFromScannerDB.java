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

import java.util.ArrayList;
import java.util.List;

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
    public Result importScoreFromScannerDB(@RequestParam("project") String project) {
        Document projectDoc = scannerDBService.findProject(project);
        if (projectDoc == null) {
            return Result.fail("没有找到项目" + project);
        }

        Document subjectCodes = (Document) projectDoc.get("subjectcodes");
        List<String> subjectIds = new ArrayList<>(subjectCodes.keySet());

        for (String subjectId : subjectIds) {
            scannerDBService.importSubjectScore(project, subjectId);
        }

        return Result.success();
    }
}
