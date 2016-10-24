package com.xz.examscore.api.server.school;

import com.xz.ajiaedu.common.lang.Result;
import com.xz.examscore.api.Param;
import com.xz.examscore.api.annotation.Function;
import com.xz.examscore.api.annotation.Parameter;
import com.xz.examscore.api.annotation.Type;
import com.xz.examscore.api.server.Server;
import com.xz.examscore.api.server.project.ProjectCollegeEntryLevelAnalysis;
import com.xz.examscore.bean.Range;
import com.xz.examscore.bean.Target;
import com.xz.examscore.services.SubjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.xz.examscore.api.server.project.ProjectTopStudentStat.filterSubject;

/**
 * @author by fengye on 2016/10/24.
 */
@Function(description = "学校成绩-学校本科录取情况", parameters = {
        @Parameter(name = "projectId", type = Type.String, description = "考试项目ID", required = true),
        @Parameter(name = "schoolId", type = Type.String, description = "学校id", required = true),
        @Parameter(name = "rankSegment", type = Type.StringArray, description = "排名分段", required = true),
        @Parameter(name = "authSubjectIds", type = Type.StringArray, description = "可访问科目范围，为空返回所有", required = false)
})
@Service
public class SchoolCollegeEntryLevelAnalysis implements Server{

    @Autowired
    ProjectCollegeEntryLevelAnalysis projectCollegeEntryLevelAnalysis;

    @Autowired
    SubjectService subjectService;

    @Override
    public Result execute(Param param) throws Exception {
        String projectId = param.getString("projectId");
        String schoolId = param.getString("schoolId");
        String[] rankSegment = param.getStringValues("rankSegment");
        String[] authSubjectIds = param.getStringValues("authSubjectIds");

        Range range = Range.school(schoolId);
        Target target = Target.project(projectId);
        List<String> subjectIds = new ArrayList<>(subjectService.querySubjects(projectId));
        subjectIds = filterSubject(subjectIds, authSubjectIds);
        subjectIds.sort(String::compareTo);

        List<Map<String, Object>> topStudents = projectCollegeEntryLevelAnalysis.getEntryLevelStudents(projectId, rankSegment, range, target, subjectIds);
        return Result.success().set("students", topStudents).set("hasHeader", !topStudents.isEmpty());
    }
}
