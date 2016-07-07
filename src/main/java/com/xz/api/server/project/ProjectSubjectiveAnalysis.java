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
import com.xz.bean.Target;
import com.xz.services.AverageService;
import com.xz.services.QuestService;
import com.xz.services.RangeService;
import com.xz.services.SchoolService;
import com.xz.util.DoubleUtils;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 总体成绩-主观题分析
 *
 * @author zhaorenwu
 */
@Function(description = "总体成绩-主观题分析", parameters = {
        @Parameter(name = "projectId", type = Type.String, description = "考试项目ID", required = true),
        @Parameter(name = "subjectId", type = Type.String, description = "科目ID", required = true),
        @Parameter(name = "schoolIds", type = Type.StringArray, description = "学校id列表", required = true)
})
@Service
public class ProjectSubjectiveAnalysis implements Server {

    private static Logger LOG = LoggerFactory.getLogger(ProjectSubjectiveAnalysis.class);

    public static final Comparator<Document> QUEST_NO_COMPARATOR = new QuestNoComparator();

    @Autowired
    SchoolService schoolService;

    @Autowired
    AverageService averageService;

    @Autowired
    QuestService questService;

    @Autowired
    RangeService rangeService;

    @Override
    public Result execute(Param param) throws Exception {
        String projectId = param.getString("projectId");
        String subjectId = param.getString("subjectId");
        String[] schoolIds = param.getStringValues("schoolIds");

        List<Map<String, Object>> schoolSubjectiveAnalysis =
                getSchoolSubjectiveAnalysis(projectId, subjectId, schoolIds);
        List<Map<String, Object>> totalSubjectiveAnalysis =
                getProjectSubjectiveAnalysis(projectId, subjectId);

        return Result.success()
                .set("totals", totalSubjectiveAnalysis)
                .set("schools", schoolSubjectiveAnalysis)
                .set("hasHeader", !totalSubjectiveAnalysis.isEmpty());
    }

    // 学校主观题分析
    private List<Map<String, Object>> getSchoolSubjectiveAnalysis(
            String projectId, String subjectId, String[] schoolIds) {
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
            List<Map<String, Object>> objectives = getSubjectiveAnalysis(projectId, subjectId, range,
                    questService, averageService);
            map.put("subjectives", objectives);

            list.add(map);
        }

        return list;
    }

    // 项目主观题分析
    private List<Map<String, Object>> getProjectSubjectiveAnalysis(String projectId, String subjectId) {
        Range range = rangeService.queryProvinceRange(projectId);
        return getSubjectiveAnalysis(projectId, subjectId, range, questService, averageService);
    }

    // 获取主观题统计分析数据
    public static List<Map<String, Object>> getSubjectiveAnalysis(String projectId, String subjectId, Range range,
                                                                 QuestService questService,
                                                                 AverageService averageService) {
        List<Document> quests = new ArrayList<>(questService.getQuests(projectId, subjectId, false));
        Collections.sort(quests, QUEST_NO_COMPARATOR);

        List<Map<String, Object>> list = new ArrayList<>();
        for (Document quest : quests) {
            Map<String, Object> map = new HashMap<>();
            String questId = quest.getString("questId");
            double score = DocumentUtils.getDouble(quest, "score", 0);

            // 试题平均得分/得分率
            Target target = Target.quest(questId);
            double average = averageService.getAverage(projectId, range, target);
            map.put("average", DoubleUtils.round(average));
            map.put("rate", DoubleUtils.round(score == 0 ? 0 : average / score, true));

            map.put("questNo", quest.getString("questNo"));
            map.put("score", score);
            list.add(map);
        }

        return list;
    }
}
