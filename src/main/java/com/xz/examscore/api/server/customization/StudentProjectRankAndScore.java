package com.xz.examscore.api.server.customization;

import com.xz.ajiaedu.common.lang.Result;
import com.xz.examscore.api.Param;
import com.xz.examscore.api.annotation.Function;
import com.xz.examscore.api.annotation.Parameter;
import com.xz.examscore.api.annotation.Type;
import com.xz.examscore.api.server.Server;
import com.xz.examscore.bean.Range;
import com.xz.examscore.bean.Target;
import com.xz.examscore.services.*;
import org.apache.commons.collections.MapUtils;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author by fengye on 2017/4/11.
 */
@Function(description = "总体学生排名和分数-简易报表", parameters = {
        @Parameter(name = "projectId", type = Type.String, description = "考试项目ID", required = true)
})
@Service
public class StudentProjectRankAndScore implements Server{

    @Autowired
    ProjectService projectService;

    @Autowired
    ProvinceService provinceService;

    @Autowired
    StudentEvaluationByRankAnalysis studentEvaluationByRankAnalysis;

    @Autowired
    ScoreService scoreService;

    @Autowired
    RankService rankService;

    @Autowired
    StudentService studentService;

    @Autowired
    ClassService classService;

    @Autowired
    SchoolService schoolService;

    @Override
    public Result execute(Param param) throws Exception {
        String projectId = param.getString("projectId");
        return mainProcess(projectId);
    }

    private Result mainProcess(String projectId) {
        Range provinceRange = Range.province(provinceService.getProjectProvince(projectId));
        Target projectTarget = Target.project(projectId);
        double rankScore = rankService.getRankScore(projectId, provinceRange, projectTarget, 300);
        List<Document> listByScore = scoreService.getListByScore(projectId, provinceRange, projectTarget, rankScore);


        List<Map<String, Object>> result = new ArrayList<>();
        for(Document doc : listByScore){
            Document range = doc.get("range", Document.class);
            String studentId = range.getString("id");
            Document studentDoc = studentService.findStudent(projectId, studentId);
            Map<String, Object> studentBaseInfo = new HashMap<>();

            double totalScore = doc.getDouble("totalScore");

            studentBaseInfo.put("studentId", studentId);
            studentBaseInfo.put("studentName", studentDoc.getString("name"));
            studentBaseInfo.put("className", classService.getClassName(projectId, studentDoc.getString("class")));
            studentBaseInfo.put("schoolName", schoolService.getSchoolName(projectId, studentDoc.getString("school")));
            studentBaseInfo.put("totalScore", totalScore);
            studentBaseInfo.put("totalRank", rankService.getRank(projectId, provinceRange, projectTarget, totalScore));

            result.add(studentBaseInfo);
        }
        Collections.sort(result, (Map<String, Object> m1, Map<String, Object> m2) -> {
            Double s1 = MapUtils.getDouble(m1, "totalScore");
            Double s2 = MapUtils.getDouble(m2, "totalScore");
            return s2.compareTo(s1);
        });
        return Result.success().set("students", result);
    }

}
