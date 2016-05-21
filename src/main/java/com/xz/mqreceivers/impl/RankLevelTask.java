package com.xz.mqreceivers.impl;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.xz.ajiaedu.common.lang.StringUtil;
import com.xz.bean.ProjectConfig;
import com.xz.bean.Range;
import com.xz.bean.Target;
import com.xz.mqreceivers.AggrTask;
import com.xz.mqreceivers.Receiver;
import com.xz.mqreceivers.ReceiverInfo;
import com.xz.services.*;
import com.xz.util.Mongo;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

import static com.xz.ajiaedu.common.mongo.MongoUtils.$set;
import static com.xz.ajiaedu.common.mongo.MongoUtils.doc;
import static com.xz.util.Mongo.range2Doc;
import static com.xz.util.Mongo.target2Doc;
import static com.xz.util.SubjectUtil.isCombinedSubject;

@ReceiverInfo(taskType = "ranking_level")
@Component
public class RankLevelTask extends Receiver {

    @Autowired
    RankService rankService;

    @Autowired
    TargetService targetService;

    @Autowired
    RangeService rangeService;

    @Autowired
    MongoDatabase scoreDatabase;

    @Autowired
    StudentService studentService;

    @Autowired
    ProjectConfigService projectConfigService;

    @Override
    protected void runTask(AggrTask aggrTask) {     // 一个 task 代表一个学生
        String projectId = aggrTask.getProjectId();
        Range stuRange = aggrTask.getRange();
        String studentId = stuRange.getId();

        List<Target> sbjTargets = getSubjectTargets(projectId);
        Range[] rankRanges = getRankRanges(projectId, studentId);

        // rankRangeName -> {subjectName -> subjectRankLevel}
        Map<String, Map<String, String>> rankLevelsMap = new HashMap<>();

        saveSubjectRankLevels(projectId, studentId, sbjTargets, rankRanges, rankLevelsMap);
        saveProjectRankLevels(projectId, studentId, rankLevelsMap);
    }

    /**
     * 保存项目排名等级
     *
     * @param projectId     项目ID
     * @param studentId     学生ID
     * @param rankLevelsMap 排名等级信息
     */
    private void saveProjectRankLevels(String projectId, String studentId, Map<String, Map<String, String>> rankLevelsMap) {

        Range student = Range.student(studentId);
        Target project = Target.project(projectId);

        boolean combinedSubjects =
                projectConfigService.getProjectConfig(projectId).isCombineCategorySubjects();

        for (String rangeName : rankLevelsMap.keySet()) {
            Map<String, String> rankLevels = rankLevelsMap.get(rangeName);

            if (combinedSubjects) {
                removeCombiningSubjects(rankLevels);
            }

            List<String> rankLevelList = new ArrayList<>(rankLevels.values());
            Collections.sort(rankLevelList);  // 把等级高的放在前面，例如 "AABAA" 排列成 "AAAAB"

            MongoCollection<Document> totalScoreCollection = scoreDatabase.getCollection("total_score");
            totalScoreCollection.updateMany(
                    doc("project", projectId).append("range", range2Doc(student)).append("target", target2Doc(project)),
                    $set("rankLevel." + rangeName, StringUtil.join(rankLevelList, ""))
            );
        }
    }

    private void removeCombiningSubjects(Map<String, String> rankLevels) {
        Iterator<Map.Entry<String, String>> iterator = rankLevels.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, String> entry = iterator.next();
            if (!StringUtil.isOneOf(entry.getKey(), "001", "002", "003", "004005006", "007008009")) {
                iterator.remove();
            }
        }
    }

    /**
     * 保存科目排名等级
     *
     * @param projectId     项目ID
     * @param studentId     学生ID
     * @param sbjTargets    科目列表
     * @param rankRanges    排名范围列表
     * @param rankLevelsMap 将科目的排名等级放入这里，用于构造考试项目排名等级（例如 "AAAAA"）
     */
    private void saveSubjectRankLevels(
            String projectId, String studentId,
            List<Target> sbjTargets, Range[] rankRanges,
            Map<String, Map<String, String>> rankLevelsMap) {

        for (Target sbjTarget : sbjTargets) {
            for (Range rankRange : rankRanges) {

                // 保存排名等级
                String rankLevel = saveRankLevel(projectId, studentId, rankRange, sbjTarget);

                // 构造整体排名等级
                if (!rankLevelsMap.containsKey(rankRange.getName())) {
                    rankLevelsMap.put(rankRange.getName(), new HashMap<>());
                }

                String subjectId = sbjTarget.getId().toString();
                rankLevelsMap.get(rankRange.getName()).put(subjectId, rankLevel);
            }
        }
    }

    private Range[] getRankRanges(String projectId, String studentId) {
        Range classRange = studentService.getStudentRange(projectId, studentId, Range.CLASS);
        Range schoolRange = studentService.getStudentRange(projectId, studentId, Range.SCHOOL);
        return new Range[]{classRange, schoolRange};
    }

    private List<Target> getSubjectTargets(String projectId) {
        List<Target> sbjTargets = targetService.queryTargets(projectId, Target.SUBJECT);

        // 如果项目要求文理合并，则去掉单独的文理科目，加上文综理综两个科目
        if (projectConfigService.getProjectConfig(projectId).isCombineCategorySubjects()) {
            sbjTargets.add(Target.subject("004005006"));
            sbjTargets.add(Target.subject("007008009"));
        }
        return sbjTargets;
    }

    private String saveRankLevel(String projectId, String studentId, Range rankRange, Target sbjTarget) {
        String rankLevel = rankService.getRankLevel(projectId, rankRange, sbjTarget, studentId);

        MongoCollection<Document> totalScoreCollection = getCollection(projectId, sbjTarget);
        totalScoreCollection.updateMany(doc()
                        .append("project", projectId)
                        .append("target", Mongo.target2Doc(sbjTarget))
                        .append("range", range2Doc(Range.student(studentId))),
                $set("rankLevel." + rankRange.getName(), rankLevel));

        return rankLevel;
    }

    private MongoCollection<Document> getCollection(String projectId, Target target) {
        ProjectConfig projectConfig = projectConfigService.getProjectConfig(projectId);
        if (projectConfig.isCombineCategorySubjects() && isCombinedSubject(target)) {
            return scoreDatabase.getCollection("total_score_combined");
        } else {
            return scoreDatabase.getCollection("total_score");
        }
    }
}
