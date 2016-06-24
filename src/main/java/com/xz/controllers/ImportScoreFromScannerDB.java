package com.xz.controllers;

import com.xz.ajiaedu.common.lang.Result;
import com.xz.scanner.ScannerDBService;
import com.xz.services.AggregationRoundService;
import com.xz.services.AggregationService;
import com.xz.taskdispatchers.TaskDispatcherFactory;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

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

        // 1. 导入阅卷记录，计算明细分数
        LOG.info("开始从网阅库导入分数...");
        importScoreData(project, projectDoc);

        // 2. 计算分数总和
        LOG.info("分数导入完毕，开始计算总和...");
        aggregateTotalScores(project);

        LOG.info("项目{}的所有成绩导入完毕。", project);
        return Result.success();
    }

    // 计算分数总和
    private void aggregateTotalScores(String projectId) {
        String aggregationId = UUID.randomUUID().toString();

        aggregationService.runDispatchers(projectId, aggregationId, Arrays.asList(
                taskDispatcherFactory.getTaskDispatcher("student_list"),
                taskDispatcherFactory.getTaskDispatcher("total_score")
        ));

        aggregationRoundService.waitForRoundCompletion(aggregationId);
    }

    private void importScoreData(@RequestParam("project") String project, Document projectDoc) {
        Document subjectCodes = (Document) projectDoc.get("subjectcodes");
        List<String> subjectIds = new ArrayList<>(subjectCodes.keySet());

        for (String subjectId : subjectIds) {
            scannerDBService.importSubjectScore(project, subjectId);
        }
    }
}
