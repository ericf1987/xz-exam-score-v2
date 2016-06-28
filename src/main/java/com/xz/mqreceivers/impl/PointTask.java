package com.xz.mqreceivers.impl;

import com.mongodb.client.FindIterable;
import com.xz.ajiaedu.common.lang.DoubleCounterMap;
import com.xz.bean.PointLevel;
import com.xz.bean.Range;
import com.xz.bean.Target;
import com.xz.mqreceivers.AggrTask;
import com.xz.mqreceivers.Receiver;
import com.xz.mqreceivers.ReceiverInfo;
import com.xz.services.QuestService;
import com.xz.services.RangeService;
import com.xz.services.ScoreService;
import com.xz.services.StudentService;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * 统计 [考生] 的 [知识点] 得分/得分率
 * 统计 [班级/学校] 的 [知识点/知识点-能力层级] 的总分
 * 统计结果都保存到 total_score 集合
 */
@ReceiverInfo(taskType = "point")
@Component
public class PointTask extends Receiver {

    @Autowired
    ScoreService scoreService;

    @Autowired
    RangeService rangeService;

    @Autowired
    StudentService studentService;

    @Autowired
    QuestService questService;

    @SuppressWarnings("unchecked")
    @Override
    protected void runTask(AggrTask aggrTask) {
        String projectId = aggrTask.getProjectId();
        Range studentRange = aggrTask.getRange();
        String studentId = studentRange.getId();

        Document student = studentService.findStudent(projectId, studentId);
        Range classRange = Range.clazz(student.getString("class"));
        Range schoolRange = Range.school(student.getString("school"));

        DoubleCounterMap<String> pointScores = new DoubleCounterMap<>();
        DoubleCounterMap<PointLevel> pointLevelScores = new DoubleCounterMap<>();

        countScores(projectId, studentId, pointScores, pointLevelScores);

        // 学生只统计知识点得分
        for (Map.Entry<String, Double> pointScoreEntry : pointScores.entrySet()) {
            Target point = Target.point(pointScoreEntry.getKey());
            double score = pointScoreEntry.getValue();
            scoreService.saveTotalScore(projectId, studentRange, null, point, score, null);
        }

        // 班级和学校只统计[知识点-能力层级]得分，以累加的方式统计
        for (Map.Entry<PointLevel, Double> pointLevelEntry : pointLevelScores.entrySet()) {
            Target pointLevel = Target.pointLevel(pointLevelEntry.getKey());
            double score = pointLevelEntry.getValue();
            scoreService.addTotalScore(projectId, classRange, pointLevel, score);
            scoreService.addTotalScore(projectId, schoolRange, pointLevel, score);
        }
    }

    @SuppressWarnings("unchecked")
    private void countScores(
            String projectId, String studentId,
            DoubleCounterMap<String> pointScores,
            DoubleCounterMap<PointLevel> pointLevelScores) {

        FindIterable<Document> scores = scoreService.getStudentQuestScores(projectId, studentId);
        for (Document scoreDoc : scores) {
            double score = scoreDoc.getDouble("score");
            String questId = scoreDoc.getString("quest");
            Document quest = questService.findQuest(projectId, questId);
            Map<String, List<String>> points = (Map<String, List<String>>) quest.get("points");

            for (Map.Entry<String, List<String>> pEntry : points.entrySet()) {
                String pointId = pEntry.getKey();
                pointScores.incre(pointId, score);

                for (String level : pEntry.getValue()) {
                    pointLevelScores.incre(new PointLevel(pointId, level), score);
                }
            }
        }
    }
}
