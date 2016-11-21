package com.xz.examscore.api.server.classes;

import com.xz.ajiaedu.common.lang.Result;
import com.xz.examscore.api.Param;
import com.xz.examscore.api.annotation.Function;
import com.xz.examscore.api.annotation.Parameter;
import com.xz.examscore.api.annotation.Type;
import com.xz.examscore.api.server.Server;
import com.xz.examscore.api.server.school.SchoolRankLevelAnalysis;
import com.xz.examscore.bean.ProjectConfig;
import com.xz.examscore.bean.Range;
import com.xz.examscore.bean.Target;
import com.xz.examscore.services.*;
import com.xz.examscore.util.RankLevelFormater;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.xz.examscore.api.server.project.ProjectTopStudentStat.filterSubject;

/**
 * @author by fengye on 2016/7/21.
 */
@Function(description = "班级成绩-等第统计分析", parameters = {
        @Parameter(name = "projectId", type = Type.String, description = "考试项目ID", required = true),
        @Parameter(name = "classId", type = Type.String, description = "班级Id", required = false),
        @Parameter(name = "authSubjectIds", type = Type.StringArray, description = "可访问科目范围，为空返回所有", required = false)
})
@Service
public class ClassRankLevelAnalysis implements Server {

    @Autowired
    TargetService targetService;

    @Autowired
    StudentService studentService;

    @Autowired
    RankLevelService rankLevelService;

    @Autowired
    ProjectConfigService projectConfigService;

    @Autowired
    ScoreService scoreService;

    @Autowired
    SubjectService subjectService;

    @Autowired
    FullScoreService fullScoreService;

    @Autowired
    SchoolRankLevelAnalysis scholRankLevelAnalysis;

    @Override
    public Result execute(Param param) throws Exception {
        String projectId = param.getString("projectId");
        String classId = param.getString("classId");
        String[] authSubjectIds = param.getStringValues("authSubjectIds");

        ProjectConfig projectConfig = projectConfigService.getProjectConfig(projectId);
        String lastRankLevel = projectConfig.getLastRankLevel();
        Map<String, Double> rankLevelsConfig = projectConfig.getRankLevels();
        List<Map<String, Object>> studentInfos = new ArrayList<>();

        List<String> subjectIds = new ArrayList<>(subjectService.querySubjects(projectId));
        subjectIds = filterSubject(subjectIds, authSubjectIds);

        List<Map<String, Object>> subjectScoreList = new ArrayList<>();

        subjectIds.forEach(subjectId -> {
            String subjectName = SubjectService.getSubjectName(subjectId);
            //double score = scoreService.getScore(projectId, Range.clazz(classId), Target.subject(subjectId));
            double fullScore = fullScoreService.getFullScore(projectId, Target.subject(subjectId));
            Map<String, Object> map = new HashMap<>();
            map.put("subjectName", subjectName);
            map.put("score", fullScore);
            subjectScoreList.add(map);
        });

        //遍历班级学生
        List<Document> studentDoc = studentService.getStudentList(projectId, Range.clazz(classId));
        for (Document student : studentDoc) {
            String studentName = student.getString("name");
            String studentId = student.getString("student");
            //查询班级中的学生在学校的等第排名
            List<Map<String, Object>> subjectList = new ArrayList<>();
            //学生的各科成绩分数和等级
            Map<String, Object> studentInfo = new HashMap<>();
            studentInfo.put("examNo", student.getString("examNo"));
            studentInfo.put("studentName", studentName);
            studentInfo.put("studentId", studentId);
            double score = scoreService.getScore(projectId, Range.student(studentId), Target.project(projectId));
            studentInfo.put("projectScore", score);
            String projectRankLevel = rankLevelService.getRankLevel(projectId, studentId, Target.project(projectId), Range.SCHOOL, lastRankLevel);
            studentInfo.put("ProjectRankLevel", RankLevelFormater.format2(projectRankLevel));


            for(String subjectId : subjectIds){
                Target target = targetService.getTarget(projectId, subjectId);
                String subjectRankLevel = rankLevelService.getRankLevel(projectId, studentId, target, Range.SCHOOL, lastRankLevel);
                double subjectScore = scoreService.getScore(projectId, Range.student(studentId), target);
                Map<String, Object> subjectMap = new HashMap<>();
                subjectMap.put("subjectRankLevel", subjectRankLevel);
                subjectMap.put("subjectScore", subjectScore);
                subjectMap.put("subjectId", subjectId);
                subjectMap.put("subjectName", SubjectService.getSubjectName(subjectId));
                //判断学生是否缺考
                boolean isAbsent = scoreService.isStudentAbsent(projectId, studentId, target);
                if(isAbsent){
                    subjectMap.put("isAbsent", isAbsent);
                }
                subjectList.add(subjectMap);
            }
            studentInfo.put("subject", subjectList);
            studentInfos.add(studentInfo);
        }

        //考试科目排序
        Collections.sort(subjectIds, String::compareTo);
        //按照总分升序排序
        Collections.sort(studentInfos, (Map<String, Object> m1, Map<String, Object> m2) -> {
            Double s1 = (Double)m1.get("projectScore");
            Double s2 = (Double)m2.get("projectScore");
            return s2.compareTo(s1);
        });

        return Result.success()
                .set("subjectList", subjectIds)
                .set("subjectScoreList", subjectScoreList)
                .set("studentInfos", studentInfos)
                .set("rankLevelsConfig", rankLevelsConfig)
                .set("hasHeader", !studentInfos.isEmpty());
    }
}
