package com.xz.examscore.asynccomponents.aggrtask.impl;

import com.mongodb.client.FindIterable;
import com.xz.ajiaedu.common.lang.DoubleCounterMap;
import com.xz.examscore.asynccomponents.aggrtask.AggrTask;
import com.xz.examscore.asynccomponents.aggrtask.AggrTaskMessage;
import com.xz.examscore.asynccomponents.aggrtask.AggrTaskMeta;
import com.xz.examscore.bean.PointLevel;
import com.xz.examscore.bean.Range;
import com.xz.examscore.bean.SubjectLevel;
import com.xz.examscore.bean.Target;
import com.xz.examscore.services.QuestService;
import com.xz.examscore.services.RangeService;
import com.xz.examscore.services.ScoreService;
import com.xz.examscore.services.StudentService;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 统计 [考生] 的 [知识点] 得分/得分率
 * 统计 [班级/学校] 的 [知识点/知识点-能力层级] 的总分
 * 统计结果都保存到 total_score 集合
 */
@AggrTaskMeta(taskType = "point")
@Component
public class PointTask extends AggrTask {

    static final Logger LOG = LoggerFactory.getLogger(PointTask.class);

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
    protected void runTask(AggrTaskMessage taskInfo) {
        String projectId = taskInfo.getProjectId();
        Range studentRange = taskInfo.getRange();
        String studentId = studentRange.getId();

        Document student = studentService.findStudent(projectId, studentId);
        Range classRange = Range.clazz(student.getString("class"));
        Range schoolRange = Range.school(student.getString("school"));
        Range provinceRange = Range.province(student.getString("province"));

        DoubleCounterMap<String> pointScores = new DoubleCounterMap<>();
        DoubleCounterMap<SubjectLevel> subjectLevelScores = new DoubleCounterMap<>();
        DoubleCounterMap<PointLevel> pointLevelScores = new DoubleCounterMap<>();

        countScores(projectId, studentId, pointScores, subjectLevelScores, pointLevelScores);

        // 统计知识点得分（学生，班级, 学校, 省份累加）
        for (Map.Entry<String, Double> pointScoreEntry : pointScores.entrySet()) {
            Target point = Target.point(pointScoreEntry.getKey());
            double score = pointScoreEntry.getValue();
            scoreService.saveTotalScore(projectId, studentRange, null, point, score, null);
            addTotalScore(projectId, classRange, point, score);
            addTotalScore(projectId, schoolRange, point, score);
            addTotalScore(projectId, provinceRange, point, score);
        }

        // 统计[知识点-能力层级]得分（班级累加，学校累加，省份累加）
        for (Map.Entry<PointLevel, Double> pointLevelEntry : pointLevelScores.entrySet()) {
            Target pointLevel = Target.pointLevel(pointLevelEntry.getKey());
            double score = pointLevelEntry.getValue();
            scoreService.saveTotalScore(projectId, studentRange, null, pointLevel, score, null);
            addTotalScore(projectId, classRange, pointLevel, score);
            addTotalScore(projectId, schoolRange, pointLevel, score);
            addTotalScore(projectId, provinceRange, pointLevel, score);
        }

        // 统计能力层级得分（学生，班级累加，学校累加，省份累加）
        for (Map.Entry<SubjectLevel, Double> levelScoreEntry : subjectLevelScores.entrySet()) {
            Target subjectLevel = Target.subjectLevel(levelScoreEntry.getKey());
            double score = levelScoreEntry.getValue();
            scoreService.saveTotalScore(projectId, studentRange, null, subjectLevel, score, null);
            addTotalScore(projectId, classRange, subjectLevel, score);
            addTotalScore(projectId, schoolRange, subjectLevel, score);
            addTotalScore(projectId, provinceRange, subjectLevel, score);
        }
    }

    private void addTotalScore(String projectId, Range range, Target target, double score) {
        int modifiedCount = scoreService.addTotalScore(projectId, range, target, score);
        LOG.debug("修改成功的记录条数为：{}，参数 project={}, range={}, target={}, score={}", modifiedCount, projectId, range, target, score);
/*        if (modifiedCount == 0 && score != 0) {
            throw new IllegalStateException(
                    String.format("分数累加失败: project=%s, range=%s, target=%s, score=%s",
                            projectId, range, target, score));
        }*/
    }

    @SuppressWarnings("unchecked")
    private void countScores(
            String projectId, String studentId,
            DoubleCounterMap<String> pointScores,
            DoubleCounterMap<SubjectLevel> subjectLevelScores,
            DoubleCounterMap<PointLevel> pointLevelScores) {

        FindIterable<Document> scores = scoreService.getStudentQuestScores(projectId, studentId);

        for (Document scoreDoc : scores) {
            double score = scoreDoc.getDouble("score");
            String subject = scoreDoc.getString("subject");
            String questId = scoreDoc.getString("quest");
            Document quest = questService.findQuest(projectId, questId);

            if (quest == null) {
                LOG.error("找不到题目: project=" + projectId + ", questId=" + questId + ", student=" + studentId);
                continue;
            }

            Map<String, List<String>> points = (Map<String, List<String>>) quest.get("points");
            Set<SubjectLevel> subjectLevels = new HashSet<>();

            // 没有知识点，跳过处理
            if (points == null || points.isEmpty()) {
                continue;
            }

            for (Map.Entry<String, List<String>> pEntry : points.entrySet()) {
                String pointId = pEntry.getKey();
                pointScores.incre(pointId, score);

                for (String level : pEntry.getValue()) {
                    pointLevelScores.incre(new PointLevel(pointId, level), score);
                    subjectLevels.add(new SubjectLevel(subject, level));
                }
            }

            for (SubjectLevel subjectLevel : subjectLevels) {
                subjectLevelScores.incre(subjectLevel, score);
            }
        }
    }
}
