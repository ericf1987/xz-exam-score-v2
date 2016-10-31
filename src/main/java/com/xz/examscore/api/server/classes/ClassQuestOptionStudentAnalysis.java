package com.xz.examscore.api.server.classes;

import com.xz.ajiaedu.common.lang.CollectionUtils;
import com.xz.ajiaedu.common.lang.Result;
import com.xz.examscore.api.Param;
import com.xz.examscore.api.annotation.Function;
import com.xz.examscore.api.annotation.Parameter;
import com.xz.examscore.api.annotation.Type;
import com.xz.examscore.api.server.Server;
import com.xz.examscore.bean.Range;
import com.xz.examscore.services.*;
import org.apache.commons.collections.MapUtils;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static com.xz.ajiaedu.common.mongo.MongoUtils.doc;

/**
 * @author by fengye on 2016/10/31.
 */
@SuppressWarnings("unchecked")
@Function(description = "班级分析-各式题选项及所选学生明细", parameters = {
        @Parameter(name = "projectId", type = Type.String, description = "考试项目ID", required = true),
        @Parameter(name = "subjectId", type = Type.String, description = "考试科目ID", required = true),
        @Parameter(name = "classId", type = Type.String, description = "班级id", required = true)
})
@Service
public class ClassQuestOptionStudentAnalysis implements Server {
    @Autowired
    ClassService classService;

    @Autowired
    QuestService questService;

    @Autowired
    OptionMapService optionMapService;

    @Autowired
    ScoreService scoreService;

    @Autowired
    StudentService studentService;

    @Override
    public Result execute(Param param) throws Exception {
        String projectId = param.getString("projectId");
        Range classRange = Range.clazz(param.getString("classId"));
        String subjectId = param.getString("subjectId");
        //获取所有试题信息
        List<Document> questList = questService.getQuests(projectId, subjectId);

        List<Map<String, Object>> quests = getQuestOptionStudents(projectId, classRange, subjectId, questList);
        return Result.success().set("quests", quests);
    }

    private List<Map<String, Object>> getQuestOptionStudents(String projectId, Range classRange, String subjectId, List<Document> questList) {
        List<Map<String, Object>> quests = new ArrayList<>();
        for (Document quest : questList) {
            List<Map<String, Object>> optionsInfo = new ArrayList<>();
            String questNo = quest.getString("questNo");
            String questId = quest.getString("questId");
            Boolean isObjective = quest.getBoolean("isObjective");
            if (quest.getBoolean("isObjective")) {
                optionsInfo.addAll(getObjectiveOptionsInfo(projectId, classRange, subjectId, quest));
            } else {
                optionsInfo.addAll(getSubjectiveScoresInfo(projectId, classRange, subjectId, quest));
            }
            Map<String, Object> questMap = new HashMap<>();
            questMap.put("questNo", questNo);
            questMap.put("questId", questId);
            questMap.put("options", optionsInfo);
            questMap.put("isObjective", isObjective);
            quests.add(questMap);
        }
        return quests;
    }

    //客观题列表
    private List<Map<String, Object>> getObjectiveOptionsInfo(String projectId, Range classRange, String subjectId, Document quest) {
        //获取客观题的所有选项
        List<String> items = (List<String>) quest.get("items");
        List<Map<String, Object>> optionsInfo = new ArrayList<>();
        if (items != null && !items.isEmpty()) {
            String questId = quest.getString("questId");
            List<Document> optionMap = optionMapService.getOptionList(projectId, questId, classRange);
            //将没有选的选项补充进去，并按照选项排序
            optionsInfo.addAll(fixOptionMap(optionMap, items));
            for (String item : items) {
                //获取每个题目对应选项的学生的姓名
                List<String> studentNames = getStudentNameByQuestItem(projectId, classRange, subjectId, questId, item);
                paddingStudentNames(studentNames, item, optionsInfo);
            }
        }
        return optionsInfo;
    }

    //主观题列表
    private List<Map<String, Object>> getSubjectiveScoresInfo(String projectId, Range classRange, String subjectId, Document quest) {
        String questId = quest.getString("questId");
        String questNo = quest.getString("questNo");
        List<Map<String, Object>> scores = new ArrayList<>();
        List<String> scoreSegment = getScoreSegment(quest.getDouble("score"));
        for (String segment : scoreSegment) {
            Map<String, Object> map = new HashMap<>();
            String[] seg = segment.split("-");
            Double min = Double.parseDouble(seg[0]);
            Double max = Double.parseDouble(seg[1]);
            List<String> studentNames = getStudentNameByScoreSegment(projectId, classRange, subjectId, questId, min, max);
            map.put("questNo", questNo);
            map.put("questId", questId);
            map.put("segment", segment);
            map.put("studentNames", studentNames);
            scores.add(map);
        }
        return scores;
    }

    private List<String> getStudentNameByScoreSegment(String projectId, Range classRange, String subjectId, String questId, Double min, Double max) {
        return scoreService.getScoreDocsByScoreSegment(projectId, classRange, subjectId, questId, min, max).stream().map(
                doc -> studentService.findStudent(projectId, doc.getString("student")).getString("name")
        ).collect(Collectors.toList());
    }

    //根据主观题分值拿到主观题分值的分段
    private List<String> getScoreSegment(Double score) {
        if (score <= 5) {
            return Arrays.asList("0-1", "1-2", "2-3", "3-4", "4-5");
        }
        if (score <= 10) {
            return Arrays.asList("0-2", "2-4", "4-6", "6-8", "8-10");
        }
        if (score <= 20) {
            return Arrays.asList("0-4", "4-8", "8-12", "12-16", "16-20");
        }
        if (score > 20) {
            return Arrays.asList("0-5", "5-10", "10-15", "15-20", "20-" + String.valueOf(score));
        }
        return Collections.singletonList("0-" + String.valueOf(score));
    }

    private List<Map<String, Object>> fixOptionMap(List<Document> optionMap, List<String> items) {
        Map<String, Object> m = new HashMap<>(CollectionUtils.toMap(optionMap, (option -> option.getString("answer"))));
        items.forEach(item -> {
            if (!m.containsKey(item)) {
                optionMap.add(doc("answer", item).append("count", 0).append("rate", 0d));
            }
        });
        return optionMap.stream().map(HashMap::new).collect(Collectors.toList());
    }

    private List<String> getStudentNameByQuestItem(String projectId, Range classRange, String subjectId, String questId, String item) {
        return scoreService.getScoreDocs(projectId, classRange, subjectId, questId, item).stream().map(doc ->
                studentService.findStudent(projectId, doc.getString("student")).getString("name")).collect(Collectors.toList());
    }

    private void paddingStudentNames(List<String> studentNames, String item, List<Map<String, Object>> optionMap) {
        optionMap.stream().filter(m -> MapUtils.getString(m, "answer").equals(item)).forEach(m -> m.put("students", studentNames));
    }

}
