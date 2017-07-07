package com.xz.examscore.controllers;

import com.xz.ajiaedu.common.lang.Result;
import com.xz.examscore.asynccomponents.aggrtaskdispatcher.TaskDispatcherFactory;
import com.xz.examscore.scanner.ScannerDBService;
import com.xz.examscore.services.AggregationRoundService;
import com.xz.examscore.services.AggregationService;
import com.xz.examscore.services.PrepareDataService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 从网阅数据库导入明细数据，并统计得分
 *
 * @author yiding_he
 */
@Controller
public class ImportScoreFromScannerDB {

    static final Logger LOG = LoggerFactory.getLogger(ImportScoreFromScannerDB.class);

    @Autowired
    ScannerDBService scannerDBService;

    @Autowired
    TaskDispatcherFactory taskDispatcherFactory;

    @Autowired
    AggregationService aggregationService;

    @Autowired
    AggregationRoundService aggregationRoundService;

    @Autowired
    PrepareDataService prepareDataService;

    /**
     * 从 Scanner 数据库导入并计算客观题成绩
     *
     * @param project 项目ID
     */
    @RequestMapping(value = "/import-score-from-scanner-db", method = RequestMethod.POST)
    @ResponseBody
    public Result importScoreFromScannerDB(@RequestParam("project") String project) {
        // 1. 导入阅卷记录，计算明细分数
        LOG.info("开始从网阅库导入分数...");
        scannerDBService.importProjectScore(project);

        // 2. 计算分数总和
        LOG.info("分数导入完毕，开始准备数据...");
        prepareData(project);

        LOG.info("项目{}的所有成绩导入完毕。", project);
        return Result.success();
    }

    @RequestMapping(value = "/import-one-subject-from-scanner-db", method = RequestMethod.POST)
    @ResponseBody
    public Result importScoreFromScannerDB(
            @RequestParam("project") String project,
            @RequestParam("subject") String subject) {
        scannerDBService.importSubjectScore0(project, subject);

        return Result.success();
    }

    // 计算分数总和
    private void prepareData(String projectId) {
        prepareDataService.prepare(projectId);
    }
}
