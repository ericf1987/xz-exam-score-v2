package com.xz.examscore.asynccomponents.aggrtask.impl.totalscore;

import com.mongodb.client.AggregateIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.xz.ajiaedu.common.lang.StringUtil;
import com.xz.examscore.asynccomponents.aggrtask.AggrTask;
import com.xz.examscore.asynccomponents.aggrtask.AggrTaskMessage;
import com.xz.examscore.asynccomponents.aggrtask.AggrTaskMeta;
import com.xz.examscore.bean.ProjectConfig;
import com.xz.examscore.bean.Range;
import com.xz.examscore.bean.SubjectObjective;
import com.xz.examscore.bean.Target;
import com.xz.examscore.services.*;
import com.xz.examscore.util.DoubleUtils;
import org.apache.commons.collections.MapUtils;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.xz.ajiaedu.common.mongo.MongoUtils.$in;
import static com.xz.ajiaedu.common.mongo.MongoUtils.doc;
import static com.xz.ajiaedu.common.report.Keys.ScoreLevel.Pass;

@Component
@AggrTaskMeta(taskType = "total_score_student")
public class TotalScoreStudentTask extends AggrTask {

    @Autowired
    private MongoDatabase scoreDatabase;

    @Autowired
    private ImportProjectService importProjectService;

    @Autowired
    private StudentService studentService;

    @Autowired
    private ScoreService scoreService;

    @Autowired
    ProjectConfigService projectConfigService;

    @Autowired
    FullScoreService fullScoreService;

    private static final Document TOTAL_SCORE_GROUP = new Document()
            .append("_id", "$subject")
            .append("totalScore", new Document("$sum", "$score"));

    @Override
    protected void runTask(AggrTaskMessage taskInfo) {

        List<Document> studentList = studentService.getStudentList(taskInfo.getProjectId(), taskInfo.getRange());
        MongoCollection<Document> scoreCollection = scoreDatabase.getCollection("score");

        for (Document document : studentList) {
            String studentId = document.getString("student");
            Range studentRange = Range.student(studentId);

            Target target = taskInfo.getTarget();

            if (target.match(Target.SUBJECT_COMBINATION)) {
                aggrStudentSubjectCombinationScores(taskInfo.getProjectId(), target, scoreCollection, studentRange);
            } else if (target.match(Target.SUBJECT) || target.match(Target.PROJECT)) {
                aggrStudentSubjectProjectScores(taskInfo.getProjectId(), target, scoreCollection, studentRange);
            } else if (target.match(Target.SUBJECT_OBJECTIVE)) {
                aggrStudentSubjectObjectiveScores(taskInfo.getProjectId(), target, scoreCollection, studentRange);
            }
        }
    }

    private void aggrStudentSubjectObjectiveScores(String projectId, Target target, MongoCollection<Document> scoreCollection, Range studentRange) {
        SubjectObjective subjectObjective = target.getId(SubjectObjective.class);
        String studentId = studentRange.getId();
        Document student = studentService.findStudent(projectId, studentId);
        AggregateIterable<Document> aggregate = scoreCollection.aggregate(Arrays.asList(
                doc("$match", doc("project", projectId)
                        .append("student", studentId)
                        .append("subject", subjectObjective.getSubject())
                        .append("isObjective", subjectObjective.isObjective())),
                doc("$group", TOTAL_SCORE_GROUP)
        ));

        Document aggregateResult = aggregate.first();

        if (aggregateResult != null) {
            Double score = DoubleUtils.round(aggregateResult.getDouble("totalScore"));
            Document extra = doc("class", student.get("class")).append("school", student.get("school"))
                    .append("area", student.get("area")).append("city", student.get("city"))
                    .append("province", student.get("province"));
            scoreService.saveTotalScore(projectId, studentRange, target, score, extra);
        }
    }

    private void aggrStudentSubjectCombinationScores(String projectId, Target target, MongoCollection<Document> c, Range studentRange) {
        String studentId = studentRange.getId();
        Document student = studentService.findStudent(projectId, studentId);
        String subjectCombinationId = target.getId().toString();
        List<String> subjectIds = importProjectService.separateSubject(subjectCombinationId);
        // 统计单个考生组合科目的总分
        AggregateIterable<Document> aggregate = c.aggregate(Arrays.asList(
                doc("$match", doc("project", projectId)
                        .append("student", studentId)
                        .append("subject", $in(subjectIds))),
                doc("$group", TOTAL_SCORE_GROUP)
        ));
        Document aggregateResult = aggregate.first();

        if (aggregateResult != null) {
            Double score = DoubleUtils.round(aggregateResult.getDouble("totalScore"));
            Document extra = doc("class", student.get("class")).append("school", student.get("school"))
                    .append("area", student.get("area")).append("city", student.get("city"))
                    .append("province", student.get("province"));
            scoreService.saveTotalScore(projectId, studentRange, target, score, extra);
        }
    }

    public void aggrStudentSubjectProjectScores(String projectId, Target target, MongoCollection<Document> c, Range studentRange) {
        String studentId = studentRange.getId();
        Document student = studentService.findStudent(projectId, studentId);

        // 统计单个考生的科目/项目总分

        if (target.match(Target.PROJECT)) {
            aggrStudentProjectScores(projectId, target, c, studentRange, studentId, student);
        } else {
            aggrStudentSubjectScores(projectId, target, c, studentRange, studentId, student);
        }

    }

    private void aggrStudentProjectScores(String projectId, Target target, MongoCollection<Document> c, Range studentRange, String studentId, Document student) {
        AggregateIterable<Document> aggregate = c.aggregate(Arrays.asList(
                doc("$match", doc("project", projectId)
                        .append("student", studentId)),
                doc("$group", TOTAL_SCORE_GROUP)
        ));

        Document extra = doc("class", student.get("class")).append("school", student.get("school"))
                .append("area", student.get("area")).append("city", student.get("city"))
                .append("province", student.get("province"));

        ProjectConfig projectConfig = projectConfigService.getProjectConfig(projectId);
        double totalScore = 0;
        for (Document document : aggregate) {
            //遍历查询每个科目的分数，相加
            String subjectId = document.getString("_id");
            double total_score = document.getDouble("totalScore");
            //是否需要把单科不及格的分数修正为及格
            if (projectConfig.isFillAlmostPass()) {
                total_score = fillAlmostPass(projectId, total_score, projectConfig, subjectId);
            }
            totalScore += total_score;
        }
        scoreService.saveTotalScore(projectId, studentRange, target, totalScore, extra);
    }

    private double fillAlmostPass(String projectId, double total_score, ProjectConfig projectConfig, String subjectId) {
        Map<String, Object> scoreLevels = projectConfigService.getScoreLevelByConfig(Target.subject(subjectId), projectConfig);
        Map<String, Object> scoreLevelsMap = new HashMap<>();

        //分数等级为得分或者得分率
        String scoreLevelConfig = projectConfig.getScoreLevelConfig();
        //差值修正
        Double offSet = Double.valueOf(projectConfig.getAlmostPassOffset());

        if (!StringUtil.isBlank(scoreLevelConfig) && scoreLevelConfig.equals("score")) {
            scoreLevelsMap.putAll(MapUtils.getMap(scoreLevels, subjectId));
            return fixByScore(total_score, offSet, scoreLevelsMap);
        } else {
            scoreLevelsMap.putAll(scoreLevels);
            return fixByRate(projectId, total_score, offSet, scoreLevelsMap, Target.subject(subjectId));
        }
    }

    private double fixByRate(String projectId, double total_score, Double offSet, Map<String, Object> scoreLevelsMap, Target target) {
        Double rate = Double.valueOf(scoreLevelsMap.get(Pass.name()).toString());
        double fullScore = fullScoreService.getFullScore(projectId, target);

        double max = DoubleUtils.round(fullScore * rate);
        double min = DoubleUtils.round(max - offSet);

        return total_score < max && total_score >= min ? fullScore * rate : total_score;
    }

    private double fixByScore(double total_score, Double offSet, Map<String, Object> scoreLevelsMap) {
        //该科目及格的分数
        Double score = Double.valueOf(scoreLevelsMap.get(Pass.name()).toString());
        double min = DoubleUtils.round(score - offSet);

        return total_score < score && total_score >= min ? score : total_score;
    }

    private void aggrStudentSubjectScores(String projectId, Target target, MongoCollection<Document> c, Range studentRange, String studentId, Document student) {
        AggregateIterable<Document> aggregate = c.aggregate(Arrays.asList(
                doc("$match", doc("project", projectId)
                        .append("student", studentId)
                        .append(target.getName(), target.getId())),
                doc("$group", TOTAL_SCORE_GROUP)
        ));
        Document aggregateResult = aggregate.first();

        // 如果缺考则会导致 aggregate() 没有返回值
        if (aggregateResult != null) {
            Double score = DoubleUtils.round(aggregateResult.getDouble("totalScore"));
            Document extra = doc("class", student.get("class")).append("school", student.get("school"))
                    .append("area", student.get("area")).append("city", student.get("city"))
                    .append("province", student.get("province"));
            scoreService.saveTotalScore(projectId, studentRange, target, score, extra);
        }
    }

}
