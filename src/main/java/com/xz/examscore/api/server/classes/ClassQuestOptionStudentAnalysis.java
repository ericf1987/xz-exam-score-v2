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
import com.xz.examscore.util.DoubleUtils;
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
@Function(description = "班级分析-试题选项及所选学生明细", parameters = {
        @Parameter(name = "projectId", type = Type.String, description = "考试项目ID", required = true),
        @Parameter(name = "subjectId", type = Type.String, description = "考试科目ID", required = true),
        @Parameter(name = "questNo", type = Type.String, description = "题号", required = true),
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
        String questNo = param.getString("questNo");

        Map<String, Object> quests = getQuestOptionStudents(projectId, classRange, subjectId, questNo);
        return Result.success().set("quests", quests);
    }

    private Map<String, Object> getQuestOptionStudents(String projectId, Range classRange, String subjectId, String questNo) {

        Document questDoc = questService.findQuest(projectId, subjectId, questNo);

        List<Map<String, Object>> optionsInfo = new ArrayList<>();
        String questId = questDoc.getString("questId");
        String answer = questDoc.getString("answer");
        Boolean isObjective = questDoc.getBoolean("isObjective");
        int count = getSubjectiveCount(projectId, classRange, subjectId, questId);
        if (questDoc.getBoolean("isObjective")) {
            optionsInfo.addAll(getObjectiveOptionsInfo(projectId, classRange, subjectId, questDoc));
        } else {
            optionsInfo.addAll(getSubjectiveScoresInfo(projectId, classRange, subjectId, questDoc, count));
        }

        //如果是客观题，需要在标记出正确选项
        if(isObjective){
            paddingAnswer(answer, optionsInfo);
        }

        Map<String, Object> questMap = new HashMap<>();
        questMap.put("questNo", questNo);
        questMap.put("questId", questId);
        questMap.put("options", optionsInfo);
        questMap.put("totalCount", count);
        questMap.put("isObjective", isObjective);

        return questMap;
    }

    //拼接正确答案至选项列表
    private List<Map<String, Object>> paddingAnswer(String answer, List<Map<String, Object>> optionsInfo) {
        List<String> rightAnswers = Arrays.asList(answer.split(","));
        optionsInfo.forEach(option -> {
            String item = option.get("answer").toString();
            if(rightAnswers.contains(item)){
                option.put("rightAnswer", true);
            }
        });
        return optionsInfo;
    }

    private int getSubjectiveCount(String projectId, Range classRange, String subjectId, String questId) {
        return scoreService.getScoreRecordCount(projectId, classRange, subjectId, questId);
    }

    //客观题列表
    private List<Map<String, Object>> getObjectiveOptionsInfo(String projectId, Range classRange, String subjectId, Document quest) {
        //获取客观题的所有选项
        List<String> items = (List<String>) quest.get("items");
        List<Map<String, Object>> optionsInfo = new ArrayList<>();
        if (items != null && !items.isEmpty()) {
            String questId = quest.getString("questId");
            List<Document> optionMap = optionMapService.getOptionList(projectId, questId, classRange);
            //保留四位小数
            optionMap.forEach(map -> map.put("rate", DoubleUtils.round(map.getDouble("rate"), true)));
            Collections.sort(optionMap, (Document m1, Document m2) -> {
                String optionItem1 = m1.getString("answer");
                String optionItem2 = m2.getString("answer");
                return optionItem1.compareTo(optionItem2);
            });
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
    private List<Map<String, Object>> getSubjectiveScoresInfo(String projectId, Range classRange, String subjectId, Document quest, int count) {
        String questId = quest.getString("questId");
        String questNo = quest.getString("questNo");
        List<Map<String, Object>> scores = new ArrayList<>();
        List<String> scoreSegment = getScoreSegment(quest.getDouble("score"));
        for (String segment : scoreSegment) {
            Map<String, Object> map = new HashMap<>();
            String[] seg = segment.split("-");
            Double min = Double.parseDouble(seg[0]);
            Double max = Double.parseDouble(seg[1]);
            List<String> students = getStudentNameByScoreSegment(projectId, classRange, subjectId, questId, min, max);
            Double rate = count == 0 ? 0 : DoubleUtils.round((double)students.size() / count, true);
            map.put("questNo", questNo);
            map.put("questId", questId);
            map.put("segment", EncloseWithBrackets(segment));
            map.put("students", students);
            map.put("count", students.size());
            map.put("rate", rate);
            scores.add(map);
        }
        return scores;
    }

    private String EncloseWithBrackets(String segment) {
        StringBuilder builder = new StringBuilder(segment);
        if(segment.startsWith("0")){
            builder.insert(0, '[').append("]");
        }else{
            builder.insert(0, '(').append("]");
        }
        return builder.toString();
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
        if (score > 20 && score < 60) {
            return Arrays.asList("0-5", "5-10", "10-15", "15-20", "20-" + DoubleUtils.cutTailZero(score));
        }
        if(score == 60){
            return Arrays.asList("0-10", "10-20", "20-30", "30-40", "40-50", "50-60");
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
