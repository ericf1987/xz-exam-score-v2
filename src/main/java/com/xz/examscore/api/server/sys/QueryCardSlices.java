package com.xz.examscore.api.server.sys;

import com.xz.ajiaedu.common.lang.Result;
import com.xz.examscore.api.Param;
import com.xz.examscore.api.annotation.Function;
import com.xz.examscore.api.annotation.Parameter;
import com.xz.examscore.api.annotation.Type;
import com.xz.examscore.api.server.Server;
import com.xz.examscore.bean.Range;
import com.xz.examscore.bean.Target;
import com.xz.examscore.scanner.ScannerDBService;
import com.xz.examscore.services.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author by fengye on 2017/2/27.
 */
@Function(description = "班级考生试卷留痕截图", parameters = {
        @Parameter(name = "projectId", type = Type.String, description = "项目ID", required = true),
        @Parameter(name = "classId", type = Type.String, description = "班级列表", required = true),
        @Parameter(name = "subjectId", type = Type.String, description = "科目列表", required = true)

})
@Service
public class QueryCardSlices implements Server {

    @Autowired
    ScannerDBService scannerDBService;

    @Autowired
    StudentService studentService;

    @Override
    public Result execute(Param param) throws Exception {
        String projectId = param.getString("projectId");
        String classId = param.getString("classId");
        String subjectId = param.getString("subjectId");

        List<Map<String, Object>> result = new ArrayList<>();
        List<String> studentIds = studentService.getStudentIds(projectId, Range.clazz(classId), Target.subject(subjectId));
        for (String studentId : studentIds) {
            Map<String, Object> studentCardSlices = scannerDBService.getStudentCardSlices(projectId, subjectId, studentId);
            result.add(studentCardSlices);
        }
        return Result.success().set("studentCardSlices", result);
    }
}
