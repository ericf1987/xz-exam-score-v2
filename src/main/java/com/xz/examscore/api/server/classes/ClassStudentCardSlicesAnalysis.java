package com.xz.examscore.api.server.classes;

import com.xz.ajiaedu.common.lang.Result;
import com.xz.ajiaedu.common.ajia.Param;
import com.xz.examscore.api.annotation.Function;
import com.xz.examscore.api.annotation.Parameter;
import com.xz.examscore.api.annotation.Type;
import com.xz.examscore.api.server.Server;
import com.xz.examscore.scanner.ScannerDBService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @author by fengye on 2016/11/23.
 */
@Function(description = "班级成绩-班级学生答题卡切图查看", parameters = {
        @Parameter(name = "projectId", type = Type.String, description = "考试项目ID", required = true),
        @Parameter(name = "studentId", type = Type.String, description = "学生ID", required = true),
        @Parameter(name = "subjectId", type = Type.String, description = "科目ID", required = true)
})
@Service
public class ClassStudentCardSlicesAnalysis implements Server{

    @Autowired
    ScannerDBService scannerDBService;

    @Override
    public Result execute(Param param) throws Exception {
        String projectId = param.getString("projectId");
        String studentId = param.getString("studentId");
        String subjectId = param.getString("subjectId");

        Map<String, Object> cardSlices = scannerDBService.getStudentCardSlices(projectId, subjectId, studentId);

        return Result.success().set("cardSlices", cardSlices);
    }
}
