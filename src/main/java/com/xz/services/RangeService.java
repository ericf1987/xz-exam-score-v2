package com.xz.services;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoDatabase;
import com.xz.bean.Range;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

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

    // range -> 集合 [range]_list 中的列表属性名
    private static final Map<String, String> RANGE_FIELD_MAP = new HashMap<>();

    static {
        RANGE_FIELD_MAP.put("province", "provinces");
        RANGE_FIELD_MAP.put("city", "citys");
        RANGE_FIELD_MAP.put("area", "areas");
        RANGE_FIELD_MAP.put("school", "schools");
        RANGE_FIELD_MAP.put("class", "classes");
    }

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
            String fieldName = RANGE_FIELD_MAP.get(range);
            if (fieldName == null) {
                throw new IllegalArgumentException("找不到 Range '" + range + "' 对应的属性名");
            }
            return queryRangeList(projectId, range + "_list", fieldName, range);
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

    private List<Range> queryRangeList(String projectId, String collectionName, String idListField, String rangeName) {
        List<Range> classIds = new ArrayList<>();

        FindIterable<Document> documents = scoreDatabase
                .getCollection(collectionName)
                .find(new Document("project", projectId));

        documents.forEach((Consumer<Document>) document -> {
            List<String> idList = (List<String>) document.get(idListField);
            if (idList == null) {
                throw new IllegalStateException("找不到集合 " + collectionName + " 的属性: " + idListField);
            }
            classIds.addAll(idList.stream().map(id -> new Range(rangeName, id)).collect(Collectors.toList()));
        });

        return classIds;
    }
}
