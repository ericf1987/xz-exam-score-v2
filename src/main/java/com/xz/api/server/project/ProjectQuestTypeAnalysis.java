package com.xz.api.server.project;

import com.xz.ajiaedu.common.beans.dic.QuestType;
import com.xz.ajiaedu.common.lang.Result;
import com.xz.ajiaedu.common.lang.StringUtil;
import com.xz.api.Param;
import com.xz.api.annotation.Function;
import com.xz.api.annotation.Parameter;
import com.xz.api.annotation.Type;
import com.xz.api.server.Server;
import com.xz.bean.Range;
import com.xz.bean.Target;
import com.xz.services.*;
import com.xz.util.DoubleUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 总体成绩-试卷题型分析
 *
 * @author zhaorenwu
 */
@Function(description = "总体成绩-试卷题型分析", parameters = {
        @Parameter(name = "projectId", type = Type.String, description = "考试项目ID", required = true),
        @Parameter(name = "subjectId", type = Type.String, description = "科目ID", required = true),
        @Parameter(name = "schoolIds", type = Type.StringArray, description = "学校id列表", required = true)
})
@Service
public class ProjectQuestTypeAnalysis implements Server {

    private static final Logger LOG = LoggerFactory.getLogger(ProjectQuestTypeAnalysis.class);

    @Autowired
    SchoolService schoolService;

    @Autowired
    RangeService rangeService;

    @Autowired
    QuestTypeService questTypeService;

    @Autowired
    FullScoreService fullScoreService;

    @Autowired
    QuestTypeScoreService questTypeScoreService;

    @Override
    public Result execute(Param param) throws Exception {
        String projectId = param.getString("projectId");
        String subjectId = param.getString("subjectId");
        String[] schoolIds = param.getStringValues("schoolIds");

        List<Map<String, Object>> schoolQuestTypeAnalysis =
                getSchoolQuestTypeAnalysis(projectId, subjectId, schoolIds);
        List<Map<String, Object>> totalQuestTypeAnalysis =
                getTotalQuestTypeAnalysis(projectId, subjectId);

        return Result.success()
                .set("totals", totalQuestTypeAnalysis)
                .set("schools", schoolQuestTypeAnalysis)
                .set("hasHeader", !totalQuestTypeAnalysis.isEmpty());
    }

    // 学校试题分析
    private List<Map<String, Object>> getSchoolQuestTypeAnalysis(
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
            List<Map<String, Object>> questTypes = getQuestTypeAnalysis(projectId, subjectId, range,
                    questTypeService, fullScoreService, questTypeScoreService);
            map.put("questTypes", questTypes);

            list.add(map);
        }

        return list;
    }

    // 整体试题分析
    private List<Map<String, Object>> getTotalQuestTypeAnalysis(String projectId, String subjectId) {
        Range range = rangeService.queryProvinceRange(projectId);
        return getQuestTypeAnalysis(projectId, subjectId, range,
                questTypeService, fullScoreService, questTypeScoreService);
    }

    // 题型分析数据
    public static List<Map<String, Object>> getQuestTypeAnalysis(
            String projectId, String subjectId, Range range,
            QuestTypeService questTypeService,
            FullScoreService fullScoreService,
            QuestTypeScoreService questTypeScoreService) {

        List<QuestType> questTypes = questTypeService.getQuestTypeList(projectId, subjectId);

        List<Map<String, Object>> list = new ArrayList<>();
        for (QuestType questType : questTypes) {
            Map<String, Object> map = new HashMap<>();
            String questTypeId = questType.getId();

            // 题型满分
            Target target = Target.questType(questTypeId);
            double fullScore = fullScoreService.getFullScore(projectId, target);
            map.put("fullScore", fullScore);

            // 题型得分/得分率
            double score = questTypeScoreService.getQuestTypeScore(projectId, range, questTypeId);
            map.put("score", DoubleUtils.round(score, false));
            map.put("scoreRate", DoubleUtils.round(fullScore == 0 ? 0 : score / fullScore, true));

            map.put("questTypeId", questTypeId);
            map.put("name", questType.getName());
            list.add(map);
        }

        return list;

    }
}
