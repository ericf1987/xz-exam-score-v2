package com.xz.mqreceivers.impl;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.xz.ajiaedu.common.lang.Value;
import com.xz.bean.Range;
import com.xz.bean.Target;
import com.xz.mqreceivers.AggrTask;
import com.xz.mqreceivers.Receiver;
import com.xz.mqreceivers.ReceiverInfo;
import com.xz.services.ProjectConfigService;
import com.xz.services.RankService;
import com.xz.services.StudentService;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import static com.xz.ajiaedu.common.mongo.MongoUtils.doc;
import static com.xz.util.Mongo.range2Doc;
import static com.xz.util.Mongo.target2Doc;

@ReceiverInfo(taskType = "top_student_list")
@Component
public class TopStudentListTask extends Receiver {

    static final Logger LOG = LoggerFactory.getLogger(TopStudentListTask.class);

    @Autowired
    RankService rankService;

    @Autowired
    StudentService studentService;

    @Autowired
    MongoDatabase scoreDatabase;

    @Autowired
    ProjectConfigService projectConfigService;

    public double getTopStudentRate(String projectId){
        return projectConfigService.getProjectConfig(projectId).getTopStudentRate();
    }

    @Override
    protected void runTask(AggrTask aggrTask) {
        String projectId = aggrTask.getProjectId();
        Range range = aggrTask.getRange();
        Target target = aggrTask.getTarget();

        if (target.match(Target.QUEST)) {
            throw new IllegalStateException("暂不支持统计单个考题的尖子生列表统计");
        }

        List<Document> scoreMap = rankService.getScoreMap(projectId, range, target);
        if (scoreMap.isEmpty()) {
            LOG.error("找不到排名信息: project={}, range={}, target={}", projectId, range, target);
        }

        Value<Integer> totalCount = Value.of(0);
        scoreMap.forEach(d -> totalCount.set(totalCount.get() + d.getInteger("count")));

        // 按照 score 倒序排列
        scoreMap.sort((doc1, doc2) -> {
            double score1 = doc1.getDouble("score");
            double score2 = doc2.getDouble("score");
            return score1 > score2 ? -1 : score1 < score2 ? 1 : 0;
        });

        ////////////////////////////////////////////////////////////// 统计尖子生的得分范围

        Map<Double, Integer> rankMap = new HashMap<>();  // 排名 Map，
        int topCount = Math.max((int) (totalCount.get() * getTopStudentRate(projectId)), 1);  // 尖子生人数
        double maxTopScore = scoreMap.get(0).getDouble("score"), minTopScore = 0;     // 尖子分数列表

        int count = 0, index = 0;
        while (count < topCount && index < scoreMap.size()) {
            Double score = scoreMap.get(index).getDouble("score");
            Integer scoreCount = scoreMap.get(index).getInteger("count");

            minTopScore = score;
            rankMap.put(score, count + 1);
            count += scoreCount;
            index++;
        }

        ////////////////////////////////////////////////////////////// 根据分数范围查询考生ID列表

        MongoCollection<Document> topStudentsList = scoreDatabase.getCollection("top_student_list");
        Document query = doc("project", projectId).append("range", range2Doc(range)).append("target", target2Doc(target));
        topStudentsList.deleteMany(query);

        Document projection = doc("range", 1).append("totalScore", 1);  // 查询结果取哪几个字段

        scoreDatabase.getCollection("total_score").find(
                doc("project", projectId)
                        .append("target", target2Doc(target))
                        .append("range.name", Range.STUDENT)
                        .append(range.getName(), range.getId())
                        .append("totalScore", doc("$lte", maxTopScore).append("$gte", minTopScore))

        ).projection(projection).forEach((Consumer<Document>) document -> {

            ////////////////////////////////////////////////////////////// 查询结果保存到数据库

            Double totalScore = document.getDouble("totalScore");
            if (!rankMap.containsKey(totalScore)) {
                throw new IllegalStateException("找不到分数 " + totalScore + " 的排名");
            }

            int rank = rankMap.get(totalScore);
            String studentId = ((Document) document.get("range")).getString("id");
            Document student = studentService.findStudent(projectId, studentId);

            topStudentsList.insertOne(doc(query)
                    .append("student", studentId)
                    .append("class", student.getString("class"))
                    .append("school", student.getString("school"))
                    .append("score", totalScore).append("rank", rank));
        });
    }
}
