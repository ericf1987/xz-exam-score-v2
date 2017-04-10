package com.xz.examscore.api.server.customization;

import com.hyd.appserver.utils.StringUtils;
import com.xz.ajiaedu.common.lang.Result;
import com.xz.examscore.api.Param;
import com.xz.examscore.api.annotation.Function;
import com.xz.examscore.api.annotation.Parameter;
import com.xz.examscore.api.annotation.Type;
import com.xz.examscore.api.server.Server;
import com.xz.examscore.bean.Range;
import com.xz.examscore.bean.Target;
import com.xz.examscore.services.*;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author by fengye on 2017/4/6.
 */
@Function(description = "学生评测报告（按排名）", parameters = {
        @Parameter(name = "projectId", type = Type.String, description = "考试项目ID", required = true),
        @Parameter(name = "pageSize", type = Type.String, description = "每页查询学生数量", required = true),
        @Parameter(name = "pageCount", type = Type.String, description = "页码数", required = true)
})
@Service
public class StudentEvaluationByRankAnalysis implements Server {

    @Autowired
    ProjectConfigService projectConfigService;

    @Autowired
    ProjectService projectService;

    @Autowired
    ProvinceService provinceService;

    @Autowired
    SchoolService schoolService;

    @Autowired
    ClassService classService;

    @Autowired
    StudentService studentService;

    @Autowired
    RankService rankService;

    @Autowired
    ScoreService scoreService;

    @Autowired
    SubjectService subjectService;

    @Autowired
    SubjectCombinationService subjectCombinationService;

    @Autowired
    StudentEvaluationFormAnalysis studentEvaluationFormAnalysis;

    @Autowired
    CollegeEntryLevelService collegeEntryLevelService;

    @Autowired
    CollegeEntryLevelAverageService collegeEntryLevelAverageService;

    @Override
    public Result execute(Param param) throws Exception {
        String projectId = param.getString("projectId");
        Range provinceRange = Range.province(provinceService.getProjectProvince(projectId));
        Target projectTarget = Target.project(projectId);
        int pageSize = Integer.valueOf(param.getString("pageSize"));
        int pageCount = Integer.valueOf(param.getString("pageCount"));
        Document projectDoc = projectService.findProject(projectId);
        String category = projectDoc.getString("category");
        double rankScore = rankService.getRankScore(projectId, provinceRange, projectTarget, getRankByProject(projectId));
        List<Document> listByScore = scoreService.getListByScore(projectId, provinceRange, projectTarget, rankScore);

        List<String> subjectIds = subjectService.querySubjects(projectId);
        //存放文理单科
        List<String> wlSubjectIds = subjectIds.stream().filter(wl -> SubjectCombinationService.isW(wl) || SubjectCombinationService.isL(wl)).collect(Collectors.toList());
        //综合科目
        List<String> combinedSubjectIds = subjectCombinationService.getAllSubjectCombinations(projectId);

        List<Map<String, Object>> resultList = new ArrayList<>();

        //根据得分从高到低排名
        Collections.sort(listByScore, (Document d1, Document d2) -> {
            Double totalScore1 = d1.getDouble("totalScore");
            Double totalScore2 = d2.getDouble("totalScore");
            return totalScore2.compareTo(totalScore1);
        });

        List<String> studentIds = listByScore.stream().map(l -> {
            Document range = l.get("range", Document.class);
            return range.getString("id");
        }).collect(Collectors.toList());

        //全科参考学生
        int studentCount = studentService.getStudentCount(projectId, Range.province(provinceService.getProjectProvince(projectId)), Target.project(projectId));

        //本科上线预测
        List<Double> entryLevelScoreLine = projectConfigService.getEntryLevelScoreLine(projectId,
                Range.province(provinceService.getProjectProvince(projectId)), Target.project(projectId), studentCount);

        //人数中的最高最低分
        Double max = listByScore.get(0).getDouble("totalScore");
        Double min = listByScore.get(listByScore.size() - 1).getDouble("totalScore");

        LinkedList<Double> scoreLine = new LinkedList<>(entryLevelScoreLine);
        scoreLine.addFirst(max);
        scoreLine.addLast(min);

        List<List<Map<String, Object>>> entryLevelList = new ArrayList<>();
        studentEvaluationFormAnalysis.queryRangeAverageInEntryLevel(projectId, provinceRange, subjectIds, combinedSubjectIds, entryLevelList);

        for (String studentId : studentIds.subList(pageSize * (pageCount - 1), pageSize * pageCount)) {

            Document studentDoc = studentService.findStudent(projectId, studentId);
            String schoolId = studentDoc.getString("school");
            String classId = studentDoc.getString("class");

            Map<String, Object> studentMap = new HashMap<>();
            //统计基础信息
            Map<String, String> studentBaseInfo = new HashMap<>();
            studentBaseInfo.put("studentId", studentId);
            studentBaseInfo.put("studentName", studentDoc.getString("name"));
            studentBaseInfo.put("className", classService.getClassName(projectId, classId));
            studentBaseInfo.put("schoolName", schoolService.getSchoolName(projectId, schoolId));
            studentBaseInfo.put("category", category);
            studentMap.put("studentBaseInfo", studentBaseInfo);

            //查询得分及排名
            Map<String, Object> scoreAndRankMap = new HashMap<>();
            scoreAndRankMap.put("project", studentEvaluationFormAnalysis.getScoreAndRankMap(projectId, schoolId, classId, studentId, Target.project(projectId)));
            //语数外得分及排名
            List<Map<String, Object>> subjectScoreAndRank = new ArrayList<>();
            subjectIds.stream().filter(s -> !SubjectCombinationService.isW(s) && !SubjectCombinationService.isL(s)).forEach(subjectId -> {
                Map<String, Object> map = studentEvaluationFormAnalysis.getSingleSubjectRankAndLevel(projectId, schoolId, classId, studentId, Target.subject(subjectId), studentEvaluationFormAnalysis.getQuestTypeScoreMap(projectId, studentId, subjectId), studentEvaluationFormAnalysis.getPointScoreMap(projectId, studentId, subjectId), studentEvaluationFormAnalysis.getSubjectAbilityLevel(projectId, studentId, subjectId));
                subjectScoreAndRank.add(map);
            });
            scoreAndRankMap.put("subjects", subjectScoreAndRank);

            //文理单科得分及排名
            List<Map<String, Object>> wlSubjectScoreAndRank = new ArrayList<>();
            wlSubjectIds.forEach(wlSubjectId -> {
                Map<String, Object> map = studentEvaluationFormAnalysis.getSingleSubjectRankAndLevel(projectId, schoolId, classId, studentId, Target.subject(wlSubjectId),
                        studentEvaluationFormAnalysis.getQuestTypeScoreMap(projectId, studentId, wlSubjectId),
                        studentEvaluationFormAnalysis.getPointScoreMap(projectId, studentId, wlSubjectId),
                        studentEvaluationFormAnalysis.getSubjectAbilityLevel(projectId, studentId, wlSubjectId));
                wlSubjectScoreAndRank.add(map);
            });
            scoreAndRankMap.put("wlSubjects", wlSubjectScoreAndRank);

            //查询组合科目得分及排名
            List<Map<String, Object>> combinedSubjectScoreAndRank = new ArrayList<>();
            combinedSubjectIds.forEach(combinedSubjectId -> {
                Map<String, Object> map = studentEvaluationFormAnalysis.getScoreAndRankMap(projectId, schoolId, classId, studentId, Target.subjectCombination(combinedSubjectId));
                combinedSubjectScoreAndRank.add(map);
            });

            scoreAndRankMap.put("combinedSubjects", combinedSubjectScoreAndRank);

            studentMap.put("scoreAndRankMap", scoreAndRankMap);

            resultList.add(studentMap);
        }

        return Result.success().set("scoreLine", scoreLine).set("entryLevelList", entryLevelList).set("studentList", resultList);
    }

    /**
     * 根据考试ID获取指定的排名
     *
     * @param projectId 项目ID
     * @return 返回
     */
    private int getRankByProject(String projectId) {
        Document projectDoc = projectService.findProject(projectId);
        String category = projectDoc.getString("category");
        if (StringUtils.isBlank(category))
            return 300;
        else
            return category.equals("W") ? 150 : 300;
    }
}
