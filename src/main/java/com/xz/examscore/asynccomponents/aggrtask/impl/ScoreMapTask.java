package com.xz.examscore.asynccomponents.aggrtask.impl;

import com.hyd.simplecache.utils.MD5;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.UpdateResult;
import com.xz.examscore.asynccomponents.aggrtask.AggrTask;
import com.xz.examscore.asynccomponents.aggrtask.AggrTaskMessage;
import com.xz.examscore.asynccomponents.aggrtask.AggrTaskMeta;
import com.xz.examscore.bean.Range;
import com.xz.examscore.bean.Target;
import com.xz.examscore.services.ImportProjectService;
import com.xz.examscore.services.ScoreService;
import com.xz.examscore.services.StudentService;
import com.xz.examscore.util.Mongo;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.xz.ajiaedu.common.mongo.MongoUtils.$set;
import static com.xz.ajiaedu.common.mongo.MongoUtils.doc;

/**
 * 生成排名记录
 */
@AggrTaskMeta(taskType = "score_map")
@Component
public class ScoreMapTask extends AggrTask {

    static final Logger LOG = LoggerFactory.getLogger(ScoreMapTask.class);

    @Autowired
    ScoreService scoreService;

    @Autowired
    StudentService studentService;

    @Autowired
    MongoDatabase scoreDatabase;

    @Autowired
    ImportProjectService importProjectService;

    @Override
    protected void runTask(AggrTaskMessage taskInfo) {
        String projectId = taskInfo.getProjectId();
        Range range = taskInfo.getRange();
        Target target = taskInfo.getTarget();

        List<String> studentIds = studentService.getStudentIds(projectId, range, target);
/*        if (studentIds.isEmpty()) {  // 可能对应的 range 考生全部没有分数
            return;
        }*/

        MongoCollection<Document> collection = scoreDatabase.getCollection("score_map");
        Document query = Mongo.query(projectId, range, target);

        List<Document> scoreCountList = createScoreMap(projectId, target, studentIds);
        UpdateResult result = collection.updateMany(query,
                $set(
                        doc("scoreMap", scoreCountList)
                                .append("count", studentIds.size())
                )
        );
        if (result.getMatchedCount() == 0) {
            collection.insertOne(
                    query.append("scoreMap", scoreCountList)
                            .append("count", studentIds.size())
                            .append("md5", MD5.digest(UUID.randomUUID().toString()))
            );
        }
    }

    private List<Document> createScoreMap(String projectId, Target target, List<String> studentIds) {
        List<Document> scoreCountList = new ArrayList<>();

        //如果当前范围内没有学生，则需要将score_map中的scoreMap元素清空
        if(studentIds.isEmpty()){
            return scoreCountList;
        }else {
            for (String studentId : studentIds) {
                Range studentRange = new Range(Range.STUDENT, studentId);
                double totalScore = scoreService.getScore(projectId, studentRange, target);
                addUpScoreMap(scoreCountList, totalScore);
            }
        }

        return scoreCountList;
    }

    private void addUpScoreMap(List<Document> scoreCountList, double score) {
        for (Document document : scoreCountList) {
            if (document.getDouble("score") == score) {
                document.put("count", document.getInteger("count") + 1);
                return;
            }
        }

        scoreCountList.add(doc("score", score).append("count", 1));
    }
}
