package com.xz.api.server.school;

import com.xz.ajiaedu.common.lang.Result;
import com.xz.api.Param;
import com.xz.api.annotation.Function;
import com.xz.api.annotation.Parameter;
import com.xz.api.annotation.Type;
import com.xz.api.server.Server;
import com.xz.bean.Range;
import com.xz.bean.Target;
import com.xz.services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.xz.api.server.project.ProjectTopStudentStat.getTopStudents;

/**
 * 学校成绩-尖子生统计
 *
 * @author zhaorenwu
 */

@Function(description = "学校成绩-尖子生统计", parameters = {
        @Parameter(name = "projectId", type = Type.String, description = "考试项目ID", required = true),
        @Parameter(name = "schoolId", type = Type.String, description = "学校id", required = true),
        @Parameter(name = "rankSegment", type = Type.StringArray, description = "排名分段", required = true)
})
@Service
public class SchoolTopStudentStat implements Server {

    @Autowired
    SubjectService subjectService;

    @Autowired
    TopStudentListService topStudentListService;

    @Autowired
    SchoolService schoolService;

    @Autowired
    ClassService classService;

    @Autowired
    StudentService studentService;

    @Autowired
    ScoreService scoreService;

    @Autowired
    RankService rankService;

    @Override
    public Result execute(Param param) throws Exception {
        String projectId = param.getString("projectId");
        String schoolId = param.getString("schoolId");
        String[] rankSegment = param.getStringValues("rankSegment");

        Range range = Range.school(schoolId);
        Target target = Target.project(projectId);
        List<String> subjectIds = new ArrayList<>(subjectService.querySubjects(projectId));
        subjectIds.sort(String::compareTo);

        List<Map<String, Object>> topStudents = getTopStudents(projectId, rankSegment, range, target, subjectIds,
                topStudentListService, studentService, schoolService, classService, scoreService, rankService);
        return Result.success().set("topStudents", topStudents).set("hasHeader", true);
    }
}
