package com.xz.mqreceivers.impl;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.xz.ajiaedu.common.lang.Value;
import com.xz.bean.Range;
import com.xz.bean.Target;
import com.xz.mqreceivers.AggrTask;
import com.xz.mqreceivers.Receiver;
import com.xz.mqreceivers.ReceiverInfo;
import com.xz.services.TargetService;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * 最高分最低分统计（不包含题目，题目在 mapreduce 中统计）
 *
 * @author yiding_he
 */
@ReceiverInfo(taskType = "minmax")
@Component
public class MinMaxTask extends Receiver {

    static final Logger LOG = LoggerFactory.getLogger(MinMaxTask.class);

    @Autowired
    MongoDatabase scoreDatabase;

    @Autowired
    TargetService targetService;

    @Override
    public void runTask(AggrTask aggrTask) {
        String projectId = aggrTask.getProjectId();
        Target target = aggrTask.getTarget();
        String subjectId = getSubjectId(target);
        Range range = aggrTask.getRange();

        Value<Double> min = Value.of((double) Integer.MAX_VALUE), max = Value.of(0d);
        List<String> studentIds = getStudentList(projectId, subjectId, range);
        LOG.info("查询到学生数量: " + studentIds.size());

        queryMinMax(projectId, target, studentIds, min, max);
        saveMinMax(projectId, target, range, min, max);
    }

    private void saveMinMax(String projectId, Target target, Range range, Value<Double> min, Value<Double> max) {
        Document id = new Document("projectId", projectId)
                .append("range", new Document("name", range.getName()).append("id", range.getId()))
                .append("target", new Document("name", target.getName()).append("id", target.getId()));

        Document result = new Document("_id", id).append("value", new Document("min", min).append("max", max));

        scoreDatabase.getCollection("min_max_score").replaceOne(new Document("_id", id), result);
    }

    private void queryMinMax(String projectId, Target target, List<String> studentIds, Value<Double> min, Value<Double> max) {
        MongoCollection<Document> totalScores = scoreDatabase.getCollection("total_score");
        for (String studentId : studentIds) {

            Document query = new Document("_id.projectId", projectId)
                    .append("_id.range.name", Range.STUDENT).append("_id.range.id", studentId)
                    .append("_id.target.name", target.getName()).append("_id.target.id", target.getId());

            Document totalScoreDoc = totalScores.find(query).first();
            if (totalScoreDoc == null) {
                throw new IllegalStateException("无法找到分数：" + query.toJson());
            }

            double totalScore = ((Document) totalScoreDoc.get("value")).getDouble("totalScore");
            if (totalScore < min.get()) {
                min.set(totalScore);
            } else if (totalScore > max.get()) {
                max.set(totalScore);
            }
        }
    }

    // 查询任务涵盖的学生列表
    @SuppressWarnings("unchecked")
    private List<String> getStudentList(String projectId, String subjectId, Range range) {
        MongoCollection<Document> students = scoreDatabase.getCollection("student_list");

        List<String> studentIds = new ArrayList<>();

        FindIterable<Document> studentLists = students.find(
                new Document("_id.projectId", projectId)
                        .append("_id.subjectId", subjectId)
                        .append("_id." + range.getName(), range.getId())
        );

        studentLists.forEach((Consumer<Document>) doc -> {
            List<String> studentIdsPart = (List<String>) ((Document) doc.get("value")).get("studentIds");
            studentIds.addAll(studentIdsPart);
        });
        return studentIds;
    }

    // 取任务信息中的科目ID
    private String getSubjectId(Target target) {
        return targetService.getTargetSubjectId(target);
    }
}
