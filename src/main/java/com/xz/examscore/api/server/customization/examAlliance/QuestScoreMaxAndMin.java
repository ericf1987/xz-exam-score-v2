package com.xz.examscore.api.server.customization.examAlliance;

import com.xz.ajiaedu.common.lang.Result;
import com.xz.ajiaedu.common.ajia.Param;
import com.xz.examscore.api.annotation.Function;
import com.xz.examscore.api.annotation.Parameter;
import com.xz.examscore.api.annotation.Type;
import com.xz.examscore.api.server.Server;
import com.xz.examscore.bean.Point;
import com.xz.examscore.bean.Range;
import com.xz.examscore.bean.Target;
import com.xz.examscore.services.*;
import com.xz.examscore.util.DoubleUtils;
import org.apache.commons.collections4.MapUtils;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author by fengye on 2016/12/4.
 */
@SuppressWarnings("unchecked")
@Function(description = "联考项目-试题突出情况查询", parameters = {
        @Parameter(name = "projectId", type = Type.String, description = "考试项目ID", required = true),
        @Parameter(name = "isObjective", type = Type.Boolean, description = "试题类型", required = true),
        @Parameter(name = "subjectId", type = Type.String, description = "科目ID", required = true)
})
@Service
public class QuestScoreMaxAndMin implements Server {


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

    @Autowired
    PointService pointService;

    @Override
    public Result execute(Param param) throws Exception {
        String projectId = param.getString("projectId");
        String subjectId = param.getString("subjectId");
        boolean isObjective = param.getBoolean("isObjective");
        List<Document> projectSchools = schoolService.getProjectSchools(projectId);
        List<Map<String, Object>> schoolList = new ArrayList<>();
        for (Document schoolDoc : projectSchools) {
            String schoolId = schoolDoc.getString("school");
            Map<String, Object> schoolMap = new HashMap<>();
            List<Document> quests = questService.getQuests(projectId, subjectId, isObjective);
            List<Map<String, Object>> questList = new ArrayList<>();
            for (Document questDoc : quests) {
                Map<String, Object> questMap = new HashMap<>();
                String questId = questDoc.getString("questId");
                double average = averageService.getAverage(projectId, Range.school(schoolId), Target.quest(questId));
                double fullScore = fullScoreService.getFullScore(projectId, Target.quest(questId));
                double rate = DoubleUtils.round(average / fullScore, true);
                List<String> pointList = getPointList(questDoc);
                questMap.put("questNo", questDoc.getString("questNo"));
                questMap.put("average", DoubleUtils.round(average));
                questMap.put("fullScore", fullScore);
                questMap.put("rate", DoubleUtils.toPercent(rate));
                questMap.put("pointList", listPointName(pointList));
                questList.add(questMap);
            }
            //对questList按照得分率高低进行排序
            Collections.sort(questList, (Map<String, Object> q1, Map<String, Object> q2) -> {
                Double r1 = MapUtils.getDouble(q1, "rate");
                Double r2 = MapUtils.getDouble(q2, "rate");
                return r2.compareTo(r1);
            });
            List<Map<String, Object>> result = new ArrayList<>();
            if (!questList.isEmpty()) {
                result.add(questList.get(0));
                result.add(questList.get(questList.size() - 1));
            }
            schoolMap.put("name", schoolDoc.getString("name"));
            schoolMap.put("questList", result);
            schoolList.add(schoolMap);
        }
        return Result.success().set("schools", schoolList);
    }

    public String listPointName(List<String> pointList) {
        if (null != pointList && !pointList.isEmpty()) {
            StringBuilder builder = new StringBuilder();
            pointList.forEach(pointName -> {
                builder.append(pointName).append("，");
            });
            String pointNames = builder.toString();
            return pointNames.substring(0, pointNames.length() - 1);
        } else {
            return "无知识点";
        }
    }

    private List<String> getPointList(Document questDoc) {
        Map<String, Object> pointMap = (Map<String, Object>) questDoc.get("points");
        List<String> pointNames = new ArrayList<>();
        if (null != pointMap && !pointMap.isEmpty()) {
            Set<String> keySet = pointMap.keySet();
            List<String> pointIds = new ArrayList<>(keySet);
            for (String pointId : pointIds) {
                Point point = pointService.getPoint(pointId);
                pointNames.add(point.getName());
            }
        }
        return pointNames;
    }
}
