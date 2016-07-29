package com.xz.api.server.classes;

import com.xz.ajiaedu.common.lang.Result;
import com.xz.api.Param;
import com.xz.api.annotation.Function;
import com.xz.api.annotation.Parameter;
import com.xz.api.annotation.Type;
import com.xz.api.server.Server;
import com.xz.bean.ProjectConfig;
import com.xz.bean.Range;
import com.xz.bean.Target;
import com.xz.services.*;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author by fengye on 2016/7/21.
 */
@Function(description = "班级成绩-等第统计分析", parameters = {
        @Parameter(name = "projectId", type = Type.String, description = "考试项目ID", required = true),
        @Parameter(name = "classId", type = Type.String, description = "班级Id", required = false)
})
@Service
public class ClassRankLevelAnalysis implements Server {

    @Autowired
    TargetService targetService;

    @Autowired
    StudentService studentService;

    @Autowired
    SchoolService schoolService;

    @Autowired
    ClassService classService;

    @Autowired
    RankLevelService rankLevelService;

    @Autowired
    ProjectConfigService projectConfigService;

    @Autowired
    ScoreService scoreService;

    @Autowired
    SubjectService subjectService;

    @Override
    public Result execute(Param param) throws Exception {
        String projectId = param.getString("projectId");
        String classId = param.getString("classId");

        ProjectConfig projectConfig = projectConfigService.getProjectConfig(projectId);
        String lastRankLevel = projectConfig.getLastRankLevel();

        List<Map<String, Object>> studentInfos = new ArrayList<>();

        List<String> subjectIds = subjectService.querySubjects(projectId);

        //遍历班级学生
        List<Document> studentDoc = studentService.getStudentList(projectId, Range.clazz(classId));
        for (Document student : studentDoc) {
            String studentName = student.getString("name");
            String studentId = student.getString("student");
            //查询班级中的学生在学校的等第排名
            List<Map<String, Object>> subjectList = new ArrayList<>();
            //学生的各科成绩分数和等级
            Map<String, Object> studentInfo = new HashMap<>();
            studentInfo.put("studentName", studentName);
            studentInfo.put("studentId", studentId);
            double score = scoreService.getScore(projectId, Range.student(studentId), Target.project(projectId));
            studentInfo.put("projectScore", score);
            String ProjectRankLevel = rankLevelService.getRankLevel(projectId, studentId, Target.project(projectId), Range.SCHOOL, lastRankLevel);
            studentInfo.put("ProjectRankLevel", ProjectRankLevel);


            for(String subjectId : subjectIds){
                Target target = targetService.getTarget(projectId, subjectId);
                String subjectRankLevel = rankLevelService.getRankLevel(projectId, studentId, target, Range.SCHOOL, lastRankLevel);
                double subjectScore = scoreService.getScore(projectId, Range.student(studentId), target);
                Map<String, Object> subjectMap = new HashMap<>();
                subjectMap.put("subjectRankLevel", subjectRankLevel);
                subjectMap.put("subjectScore", subjectScore);
                subjectMap.put("subjectId", subjectId);
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

        Result result = new Result()
                .set("subjectList", subjectIds)
                .set("studentInfos", studentInfos);

        System.out.println(result.getData());
        return result;
    }

    public List<String> getRankLevelParams(String projectId, String subjectId) {
        ProjectConfig projectConfig = projectConfigService.getProjectConfig(projectId);
        Map<String, Double> rankLevels = projectConfig.getRankLevels();

        Iterator<String> it = rankLevels.keySet().iterator();

        List<String> rankLevelParam = new ArrayList<>();
        while (it.hasNext()) {
            rankLevelParam.add(it.next());
        }

        return subjectId == null ? projectConfig.getRankLevelCombines() : rankLevelParam;
    }
}
