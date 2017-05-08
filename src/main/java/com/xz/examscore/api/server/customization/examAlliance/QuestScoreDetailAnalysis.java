package com.xz.examscore.api.server.customization.examAlliance;

import com.xz.ajiaedu.common.lang.Result;
import com.xz.ajiaedu.common.ajia.Param;
import com.xz.examscore.api.annotation.Function;
import com.xz.examscore.api.annotation.Parameter;
import com.xz.examscore.api.annotation.Type;
import com.xz.examscore.api.server.Server;
import com.xz.examscore.bean.Range;
import com.xz.examscore.bean.Target;
import com.xz.examscore.services.*;
import com.xz.examscore.util.DoubleUtils;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author by fengye on 2016/12/4.
 */
@Function(description = "联考项目-各个小题得分率", parameters = {
        @Parameter(name = "projectId", type = Type.String, description = "考试项目ID", required = true),
        @Parameter(name = "subjectId", type = Type.String, description = "科目ID", required = true)
})
@Service
public class QuestScoreDetailAnalysis implements Server {

    @Autowired
    AverageService averageService;

    @Autowired
    FullScoreService fullScoreService;

    @Autowired
    SubjectService subjectService;

    @Autowired
    SchoolService schoolService;

    @Autowired
    QuestService questService;

    @Override
    public Result execute(Param param) throws Exception {
        String projectId = param.getString("projectId");
        String subjectId = param.getString("subjectId");
        List<Document> projectSchools = schoolService.getProjectSchools(projectId);
        List<Document> quests = questService.getQuests(projectId, subjectId);
        Collections.sort(quests, (Document d1, Document d2) -> d1.getString("questNo").compareTo(d2.getString("questNo")));
        List<String> questIds = quests.stream().map(quest -> quest.getString("questNo")).collect(Collectors.toList());
        Collections.sort(questIds);
        List<Map<String, Object>> schoolList = new ArrayList<>();
        for(Document schoolDoc : projectSchools){
            Map<String, Object> schoolMap = new HashMap<>();
            String schoolId = schoolDoc.getString("school");
            String schoolName = schoolDoc.getString("name");
            List<Map<String, Object>> questList = new ArrayList<>();
            for (Document questDoc : quests){
                Map<String, Object> questMap = new HashMap<>();
                String questId = questDoc.getString("questId");
                double average = averageService.getAverage(projectId, Range.school(schoolId), Target.quest(questId));
                double fullScore = fullScoreService.getFullScore(projectId, Target.quest(questId));
                double rate = average / fullScore;
                questMap.put("questNo", questDoc.getString("questNo"));
                questMap.put("isObjective", questDoc.get("isObjective"));
                questMap.put("average", average);
                questMap.put("fullScore", fullScore);
                questMap.put("rate", DoubleUtils.round(rate, true));
                questList.add(questMap);
            }
            schoolMap.put("schoolName", schoolName);
            schoolMap.put("quests", questList);
            schoolList.add(schoolMap);
        }
        Result result = Result.success().set("questNos", questIds).set("schools", schoolList);
        return result;
    }
}
