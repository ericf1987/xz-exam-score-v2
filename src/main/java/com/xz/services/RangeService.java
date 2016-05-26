package com.xz.services;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoDatabase;
import com.xz.bean.Range;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

import static com.xz.ajiaedu.common.mongo.MongoUtils.doc;

/**
 * (description)
 * created at 16/05/10
 *
 * @author yiding_he
 */
@Service
@SuppressWarnings("unchecked")
public class RangeService {

    @Autowired
    MongoDatabase scoreDatabase;

    /**
     * 列出一个项目的所有 Range
     *
     * @param projectId 项目ID
     * @param ranges    范围名称列表
     *
     * @return 范围列表
     */
    public List<Range> queryRanges(String projectId, String... ranges) {
        List<Range> result = new ArrayList<>();

        for (String range : ranges) {
            result.addAll(queryRanges(projectId, range));
        }

        return result;
    }

    /**
     * 列出一个项目指定的范围列表
     *
     * @param projectId 项目ID
     * @param range     范围名称
     *
     * @return 范围列表
     */
    public List<Range> queryRanges(String projectId, String range) {

        if (Objects.equals(range, Range.STUDENT)) {
            return queryStudentRangeList(projectId);
        } else {
            return queryRangeList(projectId, range + "_list", range);
        }

    }

    private List<Range> queryStudentRangeList(String projectId) {
        List<Range> result = new ArrayList<>();
        Document query = doc("project", projectId);

        scoreDatabase.getCollection("student_list").find(query)
                .forEach((Consumer<Document>) document ->
                        result.add(new Range(Range.STUDENT, document.getString("student"))));

        return result;
    }

    private List<Range> queryRangeList(String projectId, String collectionName, String rangeName) {
        List<Range> classIds = new ArrayList<>();

        FindIterable<Document> documents = scoreDatabase
                .getCollection(collectionName)
                .find(new Document("project", projectId));

        documents.forEach((Consumer<Document>) document ->
                classIds.add(new Range(rangeName, document.getString(rangeName))));

        return classIds;
    }
}