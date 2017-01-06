package com.xz.examscore.services;

import com.hyd.simplecache.SimpleCache;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoDatabase;
import com.xz.ajiaedu.common.lang.CollectionUtils;
import com.xz.examscore.bean.Range;
import com.xz.examscore.bean.Target;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.xz.ajiaedu.common.mongo.MongoUtils.*;

/**
 * @author by fengye on 2017/1/4.
 */
@Service
public class QuestAbilityLevelScoreService {

    @Autowired
    MongoDatabase scoreDatabase;

    @Autowired
    SimpleCache cache;

    @Autowired
    QuestAbilityLevelService questAbilityLevelService;

    @Autowired
    FullScoreService fullScoreService;

    public double getTotalScore(String projectId, String questAbilityLevel, String subjectId, String levelOrAbility, Range range) {

        String cacheKey = "QuestAbilityLevelScoreService_getTotalScore:" + projectId + ":" + questAbilityLevel + ":" + subjectId + ":" + levelOrAbility + ":" + range;

        return cache.get(cacheKey, () -> {
            Document m = questAbilityLevelService.getQuery(projectId, questAbilityLevel, subjectId, levelOrAbility);
            Document g = doc("_id", null).append("totalScore", doc("$sum", "$score"));
            if (range != null)
                m.append(range.getName(), range.getId());
            AggregateIterable<Document> aggregate = scoreDatabase.getCollection("quest_ability_level_score").aggregate(Arrays.asList(
                    $match(m), $group(g)
            ));
            return aggregate != null && aggregate.first() != null ? aggregate.first().getDouble("totalScore") : 0d;
        });

    }

    public int getStudentCount(String projectId, String questAbilityLevel, String subjectId, String levelOrAbility, Range range) {
        String cacheKey = "QuestAbilityLevelScoreService_getStudentCount:" + projectId + ":" + questAbilityLevel + ":" + subjectId + ":" + levelOrAbility + ":" + range;

        return cache.get(cacheKey, () -> {
            Document query = questAbilityLevelService.getQuery(projectId, questAbilityLevel, subjectId, levelOrAbility);
            if (range != null)
                query.append(range.getName(), range.getId());
            return (int) scoreDatabase.getCollection("quest_ability_level_score").count(query);
        });

    }

    public List<Document> getStudentList(String projectId, String questAbilityLevel, String subjectId, String levelOrAbility, Range range) {

        String cacheKey = "QuestAbilityLevelScoreService_getStudentList:" + projectId + ":" + questAbilityLevel + ":" + subjectId + ":" + levelOrAbility + ":" + range;

        return cache.get(cacheKey, () -> {
            Document query = questAbilityLevelService.getQuery(projectId, questAbilityLevel, subjectId, levelOrAbility);
            if (range != null)
                query.append(range.getName(), range.getId());
            FindIterable<Document> findIterable = scoreDatabase.getCollection("quest_ability_level_score").find(query);
            return CollectionUtils.asArrayList(toList(findIterable));
        });

    }

    public List<Document> filterStudentList(String projectId, String questAbilityLevel, String subjectId, String levelOrAbility, Range range, double factor) {

        String cacheKey = "QuestAbilityLevelScoreService_filterStudentList:" + projectId + ":" + questAbilityLevel + ":" + subjectId + ":" + levelOrAbility + ":" + range + ":" + factor;

        return cache.get(cacheKey, () -> {
            List<Document> studentList = getStudentList(projectId, questAbilityLevel, subjectId, levelOrAbility, range);
            double fullScore = fullScoreService.getFullScore(projectId, Target.questAbilityLevel(questAbilityLevel));
            double score = fullScore * factor;
            List<Document> result = studentList.stream().filter(student -> student.getDouble("score") >= score).collect(Collectors.toList());
            return CollectionUtils.asArrayList(result);
        });

    }

}
