package com.xz.services;

import com.mongodb.client.AggregateIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.xz.ajiaedu.common.beans.dic.QuestType;
import com.xz.ajiaedu.common.lang.Converter;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import static com.xz.ajiaedu.common.lang.CollectionUtils.convertList;
import static com.xz.ajiaedu.common.mongo.MongoUtils.*;

/**
 * 在导入完成后，正式开始统计之前，需要对数据进行一些准备工作。准备工作完成后，统计就可以进行了。
 *
 * @author yiding_he
 */
@Service
public class PrepareDataService {

    static final Logger LOG = LoggerFactory.getLogger(PrepareDataService.class);

    public static final String JUDGE_QUEST = "2";  // 判断题题型

    @Autowired
    MongoDatabase scoreDatabase;

    @Autowired
    QuestTypeService questTypeService;

    @Autowired
    QuestService questService;

    @Autowired
    ScoreService scoreService;

    public void prepare(String projectId) {
        LOG.info("统计学生数量...");
        prepareStudentList(projectId);
        LOG.info("统计题型...");
        prepareQuestTypeList(projectId);
        LOG.info("统计判断题选项...");
        prepareFixQuestOptions(projectId);
    }

    /**
     * 补完客观题（主要指选择题）的选项
     *
     * @param projectId 项目ID
     */
    public void prepareFixQuestOptions(String projectId) {
        List<Document> quests = questService.getQuestsByQuestType(projectId, JUDGE_QUEST);

        for (Document quest : quests) {
            String questId = quest.getString("questId");
            Document score = scoreService.findOneJudgeQuestScore(projectId, questId);
            if (score == null) {
                continue;
            }

            List<String> items;
            String studentAnswer = score.getString("answer");

            switch (studentAnswer) {
                case "A":
                case "B":
                    items = Arrays.asList("A", "B");
                    break;
                case "T":
                case "F":
                    items = Arrays.asList("T", "F");
                    break;
                default:
                    throw new IllegalStateException("无法识别的判断题答案: " + score.toJson());
            }

            questService.saveQuestItems(projectId, questId, items);
        }
    }

    /**
     * 处理题型列表
     *
     * @param projectId 项目ID
     */
    public void prepareQuestTypeList(String projectId) {

        Converter<QuestType, Document> questType2Doc = questType ->
                doc("project", projectId)
                        .append("subject", questType.getSubjectId())
                        .append("questTypeId", questType.getId())
                        .append("questTypeName", questType.getName());

        List<QuestType> questTypeList = questTypeService.getQuestTypeList(projectId);
        List<Document> questTypeDocs = convertList(questTypeList, questType2Doc);
        MongoCollection<Document> collection = scoreDatabase.getCollection("quest_type_list");

        collection.deleteMany(doc("project", projectId));
        if (questTypeList.isEmpty()) {
            LOG.error("项目 {} 没有题型信息", projectId);
        } else {
            collection.insertMany(questTypeDocs);
        }
    }

    /**
     * 处理学生列表
     *
     * @param projectId 项目ID
     */
    public void prepareStudentList(String projectId) {
        MongoCollection<Document> stuListCollection = scoreDatabase.getCollection("student_list");
        MongoCollection<Document> scoreCollection = scoreDatabase.getCollection("score");

        Document _id = new Document("province", "$province")
                .append("city", "$city").append("area", "$area").append("school", "$school")
                .append("class", "$class").append("student", "$student");

        AggregateIterable<Document> aggregate = scoreCollection.aggregate(Arrays.asList(
                $match("project", projectId),
                $group(doc("_id", _id).append("subjects", $addToSet("$subject")))
        ));

        aggregate.forEach((Consumer<Document>) document -> {
            Document resultId = (Document) document.get("_id");
            String studentId = resultId.getString("student");
            stuListCollection.updateMany(
                    doc("project", projectId).append("student", studentId),
                    $set("subjects", document.get("subjects"))
            );
        });
    }
}
