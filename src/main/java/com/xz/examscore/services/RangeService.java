package com.xz.examscore.services;

import com.hyd.simplecache.SimpleCache;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoDatabase;
import com.xz.ajiaedu.common.concurrent.LockFactory;
import com.xz.ajiaedu.common.lang.CollectionUtils;
import com.xz.examscore.bean.Range;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
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

    static final Logger LOG = LoggerFactory.getLogger(RangeService.class);

    public static String DEFAULT_PROVINCE = "430000";

    @Autowired
    MongoDatabase scoreDatabase;

    @Autowired
    StudentService studentService;

    @Autowired
    ClassService classService;

    @Autowired
    SchoolService schoolService;

    @Autowired
    SimpleCache cache;

    /**
     * 查询指定项目的省份范围
     *
     * @param projectId 项目id
     *
     * @return  range
     */
    public Range queryProvinceRange(String projectId) {
        Document document = scoreDatabase.getCollection("province_list").find(doc("project", projectId)).first();
        if (document == null) {
            return Range.province(DEFAULT_PROVINCE);
        }

        return Range.province(document.getString("province"));
    }

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
        String cacheKey = "student_ranges:" + projectId;

        synchronized (LockFactory.getLock(cacheKey)) {
            return cache.get(cacheKey, () -> {
                List<Range> result = new ArrayList<>();
                Document query = doc("project", projectId);

                scoreDatabase.getCollection("student_list").find(query)
                        .forEach((Consumer<Document>) document ->
                                result.add(Range.student(document.getString("student"))));

                return CollectionUtils.asArrayList(result);
            });
        }
    }

    private List<Range> queryRangeList(String projectId, String collectionName, String rangeName) {

        List<Range> rangeList = new ArrayList<>();

        FindIterable<Document> documents = scoreDatabase
                .getCollection(collectionName)
                .find(new Document("project", projectId));

        documents.forEach((Consumer<Document>) document ->
                rangeList.add(new Range(rangeName, document.getString(rangeName))));

        return rangeList;
    }

    // 查询上一级的 range
    public Range getParentRange(String projectId, Range range) {
        if (range.match(Range.STUDENT)) {
            Document d = studentService.findStudent(projectId, range.getId());
            return d == null ? null : Range.clazz(d.getString("class"));
        } else if (range.match(Range.CLASS)) {
            Document d = classService.findClass(projectId, range.getId());
            return d == null ? null : Range.school(d.getString("school"));
        } else if (range.match(Range.SCHOOL)) {
            Document d = schoolService.findSchool(projectId, range.getId());
            return d == null ? null : Range.area(d.getString("area"));
        } else if (range.match(Range.AREA)) {
            return Range.city(range.getId().substring(0, 4) + "00");
        } else if (range.match(Range.CITY)) {
            return Range.province(range.getId().substring(0, 2) + "0000");
        } else {
            return null;
        }
    }

    public Document getParentsDocument(String projectId, Range range) {

        String cacheKey = "parent:" + projectId + ":range:" + range.getName() + "=" + range.getId();

        return cache.get(cacheKey, () -> {
            Document result = new Document(range.getName(), range.getId());

            Range parent = getParentRange(projectId, range);
            while (parent != null) {
                result.append(parent.getName(), parent.getId());
                parent = getParentRange(projectId, parent);
            }

            return result;
        });
    }

    public Map<String, List<Range>> getRangesMap(String projectId) {
        Map<String, List<Range>> rangesMap = new HashMap<>();
        rangesMap.put(Range.STUDENT, queryRanges(projectId, Range.STUDENT));
        rangesMap.put(Range.CLASS, queryRanges(projectId, Range.CLASS));
        rangesMap.put(Range.SCHOOL, queryRanges(projectId, Range.SCHOOL));
        rangesMap.put(Range.AREA, queryRanges(projectId, Range.AREA));
        rangesMap.put(Range.CITY, queryRanges(projectId, Range.CITY));
        rangesMap.put(Range.PROVINCE, queryRanges(projectId, Range.PROVINCE));
        Set<String> keySet = rangesMap.keySet();
        for (String key : keySet){
            List<Range> list = rangesMap.get(key);
            LOG.info("维度名称：{}, 数量：{}", key, list.size());
        }
        return rangesMap;
    }
}
