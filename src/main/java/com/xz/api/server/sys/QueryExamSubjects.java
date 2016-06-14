package com.xz.api.server.sys;

import com.xz.ajiaedu.common.lang.Result;
import com.xz.ajiaedu.common.lang.StringUtil;
import com.xz.api.Param;
import com.xz.api.annotation.*;
import com.xz.api.server.Server;
import com.xz.bean.Range;
import com.xz.services.RangeService;
import com.xz.services.StudentService;
import com.xz.services.SubjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.xz.services.SubjectService.getSubjectName;

/**
 * 查询考试科目列表
 *
 * @author zhaorenwu
 */

@Function(description = "根据考试项目ID查询考试科目列表", parameters = {
        @Parameter(name = "projectId", type = Type.String, description = "考试项目ID", required = true),
        @Parameter(name = "schoolId", type = Type.String, description = "学校ID", required = false),
}, result = @ResultInfo(properties = {
        @Property(name = "totals", type = Type.Pojo, description = "科目总信息"),
},listProperties =
@ListProperty(name = "subjects", description = "考试科目列表", properties = {
        @Property(name = "subjectId", type = Type.String, description = "科目id"),
        @Property(name = "subjectName", type = Type.String, description = "科目名称"),
        @Property(name = "studentCount", type = Type.Integer, description = "考生人数")
})))
@Service
public class QueryExamSubjects implements Server {

    @Autowired
    SubjectService subjectService;

    @Autowired
    StudentService studentService;

    @Autowired
    RangeService rangeService;

    @Override
    public Result execute(Param param) throws Exception {
        String projectId = param.getString("projectId");
        String schoolId = param.getString("schoolId");
        List<Map<String, String>> examSubjects = new ArrayList<>();

        Range range;
        if (StringUtil.isNotBlank(schoolId)) {
            range = Range.school(schoolId);
        } else {
            range = rangeService.queryProvinceRange(projectId);
        }

        // 科目信息
        List<String> subjectIds = new ArrayList<>(subjectService.querySubjects(projectId));
        subjectIds.sort(String::compareTo);

        examSubjects.addAll(subjectIds.stream().map(subjectId ->
                getSubjectInfo(projectId, subjectId, range)).collect(Collectors.toList()));

        // 总体信息
        Map<String, String> projectInfo = getProjectInfo(projectId, range);
        return Result.success().set("subjects", examSubjects).set("totals", projectInfo);
    }

    private Map<String, String> getSubjectInfo(String projectId, String subjectId, Range range) {
        Map<String, String> subjectInfo = new HashMap<>();

        subjectInfo.put("subjectId", subjectId);
        subjectInfo.put("subjectName", getSubjectName(subjectId));

        int studentCount = studentService.getStudentCount(projectId, subjectId, range);
        subjectInfo.put("studentCount", String.valueOf(studentCount));

        return subjectInfo;
    }

    private Map<String, String> getProjectInfo(String projectId, Range range) {
        Map<String, String> projectInfo = new HashMap<>();

        projectInfo.put("projectId", projectId);
        int studentCount = studentService.getStudentCount(projectId, range);
        projectInfo.put("studentCount", String.valueOf(studentCount));

        return projectInfo;
    }
}
