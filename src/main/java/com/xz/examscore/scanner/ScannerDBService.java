package com.xz.examscore.scanner;

import com.hyd.simplecache.utils.MD5;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.UpdateResult;
import com.xz.ajiaedu.common.lang.NumberUtil;
import com.xz.ajiaedu.common.lang.StringUtil;
import com.xz.ajiaedu.common.mongo.DocumentUtils;
import com.xz.ajiaedu.common.score.ScorePattern;
import com.xz.examscore.bean.ProjectConfig;
import com.xz.examscore.bean.Range;
import com.xz.examscore.bean.Target;
import com.xz.examscore.cache.ProjectCacheManager;
import com.xz.examscore.services.*;
import org.apache.commons.collections.CollectionUtils;
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
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.xz.ajiaedu.common.mongo.MongoUtils.*;

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
    MongoClient scannerMongoClient_g10;

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

    @Autowired
    ScannerDBExceptionService scannerDBExceptionService;

    @Autowired
    SubjectCombinationService subjectCombinationService;

    @Autowired
    ProjectConfigService projectConfigService;

    @Autowired
    ProjectCacheManager projectCacheManager;

    public MongoClient getMongoClient(String project) {

        MongoClient[] availableClients =
                new MongoClient[]{scannerMongoClient, scannerMongoClient_g10};

        for (MongoClient client : availableClients) {
            if (projectExists(client, project)) {
                return client;
            }
        }

        throw new IllegalArgumentException("查找不到项目的网阅数据源：" + project);
    }

    private boolean projectExists(MongoClient client, String project) {
        return client
                .getDatabase("project_database")
                .getCollection("project")
                .count(doc("projectId", project)) > 0;
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

        projectCacheManager.deleteProjectCache(project);

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

        importStudentCardSlice(project);

    }

    public void importStudentCardSlice(String project) {
        MongoClient mongoClient = getMongoClient(project);
        LOG.info("------开始导入学生试卷留痕信息------");
        List<String> subjects = subjectService.querySubjects(project);
        List<ImportStudentCardSliceTask> tasks = subjects.stream().map(
                subject -> runImportStudentCardSliceTasks(project, subject, mongoClient)
        ).collect(Collectors.toList());

        LOG.info("科目数{}", tasks.size());
        tasks.forEach(task -> LOG.info("任务队列中科目为：{}", task.getSubjectId()));

        for (ImportStudentCardSliceTask task : tasks) {
            try {
                task.join();
            } catch (InterruptedException e) {
                LOG.info("导入试卷留痕出错，科目{}", task.getSubjectId());
                throw new IllegalStateException("导入试卷留痕出现异常，请核查问题后重新导入！");
            }
        }
        LOG.info("------完成导入学生试卷留痕信息------");
    }

    public void importOneSubjectTask(String project, MongoClient mongoClient, String subject) {
        String cardSubjectId = getCombineOrSingle(project, subject);
        LOG.info("当前科目为{}， 答题卡科目为{}", subject, cardSubjectId);
        LOG.info("导入开始...答题卡科目为：{}", SubjectService.getSubjectName(cardSubjectId));
        //根据考试科目获取数据库名
        String dbName = getScannerDBName(project, cardSubjectId);
        MongoCollection<Document> students = mongoClient.getDatabase(dbName).getCollection("students");
        AtomicInteger counter = new AtomicInteger();
        students.find().forEach((Consumer<Document>) student -> doImportStuCardSlice(project, subject, cardSubjectId, student, counter));
        LOG.info("导入完成！答题卡科目为{}， 学生人数{}！", SubjectService.getSubjectName(cardSubjectId), counter.get());
    }

    public boolean existsSubjectDB(MongoClient mongoClient, String projectId, String subjectId) {
        String dbName = getScannerDBName(projectId, subjectId);
        MongoCollection<Document> students = mongoClient.getDatabase(dbName).getCollection("students");
        return students.count() != 0;
    }

    public String getCombineOrSingle(String projectId, String subjectId) {
        ProjectConfig projectConfig = projectConfigService.getProjectConfig(projectId);
        boolean separateCombine = projectConfig.isSeparateCombine();
        //如果该考试项目拆分文理科
        if (separateCombine) {
            ArrayList<String> allSubjectCombinations = subjectCombinationService.getAllSubjectCombinations(projectId);

            for (String subjectCombination : allSubjectCombinations) {
                if (subjectCombination.contains(subjectId)) {
                    return subjectCombination;
                }
            }

            return subjectId;
        } else {
            return subjectId;
        }
    }

    public void doImportStuCardSlice(String project, String subject, String cardSubjectId, Document student, AtomicInteger counter) {
        MongoCollection<Document> collection = scoreDatabase.getCollection("scanner_student_card_slice");

        List<Document> objectiveList = leaveOnlyRect(DocumentUtils.getList(student, "objectiveList", Collections.emptyList()));
        List<Document> subjectiveList = leaveOnlyRect(DocumentUtils.getList(student, "subjectiveList", Collections.emptyList()));

        Document query = doc("project", project).append("subject", subject)
                .append("student", student.getString("studentId"));
        Document questInfo = doc("cardSubjectId", cardSubjectId)
                .append("paper_positive", student.getString("paper_positive"))
                .append("paper_reverse", student.getString("paper_reverse"))
                .append("objectiveList", objectiveList)
                .append("subjectiveList", subjectiveList)
                .append("batchId", student.get("batchId"))
                .append("cardUUID", student.get("cardUUID"))
                .append("asteriskTotal", student.get("asteriskTotal"))
                .append("ossPath", student.get("ossPath"))
                .append("fileBasePath", student.get("fileBasePath"))
                .append("examRoom", student.get("examRoom"))
                .append("cardId", student.get("cardId"))
                .append("examNo", student.get("examNo"));
        UpdateResult result = collection.updateMany(query, $set(questInfo));
        if (result.getMatchedCount() == 0) {
            collection.insertOne(
                    query.append("cardSubjectId", cardSubjectId)
                            .append("paper_positive", student.getString("paper_positive"))
                            .append("paper_reverse", student.getString("paper_reverse"))
                            .append("objectiveList", objectiveList)
                            .append("subjectiveList", subjectiveList)
                            .append("batchId", student.get("batchId"))
                            .append("cardUUID", student.get("cardUUID"))
                            .append("asteriskTotal", student.get("asteriskTotal"))
                            .append("ossPath", student.get("ossPath"))
                            .append("fileBasePath", student.get("fileBasePath"))
                            .append("examRoom", student.get("examRoom"))
                            .append("cardId", student.get("cardId"))
                            .append("examNo", student.get("examNo"))
                            .append("md5", MD5.digest(UUID.randomUUID().toString()))
            );
        }

        if (counter.incrementAndGet() % 1000 == 0) {
            LOG.info("当前科目为：{}， 已完成{}学生的试卷留痕数据导入", SubjectService.getSubjectName(subject), counter.get());
        }
    }

    private List<Document> leaveOnlyRect(List<Document> questList) {
        return questList.stream().map(
                q -> doc("questionNo", q.getString("questionNo"))
                        .append("score", q.get("score"))
                        .append("fullScore", q.get("fullScore"))
                        .append("isEffective", q.getBoolean("isEffective"))
                        .append("rects", q.get("rects"))
        ).collect(Collectors.toList());
    }

    public ImportStudentCardSliceTask runImportStudentCardSliceTasks(String project, String subject, MongoClient mongoClient) {
        ImportStudentCardSliceTask task = new ImportStudentCardSliceTask(project, subject, mongoClient);
        task.start();
        return task;
    }

    class ImportStudentCardSliceTask extends Thread {
        private String project;
        private String subjectId;
        private MongoClient mongoClient;

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

        public MongoClient getMongoClient() {
            return mongoClient;
        }

        public ImportStudentCardSliceTask(String project, String subjectId, MongoClient mongoClient) {
            this.project = project;
            this.subjectId = subjectId;
            this.mongoClient = mongoClient;
        }

        @Override
        public void run() {
            importOneSubjectTask(this.getProject(), this.getMongoClient(), this.getSubjectId());
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
        scannerDBExceptionService.deleteRecord(project, subjectId);
        MongoCollection<Document> collection = getMongoClient(project).getDatabase(dbName).getCollection("students");
        AtomicInteger counter = new AtomicInteger();
        collection.find(doc()).forEach(
                (Consumer<Document>) doc -> importStudentScore(project, subjectId, doc, counter));

        LOG.info("已完成导入科目{}的{}名学生...", subjectId, counter.get());
    }

    /**
     * 导入单科分数数据
     *
     * @param projectId 项目ID
     * @param subjectId 网阅数据库科目ID
     * @param document  网阅数据库学生信息
     * @param counter   计数器
     */
    public void importStudentScore(String projectId, String subjectId, Document document, AtomicInteger counter) {
        String studentId = document.getString("studentId");
        Document student = studentService.findStudent(projectId, studentId);

        if (student == null) {
            String desc = "找不到项目 " + projectId + ", 科目 " + subjectId + " 的考生 " + studentId;
            LOG.error(desc);
            scannerDBExceptionService.recordScannerDBException(projectId, studentId, subjectId, desc);
            return;
        }

        //清除综合科目及其子科目的分数信息
        List<String> subjectList = importProjectService.separateSubject(subjectId);
        scoreDatabase.getCollection("score").deleteMany(
                doc("project", projectId).append("student", studentId).append("subject", $in(subjectList))
        );
        scoreDatabase.getCollection("score").deleteMany(
                doc("project", projectId).append("student", studentId).append("subject", subjectId)
        );

        //罗列出学生成绩无效的条件

        //是否该考生所有试题作答为0分
        boolean isQuestScoreAllZero = isQuestScoreAllZero(projectId, subjectId, document);

        //是否缺少主观题客观题数据
        boolean hasNoQuestScore = hasNoQuestScore(document);

        //判断该学生是否缺考
        boolean isAbsent = isAbsent(document);

        //是否能找到该学生的试卷
        boolean isCheating = isCheating(document);

        //是否考生缺少试卷
        boolean isLost = isLost(document);

        ProjectConfig projectConfig = projectConfigService.getProjectConfig(projectId);

        //是否得分无效
        boolean isScoreInvalid = scoreRecordInvalid(isQuestScoreAllZero, hasNoQuestScore, isAbsent, isCheating, isLost, projectConfig);

        //是否给分题无效
        boolean isAwardQuestInvalid = awardQuestInvalid(isAbsent, isCheating, isLost, projectConfig);

        if (isScoreInvalid || isAwardQuestInvalid) {
            LOG.info("学生ID{}， 科目ID{}， 是否得分为0 {}， 是否缺失主观题和客观题结构 {}， 是否有缺考标记 {}， " +
                            "是否有作弊标记 {}， 是否有却卷标记 {}, 是否得分无效{}，是否给分题无效{}",
                    studentId, subjectId, isQuestScoreAllZero, hasNoQuestScore, isAbsent, isCheating, isLost, isScoreInvalid, isAwardQuestInvalid);
        }

        saveObjectiveScores(projectId, subjectId, document, student, isScoreInvalid, isAwardQuestInvalid);
        saveSubjectiveScores(projectId, subjectId, document, student, isScoreInvalid);

        if (counter.incrementAndGet() % 100 == 0) {
            LOG.info("已导入科目 " + subjectId + " 的 " + counter.get() + " 名学生...");
        }
    }

    /**
     * 给分题是否有效
     *
     * @param isAbsent      是否有缺考标记
     * @param isCheating    是否有作弊标记
     * @param isLost        是否有缺卷标记
     * @param projectConfig 考试配置信息
     * @return
     */
    private boolean awardQuestInvalid(boolean isAbsent, boolean isCheating, boolean isLost, ProjectConfig projectConfig) {
        boolean removeAbsentStudent = projectConfig.isRemoveAbsentStudent();
        boolean removeCheatStudent = projectConfig.isRemoveCheatStudent();

        return (isAbsent && removeAbsentStudent) || (isCheating && removeCheatStudent) || (isLost);
    }

    /**
     * 学生成绩是否有效判定
     *
     * @param isQuestScoreAllZero 是否所有题目得分为0
     * @param hasNoQuestScore     是否缺少主观题和客观题信息
     * @param isAbsent            是否带有缺考标记
     * @param isCheating          是否带有作弊标记
     * @param isLost              是否带有却卷标记
     * @param projectConfig       考试配置参数
     */
    private boolean scoreRecordInvalid(boolean isQuestScoreAllZero, boolean hasNoQuestScore, boolean isAbsent, boolean isCheating, boolean isLost, ProjectConfig projectConfig) {

        //CMS配置 是否排除0分
        boolean removeZeroScores = projectConfig.isRemoveZeroScores();

        boolean removeAbsentStudent = projectConfig.isRemoveAbsentStudent();
        boolean removeCheatStudent = projectConfig.isRemoveCheatStudent();

        return (isQuestScoreAllZero && removeZeroScores) ||
                (hasNoQuestScore) || (isAbsent && removeAbsentStudent) ||
                (isCheating && removeCheatStudent) || (isLost);
    }

    /**
     * 是否全部得分为0
     *
     * @param projectId 考试ID
     * @param subjectId 科目ID
     * @param document  学生分数信息
     * @return 返回结果
     */
    protected boolean isQuestScoreAllZero(String projectId, String subjectId, Document document) {
        boolean objectiveAllZero = isObjectiveAllZero(projectId, subjectId, document);
        boolean subjectiveAllZero = isSubjectiveAllZero(document);
        return objectiveAllZero && subjectiveAllZero;
    }

    /**
     * 有无主观题，客观题分数
     *
     * @param document 学生分数信息
     * @return 返回结果
     */
    protected boolean hasNoQuestScore(Document document) {
        List<Document> objectiveList = DocumentUtils.getList(document, "objectiveList", Collections.emptyList());
        List<Document> subjectiveList = DocumentUtils.getList(document, "subjectiveList", Collections.emptyList());
        return objectiveList.isEmpty() && subjectiveList.isEmpty();
    }

    /**
     * 是否带有缺考标记
     *
     * @param document 学生分数记录
     * @return 返回结果
     */
    protected boolean isAbsent(Document document) {
        return BooleanUtils.toBoolean(document.getBoolean("isAbsent"));
    }

    /**
     * 是否带有作弊标记
     *
     * @param document 学生分数记录
     * @return 返回结果
     */
    protected boolean isCheating(Document document) {
        return BooleanUtils.toBoolean(document.getBoolean("isCheating"));
    }

    /**
     * 是否缺卷
     *
     * @param student 学生分数记录
     * @return 返回结果
     */
    protected boolean isLost(Document student) {
        return BooleanUtils.toBoolean(student.getBoolean("isLost"));
    }

    /**
     * 判断学生是否缺考
     *
     * @param student          网阅学生分数记录
     * @param removeZeroScores 是否把0分计入缺考
     * @param objectiveAllZero 客观题得分是否全部为0
     * @return 是否缺考
     */
    public boolean isAbsent(Document student, boolean removeZeroScores, boolean objectiveAllZero) {
        //是否把0分计入缺考||无网阅分数||有缺考标记 三种条件满足一中则视为缺考

        List<Document> objectiveList = DocumentUtils.getList(student, "objectiveList", Collections.emptyList());
        List<Document> subjectiveList = DocumentUtils.getList(student, "subjectiveList", Collections.emptyList());
        //无网阅分数
        boolean hasNoQuestScore = objectiveList.isEmpty() && subjectiveList.isEmpty();

        //客观题是否作答
//        Predicate<Document> predicate_o = (o) -> o.getString("answerContent").length() != 0 && !o.getString("answerContent").equals("*") && !StringUtil.isBlank(o.getString("answerContent"));
        //主观题不为0分
        Predicate<Document> predicate_s = (s) -> Double.valueOf(s.get("score").toString()) != 0;

        //所有题目得分为0
        boolean allQuestZero = false;
        //有网阅数据&&把0分视为缺考
        if (!hasNoQuestScore && removeZeroScores) {
//            List<Document> objectiveScores = objectiveList.stream().filter(predicate_o).collect(Collectors.toList());
            List<Document> subjectiveScores = subjectiveList.stream().filter(predicate_s).collect(Collectors.toList());
            allQuestZero = objectiveAllZero && subjectiveScores.isEmpty();
        }

        boolean isAbsent = BooleanUtils.toBoolean(student.get("isAbsent", Boolean.class));

        return hasNoQuestScore || allQuestZero || isAbsent;
    }

    /**
     * 判断是不是所有客观题都为0分
     *
     * @param projectId 考试ID
     * @param student   学生分数记录
     */
    public boolean isObjectiveAllZero(String projectId, String subjectId, Document student) {
        List<Document> objectiveList = DocumentUtils.getList(student, "objectiveList", Collections.emptyList());
        boolean isAllZero = true;
        for (Document o : objectiveList) {
            String questionNo = o.getString("questionNo");

            String sid = getSubjectIdInQuestList(projectId, questionNo, subjectId);

            Document quest = questService.findQuest(projectId, sid, questionNo);

            boolean awardScoreTag = BooleanUtils.toBoolean(quest.getBoolean("awardScoreTag"));

            //将学生作答排序
            String answerContent = StringUtil.isBlank(o.getString("answerContent")) ?
                    "*" : o.getString("answerContent");

            String studentAnswer = sortStudentAnswer(answerContent.toUpperCase());

            String standardAnswer = getStdAnswerFromQuest(quest);

            double fullScore = getFullScore(quest, o);

            ScoreAndRight scoreAndRight = calculateScore(fullScore, standardAnswer, studentAnswer, awardScoreTag);

            if (scoreAndRight.score != 0) {
                isAllZero = false;
            }
        }
        return isAllZero;
    }

    /**
     * 主观题是否得分全部为0
     *
     * @param student 学生分数信息
     * @return 返回结果
     */
    public boolean isSubjectiveAllZero(Document student) {
        List<Document> subjectiveList = DocumentUtils.getList(student, "subjectiveList", Collections.emptyList());
        Predicate<Document> predicate_s = (s) -> Double.valueOf(s.get("score").toString()) != 0;
        List<Document> subjectiveScores = subjectiveList.stream().filter(predicate_s).collect(Collectors.toList());
        return subjectiveScores.isEmpty();
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
    private void saveSubjectiveScores(String projectId, String subjectId, Document document, Document student, boolean isScoreInvalid) {
        List<Document> subjectiveList = (List<Document>) document.get("subjectiveList");
        String studentId = student.getString("student");

        //网阅题目ID列表
        if (null == subjectiveList || subjectiveList.isEmpty()) {
            //LOG.info("该学生{}网阅主观题列表为空", studentId);
            return;
        }

        List<Document> subQuestList = questService.getQuests(projectId, subjectId, false);
        //对于统计集合中有的，但是网阅数据中没有的数据，则插入一条记录，并标识missing=true
        fixMissingSubjectQuest(subQuestList, subjectiveList);

        for (Document subjectiveItem : subjectiveList) {
            String questionNo = subjectiveItem.getString("questionNo");
            double score = Double.parseDouble(subjectiveItem.get("score").toString());
            double fullScore = Double.parseDouble(subjectiveItem.get("fullScore").toString());
            boolean missing = BooleanUtils.toBoolean(subjectiveItem.getBoolean("missing"));

            //主观题学生作答的切图所在的URL
            Map<String, Object> url = (Map<String, Object>) subjectiveItem.get("url");
            String sid = getSubjectIdInQuestList(projectId, questionNo, subjectId);

            boolean isEffective = BooleanUtils.toBoolean(subjectiveItem.getBoolean("isEffective"));

            Document scoreDoc = doc("project", projectId)
                    .append("subject", sid)
                    .append("cardSubjectId", subjectId)
                    .append("questNo", questionNo)
                    .append("score", isScoreInvalid || !isEffective ? 0d : score)
                    .append("right", !(isScoreInvalid || !isEffective) && NumberUtil.equals(score, fullScore))
                    .append("isObjective", false)
                    .append("isEffective", isEffective)
                    .append("student", student.getString("student"))
                    .append("class", student.getString("class"))
                    .append("school", student.getString("school"))
                    .append("area", student.getString("area"))
                    .append("city", student.getString("city"))
                    .append("province", student.getString("province"))
                    .append("url", url)
                    .append("md5", MD5.digest(UUID.randomUUID().toString()));
            if (missing) {
                scoreDoc.append("missing", true);
            }
            //如果该学生得分无效，则标记为缺考
            if (isScoreInvalid) {
                scoreDoc.append("isAbsent", isScoreInvalid);
            }
            scoreDatabase.getCollection("score").insertOne(scoreDoc);
        }
    }

    @SuppressWarnings("unchecked")
    private void saveObjectiveScores(String projectId, String subjectId, Document document, Document student, boolean isScoreInvalid, boolean isAwardQuestInvalid) {
        List<Document> objectiveList = DocumentUtils.getList(document, "objectiveList", Collections.emptyList());

        String studentId = student.getString("student");

        //网阅题目ID列表
        if (objectiveList.isEmpty()) {
            //LOG.info("该学生{}网阅客观题列表为空，对该学生进行数据修补，确保客观题结构存在", studentId);
            fixMissingObjectiveQuest(projectId, subjectId, isScoreInvalid, student);
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

            boolean isEffective = BooleanUtils.toBoolean(objectiveItem.getBoolean("isEffective"));

            double fullScore = getFullScore(quest, objectiveItem);

            //将学生作答排序
            String answerContent = StringUtil.isBlank(objectiveItem.getString("answerContent")) ?
                    "*" : objectiveItem.getString("answerContent");

            //将学生作答按照字母升序排序
            String studentAnswer = sortStudentAnswer(answerContent.toUpperCase());

            //标准答案数据从统计数据库的quest_list中获取
            String standardAnswer = getStdAnswerFromQuest(quest);

            if (!isScoreInvalid && StringUtil.isBlank(studentAnswer)) {
                throw new IllegalStateException("客观题没有考生作答, project=" + projectId +
                        ", studentId=" + studentId +
                        ", subject=" + sid +
                        ", objectiveItem=" + objectiveItem +
                        ", quest=" + quest +
                        ", isScoreInvalid=" + isScoreInvalid);
            }

            if (StringUtil.isBlank(standardAnswer)) {
                throw new IllegalStateException("客观题没有标准答案, project=" + projectId +
                        ", studentId=" + studentId +
                        ", standardAnswer=" + standardAnswer +
                        ", subject=" + sid +
                        ", objectiveItem=" + objectiveItem +
                        ", quest=" + quest
                );
            }

            //如果该学生给分题无效 则取消送分规则
            boolean awardScoreTag = BooleanUtils.toBoolean(quest.getBoolean("awardScoreTag")) && !isAwardQuestInvalid;

            ScoreAndRight scoreAndRight = calculateScore(fullScore, standardAnswer, studentAnswer, awardScoreTag);

            //如果该学生得分无效，则客观题的得分为0
            Document scoreDoc = doc("project", projectId)
                    .append("subject", sid)
                    .append("cardSubjectId", subjectId)
                    .append("questNo", questionNo)
                    .append("score", isScoreInvalid || !isEffective ? 0d : scoreAndRight.score)
                    .append("answer", studentAnswer)
                    .append("right", !(isScoreInvalid || !isEffective) && scoreAndRight.right)
                    .append("isObjective", true)
                    .append("isEffective", isEffective)
                    .append("student", student.getString("student"))
                    .append("class", student.getString("class"))
                    .append("school", student.getString("school"))
                    .append("area", student.getString("area"))
                    .append("city", student.getString("city"))
                    .append("province", student.getString("province"))
                    .append("md5", MD5.digest(UUID.randomUUID().toString()));

            if (isScoreInvalid) {
                scoreDoc.append("isAbsent", isScoreInvalid);
            }
            scoreDatabase.getCollection("score").insertOne(scoreDoc);
        }
    }

    public String sortStudentAnswer(String studentAnswer) {
        char[] c = studentAnswer.toCharArray();
        Arrays.sort(c);
        return new String(c);
    }

    //将标答进行排序A1B1DA2排序成A1B1AD2
    public String sortStdAnswer(String stdAnswer) {

        if (StringUtil.isEmpty(stdAnswer)) {
            return "";
        }

        StringBuilder builder = new StringBuilder();
        //匹配数字
        Pattern p_number = Pattern.compile("\\d+");

        //匹配字母
        Pattern p_char = Pattern.compile("[a-zA-Z]+");

        //["", "1", "1", "1"]
        String[] numbers = p_char.split(stdAnswer);
        //["B", "D", "DA"]
        String[] chars = p_number.split(stdAnswer);

        numbers = Arrays.stream(numbers)
                .filter(s -> (s != null && s.length() > 0))
                .toArray(String[]::new);

        chars = Arrays.stream(chars)
                .filter(s -> (s != null && s.length() > 0))
                .toArray(String[]::new);

        if (numbers.length == 0) {
            return stdAnswer;
        } else {
            for (int i = 0; i < numbers.length; i++) {
                builder.append(sortStudentAnswer(chars[i])).append(numbers[i]);
            }
            return builder.toString();
        }
    }


    //修正客观题列表
    private void fixMissingObjectiveQuest(String projectId, String subjectId, Boolean studentRecordEffective, Document student) {
        //获取所有客观的题列表
        List<Document> questDocs = questService.getQuests(projectId, subjectId, true);
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

            if (null != studentRecordEffective) {
                scoreDoc.append("isAbsent", studentRecordEffective);
            }
            scoreDatabase.getCollection("score").insertOne(scoreDoc);
        }
    }

    //针对网阅库中题目的综合科目ID和CMS题目的科目ID不一致的情况，做出的修正
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
    public String getStdAnswerFromQuest(Document quest) {

        boolean isObjective = BooleanUtils.toBoolean(quest.getBoolean("isObjective"));
        if (isObjective) {
            return !StringUtils.isEmpty(quest.getString("scoreRule")) ?
                    quest.getString("scoreRule") : quest.getString("answer");
        }
        throw new IllegalArgumentException("获取试题" + quest.getString("questId") + "的标答出现异常，请核实试题类型和标答");
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
        MongoCollection<Document> collection = scoreDatabase.getCollection("scanner_student_card_slice");
        Document query = doc("project", projectId).append("student", studentId).append("subject", subjectId);
        Document document = collection.find(query).first();

        Map<String, Object> map = new HashMap<>();

        ProjectConfig projectConfig = projectConfigService.getProjectConfig(projectId);

        if(!projectConfig.isAllowPaperMark()){
            map.put("hasPaperPosition", false);
            return map;
        }

        if (null != document) {

            List<Document> objectiveList = fixFullScore(projectId, subjectId, document.get("objectiveList", List.class));
            List<Document> subjectiveList = fixFullScore(projectId, subjectId, document.get("subjectiveList", List.class));

            List<Document> newObjectiveList = objectiveList.stream().filter(doc -> doc.getBoolean("isEffective")).collect(Collectors.toList());
            List<Document> newSubjectiveList = subjectiveList.stream().filter(doc -> doc.getBoolean("isEffective")).collect(Collectors.toList());

            map.put("studentId", studentId);
            map.put("paper_positive", DocumentUtils.getString(document, "paper_positive", ""));
            map.put("paper_reverse", DocumentUtils.getString(document, "paper_reverse", ""));
            map.put("objectiveList", newObjectiveList);
            map.put("subjectiveList", newSubjectiveList);
            map.put("totalScore", scoreService.getScore(projectId, Range.student(studentId), Target.subject(subjectId)));
            //只要主观题和客观题有一项目为空，则返回有数据坐标
            map.put("hasPaperPosition", CollectionUtils.isNotEmpty(newObjectiveList) || CollectionUtils.isNotEmpty(newSubjectiveList));
        } else {
            map.put("hasPaperPosition", false);
        }
        return map;
    }

    private List<Document> fixFullScore(String projectId, String subjectId, List<Document> questList) {
        for (Document questDoc : questList) {
            Document quest = questService.findQuest(projectId, subjectId, questDoc.getString("questionNo"));
            double fullScore = DocumentUtils.getDouble(quest, "score", 0);
            questDoc.put("fullScore", fullScore);
        }
        return questList;
    }

    public String getScannerDBName(String projectId, String subjectId) {
        return projectId + "_" + subjectId;
    }

}
