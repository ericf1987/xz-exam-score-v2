package com.xz.services;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mongodb.client.MongoDatabase;
import com.xz.ajiaedu.common.beans.exam.ExamProject;
import com.xz.ajiaedu.common.io.ZipFileReader;
import com.xz.ajiaedu.common.lang.*;
import com.xz.api.Param;
import com.xz.bean.PointLevel;
import com.xz.bean.ProjectConfig;
import com.xz.bean.SubjectLevel;
import com.xz.bean.Target;
import com.xz.intclient.InterfaceClient;
import com.xz.scanner.ScannerDBService;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.zip.ZipEntry;

import static com.xz.ajiaedu.common.mongo.MongoUtils.doc;

/**
 * (description)
 * created at 16/06/22
 *
 * @author yiding_he
 */
@SuppressWarnings("unchecked")
@Service
public class ImportProjectService {

    static final Logger LOG = LoggerFactory.getLogger(ImportProjectService.class);

    @Autowired
    InterfaceClient interfaceClient;

    @Autowired
    ProjectService projectService;

    @Autowired
    ProvinceService provinceService;

    @Autowired
    CityService cityService;

    @Autowired
    AreaService areaService;

    @Autowired
    SchoolService schoolService;

    @Autowired
    ClassService classService;

    @Autowired
    StudentService studentService;

    @Autowired
    SubjectService subjectService;

    @Autowired
    QuestService questService;

    @Autowired
    QuestIdService questIdService;

    @Autowired
    PointService pointService;

    @Autowired
    QuestTypeService questTypeService;

    @Autowired
    FullScoreService fullScoreService;

    @Autowired
    PrepareDataService prepareDataService;

    @Autowired
    MongoDatabase scoreDatabase;

    @Autowired
    ScannerDBService scannerDBService;

    @Autowired
    ProjectConfigService projectConfigService;

    @Autowired
    DictionaryService dictionaryService;

    /**
     * 导入项目信息
     *
     * @param projectId        项目ID
     * @param reimportStudents 是否要重新导入考生信息，如果为 false，则跳过考生信息导入。
     * @return
     */
    public Context importProject(String projectId, boolean reimportStudents) {
        Context context = new Context();

        // 下面的导入顺序不能变更，否则可能造成数据错误
        importProjectInfo(projectId, context);
        importSubjects(projectId);
        importProjectReportConfig(projectId, context);
        importQuests(projectId, context);   // 该方法对 context 参数只写不读
        importPointsAndLevels(projectId, context);
        importQuestTypes(projectId, context);

        if (reimportStudents) {
            importSchools(projectId, context);
            importClasses(projectId, context);
            importStudents(projectId, context);
        }

        return context;
    }

    protected Context importProjectReportConfig(String projectId, Context context) {
        Result result = interfaceClient.request("QueryProjectReportConfig",
                new Param().setParameter("projectId", projectId));

        JSONObject rankLevel = result.get("rankLevel");
        // todo 将报表配置保存到数据库
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String currentDate = format.format(Calendar.getInstance().getTime());
        List<String> displayOptions = (List<String>) rankLevel.get("displayOptions");
        List<String> modelSubjects = (List<String>) rankLevel.get("modelSubjects");
        String startDate = rankLevel.getString("startDate") == null ? currentDate : rankLevel.getString("startDate");
        boolean isCombine = JudgeCombine(modelSubjects);
        ProjectConfig projectConfig = new ProjectConfig();
        if (null != displayOptions && !displayOptions.isEmpty()) {
            projectConfig.setProjectId(projectId);
            projectConfig.setCombineCategorySubjects(isCombine);
            projectConfig.setDisplayOptions(displayOptions);
            projectConfig.setStartDate(startDate);
            projectConfigService.mergeProjectConfig(projectConfig);
        } else {

        }
        context.put("projectConfig", projectConfig);
        return context;
    }

    private boolean JudgeCombine(List<String> modelSubjects) {
        for (String subject : modelSubjects) {
            if (subject.equals("004005006") || subject.equals("007008009")) {
                return true;
            }
        }
        return false;
    }

    // 仅导入题目数据，用于修改标答后的重新算分
    // 注意如果 importQuests() 方法对 context 参数进行了读取操作，则本方法可能会失效，因为
    // 传过去的是一个空的 Context。
    public void importProjectQuest(String projectId) {
        importQuests(projectId, new Context());
    }

    // 导入考试知识点/能力层级
    @SuppressWarnings({"unchecked", "MismatchedQueryAndUpdateOfCollection"})
    private void importPointsAndLevels(String projectId, Context context) {
        List<Document> projectQuests = context.get("quests");
        Set<String> existPoints = new HashSet<>();   // 方法内的缓存，因为 pointService.exists() 方法不做缓存
        DoubleCounterMap<String> pointFullScore = new DoubleCounterMap<>();
        DoubleCounterMap<SubjectLevel> subjectLevelFullScore = new DoubleCounterMap<>();
        DoubleCounterMap<PointLevel> pointLevelFullScore = new DoubleCounterMap<>();

        LOG.info("导入项目 " + projectId + " 知识点信息...");
        for (Document quest : projectQuests) {
            double score = quest.getDouble("score");
            String subject = quest.getString("subject");
            Map<String, List<String>> points = (Map<String, List<String>>) quest.get("points");

            if (points == null) {
                continue;
            }

            // 每个题目对每个能力层级只计算一次分数
            Set<String> levels = new HashSet<>();

            for (String pointId : points.keySet()) {

                pointFullScore.incre(pointId, score);

                for (String level : points.get(pointId)) {
                    pointLevelFullScore.incre(new PointLevel(pointId, level), score);
                    levels.add(level);
                }

                //////////////////////////////////////////////////////////////

                if (existPoints.contains(pointId)) {
                    continue;
                }

                if (pointService.exists(pointId)) {
                    existPoints.add(pointId);
                    continue;
                }

                //////////////////////////////////////////////////////////////

                Param param = new Param().setParameter("pointId", pointId);
                Result result = interfaceClient.request("QueryKnowledgePointById", param);
                Map<String, Object> point = result.get("point");
                pointService.savePoint(pointId, point.get("point_name").toString());
            }

            // 将该题目的分数累加到每个能力层级
            for (String level : levels) {
                subjectLevelFullScore.incre(new SubjectLevel(subject, level), score);
            }
        }

        for (Map.Entry<String, Double> entry : pointFullScore.entrySet()) {
            String point = entry.getKey();
            double fullScore = entry.getValue();
            fullScoreService.saveFullScore(projectId, Target.point(point), fullScore);
        }

        for (Map.Entry<SubjectLevel, Double> entry : subjectLevelFullScore.entrySet()) {
            SubjectLevel subjectLevel = entry.getKey();
            double fullScore = entry.getValue();
            fullScoreService.saveFullScore(projectId, Target.subjectLevel(subjectLevel), fullScore);
        }

        for (Map.Entry<PointLevel, Double> entry : pointLevelFullScore.entrySet()) {
            PointLevel pointLevel = entry.getKey();
            double fullScore = entry.getValue();
            fullScoreService.saveFullScore(projectId, Target.pointLevel(pointLevel), fullScore);
        }
    }

    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    private void importQuestTypes(String projectId, Context context) {
        LOG.info("导入项目 " + projectId + " 题型信息");

        questTypeService.clearQuestTypes(projectId);

        List<Document> projectQuests = context.get("quests");
        Map<String, Document> questTypeMap = new HashMap<>();
        DoubleCounterMap<String> questTypeFullScore = new DoubleCounterMap<>();

        for (Document quest : projectQuests) {
            double score = quest.getDouble("score");
            String subject = quest.getString("subject");
            String questTypeId = quest.getString("questionTypeId");
            String questTypeName = quest.getString("questionTypeName");

            if (StringUtil.isBlank(questTypeId)) {
                continue;
            }

            questTypeFullScore.incre(questTypeId, score);

            if (!questTypeMap.containsKey(questTypeId)) {
                questTypeMap.put(questTypeId, doc("project", projectId)
                        .append("subject", subject)
                        .append("questTypeId", questTypeId)
                        .append("questTypeName", questTypeName));
            }
        }

        for (Document questType : questTypeMap.values()) {
            questTypeService.saveQuestType(questType);
        }

        for (String questTypeId : questTypeFullScore.keySet()) {
            fullScoreService.saveFullScore(
                    projectId, Target.questType(questTypeId), questTypeFullScore.get(questTypeId));
        }
    }

    // 导入考题信息
    private void importQuests(String projectId, Context context) {
        LOG.info("导入项目 " + projectId + " 考题信息...");
        Param param = new Param().setParameter("projectId", projectId);
        Result result = interfaceClient.request("QueryQuestionByProject", param);
        List<Document> projectQuests = new ArrayList<>();

        JSONArray jsonArray = result.get("quests");
        context.put("questCount", jsonArray.size());

        jsonArray.forEach(o -> {
            JSONObject questObj = (JSONObject) o;
            Document questDoc = new Document();
            questDoc.put("project", projectId);
            questDoc.put("questId", questObj.getString("questId"));
            questDoc.put("subject", questObj.getString("subjectId"));
            questDoc.put("questType", questObj.getString("questType"));
            questDoc.put("isObjective", isObjective(questObj.getString("questType")));
            questDoc.put("questNo", questObj.getString("paperQuestNum"));
            questDoc.put("score", questObj.getDoubleValue("score"));
            questDoc.put("answer", questObj.getString("answer"));
            questDoc.put("scoreRule", questObj.getString("scoreRule"));
            questDoc.put("points", questObj.get("points"));
            questDoc.put("items", questObj.get("items"));
            questDoc.put("questionTypeId", questObj.getString("questionTypeId"));
            questDoc.put("questionTypeName", questObj.getString("questionTypeName"));

            fixQuest(questDoc);

            projectQuests.add(questDoc);
        });

        context.put("quests", projectQuests);
        questService.saveProjectQuests(projectId, projectQuests);
    }

    // 导入考试班级
    private void importClasses(String projectId, Context context) {
        List<Document> classes = new ArrayList<>();
        List<Document> schools = context.get("schools");
        ExamProject project = context.get("project");

        for (Document school : schools) {
            String schoolId = school.getString("school");
            List<Document> schoolClasses = new ArrayList<>();

            LOG.info("导入学校 " + schoolId + "(" + school.getString("name") + ") 班级信息...");

            Param param = new Param().setParameter("projectId", projectId)
                    .setParameter("schoolId", schoolId).setParameter("needStudentCount", false);

            Result result = interfaceClient.request("QueryExamClassByProject", param);
            JSONArray jsonArray = result.get("classes");

            jsonArray.forEach(o -> {
                JSONObject classObj = (JSONObject) o;

                Document schoolClass = new Document()
                        .append("project", projectId)
                        .append("class", classObj.getString("id"))
                        .append("name", classObj.getString("name"))
                        .append("grade", project.getGrade())
                        .append("school", schoolId)
                        .append("area", school.getString("area"))
                        .append("city", school.getString("city"))
                        .append("province", school.getString("province"));

                schoolClasses.add(schoolClass);
                classes.add(schoolClass);               // 之后查询学生用
            });

            classService.saveProjectSchoolClasses(projectId, schoolId, schoolClasses);
        }

        context.put("classes", classes);
    }

    // 导入考生列表
    private void importStudents(String projectId, Context context) {
        List<Document> classes = context.get("classes");
        int classCount = classes.size();
        int index = 0;

        for (Document classDoc : classes) {
            String classId = classDoc.getString("class");
            index++;
            LOG.info("导入班级 " + classId + " 的考生信息(" + index + "/" + classCount + ")...");

            List<Document> classStudents = new ArrayList<>();
            Param param = new Param().setParameter("projectId", projectId)
                    .setParameter("classId", classId);
            Result result = interfaceClient.request("QueryClassExamStudent", param);

            JSONArray students = result.get("examStudents");
            students.forEach(o -> {
                JSONObject studentObj = (JSONObject) o;
                Document studentDoc = new Document()
                        .append("project", projectId)
                        .append("student", studentObj.get("id"))
                        .append("name", studentObj.get("name"))
                        .append("class", classDoc.get("class"))
                        .append("school", classDoc.get("school"))
                        .append("area", classDoc.get("area"))
                        .append("city", classDoc.get("city"))
                        .append("province", classDoc.get("province"));

                classStudents.add(studentDoc);
            });

            studentService.saveProjectClassStudents(projectId, classId, classStudents);
        }
    }

    // 导入学校和区市省
    private void importSchools(String projectId, Context context) {
        LOG.info("导入项目 " + projectId + " 学校信息...");
        Param param = new Param().setParameter("projectId", projectId).setParameter("needStudentCount", false);
        Result result = interfaceClient.request("QueryExamSchoolByProject", param);
        JSONArray jsonArray = result.get("schools");

        List<Document> schoolList = new ArrayList<>();  // 存入 project_list
        Set<String> areas = new HashSet<>();
        Set<String> cities = new HashSet<>();
        Set<String> provinces = new HashSet<>();

        jsonArray.forEach(o -> {
            JSONObject schoolObj = (JSONObject) o;
            Document schoolDoc = new Document();
            schoolDoc.put("project", projectId);
            schoolDoc.put("school", schoolObj.getString("id"));
            schoolDoc.put("name", schoolObj.getString("name"));
            schoolDoc.put("area", schoolObj.getString("area"));
            schoolDoc.put("city", schoolObj.getString("city"));
            schoolDoc.put("province", schoolObj.getString("province"));


            List<Document> tags = new ArrayList<>();

            String isInCity = dictionaryService.findDictionary("isInCity", schoolObj.getString("school_region")).getString("value");
            String isGovernmental = dictionaryService.findDictionary("isGovernmental", schoolObj.getString("school_kind")).getString("value");

            //学校归属区域 0=未知 1=城区 2=农村
            tags.add(new Document().append("name", "isInCity").append("value", isInCity));
            //学校类型 0=未知 1=公办 2=民办
            tags.add(new Document().append("name", "isGovernmental").append("value", isGovernmental));
            schoolDoc.put("tags", tags);

            areas.add(schoolObj.getString("area"));
            cities.add(schoolObj.getString("city"));
            provinces.add(schoolObj.getString("province"));

            schoolList.add(schoolDoc);
        });

        context.put("schools", schoolList);  // 导入班级时要用到

        schoolService.saveProjectSchool(projectId, schoolList);
        projectService.updateProjectSchools(projectId, schoolList);
        provinceService.saveProjectProvince(projectId, provinces.iterator().next());
        cityService.saveProjectCities(projectId, cities);
        areaService.saveProjectAreas(projectId, areas);
    }

    // 修复官网的题目记录属性不全导致的问题
    private void fixQuest(Document questDoc) {

        // 如果卷库当中没有题目记录，则会导致 questId 为空
        // 在这里临时生成一个新的 questId 补上。
        if (questDoc.getString("questId") == null) {
            String project = questDoc.getString("project");
            String subject = questDoc.getString("subject");
            String questNo = questDoc.getString("questNo");
            questDoc.put("questId", questIdService.getQuestId(project, subject, questNo));
        }

    }

    private Boolean isObjective(String questType) {
        if (questType == null) {
            return null;
        } else {
            return "0".equals(questType) || "1".equals(questType) || "2".equals(questType);
        }
    }

    private void importSubjects(String projectId) {
        LOG.info("导入项目 " + projectId + " 科目信息...");
        Param param = new Param().setParameter("projectId", projectId);
        Result result = interfaceClient.request("QuerySubjectListByProjectId", param);

        JSONArray jsonArray = result.get("result");
        List<String> subjects = new ArrayList<>();
        Value<Double> projectFullScore = Value.of(0d);

        jsonArray.forEach(o -> {
            JSONObject subjectObj = (JSONObject) o;
            String subjectId = subjectObj.getString("subjectId");
            Double fullScore = subjectObj.getDouble("totalScore");

            // 科目没有录入或没有答题卡
            if (fullScore == null) {
                LOG.error("科目'" + subjectId + "'没有总分: " + jsonArray);
                return;
            }

            projectFullScore.set(projectFullScore.get() + fullScore);
            subjects.add(subjectId);
            fullScoreService.saveFullScore(projectId, Target.subject(subjectId), fullScore);  // 保存科目总分
        });

        subjectService.saveProjectSubjects(projectId, subjects);        // 保存科目列表
        fullScoreService.saveFullScore(projectId, Target.project(projectId), projectFullScore.get());  // 保存项目总分
    }

    protected void importProjectInfo(String projectId, Context context) {
        LOG.info("导入项目 " + projectId + " 基本信息...");
        Param param = new Param().setParameter("projectId", projectId);
        Result result = interfaceClient.request("QueryProjectById", param);  // 找不到项目则抛出异常

        JSONObject projectObj = result.get("result");
        ExamProject project = new ExamProject();
        project.setId(projectId);
        project.setName(projectObj.getString("name"));
        project.setGrade(projectObj.getInteger("grade"));
        project.setCreateTime(new Date());
        //考试开始日期
        project.setExamStartDate(projectObj.getString("examStartDate"));

        context.put("project", project);
        projectService.saveProject(project);
    }

    //从zip包读取学生信息
    public Result importStudentInfoFromZip(ZipFileReader zipFileReader) throws Exception {
        zipFileReader.readZipEntries("*", consumer -> {
            try {
                readEntry(consumer, zipFileReader);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        return Result.success();
    }

    private void readEntry(ZipEntry entry, ZipFileReader zipFileReader) {
        //文件名为projectId_subjectId.json
        String fileName = entry.getName().substring(0, entry.getName().lastIndexOf("."));
        String projectId = fileName.split("_")[0];
        String subjectId = fileName.split("_")[1];
        AtomicInteger counter = new AtomicInteger();
        zipFileReader.readEntryByLine(entry, "UTF-8", line -> readEntryLine(line, projectId, subjectId, counter));
    }

    private void readEntryLine(String line, String projectId, String subjectId, AtomicInteger counter) {
        //获取每个学生document对象
        Document studentDoc = Document.parse(line.trim());
        scannerDBService.importStudentScore(projectId, subjectId, studentDoc, counter);
    }

}
