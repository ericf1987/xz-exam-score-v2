package com.xz.services;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.xz.bean.SubjectObjective;
import com.xz.bean.Target;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static com.xz.ajiaedu.common.mongo.MongoUtils.doc;
import static com.xz.bean.Target.SUBJECT_OBJECTIVE;

@SuppressWarnings("unchecked")
@Service
public class TargetService {

    @Autowired
    MongoDatabase scoreDatabase;

    @Autowired
    QuestService questService;

    @Autowired
    SubjectService subjectService;

    public List<Target> queryTargets(String projectId, String... targetNames) {
        List<Target> targetList = new ArrayList<>();
        List<String> targetNameList = Arrays.asList(targetNames);

        if (targetNameList.contains(Target.PROJECT)) {
            targetList.add(new Target(Target.PROJECT, projectId));
        }

        if (targetNameList.contains(Target.SUBJECT)) {
            targetList.addAll(querySubjects(projectId));
        }

        if (targetNameList.contains(Target.QUEST)) {
            targetList.addAll(queryQuests(projectId));
        }

        if (targetNameList.contains(SUBJECT_OBJECTIVE)) {
            targetList.addAll(querySubjectObjectives(projectId));
        }

        return targetList;
    }

    private List<Target> querySubjectObjectives(String projectId) {
        List<Target> result = new ArrayList<>();

        subjectService.querySubjects(projectId).forEach(subjectId -> {
            result.add(new Target(SUBJECT_OBJECTIVE, new SubjectObjective(subjectId, true)));
            result.add(new Target(SUBJECT_OBJECTIVE, new SubjectObjective(subjectId, false)));
        });

        return result;
    }

    private List<Target> querySubjects(String projectId) {
        return subjectService.querySubjects(projectId).stream()
                .map(subjectId -> new Target(Target.SUBJECT, subjectId))
                .collect(Collectors.toList());
    }

    private List<Target> queryQuests(String projectId) {
        ArrayList<Target> quests = new ArrayList<>();
        Document query = doc("project", projectId);
        MongoCollection<Document> collection = scoreDatabase.getCollection("quest_list");

        collection.find(query).forEach((Consumer<Document>) document -> {
            String questId = document.getObjectId("_id").toString();
            quests.add(new Target(Target.QUEST, questId));
        });

        return quests;
    }

    /**
     * 从任意 Target 中获取对应的科目信息
     *
     * @param target 要获取科目信息的 target 对象
     *
     * @return target 对象对应的科目
     */
    public String getTargetSubjectId(Target target) {
        String targetName = target.getName();

        switch (targetName) {
            case Target.PROJECT:
                return "000";

            case Target.SUBJECT:
                return target.getId().toString();

            case Target.SUBJECT_OBJECTIVE:
                return target.getId(SubjectObjective.class).getSubject();

            case Target.QUEST:
                String questId = target.getId().toString();
                Document quest = questService.findQuest(questId);
                if (quest != null) {
                    return quest.getString("subject");
                } else {
                    throw new IllegalArgumentException("Target not found: " + target);
                }
        }

        throw new IllegalArgumentException("Unsupported target: " + target);
    }
}
