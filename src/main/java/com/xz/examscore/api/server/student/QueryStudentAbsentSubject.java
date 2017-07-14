package com.xz.examscore.api.server.student;

import com.xz.ajiaedu.common.ajia.Param;
import com.xz.ajiaedu.common.lang.Result;
import com.xz.examscore.api.annotation.Function;
import com.xz.examscore.api.annotation.Parameter;
import com.xz.examscore.api.annotation.Type;
import com.xz.examscore.api.server.Server;
import com.xz.examscore.services.SubjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author by fengye on 2017/7/11.
 */
@Function(description = "学生数据查询-查询学生缺考科目", parameters = {
        @Parameter(name = "projectId", type = Type.String, description = "考试项目ID", required = true),
        @Parameter(name = "studentId", type = Type.String, description = "学生ID", required = false)
})
@Service
public class QueryStudentAbsentSubject implements Server{

    @Autowired
    SubjectService subjectService;

    @Override
    public Result execute(Param param) throws Exception {

        String projectId = param.getString("projectId");

        String studentId = param.getString("studentId");

        List<Map<String, String>> subjects = subjectService.queryAbsentSubject(projectId, studentId);

        Collections.sort(subjects, (Map<String, String> m1, Map<String, String> m2) -> {
            String s1 = m1.get("subjectId");
            String s2 = m2.get("subjectId");
            return s1.compareTo(s2);
        });

        return Result.success().set("subjects", subjects);
    }
}
