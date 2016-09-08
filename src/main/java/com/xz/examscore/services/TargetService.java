package com.xz.examscore.services;

import com.hyd.simplecache.SimpleCache;
import com.mongodb.client.MongoDatabase;
import com.xz.ajiaedu.common.beans.dic.QuestType;
import com.xz.ajiaedu.common.lang.StringUtil;
import com.xz.examscore.bean.Point;
import com.xz.examscore.bean.SubjectObjective;
import com.xz.examscore.bean.Target;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.xz.examscore.bean.Target.*;

@SuppressWarnings("unchecked")
@Service
public class TargetService {

    @Autowired
    MongoDatabase scoreDatabase;

    @Autowired
    QuestService questService;

    @Autowired
    QuestTypeService questTypeService;

    @Autowired
    PointService pointService;

    @Autowired
    AbilityLevelService abilityLevelService;

    @Autowired
    SubjectService subjectService;

    @Autowired
    SimpleCache cache;

    public Target getTarget(String projectId, String subjectId) {
        if (StringUtil.isNotBlank(subjectId)) {
            return Target.subject(subjectId);
        } else {
            return Target.project(projectId);
        }
    }

    public List<Target> queryTargets(String projectId, String... targetNames) {
        List<Target> targetList = new ArrayList<>();
        List<String> targetNameList = Arrays.asList(targetNames);

        if (targetNameList.contains(PROJECT)) {
            targetList.add(new Target(PROJECT, projectId));
        }

        if (targetNameList.contains(SUBJECT)) {
            targetList.addAll(querySubjects(projectId));
        }

        if (targetNameList.contains(QUEST)) {
            targetList.addAll(queryQuests(projectId));
        }

        if (targetNameList.contains(QUEST_TYPE)) {
            targetList.addAll(queryQuestTypes(projectId));
        }

        if (targetNameList.contains(POINT)) {
            targetList.addAll(queryPoints(projectId));
        }

        if (targetNameList.contains(SUBJECT_OBJECTIVE)) {
            targetList.addAll(querySubjectObjectives(projectId));
        }

        return targetList;
    }

    private List<Target> queryPoints(String projectId) {
        List<Point> points = pointService.getPoints(projectId);
        return points.stream()
                .map(p -> Target.point(p.getId()))
                .collect(Collectors.toList());
    }

    private List<Target> queryQuestTypes(String projectId) {
        List<QuestType> questTypes = questTypeService.generateQuestTypeList(projectId);

        return questTypes.stream()
                .map(questType -> Target.questType(questType.getId()))
                .collect(Collectors.toList());
    }

    private List<Target> querySubjectObjectives(String projectId) {
        String cacheKey = "project_subject_objectives:" + projectId;

        return cache.get(cacheKey, () -> {
            List<Target> result = new ArrayList<>();

            subjectService.querySubjects(projectId).forEach(subjectId -> {
                result.add(new Target(SUBJECT_OBJECTIVE, new SubjectObjective(subjectId, true)));
                result.add(new Target(SUBJECT_OBJECTIVE, new SubjectObjective(subjectId, false)));
            });

            return new ArrayList<>(result);
        });
    }

    private List<Target> querySubjects(String projectId) {
        return subjectService.querySubjects(projectId).stream()
                .map(subjectId -> new Target(SUBJECT, subjectId))
                .collect(Collectors.toList());
    }

    private List<Target> queryQuests(String projectId) {

        String cacheKey = "project_quest_target_list:" + projectId;

        return cache.get(cacheKey, () -> {
            List<Document> questDocs = questService.getQuests(projectId);

            List<Target> targets = questDocs.stream()
                    .map(doc -> Target.quest(doc.getString("questId")))
                    .collect(Collectors.toList());

            return new ArrayList<>(targets);
        });

    }

    /**
     * 从任意 Target 中获取对应的科目信息
     *
     * @param target 要获取科目信息的 target 对象
     *
     * @return target 对象对应的科目
     */
    public String getTargetSubjectId(String projectId, Target target) {
        String targetName = target.getName();

        switch (targetName) {
            case PROJECT:
                return null;

            case SUBJECT:
                return target.getId().toString();

            case Target.SUBJECT_OBJECTIVE:
                return target.getId(SubjectObjective.class).getSubject();

            case QUEST_TYPE:
                String questTypeId = target.getId().toString();
                QuestType questType = questTypeService.getQuestType(projectId, questTypeId);
                if (questType != null) {
                    return questType.getSubjectId();
                } else {
                    throw new IllegalArgumentException("Target not found in project " + projectId + ": " + target);
                }

            case QUEST:
                String questId = target.getId().toString();
                Document quest = questService.findQuest(projectId, questId);
                if (quest != null) {
                    return quest.getString("subject");
                } else {
                    throw new IllegalArgumentException("Target not found in project " + projectId + ": " + target);
                }
            case POINT:
                String pointId = target.getId().toString();
                Point point = pointService.getPoint(pointId);
                if (point != null) {
                    if(!StringUtils.isEmpty(point.getSubject()))
                        return point.getSubject();
                    else
                        throw new IllegalArgumentException("Subject does not exist in Point:" + point.getId());
                } else {
                    throw new IllegalArgumentException("Target not found in project " + projectId + ": " + target);
                }
            case SUBJECT_LEVEL:
                Document subjectLevel = (Document)target.getId();
                String subject = subjectLevel.getString("subject");
                if(!StringUtils.isEmpty(subject)){
                    return subject;
                }else{
                    throw new IllegalArgumentException("Subject does not exist in subjectLevel:" + subjectLevel);
                }
        }

        throw new IllegalArgumentException("Unsupported target: " + target);
    }
}
