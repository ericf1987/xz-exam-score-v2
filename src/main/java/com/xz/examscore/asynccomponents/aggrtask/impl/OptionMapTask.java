package com.xz.examscore.asynccomponents.aggrtask.impl;

import com.hyd.simplecache.utils.MD5;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.xz.ajiaedu.common.lang.StringUtil;
import com.xz.examscore.asynccomponents.aggrtask.AggrTask;
import com.xz.examscore.asynccomponents.aggrtask.AggrTaskMessage;
import com.xz.examscore.asynccomponents.aggrtask.AggrTaskMeta;
import com.xz.examscore.bean.Range;
import com.xz.examscore.services.StudentService;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

import static com.xz.ajiaedu.common.mongo.MongoUtils.*;
import static com.xz.examscore.util.Mongo.query;

@Component
@AggrTaskMeta(taskType = "option_map")
public class OptionMapTask extends AggrTask {

    @Autowired
    MongoDatabase scoreDatabase;

    @Autowired
    StudentService studentService;

    @Override
    protected void runTask(AggrTaskMessage taskInfo) {
        String projectId = taskInfo.getProjectId();
        String questId = taskInfo.getTarget().getId().toString();

        MongoCollection<Document> scoreCollection = scoreDatabase.getCollection("score");
        MongoCollection<Document> optionMapCollection = scoreDatabase.getCollection("option_map");

        processRange(projectId, questId, taskInfo.getRange(), scoreCollection, optionMapCollection);
    }

    private void processRange(
            String projectId, String questId, Range range,
            MongoCollection<Document> scoreCollection, MongoCollection<Document> optionMapCollection) {

        AggregateIterable<Document> aggregate = scoreCollection.aggregate(Arrays.asList(
                $match(doc("project", projectId).append("quest", questId)
                        .append(range.getName(), range.getId())),
                $group(doc("_id", doc("answer", "$answer")).append("count", doc("$sum", 1)))
        ));

        List<Document> optionMapList = new ArrayList<>();
        int studentCount = studentService.getStudentCount(projectId, range);

        aggregate.forEach((Consumer<Document>) document -> {
            List<Document> newOptionMapList = convertOneDoc(document);
            for (Document newOptionMap : newOptionMapList) {
                String answer = ((Document) newOptionMap.get("_id")).getString("answer");
                int count;
                double rate;

                if (studentCount == 0) {
                    count = 0;
                    rate = 0;
                } else {
                    count = newOptionMap.getInteger("count");
                    rate = (double) count / studentCount;
                }

                addUpToList(optionMapList, answer, count, rate, studentCount);
            }
        });

        Document query = query(projectId, range).append("quest", questId);
        Document update = $set(
                doc("optionMap", optionMapList)
                        .append("count", studentCount)
                        .append("md5", MD5.digest(UUID.randomUUID().toString())
                        )
        );
        optionMapCollection.updateOne(query, update, UPSERT);
    }

    private void addUpToList(List<Document> optionMapList, String answer, int oneCount, double rate, int studentCount) {
        for (Document document : optionMapList) {
            if (document.getString("answer").equals(answer)) {
                //如果答案选项已经存在，则累加count，同时修改rate
                int count = StringUtil.isEmpty(document.getInteger("count").toString()) ? 0 : document.getInteger("count");
                document.put("count", count + oneCount);
                document.put("rate", (count + oneCount) / studentCount);
                return;
            }
        }

        //如果没有找到选项，则新增选项
        optionMapList.add(doc("answer", answer).append("count", oneCount).append("rate", rate));
    }

    //将answer为多个选项的doc转化成多个单选项的组成的list
    public List<Document> convertOneDoc(Document optionMap) {
        List<Document> newOptionMapList = new ArrayList<>();
        String answer = ((Document) optionMap.get("_id")).getString("answer");
        int count = optionMap.getInteger("count");
        char[] arr = answer.toCharArray();
        for (char a : arr) {
            newOptionMapList.add(new Document().append("_id", doc("answer", String.valueOf(a))).append("count", count));
        }
        return newOptionMapList;
    }
}
