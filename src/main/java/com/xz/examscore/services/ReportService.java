package com.xz.examscore.services;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.UpdateResult;
import com.xz.ajiaedu.common.mongo.MongoUtils;
import com.xz.examscore.asynccomponents.report.ReportManager;
import com.xz.examscore.bean.Range;
import com.xz.examscore.bean.Target;
import com.xz.examscore.cache.ProjectCacheManager;
import com.xz.examscore.util.Mongo;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.xz.ajiaedu.common.mongo.MongoUtils.doc;

/**
 * (description)
 * created at 16/05/30
 *
 * @author yiding_he
 */
@Service
public class ReportService {

    @Autowired
    ReportManager reportManager;

    @Autowired
    private MongoDatabase scoreDatabase;

    @Autowired
    private ProjectCacheManager projectCacheManager;

    static final Logger LOG = LoggerFactory.getLogger(ReportService.class);

    public void generateOneReport(String projectId, boolean isExamAlliance,
                                  String category, String filename, String rangeName, String rangeId, String reportGeneratorName){
        Range range = new Range(rangeName, rangeId);
        reportManager.generateOneReport(projectId, isExamAlliance, category, filename, range, reportGeneratorName);
    }

    public void generateReports(String projectId, boolean async, boolean isExamAlliance) {
        deleteReportRuntimeRecord(projectId);
        //清除项目缓存，确保EXCEL数据生成的准确性
        LOG.info("----报表统计阶段----开始清理项目缓存----");
        projectCacheManager.deleteProjectCache(projectId);
        LOG.info("----报表统计阶段----项目缓存清理完成----");
        reportManager.generateReports(projectId, async, isExamAlliance);
    }

    public void recordReportRuntime(String projectId, Range range, Target target, String className, long runtime){
        MongoCollection<Document> report_runtime_list = scoreDatabase.getCollection("report_runtime_list");
        Document query = Mongo.query(projectId, range, target).append("className", className);

        UpdateResult updateResult = report_runtime_list.updateMany(query, MongoUtils.$set(doc("runtime", runtime)));
        if(updateResult.getMatchedCount() == 0){
            report_runtime_list.insertOne(query.append("runtime", runtime).append("md5", Mongo.md5()));
        }
    }

    public void deleteReportRuntimeRecord(String projectId){
        MongoCollection<Document> report_runtime_list = scoreDatabase.getCollection("report_runtime_list");
        report_runtime_list.deleteMany(doc("project", projectId));
    }
}
