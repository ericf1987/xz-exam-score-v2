package com.xz.examscore.asynccomponents.aggrtask.impl.optionMap;

import com.hyd.simplecache.utils.MD5;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.UpdateResult;
import com.xz.ajiaedu.common.lang.CounterMap;
import com.xz.ajiaedu.common.mongo.MongoUtils;
import com.xz.examscore.asynccomponents.aggrtask.AggrTask;
import com.xz.examscore.asynccomponents.aggrtask.AggrTaskMessage;
import com.xz.examscore.asynccomponents.aggrtask.AggrTaskMeta;
import com.xz.examscore.bean.Range;
import com.xz.examscore.bean.Target;
import com.xz.examscore.services.OptionMapService;
import com.xz.examscore.services.SchoolService;
import com.xz.examscore.services.StudentService;
import com.xz.examscore.services.TargetService;
import com.xz.examscore.util.Mongo;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

import static com.xz.ajiaedu.common.mongo.MongoUtils.$set;
import static com.xz.ajiaedu.common.mongo.MongoUtils.doc;

/**
 * @author by fengye on 2017/1/30.
 */
@Component
@AggrTaskMeta(taskType = "province_option_map")
public class ProvinceOptionMapTask extends AggrTask {
    @Autowired
    MongoDatabase scoreDatabase;

    @Autowired
    StudentService studentService;

    @Autowired
    OptionMapService optionMapService;

    @Autowired
    SchoolOptionMapTask schoolOptionMapTask;

    @Autowired
    SchoolService schoolService;

    @Autowired
    TargetService targetService;

    @Override
    protected void runTask(AggrTaskMessage taskInfo) {
        String projectId = taskInfo.getProjectId();
        Range provinceRange = taskInfo.getRange();
        Target questTarget = taskInfo.getTarget();

        processOptionMap(projectId, provinceRange, questTarget);
    }

    private void processOptionMap(String projectId, Range provinceRange, Target questTarget) {
        String questId = questTarget.getId().toString();
        CounterMap<String> counterMap = new CounterMap<>();

        //查询子维度
        List<Document> projectSchools = schoolService.getProjectSchools(projectId);

        for (Document schoolDoc : projectSchools) {
            String schoolId = schoolDoc.getString("school");
            Range schoolRange = Range.school(schoolId);
            List<Document> optionList = optionMapService.getOptionList(projectId, questId, schoolRange);
            optionList.forEach(option -> {
                String answer = option.getString("answer");
                Integer count = option.getInteger("count");
                counterMap.incre(answer, count);
            });
        }

        String targetSubjectId = targetService.getTargetSubjectId(projectId, questTarget);

        //获取参考人数
        int studentCount = studentService.getStudentCount(projectId, targetSubjectId, provinceRange);
        List<Document> optionMapList = schoolOptionMapTask.convert2OptionDoc(counterMap, studentCount);

        MongoCollection<Document> collection = scoreDatabase.getCollection("option_map");

        Document query = MongoUtils.doc("project", projectId).append("range", Mongo.range2Doc(provinceRange))
                .append("quest", questId);

        Document update = $set(
                doc("optionMap", optionMapList)
                        .append("count", studentCount)
        );
        UpdateResult result = collection.updateMany(query, update);
        if (result.getMatchedCount() == 0) {
            collection.insertOne(
                    query.append("optionMap", optionMapList)
                            .append("count", studentCount)
                            .append("md5", MD5.digest(UUID.randomUUID().toString()))
            );
        }

    }

}
