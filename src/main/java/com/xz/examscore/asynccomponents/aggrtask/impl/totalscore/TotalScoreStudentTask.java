package com.xz.examscore.asynccomponents.aggrtask.impl.totalscore;

import com.mongodb.client.AggregateIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.xz.examscore.asynccomponents.aggrtask.AggrTask;
import com.xz.examscore.asynccomponents.aggrtask.AggrTaskMessage;
import com.xz.examscore.asynccomponents.aggrtask.AggrTaskMeta;
import com.xz.examscore.bean.Range;
import com.xz.examscore.bean.SubjectObjective;
import com.xz.examscore.bean.Target;
import com.xz.examscore.services.ImportProjectService;
import com.xz.examscore.services.ScoreService;
import com.xz.examscore.services.StudentService;
import com.xz.examscore.util.DoubleUtils;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

import static com.xz.ajiaedu.common.mongo.MongoUtils.$in;
import static com.xz.ajiaedu.common.mongo.MongoUtils.doc;

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

    private static final Document TOTAL_SCORE_GROUP = new Document()
            .append("_id", null)
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
            } else if (target.match(Target.SUBJECT_OBJECTIVE)){
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

    private void aggrStudentSubjectProjectScores(String projectId, Target target, MongoCollection<Document> c, Range studentRange) {
        String studentId = studentRange.getId();
        Document student = studentService.findStudent(projectId, studentId);

        // 统计单个考生的科目/项目总分
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
