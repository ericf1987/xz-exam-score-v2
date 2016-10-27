package com.xz.examscore.asynccomponents.aggrtask.impl;

import com.hyd.simplecache.utils.MD5;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.xz.ajiaedu.common.lang.Value;
import com.xz.examscore.asynccomponents.aggrtask.AggrTask;
import com.xz.examscore.asynccomponents.aggrtask.AggrTaskMessage;
import com.xz.examscore.asynccomponents.aggrtask.AggrTaskMeta;
import com.xz.examscore.bean.Range;
import com.xz.examscore.bean.Target;
import com.xz.examscore.services.*;
import org.apache.commons.collections.MapUtils;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static com.xz.ajiaedu.common.mongo.MongoUtils.doc;
import static com.xz.examscore.util.Mongo.range2Doc;
import static com.xz.examscore.util.Mongo.target2Doc;

/**
 * @author by fengye on 2016/10/24.
 * 本科上线率统计
 */
@Component
@AggrTaskMeta(taskType = "college_entry_level")
public class CollegeEntryLevelTask extends AggrTask{

    static final Logger LOG = LoggerFactory.getLogger(CollegeEntryLevelTask.class);

    @Autowired
    MongoDatabase scoreDatabase;

    @Autowired
    ScoreService scoreService;

    @Autowired
    RankService rankService;

    @Autowired
    StudentService studentService;

    @Autowired
    CollegeEntryLevelService collegeEntryLevelService;

    @Autowired
    RangeService rangeService;

    @Override
    protected void runTask(AggrTaskMessage taskInfo) {
        String projectId = taskInfo.getProjectId();
        Range range = taskInfo.getRange();
        Target projectTarget = taskInfo.getTarget();
        Range provinceRange = rangeService.queryProvinceRange(projectId);

        //获取考试总人数
        int studentCount = studentService.getStudentCount(projectId, range, projectTarget);

        //获取本科上线率（按所有人数排名百分比录取或）
        Map<String, Double> entry_level = collegeEntryLevelService.getEntryLevel(projectId, provinceRange, projectTarget, studentCount);
        //获取当前维度下考试总成绩的排名
        List<Document> scoreMap = rankService.getScoreMap(projectId, range, projectTarget);
        if (scoreMap.isEmpty()) {
            LOG.error("找不到排名信息: project={}, range={}, target={}", projectId, range, projectTarget);
            return;
        }

        // 按照 score 从高到低排序
        scoreMap.sort((doc1, doc2) -> {
            double score1 = doc1.getDouble("score");
            double score2 = doc2.getDouble("score");
            return score1 > score2 ? -1 : score1 < score2 ? 1 : 0;
        });

        Map<Double, Integer> rankMap = new HashMap<>();  // 排名 Map，
        //获取高于本科录取率的分数
        Double baseLineScore = entry_level.get("THREE");
        //过滤未录取的数据
        List<Document> newScoreMap = scoreMap.stream().filter(m -> MapUtils.getDouble(m, "score") >= baseLineScore).collect(Collectors.toList());
        Value<Integer> totalCount = Value.of(0);
        newScoreMap.forEach(d -> totalCount.set(totalCount.get() + d.getInteger("count")));

        //本科录取的总人数
        int onlineCount = totalCount.get();
        //录取人数中的最低分
        double minScore = 0;

        int count = 0, index = 0;
        while (count < onlineCount && index < newScoreMap.size()) {
            Integer scoreCount = newScoreMap.get(index).getInteger("count");
            Double score = newScoreMap.get(index).getDouble("score");
            minScore = score;
            rankMap.put(score, count + 1);
            count += scoreCount;
            index++;
        }

        //删除原有数据
        MongoCollection<Document> entry_level_students = scoreDatabase.getCollection("college_entry_level");
        Document query = doc("project", projectId).append("range", range2Doc(range)).append("target", target2Doc(projectTarget));
        entry_level_students.deleteMany(query);

        Document projection = doc("range", 1).append("totalScore", 1);  // 查询结果取哪几个字段

        //查询当前维度下分数大于最小录取分数的所有学生
        scoreDatabase.getCollection("total_score").find(
                doc("project", projectId)
                        .append("target", target2Doc(projectTarget))
                        .append("range.name", Range.STUDENT)
                        .append(range.getName(), range.getId())
                        .append("totalScore", doc("$gte", minScore))

        ).projection(projection).forEach((Consumer<Document>) document -> {

            Double totalScore = document.getDouble("totalScore");
            if (!rankMap.containsKey(totalScore)) {
                throw new IllegalStateException("找不到分数 " + totalScore + " 的排名");
            }

            Map<String, Object> oneEntryLevel = getOneEntryLevel(totalScore, entry_level);
            Double dValue = totalScore - MapUtils.getDouble(oneEntryLevel, "score");

            int rank = rankMap.get(totalScore);
            String studentId = ((Document) document.get("range")).getString("id");
            Document student = studentService.findStudent(projectId, studentId);

            entry_level_students.insertOne(doc(query)
                    .append("student", studentId)
                    .append("class", student.getString("class"))
                    .append("school", student.getString("school"))
                    .append("totalScore", totalScore).append("rank", rank)
                    .append("college_entry_level", oneEntryLevel)
                    .append("dValue", dValue)
                    .append("md5", MD5.digest(UUID.randomUUID().toString())));
        });
    }

    private Map<String, Object> getOneEntryLevel(Double totalScore, Map<String, Double> entry_level) {
        Map<String, Object> map = new HashMap<>();
        if(totalScore >= entry_level.get("ONE")){
            map.put("level", "ONE");
            map.put("score", entry_level.get("ONE"));
            return map;
        }
        else if(totalScore >= entry_level.get("TWO")){
            map.put("level", "TWO");
            map.put("score", entry_level.get("TWO"));
            return map;
        }
        else if(totalScore >= entry_level.get("THREE")){
            map.put("level", "THREE");
            map.put("score", entry_level.get("THREE"));
            return map;
        } else {
            map.put("level", "THREE");
            map.put("score", entry_level.get("THREE"));
            return map;
        }
    }

}
