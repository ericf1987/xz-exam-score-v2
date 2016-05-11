package com.xz.services;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.xz.bean.Target;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@SuppressWarnings("unchecked")
@Service
public class TargetService {

    @Autowired
    MongoDatabase scoreDatabase;

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

        return targetList;
    }

    private List<Target> querySubjects(String projectId) {
        ArrayList<Target> targets = new ArrayList<>();
        Document query = new Document("projectId", projectId);
        MongoCollection<Document> collection = scoreDatabase.getCollection("subject_list");

        collection.find(query).forEach((Consumer<Document>) document -> {
            List<String> subjectIds = (List<String>) document.get("subjectIds");
            targets.addAll(subjectIds.stream()
                    .map(subjectId -> new Target(Target.SUBJECT, subjectId))
                    .collect(Collectors.toList())
            );
        });

        return targets;
    }

    private List<Target> queryQuests(String projectId) {
        ArrayList<Target> quests = new ArrayList<>();
        Document query = new Document("projectId", projectId);
        MongoCollection<Document> collection = scoreDatabase.getCollection("quest_list");

        collection.find(query).forEach((Consumer<Document>) document -> {
            String subjectId = document.getString("subjectId");
            String questNo = document.getString("questNo");
            quests.add(new Target(Target.QUEST, questNo).setSubjectId(subjectId));
        });

        return quests;
    }
}
