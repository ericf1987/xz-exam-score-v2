package com.xz.examscore.asynccomponents.aggrtask.impl;

import com.hyd.simplecache.utils.MD5;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.UpdateResult;
import com.xz.ajiaedu.common.lang.StringUtil;
import com.xz.examscore.asynccomponents.aggrtask.AggrTask;
import com.xz.examscore.asynccomponents.aggrtask.AggrTaskMessage;
import com.xz.examscore.asynccomponents.aggrtask.AggrTaskMeta;
import com.xz.examscore.bean.Range;
import com.xz.examscore.bean.Target;
import com.xz.examscore.services.*;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

import static com.xz.ajiaedu.common.mongo.MongoUtils.*;
import static com.xz.examscore.util.Mongo.target2Doc;

@AggrTaskMeta(taskType = "rank_level")
@Component
public class RankLevelTask extends AggrTask {

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
    protected void runTask(AggrTaskMessage taskInfo) {     // 一个 task 代表一个学生
        String projectId = taskInfo.getProjectId();
        Range stuRange = taskInfo.getRange();
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

            MongoCollection<Document> collection = scoreDatabase.getCollection("rank_level");
            Document query = doc("project", projectId)
                    .append("target", target2Doc(project))
                    .append("student", studentId);

            String levels = StringUtil.join(rankLevelList, "");
            UpdateResult result = collection.updateMany(query,
                    $set(doc("rankLevel." + rangeName, levels))
            );
            if (result.getModifiedCount() == 0) {
                collection.insertOne(
                        query.append("rankLevel." + rangeName, levels)
                                .append("md5", MD5.digest(UUID.randomUUID().toString()))
                );
            }
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
                // 缺考考生没有排名等级
                if (null == rankLevel) {
                    continue;
                }

                // 构造整体排名等级
                if (!rankLevelsMap.containsKey(rankRange.getName())) {
                    rankLevelsMap.put(rankRange.getName(), new HashMap<>());
                }

                String subjectId = sbjTarget.getId().toString();
                rankLevelsMap.get(rankRange.getName()).put(subjectId, rankLevel);
            }
        }
    }

    // 计算并保存科目排名等级
    private String saveRankLevel(String projectId, String studentId, Range rankRange, Target sbjTarget) {
        String rankLevel = rankService.getRankLevel(projectId, rankRange, sbjTarget, studentId);

        Document query = doc("project", projectId)
                .append("student", studentId)
                .append("target", target2Doc(sbjTarget));

        MongoCollection<Document> collection = scoreDatabase.getCollection("rank_level");
        UpdateResult result = collection.updateMany(query,
                $set(doc("rankLevel." + rankRange.getName(), rankLevel))
        );
        if(result.getModifiedCount() == 0){}

        return rankLevel;
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

}
