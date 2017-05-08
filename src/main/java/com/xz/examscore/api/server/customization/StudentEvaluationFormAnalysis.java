package com.xz.examscore.api.server.customization;

import com.hyd.appserver.utils.StringUtils;
import com.mongodb.client.FindIterable;
import com.xz.ajiaedu.common.lang.Result;
import com.xz.ajiaedu.common.lang.StringUtil;
import com.xz.ajiaedu.common.ajia.Param;
import com.xz.examscore.api.annotation.Function;
import com.xz.examscore.api.annotation.Parameter;
import com.xz.examscore.api.annotation.Type;
import com.xz.examscore.api.server.Server;
import com.xz.examscore.api.server.classes.ClassAbilityLevelAnalysis;
import com.xz.examscore.api.server.classes.ClassPointAnalysis;
import com.xz.examscore.api.server.customization.baseQuery.StudentEvaluationBaseQuery;
import com.xz.examscore.api.server.project.ProjectPointAbilityLevelAnalysis;
import com.xz.examscore.api.server.project.ProjectQuestTypeAnalysis;
import com.xz.examscore.bean.Range;
import com.xz.examscore.bean.Target;
import com.xz.examscore.services.*;
import com.xz.examscore.util.DoubleUtils;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static com.xz.ajiaedu.common.mongo.MongoUtils.doc;

/**
 * @author by fengye on 2016/12/8.
 */
@Function(description = "学生测评报告分析", parameters = {
        @Parameter(name = "projectId", type = Type.String, description = "考试项目ID", required = true),
        @Parameter(name = "schoolId", type = Type.String, description = "学校ID", required = false),
        @Parameter(name = "classId", type = Type.String, description = "班级ID", required = false),
        @Parameter(name = "pageSize", type = Type.String, description = "每页查询学生数量", required = false),
        @Parameter(name = "pageCount", type = Type.String, description = "页码数", required = false)
})
@Service
public class StudentEvaluationFormAnalysis implements Server {

    @Autowired
    SchoolService schoolService;

    @Autowired
    ClassService classService;

    @Autowired
    StudentService studentService;

    @Autowired
    ProjectService projectService;

    @Autowired
    ScoreService scoreService;

    @Autowired
    RankService rankService;

    @Autowired
    SubjectService subjectService;

    @Autowired
    TargetService targetService;

    @Autowired
    MinMaxScoreService minMaxScoreService;

    @Autowired
    AverageService averageService;

    @Autowired
    ProjectConfigService projectConfigService;

    @Autowired
    ProvinceService provinceService;

    @Autowired
    QuestTypeService questTypeService;

    @Autowired
    ScoreRateService scoreRateService;

    @Autowired
    QuestTypeScoreService questTypeScoreService;

    @Autowired
    ClassPointAnalysis classPointAnalysis;

    @Autowired
    PointService pointService;

    @Autowired
    AbilityLevelService abilityLevelService;

    @Autowired
    QuestService questService;

    @Autowired
    ProjectPointAbilityLevelAnalysis projectPointAbilityLevelAnalysis;

    @Autowired
    ProjectQuestTypeAnalysis projectQuestTypeAnalysis;

    @Autowired
    ClassAbilityLevelAnalysis classAbilityLevelAnalysis;

    @Autowired
    SubjectCombinationService subjectCombinationService;

    @Autowired
    CollegeEntryLevelService collegeEntryLevelService;

    @Autowired
    CollegeEntryLevelAverageService collegeEntryLevelAverageService;

    @Autowired
    FullScoreService fullScoreService;

    @Autowired
    StudentCompetitiveService studentCompetitiveService;

    @Autowired
    StudentEvaluationBaseQuery studentEvaluationBaseQuery;

    public static final int PAGE_SIZE_DEFAULT = 10;

    public static final int PAGE_COUNT_DEFAULT = 0;

    public static final Document SORT = doc("school", 1).append("class", 1).append("student", 1);

    public static final Document PROJECTION = doc("student", 1).append("name", 1).append("school", 1).append("class", 1);

    @Override
    public Result execute(Param param) throws Exception {
        String schoolId = param.getString("schoolId");
        String classId = param.getString("classId");
        if (!StringUtil.isBlank(schoolId) && !StringUtils.isBlank(classId)) {
            return mainProcess1(param);
        }
        return mainProcess2(param);
    }

    private Result mainProcess1(Param param) {
        StudentEvaluationParam studentEvaluationParam = new StudentEvaluationParam(param).invoke();
        String projectId = studentEvaluationParam.getProjectId();
        String classId = studentEvaluationParam.getClassId();
        int pageSize = studentEvaluationParam.getPageSize();
        int pageCount = studentEvaluationParam.getPageCount();
        String schoolId = studentEvaluationParam.getSchoolId();
        String category = studentEvaluationParam.getCategory();
        Range provinceRange = studentEvaluationParam.getProvinceRange();
        List<String> subjectIds = studentEvaluationParam.getSubjectIds();
        List<String> wlSubjectIds = studentEvaluationParam.getWlSubjectIds();
        List<String> combinedSubjectIds = studentEvaluationParam.getCombinedSubjectIds();
        List<Double> entryLevelScoreLine = studentEvaluationParam.getEntryLevelScoreLine();

        //获取全部学生，按照学校，班级排序
        FindIterable<Document> projectStudentList = studentService.getProjectStudentList(projectId, Range.clazz(classId),
                pageSize, pageSize * pageCount, PROJECTION, SORT);

        return packingResult(projectId, schoolId, classId, category, provinceRange,
                subjectIds, wlSubjectIds, combinedSubjectIds, entryLevelScoreLine, projectStudentList);
    }

    //获取整个项目的数据，明细数据按照学校，班级排序
    private Result mainProcess2(Param param) {
        StudentEvaluationParam studentEvaluationParam = new StudentEvaluationParam(param).invoke();
        String projectId = studentEvaluationParam.getProjectId();
        String classId = studentEvaluationParam.getClassId();
        int pageSize = studentEvaluationParam.getPageSize();
        int pageCount = studentEvaluationParam.getPageCount();
        String schoolId = studentEvaluationParam.getSchoolId();
        String category = studentEvaluationParam.getCategory();
        Range provinceRange = studentEvaluationParam.getProvinceRange();
        List<String> subjectIds = studentEvaluationParam.getSubjectIds();
        List<String> wlSubjectIds = studentEvaluationParam.getWlSubjectIds();
        List<String> combinedSubjectIds = studentEvaluationParam.getCombinedSubjectIds();
        List<Double> entryLevelScoreLine = studentEvaluationParam.getEntryLevelScoreLine();

        FindIterable<Document> projectStudentList = studentService.getProjectStudentList(projectId, provinceRange,
                pageSize, pageSize * pageCount, PROJECTION, SORT);

        return packingResult(projectId, schoolId, classId, category, provinceRange,
                subjectIds, wlSubjectIds, combinedSubjectIds, entryLevelScoreLine, projectStudentList);
    }

    //封装结果并返回
    private Result packingResult(String projectId, String schoolId, String classId, String category, Range provinceRange,
                                 List<String> subjectIds, List<String> wlSubjectIds, List<String> combinedSubjectIds,
                                 List<Double> entryLevelScoreLine, FindIterable<Document> projectStudentList) {
        //结果参数-追加项目最高最低分
        List<Double> scoreLine = paddingMaxMinScore(projectId, entryLevelScoreLine);

        //结果参数-本科线
        List<List<Map<String, Object>>> entryLevelList = new ArrayList<>();
        queryRangeAverageInEntryLevel(projectId, provinceRange, subjectIds, combinedSubjectIds, entryLevelList);

        List<Map<String, Object>> resultList = new ArrayList<>();

        for (Document studentDoc : projectStudentList) {
            Map<String, Object> studentMap = new HashMap<>();
            //统计基础信息
            String studentId = studentDoc.getString("student");
            if(!isRequiredStudent(projectId, studentId, subjectIds)){
                continue;
            }

            //单个学生基础信息
            getStudentBaseInfo(projectId, category, studentDoc, studentMap, studentId);

            //获取学生分数和排名信息
            getStudentScoreAndRank(projectId, schoolId, classId, subjectIds, wlSubjectIds, combinedSubjectIds, studentMap, studentId);

            resultList.add(studentMap);

        }
        return Result.success().set("scoreLine", scoreLine).set("entryLevelList", entryLevelList).set("studentList", resultList);
    }

    //获取学生分数排名数据信息
    private void getStudentScoreAndRank(String projectId, String schoolId, String classId, List<String> subjectIds, List<String> wlSubjectIds, List<String> combinedSubjectIds, Map<String, Object> studentMap, String studentId) {
        //查询得分及排名
        Map<String, Object> scoreAndRankMap = new HashMap<>();

        //全科得分及排名
        scoreAndRankMap.put("project", studentEvaluationBaseQuery.getScoreAndRankMap(projectId, schoolId, classId, studentId, Target.project(projectId)));

        //语数外得分及排名
        List<Map<String, Object>> subjectScoreAndRank = new ArrayList<>();
        subjectIds.stream().filter(s -> !SubjectCombinationService.isW(s) && !SubjectCombinationService.isL(s)).forEach(subjectId -> {
            Map<String, Object> map = studentEvaluationBaseQuery.getSingleSubjectRankAndLevel(
                    projectId, schoolId, classId, studentId, Target.subject(subjectId),
                    studentEvaluationBaseQuery.getQuestTypeScoreMap(projectId, studentId, subjectId),
                    studentEvaluationBaseQuery.getPointScoreMap(projectId, studentId, subjectId),
                    studentEvaluationBaseQuery.getSubjectAbilityLevel(projectId, studentId, subjectId)
            );
            subjectScoreAndRank.add(map);
        });
        scoreAndRankMap.put("subjects", subjectScoreAndRank);

        //文理单科得分及排名
        List<Map<String, Object>> wlSubjectScoreAndRank = new ArrayList<>();
        wlSubjectIds.forEach(wlSubjectId -> {
            Map<String, Object> map = studentEvaluationBaseQuery.getSingleSubjectRankAndLevel(projectId, schoolId, classId, studentId, Target.subject(wlSubjectId),
                    studentEvaluationBaseQuery.getQuestTypeScoreMap(projectId, studentId, wlSubjectId),
                    studentEvaluationBaseQuery.getPointScoreMap(projectId, studentId, wlSubjectId),
                    studentEvaluationBaseQuery.getSubjectAbilityLevel(projectId, studentId, wlSubjectId));
            wlSubjectScoreAndRank.add(map);
        });
        scoreAndRankMap.put("wlSubjects", wlSubjectScoreAndRank);

        //查询组合科目得分及排名
        List<Map<String, Object>> combinedSubjectScoreAndRank = new ArrayList<>();
        combinedSubjectIds.forEach(combinedSubjectId -> {
            Map<String, Object> map = studentEvaluationBaseQuery.getScoreAndRankMap(projectId, schoolId, classId, studentId,
                    Target.subjectCombination(combinedSubjectId));
            combinedSubjectScoreAndRank.add(map);
        });
        scoreAndRankMap.put("combinedSubjects", combinedSubjectScoreAndRank);

        studentMap.put("scoreAndRankMap", scoreAndRankMap);
    }

    //获取学生基础数据信息
    private void getStudentBaseInfo(String projectId, String category, Document studentDoc, Map<String, Object> studentMap, String studentId) {
        Map<String, String> studentBaseInfo = new HashMap<>();
        studentBaseInfo.put("studentId", studentId);
        studentBaseInfo.put("studentName", studentDoc.getString("name"));
        studentBaseInfo.put("className", classService.getClassName(projectId, studentDoc.getString("class")));
        studentBaseInfo.put("schoolName", schoolService.getSchoolName(projectId, studentDoc.getString("school")));
        studentBaseInfo.put("category", category);
        studentMap.put("studentBaseInfo", studentBaseInfo);
    }


    private List<Double> paddingMaxMinScore(String projectId, List<Double> entryLevelScoreLine) {
        //项目最高最低分
        double[] minMaxScore = minMaxScoreService.getMinMaxScore(projectId, Range.province(provinceService.getProjectProvince(projectId)), Target.project(projectId));
        double min = minMaxScore[0];
        double max = minMaxScore[1];

        LinkedList<Double> scoreLine = new LinkedList<>(entryLevelScoreLine);
        scoreLine.addFirst(max);
        scoreLine.addLast(min);
        return scoreLine;
    }

    public boolean isRequiredStudent(String projectId, String studentId, List<String> subjectIds) {
        //只有全科参考且总分不为0才满足条件
        boolean b = studentService.hasAllSubjects(projectId, studentId, subjectIds);
        double totalScore = scoreService.getScore(projectId, Range.student(studentId), Target.project(projectId));
        return b && totalScore != 0;
    }

    public void queryRangeAverageInEntryLevel(String projectId, Range provinceRange, List<String> subjectIds, List<String> combinedSubjectIds, List<List<Map<String, Object>>> entryLevelList) {
        //查询各个本科录取段内，各个目标维度的平均分
        List<Document> entryLevelDoc = collegeEntryLevelService.getEntryLevelDoc(projectId);
        Collections.sort(entryLevelDoc, (Document d1, Document d2) -> {
            Double s1 = d1.getDouble("score");
            Double s2 = d2.getDouble("score");
            return s2.compareTo(s1);
        });
        List<String> entryLevelKey = entryLevelDoc.stream().map(doc -> doc.getString("level")).collect(Collectors.toList());
        //增加统计本科未上线学生的平均得分
        entryLevelKey.add("OFFLINE");
        for (String key : entryLevelKey) {
            //科目
            List<Map<String, Object>> averagesInLevel = new ArrayList<>();
            for (String subjectId : subjectIds.stream().filter(s -> !SubjectCombinationService.isW(s) && !SubjectCombinationService.isL(s)).collect(Collectors.toList())) {
                Map<String, Object> averageInLevel = studentEvaluationBaseQuery.getAveragesInLevel(projectId, provinceRange, key, subjectId);
                averagesInLevel.add(averageInLevel);
            }
            //组合科目
            for (String combinedSubjectId : combinedSubjectIds) {
                Map<String, Object> averageInLevel = new HashMap<>();
                double average = collegeEntryLevelAverageService.getAverage(projectId, provinceRange, Target.subjectCombination(combinedSubjectId), key);
                String combinedSubjectName = SubjectService.getSubjectName(combinedSubjectId);
                averageInLevel.put("subjectId", combinedSubjectId);
                averageInLevel.put("subjectName", combinedSubjectName);
                averageInLevel.put("average", DoubleUtils.round(average));
                averagesInLevel.add(averageInLevel);
            }
            entryLevelList.add(averagesInLevel);
        }
    }

    private class StudentEvaluationParam {
        private Param param;
        private String projectId;
        private String schoolId;
        private String classId;
        private int pageSize;
        private int pageCount;
        private String category;
        private Range provinceRange;
        private List<String> subjectIds;
        private List<String> wlSubjectIds;
        private List<String> combinedSubjectIds;
        private List<Double> entryLevelScoreLine;

        public StudentEvaluationParam(Param param) {
            this.param = param;
        }

        public String getProjectId() {
            return projectId;
        }

        public String getSchoolId() {
            return schoolId;
        }

        public String getClassId() {
            return classId;
        }

        public int getPageSize() {
            return pageSize;
        }

        public int getPageCount() {
            return pageCount;
        }

        public String getCategory() {
            return category;
        }

        public Range getProvinceRange() {
            return provinceRange;
        }

        public List<String> getSubjectIds() {
            return subjectIds;
        }

        public List<String> getWlSubjectIds() {
            return wlSubjectIds;
        }

        public List<String> getCombinedSubjectIds() {
            return combinedSubjectIds;
        }

        public List<Double> getEntryLevelScoreLine() {
            return entryLevelScoreLine;
        }

        public StudentEvaluationParam invoke() {
            projectId = param.getString("projectId");
            schoolId = param.getString("schoolId");
            classId = param.getString("classId");

            //分页参数
            pageSize = StringUtils.isEmpty(param.getString("pageSize")) ? PAGE_SIZE_DEFAULT : Integer.valueOf(param.getString("pageSize"));
            pageCount = StringUtils.isEmpty(param.getString("pageCount")) ? PAGE_COUNT_DEFAULT : Integer.valueOf(param.getString("pageCount"));

            //项目参数
            Document projectDoc = projectService.findProject(projectId);
            category = projectDoc.getString("category");
            provinceRange = Range.province(provinceService.getProjectProvince(projectId));

            //科目列表
            subjectIds = subjectService.querySubjects(projectId);
            //存放文理单科
            wlSubjectIds = subjectIds.stream().filter(wl ->
                    SubjectCombinationService.isW(wl) || SubjectCombinationService.isL(wl)).collect(Collectors.toList()
            );
            //综合科目
            combinedSubjectIds = subjectCombinationService.getAllSubjectCombinations(projectId);

            //全科参考学生
            int studentCount = studentService.getStudentCount(projectId, Range.province(provinceService.getProjectProvince(projectId)), Target.project(projectId));

            //本科上线预测
            entryLevelScoreLine = projectConfigService.getEntryLevelScoreLine(projectId,
                    Range.province(provinceService.getProjectProvince(projectId)), Target.project(projectId), studentCount);
            return this;
        }
    }
}
