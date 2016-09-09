package com.xz.examscore.asynccomponents.aggrtaskdispatcher.impl;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.xz.examscore.asynccomponents.aggrtaskdispatcher.TaskDispatcher;
import com.xz.examscore.asynccomponents.aggrtaskdispatcher.TaskDispatcherInfo;
import com.xz.examscore.bean.ProjectConfig;
import com.xz.examscore.bean.Range;
import com.xz.examscore.bean.Target;
import com.xz.examscore.services.RangeService;
import com.xz.examscore.services.ScoreService;
import com.xz.examscore.services.StudentService;
import com.xz.examscore.services.TargetService;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.xz.ajiaedu.common.mongo.MongoUtils.$in;
import static com.xz.ajiaedu.common.mongo.MongoUtils.doc;

@TaskDispatcherInfo(taskType = "point", isAdvanced = true)
@Component
public class PointDispatcher extends TaskDispatcher {

    private static final Logger LOG = LoggerFactory.getLogger(PointDispatcher.class);

    @Autowired
    StudentService studentService;

    @Autowired
    ScoreService scoreService;

    @Autowired
    TargetService targetService;

    @Autowired
    RangeService rangeService;

    @Autowired
    MongoDatabase scoreDatabase;

    private static final String[] TARGETS = {Target.POINT, Target.POINT_LEVEL, Target.SUBJECT_LEVEL};

    private static final String[] RANGES = {Range.CLASS, Range.SCHOOL, Range.PROVINCE};

    @Override
    public void dispatch(String projectId, String aggregationId, ProjectConfig projectConfig) {

        // 删除旧数据
        LOG.info("删除项目 {} 的 point 相关旧数据...", projectId);
        deleteOldData(projectId);

        LOG.info("为项目 {} 预先创建总分记录...", projectId);
        createBlankTotalScoreDocuments(projectId);

        LOG.info("项目 {} 的 point 相关旧数据删除完毕，开始统计...", projectId);
        dispatchTaskForEveryStudent(projectId, aggregationId);
    }

    private void createBlankTotalScoreDocuments(String projectId) {
        List<Range> ranges = rangeService.queryRanges(projectId, RANGES);
        List<Target> targets = targetService.queryTargets(projectId, TARGETS);
        for (Target target : targets) {
            for (Range range : ranges) {
                scoreService.createTotalScore(projectId, range, target);
            }
        }
    }

    void deleteOldData(String projectId) {
        MongoCollection<Document> c = scoreDatabase.getCollection("total_score");
        c.deleteMany(doc("project", projectId).append("target.name",
                $in(Target.POINT, Target.POINT_LEVEL, Target.SUBJECT_LEVEL)));
    }

}
