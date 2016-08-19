package com.xz.controllers;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.xz.ajiaedu.common.lang.Context;
import com.xz.ajiaedu.common.lang.Result;
import com.xz.ajiaedu.common.lang.StringUtil;
import com.xz.ajiaedu.common.mongo.MongoUtils;
import com.xz.services.ClassService;
import com.xz.services.ProjectService;
import com.xz.services.SchoolService;
import com.xz.services.StudentService;
import com.xz.util.ChineseName;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static com.xz.ajiaedu.common.lang.RandomUtil.pickRandomWithout;
import static com.xz.ajiaedu.common.mongo.MongoUtils.doc;

/**
 * 模拟数据生成
 * 清空数据命令：<br/>
 * db.project_list.remove({fake:true});<br/>
 * db.school_list.remove({project:/(FAKE_PROJ_.*)/});<br/>
 * db.class_list.remove({project:/(FAKE_PROJ_.*)/});<br/>
 * db.student_list.remove({project:/(FAKE_PROJ_.*)/});<br/>
 * db.subject_list.remove({project:/(FAKE_PROJ_.*)/});<br/>
 */
@Controller
public class FakeDataController {

    static final Logger LOG = LoggerFactory.getLogger(FakeDataController.class);

    private static final Random RANDOM = new Random();

    private static final String[] OPTIONS = {"A", "B", "C", "D"};

    private static final List<String> OPTIONS_LIST = Arrays.asList(OPTIONS);

    private static final int GRADE = 12;

    private static final String QUEST_TYPE_SELECT = "0";

    private static final String QUEST_TYPE_ANSWER = "3";

    private static final String QUESTION_TYPE_SELECT_ID = "29ce182c-3377-478d-966d-c9e0a68f7eff";

    private static final String QUESTION_TYPE_SELECT_NAME = "选择题";

    private static final String QUESTION_TYPE_BLANK_ID = "29ce182c-3377-478d-966d-c9e0a68f7eff";

    private static final String QUESTION_TYPE_BLANK_NAME = "填空题";

    private static ThreadLocal<Integer> idCounter = new ThreadLocal<>();

    private static ThreadLocal<Context> contextThreadLocal = new ThreadLocal<>();

    @Autowired
    ProjectService projectService;

    @Autowired
    SchoolService schoolService;

    @Autowired
    ClassService classService;

    @Autowired
    StudentService studentService;

    @Autowired
    MongoDatabase scoreDatabase;

    @RequestMapping(value = "/fake/data_clear", method = RequestMethod.POST)
    @ResponseBody
    public Result clearFakeData() {

        List<Document> fakeProjects = MongoUtils.toList(
                scoreDatabase.getCollection("project_list").find(doc("fake", true)));

        for (Document fakeProject : fakeProjects) {
            String projectId = fakeProject.getString("project");
            scoreDatabase.getCollection("school_list").deleteMany(doc("project", projectId));
            scoreDatabase.getCollection("class_list").deleteMany(doc("project", projectId));
            scoreDatabase.getCollection("student_list").deleteMany(doc("project", projectId));
            scoreDatabase.getCollection("subject_list").deleteMany(doc("project", projectId));
            scoreDatabase.getCollection("quest_list").deleteMany(doc("project", projectId));
            scoreDatabase.getCollection("score").deleteMany(doc("project", projectId));
            scoreDatabase.getCollection("average").deleteMany(doc("project", projectId));
            scoreDatabase.getCollection("full_score").deleteMany(doc("project", projectId));
            scoreDatabase.getCollection("obj_correct_map").deleteMany(doc("project", projectId));
            scoreDatabase.getCollection("option_map").deleteMany(doc("project", projectId));
            scoreDatabase.getCollection("over_average").deleteMany(doc("project", projectId));
            scoreDatabase.getCollection("quest_deviation").deleteMany(doc("project", projectId));
            scoreDatabase.getCollection("quest_type_list").deleteMany(doc("project", projectId));
            scoreDatabase.getCollection("quest_type_score").deleteMany(doc("project", projectId));
            scoreDatabase.getCollection("quest_type_score_average").deleteMany(doc("project", projectId));
            scoreDatabase.getCollection("rank_level").deleteMany(doc("project", projectId));
            scoreDatabase.getCollection("rank_level_map").deleteMany(doc("project", projectId));
            scoreDatabase.getCollection("rank_position").deleteMany(doc("project", projectId));
            scoreDatabase.getCollection("rank_segment").deleteMany(doc("project", projectId));
            scoreDatabase.getCollection("score_level_map").deleteMany(doc("project", projectId));
            scoreDatabase.getCollection("score_map").deleteMany(doc("project", projectId));
            scoreDatabase.getCollection("score_minmax").deleteMany(doc("project", projectId));
            scoreDatabase.getCollection("score_rate").deleteMany(doc("project", projectId));
            scoreDatabase.getCollection("score_segment").deleteMany(doc("project", projectId));
            scoreDatabase.getCollection("std_deviation").deleteMany(doc("project", projectId));
            scoreDatabase.getCollection("t_score").deleteMany(doc("project", projectId));
            scoreDatabase.getCollection("top_average").deleteMany(doc("project", projectId));
            scoreDatabase.getCollection("top_student_list").deleteMany(doc("project", projectId));
            scoreDatabase.getCollection("total_score").deleteMany(doc("project", projectId));
            scoreDatabase.getCollection("total_score_combined").deleteMany(doc("project", projectId));
        }

        return Result.success("模拟数据删除完毕。");
    }

    @RequestMapping(value = "/fake/data_generate", method = RequestMethod.POST)
    @ResponseBody
    public Result generateFakeData(
            @RequestParam(value = "projectCount", required = false, defaultValue = "1") int projectCount,
            @RequestParam(value = "schoolsPerProject") int schoolsPerProject,
            @RequestParam(value = "classesPerSchool") int classesPerSchool,
            @RequestParam(value = "studentsPerClass") int studentsPerClass,
            @RequestParam(value = "subjectCount", required = false, defaultValue = "9") int subjectCount
    ) {

        idCounter.set(0);
        contextThreadLocal.set(new Context());

        for (int i = 0; i < projectCount; i++) {
            createProject(schoolsPerProject, classesPerSchool, studentsPerClass, subjectCount);
        }

        contextThreadLocal.set(null);
        return Result.success();
    }

    private void createProject(int schoolsPerProject, int classesPerSchool, int studentsPerClass, int subjectCount) {

        // 创建项目
        Document projectDoc = createProjectDocument();
        String projectId = projectDoc.getString("project");

        projectDoc.append("schools", createSchools(
                projectId, schoolsPerProject, classesPerSchool, studentsPerClass));

        scoreDatabase.getCollection("project_list").insertOne(projectDoc);

        // 创建科目
        createSubjects(projectId, subjectCount);

        // 创建题目
        createQuests(projectId, subjectCount);

        // 创建分数
        createScore(projectId);
    }

    private void createQuests(String projectId, int subjectCount) {
        for (int i = 0; i < subjectCount; i++) {
            String subjectId = "00" + (i + 1);
            createSubjectQuests(projectId, subjectId);
        }
    }

    private void createSubjectQuests(String projectId, String subjectId) {

        List<Document> questList = new ArrayList<>();
        int questNoCounter = 1;
        boolean isBigSubject = StringUtil.isOneOf(subjectId, "001", "002", "003");

        for (int i = 0; i < (isBigSubject ? 30 : 20); i++) {
            String questNo = String.valueOf(questNoCounter++);
            String questId = projectId + ":" + subjectId + ":" + questNo;
            Document objQuest = doc("project", projectId).append("questId", questId)
                    .append("subject", subjectId).append("isObjective", true)
                    .append("questType", QUEST_TYPE_SELECT).append("score", 1.0)
                    .append("answer", randomSelectAnswer()).append("items", OPTIONS_LIST)
                    .append("questionTypeId", QUESTION_TYPE_SELECT_ID)
                    .append("questionTypeName", QUESTION_TYPE_SELECT_NAME);
            questList.add(objQuest);
        }

        for (int i = 0; i < (isBigSubject ? 30 : 20); i++) {
            String questNo = String.valueOf(questNoCounter++);
            String questId = projectId + ":" + subjectId + ":" + questNo;
            Document sbjQuest = doc("project", projectId).append("questId", questId)
                    .append("subject", subjectId).append("isObjective", false)
                    .append("questType", QUEST_TYPE_ANSWER).append("score", 4.0)
                    .append("questionTypeId", QUESTION_TYPE_BLANK_ID)
                    .append("questionTypeName", QUESTION_TYPE_BLANK_NAME);
            questList.add(sbjQuest);
        }

        List<Document> contextQuestList = getContext().get("quests");
        if (contextQuestList == null) {
            contextQuestList = new ArrayList<>();
            getContext().put("quests", contextQuestList);
        }

        contextQuestList.addAll(questList);
        scoreDatabase.getCollection("quest_list").insertMany(questList);
    }

    private String randomSelectAnswer() {
        return OPTIONS[RANDOM.nextInt(OPTIONS.length)];
    }

    private void createScore(String projectId) {
        List<Document> students = getContext().get("students");
        List<Document> quests = getContext().get("quests");
        MongoCollection<Document> scoreCollection = scoreDatabase.getCollection("score");
        AtomicInteger counter = new AtomicInteger(0);

        for (Document student : students) {
            int intelligence = RANDOM.nextInt(51) + 40;

            List<Document> studentScores = quests.stream()
                    .map(quest -> createScore(projectId, quest, student, intelligence))
                    .collect(Collectors.toList());

            scoreCollection.insertMany(studentScores);
            LOG.info("已生成 " + counter.incrementAndGet() + "/" + students.size() + " 个考生成绩");
        }
    }

    /**
     * 创建模拟得分记录
     *
     * @param projectId    考试项目ID
     * @param quest        题目信息
     * @param student      学生信息
     * @param intelligence 该学生有多聪明(40-90)
     */
    private Document createScore(String projectId, Document quest, Document student, int intelligence) {

        Document scoreDoc = new Document()
                .append("project", projectId)
                .append("student", student.getString("student"))
                .append("subject", quest.getString("subject"))
                .append("quest", quest.getString("questId"))
                .append("questNo", quest.getString("questNo"));

        scoreDoc.append("score", generateScoreValue(quest, intelligence, scoreDoc))
                .append("class", student.getString("class"))
                .append("school", student.getString("school"))
                .append("area", student.getString("area"))
                .append("city", student.getString("city"))
                .append("province", student.getString("province"));

        return scoreDoc;
    }

    private double generateScoreValue(Document quest, int intelligence, Document scoreDoc) {
        double fullScore = quest.getDouble("score");
        boolean isObjective = quest.getBoolean("isObjective");
        boolean isRight = RANDOM.nextInt(100) < intelligence;

        scoreDoc.put("right", isRight);
        scoreDoc.put("isObjective", isObjective);

        double scoreValue;
        if (isObjective) {
            scoreValue = isRight ? fullScore : 0;
            String studentAnswer = pickRandomWithout(OPTIONS_LIST, quest.getString("answer"));
            scoreDoc.put("answer", studentAnswer);
        } else {
            scoreValue = isRight ? fullScore : (fullScore * (RANDOM.nextInt(20) + intelligence - 15) / 100);
            scoreValue = (int) (scoreValue * 2) / 2.0;
        }
        return scoreValue;
    }

    private void createSubjects(String projectId, int subjectCount) {
        List<String> subjects = new ArrayList<>();
        for (int i = 0; i < subjectCount; i++) {
            subjects.add("00" + (i + 1));
        }
        Document subject = doc("project", projectId).append("subjects", subjects);
        scoreDatabase.getCollection("subject_list").insertOne(subject);
    }

    private List<Document> createSchools(String projectId, int schoolsPerProject, int classesPerSchool, int studentsPerClass) {
        ArrayList<Document> schools = new ArrayList<>();
        for (int i = 0; i < schoolsPerProject; i++) {
            schools.add(createSchoolWithStudents(projectId, classesPerSchool, studentsPerClass));
        }
        return schools;
    }

    private Document createSchoolWithStudents(String projectId, int classesPerSchool, int studentsPerClass) {
        String schoolId = "SCHOOL_" + nextId();

        for (int i = 0; i < classesPerSchool; i++) {
            createClassWithStudents(projectId, schoolId, studentsPerClass);
        }

        Document school = doc("project", projectId).append("school", schoolId).append("name", schoolId)
                .append("area", "430101").append("city", "430100").append("province", "430000");
        scoreDatabase.getCollection("school_list").insertOne(school);
        return school;
    }

    private void createClassWithStudents(String projectId, String schoolId, int studentsPerClass) {
        String classId = "CLASS_" + nextId();

        for (int i = 0; i < studentsPerClass; i++) {
            createStudent(projectId, schoolId, classId);
        }

        Document _class = doc("project", projectId).append("school", schoolId)
                .append("class", classId).append("name", classId).append("grade", GRADE)
                .append("area", "430101").append("city", "430100").append("province", "430000");
        scoreDatabase.getCollection("class_list").insertOne(_class);
    }

    private void createStudent(String projectId, String schoolId, String classId) {
        Document student = doc("project", projectId).append("name", ChineseName.nextRandomName())
                .append("student", "STU_" + nextId()).append("class", classId).append("school", schoolId)
                .append("area", "430101").append("city", "430100").append("province", "430000");

        List<Document> students = getContext().get("students");
        if (students == null) {
            students = new ArrayList<>();
            getContext().put("students", students);
        }
        students.add(student);

        scoreDatabase.getCollection("student_list").insertOne(student);
    }

    private Document createProjectDocument() {
        String projectId = "FAKE_PROJ_" + nextId();
        return doc("project", projectId).append("name", projectId).append("grade", GRADE).append("fake", true);
    }

    private String nextId() {
        if (idCounter.get() == null) {
            idCounter.set(0);
        }
        int id = idCounter.get();
        idCounter.set((id + 1) % 100);
        return System.currentTimeMillis() + "_" + id;
    }

    private static Context getContext() {
        return contextThreadLocal.get();
    }
}
