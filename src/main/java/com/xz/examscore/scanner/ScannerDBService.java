package com.xz.examscore.scanner;

import com.hyd.simplecache.utils.MD5;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.xz.ajiaedu.common.lang.NumberUtil;
import com.xz.ajiaedu.common.lang.StringUtil;
import com.xz.ajiaedu.common.mongo.DocumentUtils;
import com.xz.ajiaedu.common.score.ScorePattern;
import com.xz.examscore.services.*;
import org.apache.commons.lang.BooleanUtils;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static com.xz.ajiaedu.common.mongo.MongoUtils.$in;
import static com.xz.ajiaedu.common.mongo.MongoUtils.doc;

/**
 * (description)
 * created at 16/06/16
 *
 * @author yiding_he
 */
@Service
public class ScannerDBService {

    static final Logger LOG = LoggerFactory.getLogger(ScannerDBService.class);

    @Autowired
    MongoClient scannerMongoClient;

    @Autowired
    MongoClient scannerMongoClient2;

    @Autowired
    MongoClient scannerMongoClient3;

    @Autowired
    MongoDatabase scoreDatabase;

    @Autowired
    StudentService studentService;

    @Autowired
    QuestService questService;

    @Autowired
    ImportProjectService importProjectService;

    @Autowired
    SubjectService subjectService;

    @Autowired
    ScoreService scoreService;

    public MongoClient getMongoClient(String project) {
        Document projectDoc = scannerMongoClient.getDatabase("project_database")
                .getCollection("project").find(doc("projectId", project)).first();
        Document projectDoc2 = scannerMongoClient2.getDatabase("project_database")
                .getCollection("project").find(doc("projectId", project)).first();
        Document projectDoc3 = scannerMongoClient3.getDatabase("project_database")
                .getCollection("project").find(doc("projectId", project)).first();
        if(null != projectDoc){
            return scannerMongoClient;
        }
        if(null != projectDoc2){
            return scannerMongoClient2;
        }
        if(null != projectDoc3){
            return scannerMongoClient3;
        }
        throw new IllegalArgumentException("查找不到项目的网阅数据源：{}" + project);
    }

    public Document findProject(String project) {
        return getMongoClient(project).getDatabase("project_database")
                .getCollection("project").find(doc("projectId", project)).first();
    }

    /**
     * 从网阅数据库中导入成绩
     *
     * @param project 项目ID
     */
    public void importProjectScore(String project) {

        Document projectDoc = findProject(project);
        if (projectDoc == null) {
            LOG.error("没有找到项目" + project);
            return;
        }

        Document subjectCodes = (Document) projectDoc.get("subjectcodes");
        List<String> subjectIds = new ArrayList<>(subjectCodes.keySet());
        //导入分数任务列表
        List<ImportSubjectScoreTask> tasks = new ArrayList<>();

        boolean result = doImportAllSubjectScore(project, subjectIds, tasks);

        if (result) {
            LOG.info("导入分数包完成！");
        } else {
            throw new IllegalStateException("导入分数包出现异常，请核查问题后重新导入！");
        }
    }

    public boolean doImportAllSubjectScore(String project, List<String> subjectIds, List<ImportSubjectScoreTask> tasks) {
        for (String subjectId : subjectIds) {
            ImportSubjectScoreTask task = runImportSubjectScore(project, subjectId);
            tasks.add(task);
        }

        boolean success = true;
        for (ImportSubjectScoreTask task : tasks) {
            try {
                task.join();
                if (!task.isSuccess()) {
                    success = false;
                    LOG.error("导入考试项目{}的科目{}的分数包出现异常！", task.getProject(), task.getSubjectId());
                }
            } catch (InterruptedException e) {
                LOG.error("等待导入线程结束失败", e);
            }
        }

        return success;
    }

    //执行并返回导入分数任务
    private ImportSubjectScoreTask runImportSubjectScore(String project, String subjectId) {
        ImportSubjectScoreTask task = new ImportSubjectScoreTask(project, subjectId);
        task.start();
        return task;
    }

    public boolean doImportSubjectScore(String project, String subjectId) {
        boolean success = true;
        try {
            importSubjectScore0(project, subjectId);
        } catch (Exception e) {
            LOG.error("导入科目" + subjectId + "分数失败", e);
            success = false;
        }
        return success;
    }

    /**
     * 从网阅数据库中导入成绩
     *
     * @param project   项目ID
     * @param subjectId 科目ID
     */
    public void importSubjectScore0(String project, String subjectId) {
        String dbName = project + "_" + subjectId;
        LOG.info("导入 " + dbName + " 的成绩...");
        MongoCollection<Document> collection = getMongoClient(project).getDatabase(dbName).getCollection("students");
        AtomicInteger counter = new AtomicInteger();
        collection.find(doc()).forEach(
                (Consumer<Document>) doc -> importStudentScore(project, subjectId, doc, counter));

        LOG.info("已完成导入科目{}的{}名学生...", subjectId, counter.get());
    }

    /**
     * @param projectId 项目ID
     * @param subjectId 网阅数据库科目ID
     * @param document  网阅数据库学生信息
     * @param counter   计数器
     */
    public void importStudentScore(String projectId, String subjectId, Document document, AtomicInteger counter) {
        String studentId = document.getString("studentId");
        Document student = studentService.findStudent(projectId, studentId);

        if (student == null) {
            throw new IllegalStateException("找不到项目 " + projectId + " 的考生 " + studentId);
        }

        List<String> subjectList = importProjectService.separateSubject(subjectId);
        scoreDatabase.getCollection("score").deleteMany(
                doc("project", projectId).append("student", studentId).append("subject", $in(subjectList))
        );
        scoreDatabase.getCollection("score").deleteMany(
                doc("project", projectId).append("student", studentId).append("subject", subjectId)
        );

        //查询学生该科目是否作弊
        boolean isCheating = isCheating(document);
        if (isCheating) {
            LOG.info("该学生{}在科目{}的考试中存在作弊标记，将主观题和客观题分数改为0分", studentId, subjectId);
        }

        saveObjectiveScores(projectId, subjectId, document, student, isCheating);
        saveSubjectiveScores(projectId, subjectId, document, student, isCheating);

        if (counter.incrementAndGet() % 100 == 0) {
            LOG.info("已导入科目 " + subjectId + " 的 " + counter.get() + " 名学生...");
        }
    }

    private boolean isCheating(Document document) {
        return BooleanUtils.toBoolean(document.getBoolean("isCheating"));
    }


    private void fixMissingSubjectQuest(List<Document> subQuestList, List<Document> subjectiveList) {

        List<String> subjectiveIds = subjectiveList.stream().map(subQuestItem -> subQuestItem.getString("questionNo")).collect(Collectors.toList());

        //统计库题目ID列表
        List<String> subQuestIds = subQuestList.stream().map(subQuestItem -> subQuestItem.getString("questNo")).collect(Collectors.toList());


        for (int i = 0; i < subQuestIds.size(); i++) {
            //判断是否网阅题目ID中存在遗漏
            if (!subjectiveIds.contains(subQuestIds.get(i))) {
                Document subQuest = subQuestList.get(i);
                subjectiveList.add(
                        doc("questionNo", subQuest.getString("questNo"))
                                .append("score", 0)
                                .append("fullScore", subQuest.getDouble("score"))
                                .append("missing", true)
                );
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void saveSubjectiveScores(String projectId, String subjectId, Document document, Document student, boolean isCheating) {
        List<Document> subjectiveList = (List<Document>) document.get("subjectiveList");
        Boolean isAbsent = document.getBoolean("isAbsent");
        String studentId = student.getString("student");
        if (null != isAbsent) {
            LOG.info("该学生{}的考试科目{}为缺考状态！所以主观题得分为0", studentId, subjectId);
        }
        //网阅题目ID列表
        if (null == subjectiveList || subjectiveList.isEmpty()) {
            LOG.info("该学生{}网阅主观题列表为空，该学生是否有客观题和主观题得分！", studentId);
            return;
        }
        //获取统计集合中主观题信息
/*        List<Document> subQuestList = new ArrayList<>();

        //获取拆分后所有综合科目的题目列表（quest_list）
        List<String> subjectIds = importProjectService.separateSubject(subjectId);
        subjectIds.forEach(s -> {
            List<Document> subList = questService.getQuests(projectId, s, false);
            subQuestList.addAll(subList);
        });*/

        List<Document> subQuestList = questService.getQuests(projectId, subjectId, false);
        //对于统计集合中有的，但是网阅数据中没有的数据，则插入一条记录，并标识missing=true
        fixMissingSubjectQuest(subQuestList, subjectiveList);

        for (Document subjectiveItem : subjectiveList) {
            String questionNo = subjectiveItem.getString("questionNo");
            double score = Double.parseDouble(subjectiveItem.get("score").toString());
            double fullScore = Double.parseDouble(subjectiveItem.get("fullScore").toString());
            Boolean missing = subjectiveItem.getBoolean("missing");
            //主观题学生作答的切图所在的URL
            Map<String, Object> url = (Map<String, Object>) subjectiveItem.get("url");
            String sid = getSubjectIdInQuestList(projectId, questionNo, subjectId);
            //如果该生作弊或缺考，则主观题得分为0
            Document scoreDoc = doc("project", projectId)
                    .append("subject", sid)
                    .append("questNo", questionNo)
                    .append("score", isCheating || (null != isAbsent && isAbsent) ? 0d : score)
                    .append("right", !(isCheating || (null != isAbsent && isAbsent)) && NumberUtil.equals(score, fullScore))
                    .append("isObjective", false)
                    .append("student", student.getString("student"))
                    .append("class", student.getString("class"))
                    .append("school", student.getString("school"))
                    .append("area", student.getString("area"))
                    .append("city", student.getString("city"))
                    .append("province", student.getString("province"))
                    .append("url", url)
                    .append("md5", MD5.digest(UUID.randomUUID().toString()));
            if (null != missing && missing) {
                scoreDoc.append("missing", true);
            }
            //如果有缺考标记，则标记缺考
            if (null != isAbsent) {
                scoreDoc.append("isAbsent", isAbsent);
            }

            scoreDatabase.getCollection("score").insertOne(scoreDoc);
        }
    }

    @SuppressWarnings("unchecked")
    private void saveObjectiveScores(String projectId, String subjectId, Document document, Document student, boolean isCheating) {
        List<Document> objectiveList = (List<Document>) document.get("objectiveList");
        Boolean isAbsent = document.getBoolean("isAbsent");
        String studentId = student.getString("student");
        if (null != isAbsent) {
            LOG.info("该学生{}的考试科目{}为缺考状态！所以客观题得分为0", studentId, subjectId);
        }
        //网阅题目ID列表
        if (null == objectiveList || objectiveList.isEmpty()) {
            LOG.info("该学生{}网阅客观题列表为空，对该学生进行数据修补，确保客观题结构存在", studentId);
            fixMissingObjectiveQuest(projectId, subjectId, isAbsent, student);
            return;
        }

        for (Document objectiveItem : objectiveList) {

            String questionNo = objectiveItem.getString("questionNo");
            String sid = getSubjectIdInQuestList(projectId, questionNo, subjectId);
            Document quest = questService.findQuest(projectId, sid, questionNo);
            if (null == quest) {
                LOG.error("网阅客观题题号在quest_list中查找不到对应题目，projectId={}, subjectId={}, sid={}, questionNo={}", projectId, subjectId, sid, questionNo);
                throw new IllegalArgumentException("获取quest_list题号失败！统计失败");
            }
            double fullScore = getFullScore(quest, objectiveItem);
            String studentAnswer = objectiveItem.getString("answerContent").toUpperCase();
            //标准答案数据从统计数据库的quest_list中获取
            //String standardAnswer = objectiveItem.getString("standardAnswer").toUpperCase();
            String standardAnswer = getStdAnswerFromQuest(objectiveItem, quest);

            //对于没有缺考的学生，客观题如果作答为空字符串，才报错判定为没有作答
            if (null == isAbsent && StringUtil.isBlank(studentAnswer)) {
                throw new IllegalStateException("客观题没有考生作答, project=" +
                        projectId + ", subject=" + sid + ", quest=" + objectiveItem);
            }

            if (StringUtil.isBlank(standardAnswer)) {
                throw new IllegalStateException("客观题没有标准答案, project=" +
                        projectId + ", subject=" + sid + ", quest=" + objectiveItem);
            }

            Boolean awardScoreTag = quest.getBoolean("awardScoreTag");

            ScoreAndRight scoreAndRight = calculateScore(fullScore, standardAnswer, studentAnswer, awardScoreTag);

            //如果学生作弊，则客观题的得分为0
            Document scoreDoc = doc("project", projectId)
                    .append("subject", sid)
                    .append("questNo", questionNo)
                    .append("score", isCheating || (null != isAbsent && isAbsent) ? 0d : scoreAndRight.score)
                    .append("answer", studentAnswer)
                    .append("right", !(isCheating || (null != isAbsent && isAbsent)) && scoreAndRight.right)
                    .append("isObjective", true)
                    .append("student", student.getString("student"))
                    .append("class", student.getString("class"))
                    .append("school", student.getString("school"))
                    .append("area", student.getString("area"))
                    .append("city", student.getString("city"))
                    .append("province", student.getString("province"))
                    .append("md5", MD5.digest(UUID.randomUUID().toString()));

            if (null != isAbsent) {
                scoreDoc.append("isAbsent", isAbsent);
            }

            scoreDatabase.getCollection("score").insertOne(scoreDoc);
        }
    }

    //修正客观题列表
    private void fixMissingObjectiveQuest(String projectId, String subjectId, Boolean isAbsent, Document student) {
        List<Document> questDocs = questService.getQuests(projectId, subjectId);
        for (Document questDoc : questDocs) {
            Document scoreDoc = doc("project", projectId)
                    .append("subject", subjectId)
                    .append("questNo", questDoc.getString("questNo"))
                    .append("score", 0d)
                    .append("answer", "*")
                    .append("right", false)
                    .append("isObjective", true)
                    .append("student", student.getString("student"))
                    .append("class", student.getString("class"))
                    .append("school", student.getString("school"))
                    .append("area", student.getString("area"))
                    .append("city", student.getString("city"))
                    .append("province", student.getString("province"))
                    .append("md5", MD5.digest(UUID.randomUUID().toString()))
                    .append("quest", questDoc.getString("questId"))
                    .append("missing", true);

            if (null != isAbsent) {
                scoreDoc.append("isAbsent", isAbsent);
            }
            scoreDatabase.getCollection("score").insertOne(scoreDoc);
        }
    }

    public String getSubjectIdInQuestList(String projectId, String questionNo, String subjectId) {
        //如果网阅接口的科目ID为综合科目ID
        if (subjectId.length() != ImportProjectService.SUBJECT_LENGTH) {
            Document q1 = questService.findQuest(projectId, subjectId, questionNo);
            //查询到综合科目ID对应的试题，则返回综合科目ID
            if (null != q1) {
                return q1.getString("subject");
            } else {
                //如果未查到，则该题目肯定对应综合中某一特定科目ID,返回科目ID，确保分数数据对应的科目ID和题目数据科目ID一致
                List<String> subjectIds = importProjectService.separateSubject(subjectId);
                Document q2 = questService.findQuest(projectId, subjectIds, questionNo);
                return q2.getString("subject");
            }
        } else {
            return subjectId;
        }
    }

    //获取标准答案
    private String getStdAnswerFromQuest(Document objectiveItem, Document quest) {
        String standardAnswer = objectiveItem.getString("standardAnswer").toUpperCase();

        Boolean isObjective = quest.getBoolean("isObjective");
        if (isObjective != null && isObjective) {
            if (!StringUtils.isEmpty(quest.getString("scoreRule"))) {
                standardAnswer = quest.getString("scoreRule");
            } else {
                standardAnswer = quest.getString("answer");
            }
        }
        return standardAnswer;
    }

    //////////////////////////////////////////////////////////////

    private double getFullScore(Document quest, Document objectiveItem) {
        if (quest == null) {
            return Double.parseDouble(objectiveItem.getString("fullScore"));
        } else {
            return quest.getDouble("score");
        }
    }

    /**
     * 计算分数
     *
     * @param fullScore      题目满分值
     * @param standardAnswer 题目的标准答案
     * @param answerContent  考生作答
     * @param awardScoreTag  是否为给分题（一律给满分/一律不给分）：true=给满分，false=不给分，null=按照规则给分
     * @return 分数
     */
    protected static ScoreAndRight calculateScore(
            double fullScore, String standardAnswer, String answerContent, Boolean awardScoreTag) {

        //如果给分标记为true，则直接给分
        if (null != awardScoreTag && awardScoreTag) {
            return new ScoreAndRight(fullScore, true);
        }
        //其他情况则根据给分规则来判断
        else {
            if (answerContent.equals(standardAnswer)) {
                return new ScoreAndRight(fullScore, true);
            } else {
                ScorePattern scorePattern = new ScorePattern(standardAnswer, fullScore);
                double score = scorePattern.getScore(answerContent);
                return new ScoreAndRight(score, score > 0);
            }
        }
    }

    private static List<String> parseUsrAnswer(String answerContent) {
        List<String> result = new ArrayList<>();
        for (char c : answerContent.toCharArray()) {
            result.add(new String(new char[]{c}));
        }
        Collections.sort(result);
        return result;
    }

    private static List<String> parseStdAnswer(String standardAnswer) {
        List<String> result = new ArrayList<>(Arrays.asList(standardAnswer.split(",")));
        Collections.sort(result);
        return result;
    }

    //////////////////////////////////////////////////////////////

    public static class ScoreAndRight {

        public double score;

        public boolean right;

        public ScoreAndRight(double score, boolean right) {
            this.score = score;
            this.right = right;
        }
    }

    /////////////////////////////////////////////////////////////

    private class ImportSubjectScoreTask extends Thread {

        private String project;

        private String subjectId;

        private boolean success;

        public ImportSubjectScoreTask(String project, String subjectId) {
            this.project = project;
            this.subjectId = subjectId;
        }

        @Override
        public void run() {
            LOG.info("线程{}开始执行，项目{}，科目{}的分数数据开始导入...", this.getName(), project, subjectId);
            success = doImportSubjectScore(project, subjectId);
        }

        public boolean isSuccess() {
            return success;
        }

        public String getProject() {
            return project;
        }

        public void setProject(String project) {
            this.project = project;
        }

        public String getSubjectId() {
            return subjectId;
        }

        public void setSubjectId(String subjectId) {
            this.subjectId = subjectId;
        }
    }

    //查询学生某以科目的答题切图和留痕
    public Map<String, Object> getStudentCardSlices(String projectId, String subjectId, String studentId) {
        String dbName = projectId + "_" + subjectId;
        //LOG.info("查询考试项目{}，科目{}, 学生{}的答题卡切图信息...", projectId, subjectId, studentId);
        MongoCollection<Document> cardCollection = getMongoClient(projectId).getDatabase(dbName).getCollection("card");
        MongoCollection<Document> studentsCollection = getMongoClient(projectId).getDatabase(dbName).getCollection("students");
        //是否有整张答题卡切图
        boolean hasPaperPosition = true;
        //查找学生的答题卡留痕
        Document studentDoc = studentsCollection.find(doc("studentId", studentId)).first();
        String cardId = DocumentUtils.getString(studentDoc, "cardId", "");
        Document cardDoc = cardCollection.find(doc("cardId", cardId)).first();
        //适配老版本数据结构，如果没有偏移量，则使用新版本数据结构，反之，则使用老版本数据结构
        if (null != studentDoc && null != cardDoc) {
            if (null != cardDoc.get("positions")) {
                LOG.info("无法获取偏移量，使用老版本网阅数据结构");
                List<Document> positions = cardDoc.get("positions", List.class);
                if (positions.isEmpty() || StringUtil.isBlank(DocumentUtils.getString(studentDoc, "paper_positive", ""))) {
                    hasPaperPosition = false;
                }
                return getPreviousCardSlice(studentDoc, cardDoc, hasPaperPosition);
            } else {
                LOG.info("使用新版本网阅数据结构");
                return getCurrentCardSlice(studentDoc, true);
            }
        } else {
            Map<String, Object> resultMap = new HashMap<>();
            resultMap.put("hasPaperPosition", false);
            return resultMap;
        }
    }

    private Map<String, Object> getPreviousCardSlice(Document studentDoc, Document cardDoc, boolean hasPaperPosition) {
        Map<String, Object> resultMap = new HashMap<>();

        resultMap.put("cardId", DocumentUtils.getString(studentDoc, "cardId", ""));
        resultMap.put("paper_positive", DocumentUtils.getString(studentDoc, "paper_positive", ""));
        resultMap.put("paper_reverse", DocumentUtils.getString(studentDoc, "paper_reverse", ""));
        resultMap.put("offset1X", DocumentUtils.getString(studentDoc, "offset1X", ""));
        resultMap.put("offset1Y", DocumentUtils.getString(studentDoc, "offset1X", ""));
        resultMap.put("offset2X", DocumentUtils.getString(studentDoc, "offset1X", ""));
        resultMap.put("offset2Y", DocumentUtils.getString(studentDoc, "offset1X", ""));

        List<Document> objectiveList = studentDoc.get("objectiveList", List.class);
        List<Document> subjectiveList = studentDoc.get("subjectiveList", List.class);

        for (Document doc : objectiveList) {
            String questionNo = doc.getString("questionNo");
            List<Document> rects = getRect(questionNo, cardDoc);
            doc.append("rects", rects);
        }

        for (Document doc : subjectiveList) {
            String questionNo = doc.getString("questionNo");
            List<Document> rects = getRect(questionNo, cardDoc);
            doc.append("rects", rects);
        }

        resultMap.put("objectiveList", objectiveList);
        resultMap.put("subjectiveList", subjectiveList);
        resultMap.put("hasPaperPosition", hasPaperPosition);
        return resultMap;
    }

    //获取题目的坐标信息
    private List<Document> getRect(String questionNo, Document cardDoc) {
        List<Document> rects = new ArrayList<>();
        List<Document> positions = cardDoc.get("positions", List.class);
        positions.forEach(position -> {
            String qNo = position.getString("questionNo");
            if (questionNo.equals(qNo)) {
                rects.addAll(position.get("positionsBeanList", List.class));
            }
        });
        return rects;
    }

    private Map<String, Object> getCurrentCardSlice(Document studentDoc, boolean hasPaperPosition) {
        Map<String, Object> resultMap = new HashMap<>();
        //答题卡ID
        String cardId = DocumentUtils.getString(studentDoc, "cardId", "");
        //答题卡正面
        String paper_positive = DocumentUtils.getString(studentDoc, "paper_positive", "");
        //答题卡反面
        String paper_reverse = DocumentUtils.getString(studentDoc, "paper_reverse", "");

        //客观题信息
        List<Document> objectiveList = studentDoc.get("objectiveList", List.class);
        //主观题作答
        List<Document> subjectiveList = studentDoc.get("subjectiveList", List.class);
        //获取学生作答了的题目（过滤选做题）
        objectiveList.stream().filter(doc -> doc.getBoolean("isEffective")).collect(Collectors.toList());
        subjectiveList.stream().filter(doc -> doc.getBoolean("isEffective")).collect(Collectors.toList());

        resultMap.put("cardId", cardId);
        resultMap.put("offset1X", "");
        resultMap.put("offset1Y", "");
        resultMap.put("offset2X", "");
        resultMap.put("offset2Y", "");
        resultMap.put("paper_positive", paper_positive);
        resultMap.put("paper_reverse", paper_reverse);
        resultMap.put("objectiveList", objectiveList);
        resultMap.put("subjectiveList", subjectiveList);
        resultMap.put("hasPaperPosition", hasPaperPosition);

        return resultMap;
    }
}
