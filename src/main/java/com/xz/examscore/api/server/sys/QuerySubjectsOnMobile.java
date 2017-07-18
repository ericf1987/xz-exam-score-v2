package com.xz.examscore.api.server.sys;

import com.xz.ajiaedu.common.ajia.Param;
import com.xz.ajiaedu.common.lang.Result;
import com.xz.examscore.api.annotation.Function;
import com.xz.examscore.api.annotation.Parameter;
import com.xz.examscore.api.annotation.Type;
import com.xz.examscore.api.server.Server;
import com.xz.examscore.bean.Target;
import com.xz.examscore.services.FullScoreService;
import com.xz.examscore.services.SubjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author by fengye on 2017/7/18.
 */
@Function(description = "手机端查看科目及满分信息", parameters = {
        @Parameter(name = "projectId", type = Type.String, description = "考试项目ID", required = true)
})
@Service
public class QuerySubjectsOnMobile implements Server{

    @Autowired
    SubjectService subjectService;

    @Autowired
    FullScoreService fullScoreService;

    @Override
    public Result execute(Param param) throws Exception {
        String projectId = param.getString("projectId");

        List<String> subjects = subjectService.querySubjects(projectId);

        List<Map<String, Object>> subjectMap = subjects.stream().map(subjectId -> {
            Map<String, Object> map = new HashMap<>();
            map.put("subjectId", subjectId);
            map.put("subjectName", SubjectService.getSubjectName(subjectId));
            map.put("fullScore", fullScoreService.getFullScore(projectId, Target.subject(subjectId)));
            return map;
        }).collect(Collectors.toList());

        return Result.success().set("subjects", subjectMap);
    }
}
