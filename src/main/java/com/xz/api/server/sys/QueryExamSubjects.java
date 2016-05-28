package com.xz.api.server.sys;

import com.xz.ajiaedu.common.lang.Result;
import com.xz.api.Param;
import com.xz.api.annotation.*;
import com.xz.api.server.Server;
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
        @Parameter(name = "projectId", type = Type.String, description = "考试项目ID", required = true)
}, result = @ResultInfo(listProperties =
@ListProperty(name = "subjects", description = "考试科目列表", properties = {
        @Property(name = "subjectId", type = Type.String, description = "科目id"),
        @Property(name = "subjectName", type = Type.String, description = "科目名称")
})))
@Service
public class QueryExamSubjects implements Server {

    @Autowired
    SubjectService subjectService;

    @Override
    public Result execute(Param param) throws Exception {
        String projectId = param.getString("projectId");
        List<Map<String, String>> examSubjects = new ArrayList<>();

        List<String> subjectIds = subjectService.querySubjects(projectId);
        examSubjects.addAll(subjectIds.stream().map(this::getSubjectInfo).collect(Collectors.toList()));

        return Result.success().set("subjects", examSubjects);
    }

    private Map<String, String> getSubjectInfo(String subjectId) {
        Map<String, String> subjectInfo = new HashMap<>();

        subjectInfo.put("subjectId", subjectId);
        subjectInfo.put("subjectName", getSubjectName(subjectId).toString());
        return subjectInfo;
    }
}
