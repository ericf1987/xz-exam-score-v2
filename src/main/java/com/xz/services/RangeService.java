package com.xz.services;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoDatabase;
import com.xz.bean.Range;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

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
        return queryRangeList(projectId, range + "_list", range + "Ids", range);
    }

    private List<Range> queryRangeList(String projectId, String collectionName, String idListField, String rangeName) {
        List<Range> classIds = new ArrayList<>();

        FindIterable<Document> documents = scoreDatabase
                .getCollection(collectionName)
                .find(new Document("projectId", projectId));

        documents.forEach((Consumer<Document>) document ->
                classIds.addAll(
                        ((List<String>) document.get(idListField)).stream()
                                .map(id -> new Range(rangeName, id))
                                .collect(Collectors.toList())
                )
        );

        return classIds;
    }
}
