package com.xz.examscore.api.server.classes;

import com.xz.ajiaedu.common.lang.Result;
import com.xz.examscore.api.Param;
import com.xz.examscore.api.annotation.Function;
import com.xz.examscore.api.annotation.Parameter;
import com.xz.examscore.api.annotation.Type;
import com.xz.examscore.api.server.Server;
import com.xz.examscore.bean.Range;
import com.xz.examscore.bean.Target;
import com.xz.examscore.services.AverageService;
import com.xz.examscore.services.QuestService;
import com.xz.examscore.util.DoubleUtils;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author by fengye on 2016/11/1.
 */
@SuppressWarnings("unchecked")
@Function(description = "班级分析-各式题选项及所选学生明细", parameters = {
        @Parameter(name = "projectId", type = Type.String, description = "考试项目ID", required = true),
        @Parameter(name = "classId", type = Type.String, description = "班级ID", required = true),
        @Parameter(name = "subjectId", type = Type.String, description = "考试科目ID", required = true)
})
@Service
public class ClassQuestNoAnalysis implements Server {
    @Autowired
    QuestService questService;

    @Autowired
    AverageService averageService;

    @Override
    public Result execute(Param param) throws Exception {
        String projectId = param.getString("projectId");
        String classId = param.getString("classId");
        String subjectId = param.getString("subjectId");
        List<Map<String, Object>> result = new ArrayList<>();
        questService.getQuests(projectId, subjectId).forEach(questDoc -> {
            double score = questDoc.getDouble("score");
            double average = averageService.getAverage(projectId, Range.clazz(classId), Target.quest(questDoc.getString("questId")));
            double rate = DoubleUtils.round(average / score, true);
            Map<String, Object> questMap = new HashMap<>();
            questMap.put("questNo", questDoc.getString("questNo"));
            questMap.put("isObjective", questDoc.getBoolean("isObjective"));
            questMap.put("rate", rate);
            result.add(questMap);
        });
        Collections.sort(result, (Map<String, Object> m1, Map<String, Object> m2) -> {
            Double d1 = MapUtils.getDouble(m1, "rate");
            Double d2 = MapUtils.getDouble(m2, "rate");
            return d1.compareTo(d2);
        });
        return Result.success().set("questNos", result);
    }
}
