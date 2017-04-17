package com.xz.examscore.services;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hyd.simplecache.utils.MD5;
import com.xz.ajiaedu.common.aliyun.ApiResponse;
import com.xz.ajiaedu.common.beans.exam.ExamProject;
import com.xz.ajiaedu.common.io.ZipFileReader;
import com.xz.ajiaedu.common.lang.Context;
import com.xz.ajiaedu.common.lang.DoubleCounterMap;
import com.xz.ajiaedu.common.lang.StringUtil;
import com.xz.ajiaedu.common.lang.Value;
import com.xz.examscore.bean.PointLevel;
import com.xz.examscore.bean.ProjectConfig;
import com.xz.examscore.bean.SubjectLevel;
import com.xz.examscore.bean.Target;
import com.xz.examscore.cache.ProjectCacheManager;
import com.xz.examscore.intclient.InterfaceClient;
import com.xz.examscore.scanner.ScannerDBService;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.BooleanUtils;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;

import static com.xz.ajiaedu.common.mongo.MongoUtils.doc;
import static com.xz.ajiaedu.common.report.Keys.ScoreLevel.*;

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
    ScannerDBService scannerDBService;

    @Autowired
    ProjectConfigService projectConfigService;

    @Autowired
    SubjectCombinationService subjectCombinationService;

    @Autowired
    QuestAbilityLevelService questAbilityLevelService;

    @Autowired
    ProjectCacheManager projectCacheManager;

    public static final int SUBJECT_LENGTH = 3;

    /**
     * 导入项目信息
     *
     * @param projectId        项目ID
     * @param reimportStudents 是否要重新导入考生信息，如果为 false，则跳过考生信息导入。
     * @return ?
     */
    public Context importProject(String projectId, boolean reimportStudents) {

        projectCacheManager.deleteProjectCache(projectId);

        Context context = new Context();

        // 下面的导入顺序不能变更，否则可能造成数据错误
        importProjectInfo(projectId, context);
        importProjectReportConfig(projectId, context);
        importSubjects(projectId, context);
        importQuests(projectId, context);   // 该方法对 context 参数只写不读
        importPointsAndLevels(projectId, context);
        importQuestTypes(projectId, context);
        importQuestAbilityLevel(projectId, context);

        if (reimportStudents) {
            importSchools(projectId, context);
            importClasses(projectId, context);
            importStudents(projectId, context);
        }

        return context;
    }

    /**
     * 导入考试项目信息
     *
     * @param projectId 考试项目ID
     * @param context   上下文对象
     */
    protected void importProjectInfo(String projectId, Context context) {
        LOG.info("导入项目 " + projectId + " 基本信息...");
        JSONObject projectObj = interfaceClient.queryProjectById(projectId);

        if (projectObj == null) {
            LOG.info("没有找到项目 " + projectId);
            return;
        }

        ExamProject project = new ExamProject();
        project.setId(projectId);
        project.setName(projectObj.getString("name"));
        project.setGrade(projectObj.getInteger("grade"));
        project.setCreateTime(new Date());
        //项目类型 文科：W，理科：L
        project.setCategory(projectObj.getString("category"));
        //考试开始日期
        project.setExamStartDate(projectObj.getString("examStartDate"));

        context.put("project", project);
        projectService.saveProject(project);
    }

    /**
     * 导入科目信息
     *
     * @param projectId 考试项目ID
     * @param context   上下文信息
     */
    public void importSubjects(String projectId, Context context) {
        LOG.info("导入项目 " + projectId + " 科目信息...");
        boolean isSubjectSplited = splitSubject(projectId, context);
        if (isSubjectSplited) {
            importSlicedSubjects(projectId, context);
        } else {
            importNormalSubjects(projectId, context);
        }
        context.put("isSubjectSplited", isSubjectSplited);
    }

    private boolean splitSubject(String projectId, Context context) {
        ProjectConfig projectConfig = context.get("projectConfig");
        return projectConfig.isSeparateCombine();
    }

    /**
     * 导入考试配置参数信息
     *
     * @param projectId 考试项目ID
     * @param context   上下文信息
     */
    public void importProjectReportConfig(String projectId, Context context) {
        ApiResponse result = interfaceClient.queryProjectReportConfig(projectId);
        JSONObject rankLevel = result.get("rankLevel");
        JSONObject scoreLevels = result.get("scoreLevels");
        String topStudentRatio = result.get("topStudentRatio");
        String highScoreRatio = result.get("highScoreRatio");
        String scoreLevelConfig = result.get("scoreLevelConfig");

        String almostPassOffset = null == result.get("almostPassOffset") ? "" : result.get("almostPassOffset").toString();
        boolean fillAlmostPass = null != result.get("fillAlmostPass") && BooleanUtils.toBoolean(result.get("fillAlmostPass").toString());
        boolean removeCheatStudent = null != result.get("removeCheatStudent") && BooleanUtils.toBoolean(result.get("removeCheatStudent").toString());
        boolean removeZeroScores = null != result.get("removeZeroScores") && BooleanUtils.toBoolean(result.get("removeZeroScores").toString());
        boolean removeAbsentStudent = null == result.get("removeAbsentStudent") || BooleanUtils.toBoolean(result.get("removeAbsentStudent").toString());

        Map<String, Object> scoreLevelsMap = new HashMap<>();
        boolean splitUnionSubject = false;
        //是否开启学校信息共享(默认开启联考数据共享，如果是联考项目，则根据CMS配置的是否共享开关进行设置)
        boolean shareSchoolReport = true;
        if (result.get("shareSchoolReport") != null) {
            shareSchoolReport = BooleanUtils.toBoolean(result.get("shareSchoolReport").toString());
        }
        //是否拆分科目
        if (result.get("splitUnionSubject") != null) {
            splitUnionSubject = Boolean.parseBoolean(result.get("splitUnionSubject").toString());
        }
        //获取本科上线率统计相关参数
        String entryLevelStatType = "rate", entryLevelEnable = "false";
        List<String> collegeEntryLevel = new ArrayList<>();
        JSONObject onlineRateStat = result.get("onlineRateStat");
        if (onlineRateStat != null && !onlineRateStat.isEmpty()) {
            if (onlineRateStat.get("isOn") != null) {
                entryLevelEnable = onlineRateStat.get("isOn").toString();
            }
            if (onlineRateStat.get("onlineStatType") != null) {
                entryLevelStatType = onlineRateStat.get("onlineStatType").toString();
            }
            if (onlineRateStat.get("values") != null) {
                collegeEntryLevel.addAll((List<String>) onlineRateStat.get("values"));
            }
        }

        if (null != rankLevel) {
            List<String> displayOptions = (List<String>) rankLevel.get("displayOptions");
            Map<String, Object> standard = (Map<String, Object>) rankLevel.get("standard");
            Map<String, Double> rankLevels = formatRankLevel(standard);
            boolean isCombine = JudgeCombine((List<String>) rankLevel.get("modelSubjects"));
            //尖子生比例
            Double topStudentRate = 0.05d;
            //高分段比例
            Double highScoreRate = 0.3d;

            //获取和分数等级参数
            if (null != scoreLevels && !scoreLevels.isEmpty()) {
                getScoreLevelByConfig(scoreLevels, scoreLevelsMap, scoreLevelConfig);
            }

            //获取尖子生比例
            if (!StringUtils.isEmpty(topStudentRatio)) {
                topStudentRate = Double.parseDouble(topStudentRatio);
            }

            //获取高分段比例
            if (!StringUtils.isEmpty(highScoreRatio)) {
                highScoreRate = Double.parseDouble(highScoreRatio);
            }

            //构建新的考试配置
            ProjectConfig projectConfig = new ProjectConfig();
            projectConfig.setProjectId(projectId);
            projectConfig.setRankLevels(rankLevels);
            projectConfig.setCombineCategorySubjects(isCombine);
            projectConfig.setRankLevelCombines(displayOptions);
            projectConfig.setScoreLevels(scoreLevelsMap);
            projectConfig.setTopStudentRate(topStudentRate);
            projectConfig.setHighScoreRate(highScoreRate);
            projectConfig.setSeparateCombine(splitUnionSubject);
            projectConfig.setEntryLevelStatType(entryLevelStatType);
            projectConfig.setEntryLevelEnable(Boolean.parseBoolean(entryLevelEnable));
            projectConfig.setCollegeEntryLevel(collegeEntryLevel);
            projectConfig.setShareSchoolReport(shareSchoolReport);

            projectConfig.setAlmostPassOffset(almostPassOffset);
            projectConfig.setFillAlmostPass(fillAlmostPass);
            projectConfig.setRemoveCheatStudent(removeCheatStudent);
            projectConfig.setRemoveZeroScores(removeZeroScores);
            projectConfig.setRemoveAbsentStudent(removeAbsentStudent);

            projectConfigService.fixProjectConfig(projectConfig);

            projectConfigService.updateRankLevelConfig(projectConfig);

            context.put("projectConfig", projectConfigService.getProjectConfig(projectId));

        } else {
            ProjectConfig projectConfig = projectConfigService.getProjectConfig(projectId);

            projectConfigService.updateRankLevelConfig(projectId,
                    projectConfig.getRankLevels(), projectConfig.isCombineCategorySubjects(),
                    projectConfig.getRankLevelCombines(), projectConfig.getScoreLevels(),
                    projectConfig.getTopStudentRate(), projectConfig.getLastRankLevel(),
                    projectConfig.getRankSegmentCount(), projectConfig.getHighScoreRate(),
                    projectConfig.isSeparateCombine(), projectConfig.getEntryLevelStatType(),
                    projectConfig.isEntryLevelEnable(), projectConfig.getCollegeEntryLevel(),
                    projectConfig.isShareSchoolReport(), projectConfig.getAlmostPassOffset(),
                    projectConfig.isFillAlmostPass(), projectConfig.isRemoveAbsentStudent(),
                    projectConfig.isRemoveZeroScores(), projectConfig.isRemoveCheatStudent()
            );
            context.put("projectConfig", projectConfigService.getProjectConfig(projectId));
        }
    }

    public void getScoreLevelByConfig(JSONObject scoreLevels, Map<String, Object> scoreLevelsMap, String scoreLevelConfig) {
        if (!StringUtil.isBlank(scoreLevelConfig) && scoreLevelConfig.equals("score")) {
            for (String subjectKey : scoreLevels.keySet()) {
                scoreLevelsMap.put(subjectKey, scoreLevels.get(subjectKey));
            }
        } else {
            scoreLevelsMap.put(Excellent.name(), scoreLevels.get("excellent"));
            scoreLevelsMap.put(Good.name(), scoreLevels.get("good"));
            scoreLevelsMap.put(Pass.name(), scoreLevels.get("pass"));
            scoreLevelsMap.put(Fail.name(), scoreLevels.get("fail"));
        }
    }

    /**
     * 导入考题信息
     *
     * @param projectId 考试项目ID
     * @param context   上下文对象
     */
    private void importQuests(String projectId, Context context) {
        LOG.info("导入项目 " + projectId + " 考题信息...");
        JSONArray jsonArray = interfaceClient.queryQuestionByProject(projectId);
        List<Document> projectQuests = new ArrayList<>();

        //如果科目中包含综合科目，则将题目的科目ID由单科改为综合科目ID
        boolean isSubjectSplited = context.get("isSubjectSplited");

        context.put("questCount", jsonArray.size());
        //判断是否为综合科目，是否需要合并科目ID
        jsonArray.forEach(o -> {
            JSONObject questObj = (JSONObject) o;
            Document questDoc = new Document();
            questDoc.put("project", projectId);
            questDoc.put("questId", questObj.getString("questId"));
            //如果拆分，则subject取答题卡的科目ID
            String sid = !isSubjectSplited ? questObj.getString("cardSubjectId") : questObj.getString("subjectId");
            questDoc.put("subject", sid);
            questDoc.put("cardSubjectId", questObj.getString("cardSubjectId"));
            questDoc.put("questType", questObj.getString("questType"));
            questDoc.put("isObjective", isObjective(questObj.getString("questType"), questObj.getString("subObjTag")));
            questDoc.put("questNo", questObj.getString("paperQuestNum"));
            questDoc.put("score", questObj.getDoubleValue("score"));
            questDoc.put("answer", questObj.getString("answer"));
            questDoc.put("scoreRule", questObj.getString("scoreRule"));
            questDoc.put("points", questObj.get("points"));
            questDoc.put("items", questObj.get("items"));
            questDoc.put("questionTypeId", questObj.getString("questionTypeId"));
            questDoc.put("questionTypeName", questObj.getString("questionTypeName"));
            questDoc.put("levelOrAbility", questObj.getString("levelOrAbility"));
/*            String levelOrAbility = generateByRandom();
            questDoc.put("levelOrAbility", levelOrAbility);*/
            questDoc.put("questAbilityLevel", questAbilityLevelService.getId(sid, questObj.getString("levelOrAbility")));
            //是否直接给分
            questDoc.put("awardScoreTag", questObj.get("awardScoreTag"));
            questDoc.put("md5", MD5.digest(UUID.randomUUID().toString()));

            fixQuest(questDoc);

            projectQuests.add(questDoc);
        });

        context.put("quests", projectQuests);
        questService.saveProjectQuests(projectId, projectQuests);
    }

    private String generateByRandom() {
        int r = (int) (Math.random() * 10);
        if (r > 7) {
            return "ability";
        }
        return "level";
    }

    /**
     * 导入考试知识点/能力层级
     *
     * @param projectId 考试项目ID
     * @param context   上下文对象
     */
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

                if (pointService.exists(pointId, subject)) {
                    existPoints.add(pointId);
                    continue;
                }

                //////////////////////////////////////////////////////////////

                JSONObject point = interfaceClient.queryKnowledgePointById(pointId);
                if (point != null) {
                    pointService.savePoint(pointId,
                            point.getString("point_name"),
                            point.getString("parent_point_id"),
                            point.getString("subject"));
                }
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

    /**
     * 导入试卷题型信息
     *
     * @param projectId 考试项目ID
     * @param context   上下文对象
     */
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

    /**
     * 导入试题能力层级
     *
     * @param projectId 考试项目ID
     * @param context   上下文对象
     */
    private void importQuestAbilityLevel(String projectId, Context context) {
        LOG.info("导入项目 " + projectId + " 试题能力层级");
        questAbilityLevelService.clearQuestAbilityLevel(projectId);

        List<Document> projectQuests = context.get("quests");
        Map<String, Document> questAbilityLevelMap = new HashMap<>();
        DoubleCounterMap<String> questAbilityLevelScore = new DoubleCounterMap<>();

        for (Document quest : projectQuests) {
            double score = quest.getDouble("score");
            String subject = quest.getString("subject");
            String levelOrAbility = quest.getString("levelOrAbility");
            if (StringUtil.isBlank(levelOrAbility)) {
                continue;
            }

            String questAbilityLevel = questAbilityLevelService.getId(subject, levelOrAbility);
            String questAbilityLevelName = questAbilityLevel.contains("level") ? "水平检测" : "能力检测";

            questAbilityLevelScore.incre(questAbilityLevel, score);

            if (!questAbilityLevelMap.containsKey(questAbilityLevel)) {
                questAbilityLevelMap.put(questAbilityLevel, doc("project", projectId)
                        .append("subject", subject)
                        .append("levelOrAbility", levelOrAbility)
                        .append("questAbilityLevel", questAbilityLevel)
                        .append("questAbilityLevelName", questAbilityLevelName));
            }
        }

        //保存题目能力层级
        for (Document doc : questAbilityLevelMap.values()) {
            questAbilityLevelService.saveQuestAbilityLevel(doc);
        }

        //保存题目能力层级满分
        for (String questAbilityLevel : questAbilityLevelMap.keySet()) {
            fullScoreService.saveFullScore(projectId, Target.questAbilityLevel(questAbilityLevel), questAbilityLevelScore.get(questAbilityLevel));
        }
    }


    /**
     * 导入学校和区市省
     *
     * @param projectId 考试项目ID
     * @param context   上下文对象
     */
    private void importSchools(String projectId, Context context) {
        LOG.info("导入项目 " + projectId + " 学校信息...");
        JSONArray jsonArray = interfaceClient.queryExamSchoolByProject(projectId, false);

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
            schoolDoc.put("md5", MD5.digest(UUID.randomUUID().toString()));


            areas.add(schoolObj.getString("area"));
            cities.add(schoolObj.getString("city"));
            provinces.add(schoolObj.getString("province"));

            schoolList.add(schoolDoc);
        });

        context.put("schools", schoolList);  // 导入班级时要用到

        schoolService.saveProjectSchool(projectId, schoolList);
        projectService.updateProjectSchools(projectId, schoolList);
        provinceService.saveProjectProvince(projectId, provinces.isEmpty() ? null : provinces.iterator().next());
        cityService.saveProjectCities(projectId, cities);
        areaService.saveProjectAreas(projectId, areas);
    }

    /**
     * 导入考试班级
     *
     * @param projectId 考试项目ID
     * @param context   上下文对象
     */
    private void importClasses(String projectId, Context context) {
        List<Document> classes = new ArrayList<>();
        List<Document> schools = context.get("schools");
        ExamProject project = context.get("project");

        for (Document school : schools) {
            String schoolId = school.getString("school");
            List<Document> schoolClasses = new ArrayList<>();

            LOG.info("导入学校 " + schoolId + "(" + school.getString("name") + ") 班级信息...");

            JSONArray jsonArray = interfaceClient.queryExamClassByProject(projectId, schoolId, false);

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
                        .append("province", school.getString("province"))
                        .append("md5", MD5.digest(UUID.randomUUID().toString()));

                schoolClasses.add(schoolClass);
                classes.add(schoolClass);               // 之后查询学生用
            });

            classService.saveProjectSchoolClasses(projectId, schoolId, schoolClasses);
        }

        context.put("classes", classes);
    }

    /**
     * 导入考生列表
     *
     * @param projectId 考试项目ID
     * @param context   上下文对象
     */
    private void importStudents(String projectId, Context context) {
        List<Document> classes = context.get("classes");
        int classCount = classes.size();
        int index = 0;

        for (Document classDoc : classes) {
            String classId = classDoc.getString("class");
            index++;
            LOG.info("导入班级 " + classId + " 的考生信息(" + index + "/" + classCount + ")...");

            List<Document> classStudents = new ArrayList<>();
            JSONArray students = interfaceClient.queryClassExamStudent(projectId, classId);
            students.forEach(o -> {
                JSONObject studentObj = (JSONObject) o;
                Document studentDoc = new Document()
                        .append("project", projectId)
                        //导入学生考号
                        .append("examNo", studentObj.get("examNo"))
                        //导入学校自定义卡号
                        .append("customExamNo", studentObj.get("customExamNo"))
                        .append("student", studentObj.get("id"))
                        .append("name", studentObj.get("name"))
                        .append("class", classDoc.get("class"))
                        .append("school", classDoc.get("school"))
                        .append("area", classDoc.get("area"))
                        .append("city", classDoc.get("city"))
                        .append("province", classDoc.get("province"))
                        .append("md5", MD5.digest(UUID.randomUUID().toString()));

                classStudents.add(studentDoc);
            });

            studentService.saveProjectClassStudents(projectId, classId, classStudents);
        }
    }

    private Map<String, Double> formatRankLevel(Map<String, Object> m) {
        Map<String, Double> rankLevels = new HashMap<>();
        Set<String> keys = m.keySet();
        for (String key : keys) {
            double d = Double.parseDouble(m.get(key).toString());
            rankLevels.put(key, d / 100);
        }
        return rankLevels;
    }

    //判断是否文理分科
    private boolean JudgeCombine(List<String> modelSubjects) {
        if (null != modelSubjects && !modelSubjects.isEmpty()) {
            for (String subject : modelSubjects)
                if (subject.equals("004005006") || subject.equals("007008009")) return true;
            return false;
        }
        return false;
    }

    // 仅导入题目数据，用于修改标答后的重新算分
    // 注意如果 importQuests() 方法对 context 参数进行了读取操作，则本方法可能会失效，因为
    // 传过去的是一个空的 Context。
    public void importProjectQuest(String projectId) {
        importQuests(projectId, new Context());
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

    private Boolean isObjective(String questType, String subObjTag) {
        if (questType == null) {
            //如果questType为空，则根据subObjTag来判断是否为客观题
            return null != subObjTag && subObjTag.equals("o");
        } else {
            return "0".equals(questType) || "1".equals(questType) || "2".equals(questType);
        }
    }

    //判断改题目是否是选做题
    private boolean isOptionalQuest(Map<String, Object> optionalMap, String subjectId, String paperQuestNum) {
        if (null == optionalMap || optionalMap.isEmpty()) {
            return false;
        }
        List<String> o = (List<String>) optionalMap.get(subjectId);
        return o.contains(paperQuestNum);
    }

    public Map<String, Double> gatherQuestScoreBySubject(JSONArray jsonQuest, Map<String, Object> optionalQuestMap) {
        List<Map<String, Object>> quests = new ArrayList<>();
        Map<String, Object> optionalMap = getOptionalQuestNo(optionalQuestMap);

        //过滤选做题
        jsonQuest.stream().filter(quest -> {
            JSONObject questObj = (JSONObject) quest;
            String cardSubjectId = questObj.getString("cardSubjectId");
            return !isOptionalQuest(optionalMap, cardSubjectId, questObj.getString("paperQuestNum"));
        }).forEach(quest -> {
            JSONObject questObj = (JSONObject) quest;
            Map<String, Object> questMap = new HashMap<>();
            questMap.put("subjectId", questObj.getString("subjectId"));
            questMap.put("score", questObj.getDouble("score"));
            quests.add(questMap);
        });

        return quests.stream().collect(
                //分组计算各科的题目总分
                Collectors.groupingBy(quest -> quest.get("subjectId").toString(),
                        Collectors.summingDouble(quest -> MapUtils.getDouble(quest, "score"))
                )
        );
    }

    //获取选做题题号
    public Map<String, Object> getOptionalQuestNo(Map<String, Object> optionalQuestMap) {
        List<String> subjectInOptional = new ArrayList<>(optionalQuestMap.keySet());
        Map<String, Object> map = new HashMap<>();
        for (String subjectId : subjectInOptional) {
            List<Map<String, Object>> optionals = (List<Map<String, Object>>) optionalQuestMap.get(subjectId);
            List<String> withOut = new ArrayList<>();
            optionals.forEach(option -> {
                List<String> quest_nos = (List<String>) option.get("quest_nos");
                int choose_count = MapUtils.getInteger(option, "choose_count");
                withOut.addAll(quest_nos.subList(choose_count, quest_nos.size()));
            });
            map.put(subjectId, withOut);
        }
        return map;
    }

    private void importNormalSubjects(String projectId, Context context) {
        JSONArray jsonArray = interfaceClient.querySubjectListByProjectId(projectId);
        if (jsonArray == null) {
            LOG.info("没有项目 " + projectId + " 的科目信息。");
            return;
        }

        //查询题目信息
        JSONArray jsonQuest = interfaceClient.queryQuestionByProject(projectId);

        //获取选做题
        Map<String, Object> optionalQuestMap = interfaceClient.queryQuestionByProject(projectId, true);

        //统计出每个考试的总分
        Map<String, Double> subjectScore = gatherQuestScoreBySubject(jsonQuest, optionalQuestMap);

        List<String> subjects = new ArrayList<>();
        Value<Double> projectFullScore = Value.of(0d);
        //文综满分
        Value<Double> artsFullScore = Value.of(0d);
        //理综满分
        Value<Double> scienceFullScore = Value.of(0d);

        jsonArray.forEach(o -> {
            JSONObject subjectObj = (JSONObject) o;
            String subjectId = subjectObj.getString("subjectId");
            Double fullScore = subjectObj.getDouble("totalScore");
            // 科目没有录入或没有答题卡
            if (fullScore == null) {
                LOG.error("科目'" + subjectId + "'没有总分: " + jsonArray);
                return;
            }
            //累加考试项目满分
            projectFullScore.set(projectFullScore.get() + fullScore);
            subjects.add(subjectId);
            fullScoreService.saveFullScore(projectId, Target.subject(subjectId), fullScore);  // 保存科目总分
        });

        processCombine(projectId, subjectScore, artsFullScore, scienceFullScore);

        subjectService.saveProjectSubjects(projectId, subjects);  // 保存科目列表
        fullScoreService.saveFullScore(projectId, Target.project(projectId), projectFullScore.get());  // 保存项目总分
        context.put("subjectList", subjects);
    }

    private void processCombine(String projectId, Map<String, Double> subjects, Value<Double> artsFullScore, Value<Double> scienceFullScore) {
        List<String> subjectIds = new ArrayList<>(subjects.keySet());
        //组合科目ID列表
        List<String> combinedSubjectIds = new ArrayList<>();
        if (subjectIds.containsAll(Arrays.asList("007", "008", "009"))) {
            combinedSubjectIds.add("007008009");
            artsFullScore.set(artsFullScore.get() + subjects.get("007") + subjects.get("008") + subjects.get("009"));
            fullScoreService.saveFullScore(projectId, Target.subjectCombination("007008009"), artsFullScore.get());
        } else if (subjectIds.contains("007008009")) {
            combinedSubjectIds.add("007008009");
            artsFullScore.set(artsFullScore.get() + subjects.get("007008009"));
            fullScoreService.saveFullScore(projectId, Target.subjectCombination("007008009"), artsFullScore.get());
        }

        if (subjectIds.containsAll(Arrays.asList("004", "005", "006"))) {
            combinedSubjectIds.add("004005006");
            scienceFullScore.set(scienceFullScore.get() + subjects.get("004") + subjects.get("005") + subjects.get("006"));
            fullScoreService.saveFullScore(projectId, Target.subjectCombination("004005006"), scienceFullScore.get());
        } else if (subjectIds.contains("004005006")) {
            combinedSubjectIds.add("004005006");
            scienceFullScore.set(scienceFullScore.get() + subjects.get("004005006"));
            fullScoreService.saveFullScore(projectId, Target.subjectCombination("004005006"), scienceFullScore.get());
        }

        subjectCombinationService.saveProjectSubjectCombinations(projectId, combinedSubjectIds);
    }

    private void importSlicedSubjects(String projectId, Context context) {
        JSONArray jsonArray = interfaceClient.querySubjectListByProjectId(projectId);
        //查询题目信息
        JSONArray jsonQuest = interfaceClient.queryQuestionByProject(projectId);
        //获取选做题
        Map<String, Object> optionalQuestMap = interfaceClient.queryQuestionByProject(projectId, true);

        //统计出每个考试的总分
        Map<String, Double> subjectScore = gatherQuestScoreBySubject(jsonQuest, optionalQuestMap);
        LOG.info("CMS试题明细接口计算出的科目总分为：{}", subjectScore.toString());
        if (jsonArray == null) {
            LOG.info("没有项目 " + projectId + " 的科目信息。");
            return;
        }

        List<String> subjects = new ArrayList<>();
        Value<Double> projectFullScore = Value.of(0d);
        //文综满分
        Value<Double> artsFullScore = Value.of(0d);
        //理综满分
        Value<Double> scienceFullScore = Value.of(0d);
        List<String> subjectIds = new ArrayList<>();
        jsonArray.forEach(o -> {
            JSONObject subjectObj = (JSONObject) o;
            String subjectId = subjectObj.getString("subjectId");
            //找到综合科目ID，进行拆分
            if (subjectId.length() > SUBJECT_LENGTH) {
                subjectIds.addAll(separateSubject(subjectId));
            } else {
                subjectIds.add(subjectObj.getString("subjectId"));
            }
        });

        for (String subjectId : subjectIds) {
            Double fullScore = MapUtils.getDouble(subjectScore, subjectId);
            // 科目没有录入或没有答题卡
            if (fullScore == null) {
                LOG.error("科目'" + subjectId + "'没有总分: " + jsonArray);
                continue;
            }
            projectFullScore.set(projectFullScore.get() + fullScore);
            subjects.add(subjectId);
            fullScoreService.saveFullScore(projectId, Target.subject(subjectId), fullScore);  // 保存科目总分
        }

        processCombine(projectId, subjectScore, artsFullScore, scienceFullScore);

        subjectService.saveProjectSubjects(projectId, subjects);        // 保存科目列表
        fullScoreService.saveFullScore(projectId, Target.project(projectId), projectFullScore.get());  // 保存项目总分
        context.put("subjectList", subjects);
    }

    //拆分科目并保存科目信息
    public List<String> separateSubject(String subjectId) {
        List<String> subjectIds = new ArrayList<>();
        if (subjectId.length() % SUBJECT_LENGTH != 0) {
            throw new IllegalArgumentException("综合科目ID不合法，导入科目失败：" + subjectId);
        }
        for (int i = 0; i < subjectId.length(); i += SUBJECT_LENGTH) {
            subjectIds.add(subjectId.substring(i, i + SUBJECT_LENGTH));
        }
        return subjectIds;
    }

    //从zip包读取学生信息
    public void importStudentInfoFromZip(ZipFileReader zipFileReader) throws Exception {
        zipFileReader.readZipEntries("*", consumer -> readEntry(consumer, zipFileReader));
    }

    private void readEntry(ZipEntry entry, ZipFileReader zipFileReader) {
        //文件名为projectId_subjectId.json
        String fileName = entry.getName().substring(0, entry.getName().lastIndexOf("."));
        String projectId = fileName.split("_")[0];
        String subjectId = fileName.split("_")[1];
        zipFileReader.readEntryByLine(entry, "UTF-8", line -> readEntryLine(line, projectId, subjectId));
    }

    private void readEntryLine(String line, String projectId, String subjectId) {
        //获取每个学生document对象
        Document studentDoc = Document.parse(line.trim());
        AtomicInteger counter = new AtomicInteger();
        scannerDBService.importStudentScore(projectId, subjectId, studentDoc, counter);
        //文件导入方式
        //scannerDBService.importSubjectScore(projectId, subjectId);
    }

}
