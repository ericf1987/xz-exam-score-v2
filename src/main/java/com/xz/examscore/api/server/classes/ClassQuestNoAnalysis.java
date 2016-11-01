package com.xz.examscore.api.server.classes;

import com.xz.ajiaedu.common.lang.Result;
import com.xz.examscore.api.Param;
import com.xz.examscore.api.annotation.Function;
import com.xz.examscore.api.annotation.Parameter;
import com.xz.examscore.api.annotation.Type;
import com.xz.examscore.api.server.Server;
import com.xz.examscore.services.QuestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author by fengye on 2016/11/1.
 */
@SuppressWarnings("unchecked")
@Function(description = "班级分析-各式题选项及所选学生明细", parameters = {
        @Parameter(name = "projectId", type = Type.String, description = "考试项目ID", required = true),
        @Parameter(name = "subjectId", type = Type.String, description = "考试科目ID", required = true)
})
@Service
public class ClassQuestNoAnalysis implements Server {
    @Autowired
    QuestService questService;

    @Override
    public Result execute(Param param) throws Exception {
        String projectId = param.getString("projectId");
        String subjectId = param.getString("subjectId");
        List<Map<String, Object>> result = new ArrayList<>();
        questService.getQuests(projectId, subjectId).forEach(questDoc -> {
            Map<String, Object> questMap = new HashMap<>();
            questMap.put("questNo", questDoc.getString("questNo"));
            questMap.put("isObjective", questDoc.getBoolean("isObjective"));
            result.add(questMap);
        });
        Collections.sort(result, (Map<String, Object> m1, Map<String, Object> m2) -> {
            String q1 = m1.get("questNo").toString();
            String q2 = m2.get("questNo").toString();
            return q1.compareTo(q2);
        });
        return Result.success().set("questNos", result);
    }
}
