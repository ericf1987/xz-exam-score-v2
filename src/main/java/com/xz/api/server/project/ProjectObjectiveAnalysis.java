package com.xz.api.server.project;

import com.xz.ajiaedu.common.lang.Result;
import com.xz.ajiaedu.common.lang.StringUtil;
import com.xz.ajiaedu.common.mongo.DocumentUtils;
import com.xz.ajiaedu.common.mongo.QuestNoComparator;
import com.xz.api.Param;
import com.xz.api.annotation.Function;
import com.xz.api.annotation.Parameter;
import com.xz.api.annotation.Type;
import com.xz.api.server.Server;
import com.xz.bean.Range;
import com.xz.services.*;
import com.xz.util.DoubleUtils;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 总体成绩-客观题分析
 *
 * @author zhaorenwu
 */
@Function(description = "总体成绩-客观题分析", parameters = {
        @Parameter(name = "projectId", type = Type.String, description = "考试项目ID", required = true),
        @Parameter(name = "subjectId", type = Type.String, description = "科目ID", required = true),
        @Parameter(name = "schoolIds", type = Type.StringArray, description = "学校id列表", required = true)
})
@Service
public class ProjectObjectiveAnalysis implements Server {

    private static Logger LOG = LoggerFactory.getLogger(ProjectObjectiveAnalysis.class);

    public static final Comparator<Document> QUEST_NO_COMPARATOR = new QuestNoComparator();

    @Autowired
    SchoolService schoolService;

    @Autowired
    RangeService rangeService;

    @Autowired
    QuestService questService;

    @Autowired
    QuestDeviationService questDeviationService;

    @Autowired
    OptionMapService optionMapService;

    @Override
    public Result execute(Param param) throws Exception {
        String projectId = param.getString("projectId");
        String subjectId = param.getString("subjectId");
        String[] schoolIds = param.getStringValues("schoolIds");

        List<Map<String, Object>> schoolObjectiveAnalysis =  getSchoolAnalysis(projectId, subjectId, schoolIds);
        List<Map<String, Object>> totalObjectiveAnalysis = getProjectTotalAnalysis(projectId, subjectId);

        return Result.success().set("totals", totalObjectiveAnalysis).set("schools", schoolObjectiveAnalysis);
    }

    // 学校客观题分数
    private List<Map<String, Object>> getSchoolAnalysis(String projectId, String subjectId, String[] schoolIds) {
        List<Map<String, Object>> list = new ArrayList<>();

        for (String schoolId : schoolIds) {
            String schoolName = schoolService.getSchoolName(projectId, schoolId);
            if (StringUtil.isBlank(schoolName)) {
                LOG.warn("找不到学校:'{}'的考试记录", schoolId);
                continue;
            }

            Map<String, Object> map = new HashMap<>();
            map.put("schoolName", schoolName);

            Range range = Range.school(schoolId);
            List<Map<String, Object>> objectives = getObjectiveAnalysis(projectId, subjectId, range,
                    questService, optionMapService, questDeviationService);
            map.put("objectives", objectives);

            list.add(map);
        }

        return list;
    }

    // 项目整体客观题分数
    private List<Map<String, Object>> getProjectTotalAnalysis(String projectId, String subjectId) {
        Range range = rangeService.queryProvinceRange(projectId);
        return getObjectiveAnalysis(projectId, subjectId, range,
                questService, optionMapService, questDeviationService);
    }

    // 获取客观题统计分析数据
    @SuppressWarnings("unchecked")
    public static List<Map<String, Object>> getObjectiveAnalysis(String projectId, String subjectId, Range range,
                                                                 QuestService questService,
                                                                 OptionMapService optionMapService,
                                                                 QuestDeviationService questDeviationService) {
        List<Document> quests = new ArrayList<>(questService.getQuests(projectId, subjectId, true));
        Collections.sort(quests, QUEST_NO_COMPARATOR);

        List<Map<String, Object>> list = new ArrayList<>();
        for (Document quest : quests) {
            Map<String, Object> map = new HashMap<>();
            String questId = quest.getString("questId");
            List<String> items = quest.get("items", List.class);
            Map<String, Document> optionMap = optionMapService.getOptionMap(projectId, questId, range);

            // 试题区分度
            double questDeviation = questDeviationService.getQuestDeviation(projectId, questId, range);
            map.put("questDeviation", DoubleUtils.round(questDeviation));

            // 试题选项选率
            if (items != null) {
                for (String itemName : items) {
                    Map<String, Object> itemNameStat = getOptionValue(optionMap, itemName.trim());
                    map.put(itemName, itemNameStat);
                }
            }

            // 不选率
            Map<String, Object> unSelect = getOptionValue(optionMap, "*");
            map.put("unSelect", unSelect);

            map.put("questNo", quest.getString("questNo"));
            map.put("score", DocumentUtils.getDouble(quest, "score", 0));
            String standardAnswer = quest.getString("standardAnswer");
            map.put("standardAnswer", standardAnswer == null ? "" : standardAnswer.trim());
            list.add(map);
        }

        return list;
    }

    private static Map<String, Object> getOptionValue(Map<String, Document> optionMap, String key) {
        Map<String, Object> map = new HashMap<>();
        Document document = optionMap.get(key);
        if (document == null) {
            map.put("count", 0);
            map.put("rate", 0);
            map.put("answer", key);
        } else {
            map.put("count", document.getInteger("count"));
            map.put("rate", DoubleUtils.round(document.getDouble("rate"), true));
            map.put("answer", document.getString("answer"));
        }

        return map;
    }
}
