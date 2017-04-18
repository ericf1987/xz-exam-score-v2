package com.xz.examscore.services;

import com.hyd.simplecache.SimpleCache;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoDatabase;
import com.xz.examscore.XzExamScoreV2ApplicationTests;
import com.xz.examscore.api.server.customization.StudentEvaluationByRankAnalysis;
import com.xz.examscore.bean.PointLevel;
import com.xz.examscore.bean.Range;
import com.xz.examscore.bean.SubjectObjective;
import com.xz.examscore.bean.Target;
import com.xz.examscore.paperScreenShot.service.PaintService;
import com.xz.examscore.util.Mongo;
import org.bson.Document;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static com.xz.ajiaedu.common.mongo.MongoUtils.doc;
import static com.xz.ajiaedu.common.mongo.MongoUtils.toList;
import static com.xz.examscore.util.Mongo.target2Doc;

/**
 * (description)
 * created at 16/05/13
 *
 * @author yiding_he
 */
public class ScoreServiceTest extends XzExamScoreV2ApplicationTests {

    public static final String PROJECT = "430100-a05db0d05ad14010a5c782cd31c0283f";

    @Autowired
    ScoreService scoreService;

    @Autowired
    MongoDatabase scoreDatabase;

    @Autowired
    StudentService studentService;

    @Autowired
    RankService rankService;

    @Autowired
    ClassService classService;

    @Autowired
    PointService pointService;

    @Autowired
    PaintService packScoreData;

    @Autowired
    SimpleCache cache;

    @Autowired
    ProvinceService provinceService;

    @Autowired
    StudentEvaluationByRankAnalysis studentEvaluationByRankAnalysis;

    @Test
    public void testGetTotalScore() throws Exception {
        double score = scoreService.getScore("430500-858a2da0e24f4c329aafb9071e022e3b",
                Range.student("d86cd293-ecff-41a8-ae8e-3700e24fcddd"),
                Target.quest("58b39c412d5602875578ea32"));

        System.out.println(score);
    }

    @Test
    public void testGetQuestCorrectCount() throws Exception {
        int count = scoreService.getQuestCorrectCount(
                "430200-89c9dc7481cd47a69d85af3f0808e0c4",
                "57403a032d560287556b90d4",
                Range.school("7e34fa5e-9023-4ad4-b4fa-fe4e3d7d1b52")
        );

        System.out.println(count);
    }

    @Test
    public void testGetSubjectLevelTotalScore() throws Exception {
        Range range = Range.clazz("a1895cd9-d82c-4b12-a698-164fb5ceb1f3");
        double totalScore = scoreService.getScore(PROJECT, range, Target.subjectLevel("003", "B"));
        System.out.println(totalScore);
    }

    @Test
    public void testAddTotalScore() throws Exception {
        String projectId = "430300-672a0ed23d9148e5a2a31c8bf1e08e62";
        Range range = Range.clazz("747f3d0f-e108-4cc8-95ea-f0293b7cfc41");
        PointLevel pointLevel = new PointLevel("1019056", "A");
        Target target = Target.pointLevel(pointLevel);
        int modifiedCount = scoreService.addTotalScore(projectId, range, target, 1.1);
        System.out.println(modifiedCount);
    }

    @Test
    public void testGetScore() throws Exception {
        String projectId = "430900-8f11fe8dbac842a3805d45e05eb31095";
        Range range = Range.student("5eaf29f7-c9a6-47e1-a73e-9c91462f5de6");
        SubjectObjective subjectObjective = new SubjectObjective("005", true);
        Target target = Target.subjectObjective(subjectObjective);
        System.out.println(projectId + "|" + range.toString() + "|" + target.toString());
        System.out.println(Mongo.target2Doc(target).toString());
        double d = scoreService.getScore(projectId, range, target);
        System.out.println(d);
    }

    @Test
    public void testGetCountByScore() throws Exception {
        String projectId = "430300-672a0ed23d9148e5a2a31c8bf1e08e62";
        Range range = Range.school("c99a630b-d8e6-4758-b27d-4b062f9fec0a");
        Target target = Target.project(projectId);
        int count = scoreService.getCountByScore(projectId, range, target, 800);
        System.out.println(count);
    }

    @Test
    public void testGetRankByScore() throws Exception {
        String projectId = "430200-5446510d585c40c0a226a717a9d4cb2b";
        double score = 89.5d;
        String subjectId = "003";
        Target target = Target.subject(subjectId);
        String collectionName = scoreService.getTotalScoreCollection(projectId, target);
        Document query = doc("project", projectId).append("range.name", Range.STUDENT)
                .append("target", target2Doc(target))
                .append("totalScore", score);
        FindIterable<Document> documents = scoreDatabase.getCollection(collectionName).find(query);
        toList(documents).forEach(document -> {
            Document studentDoc = (Document) document.get("range");
            String studentId = studentDoc.getString("id");
            Document doc = studentService.findStudent(projectId, studentId);
            int rank = rankService.getRank(projectId, Range.clazz(doc.getString("class")), target, studentId);
            System.out.println("班级：" + classService.getClassName(projectId, doc.getString("class")) + "，学生姓名：" + doc.getString("name") + ", 排名：" + rank);
        });
    }

    @Test
    public void testGetStudentScores() throws Exception {
        String projectId = "430200-3e67c524f149491597279ef6ae31baef";
        String studentId = "00708600-9b39-49ad-a8e5-80f7aaa4cb1f";
        List<Document> studentScores = scoreService.getStudentQuestScores(projectId, studentId);
        System.out.println(studentScores);
    }

    @Test
    public void testGetCountByScoreSpan() throws Exception {
        String projectId = "430300-32d8433951ce43cab5883abff77c8ea3";
        Range range = Range.school("15e70531-5ac0-475d-a2da-2fc04242ac75");
        Target target = Target.project(projectId);
        int countByScoreSpan = scoreService.getCountByScoreSpan(projectId, range, target, 1000, 0);
        int schoolCount = studentService.getStudentCount(projectId, range);
        System.out.println(countByScoreSpan);
        System.out.println(schoolCount);
    }

    @Test
    public void testGetScoreByCacheKey() throws Exception {
        String projectId = "430500-858a2da0e24f4c329aafb9071e022e3b";
        String studentId = "d86cd293-ecff-41a8-ae8e-3700e24fcddd";
        String subjectId = "003";
        Range range = Range.student(studentId);
        Target target = Target.subject(subjectId);
        String collectionName = scoreService.getTotalScoreCollection(projectId, target);
        String cacheKey = "score:" + collectionName + ":" + projectId + ":" + range + ":" + target;
        System.out.println(cacheKey);
        cache.get(cacheKey);
    }

    @Test
    public void testgetErrorQuestNo() throws Exception {
        String projectId = "430300-9cef9f2059ce4a36a40a7a60b07c7e00";
//        String studentId = "d86cd293-ecff-41a8-ae8e-3700e24fcddd";
        String studentId = "0da6026e-325e-4d7b-89a2-7fe28669eb17";
        String subjectId = "006";

        List<String> errorQuestNo = scoreService.getErrorQuestNo(projectId, studentId, subjectId, true, false);

        List<Double> nos = errorQuestNo.stream().map(Double::valueOf).collect(Collectors.toList());

        Collections.sort(nos);

        List<String> collect = nos.stream().map(packScoreData::packScoreData).collect(Collectors.toList());

        System.out.println(collect);

        System.out.println(nos);

        Collections.sort(errorQuestNo);
        System.out.println(errorQuestNo.toString());
    }

    @Test
    public void testisAbsentStudent() throws Exception {
        String projectId = "430100-5d2142085fc747c9b5b230203bbfd402";
        String studentId = "0b36fc96-6f56-406f-aaad-3077cc907395";
        Target target = Target.project(projectId);

        boolean studentAbsent = scoreService.isStudentAbsent(projectId, studentId, target);
        System.out.println(studentAbsent);
    }

    @Test
    public void testGetStudentIdsByRanks() throws Exception {
//        String projectId = "430300-29c4d40d93bf41a5a82baffe7e714dd9";
        String projectId = "430300-29c4d40d93bf41a5a82baffe7e714dd9";
        Range provinceRange = Range.province(provinceService.getProjectProvince(projectId));
        Target projectTarget = Target.project(projectId);
        double rankScore = rankService.getRankScore(projectId, provinceRange, projectTarget, studentEvaluationByRankAnalysis.getRankByProject(projectId));
        List<Document> listByScore = scoreService.getListByScore(projectId, provinceRange, projectTarget, rankScore);

        Collections.sort(listByScore, (Document d1, Document d2) -> {
            Double totalScore1 = d1.getDouble("totalScore");
            Double totalScore2 = d2.getDouble("totalScore");
            return totalScore2.compareTo(totalScore1);
        });

        List<Double> tt = listByScore.stream().map(l -> l.getDouble("totalScore")).collect(Collectors.toList());
        List<String> ttt = listByScore.stream().map(l -> l.get("range", Document.class).getString("id")).collect(Collectors.toList());

        System.out.println(listByScore.size());
        System.out.println(tt.toString());
        System.out.println(ttt.toString());
        System.out.println(ttt.subList(50 * (6-1), 50 * 6).toString());
    }

    @Test
    public void test2() throws Exception {
        String projectId = "431100-ac367ba398d744d489e9de4ed225b755";
        Range provinceRange = Range.province(provinceService.getProjectProvince(projectId));
        Target projectTarget = Target.project(projectId);
        List<Document> listByScore = scoreService.getListByScore(projectId, provinceRange, projectTarget, 384);
        List<Document> listByScore1 = scoreService.getListByScore(projectId, provinceRange, projectTarget, 448);
        List<Document> listByScore2 = scoreService.getListByScore(projectId, provinceRange, projectTarget, 512);
        System.out.println(listByScore.size());
        System.out.println(listByScore1.size());
        System.out.println(listByScore2.size());

        int studentCount = studentService.getStudentCount(projectId, provinceRange, projectTarget);
        List<String> studentIds = studentService.getStudentIds(projectId, provinceRange, projectTarget);
        System.out.println("总体参考人数：" + studentCount);
        System.out.println(studentIds.size());
    }

    /**
     * 测试修正不及格分数
     * @throws Exception
     */
    @Test
    public void testFixTotalScore() throws Exception {
        String projectId = "430100-354dce3ac8ef4800a1b57f81a10b8baa";
        Target subjectTarget = Target.subject("004005006");
        Range studentRange = Range.student("7fae2420-e5d4-4431-bcec-e4144096f0e5");
        double totalScore = 15;
        Document update = doc("totalScore", totalScore);
        scoreService.fixTotalScoreByProjectConfig(projectId, subjectTarget, update, totalScore);
        System.out.println(update.toString());
    }

    /**
     * 测试保存总分
     * @throws Exception
     */
    @Test
    public void testSaveTotalScore() throws Exception {
        String projectId = "430600-d248e561aefc425b9971f2a26d267478";
        String studentId = "a36fd8a5-d0d8-495f-9682-8f20218d7952";
        Target subjectTarget = Target.subject("003");

        Range studentRange = Range.student(studentId);
        double totalScore = 70;

        Document student = studentService.findStudent(projectId, studentId);
        Document extra = doc("class", student.get("class")).append("school", student.get("school"))
                .append("area", student.get("area")).append("city", student.get("city"))
                .append("province", student.get("province"));

        scoreService.saveTotalScore(projectId, studentRange, subjectTarget, totalScore, extra);
    }

}