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

    public void generateReports(String projectId, boolean async, boolean isExamAlliance) {
        deleteReportRuntimeRecord(projectId);
        //清除项目缓存，确保EXCEL数据生成的准确性
        projectCacheManager.deleteProjectCache(projectId);
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
