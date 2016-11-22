package com.xz.examscore.services;

import com.hyd.simplecache.SimpleCache;
import com.hyd.simplecache.utils.MD5;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.xz.ajiaedu.common.lang.StringUtil;
import com.xz.ajiaedu.common.mongo.DocumentUtils;
import com.xz.examscore.bean.ProjectConfig;
import com.xz.examscore.bean.Range;
import com.xz.examscore.bean.Target;
import com.xz.examscore.util.DocUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.xz.ajiaedu.common.mongo.DocumentUtils.addList;
import static com.xz.ajiaedu.common.mongo.MongoUtils.$or;
import static com.xz.ajiaedu.common.mongo.MongoUtils.doc;
import static com.xz.examscore.util.Mongo.range2Doc;

/**
 * 报表条目管理
 *
 * @author zhaorenwu
 */
@Service
public class ReportItemService {

    public static final String COMMON_RANGE_ID = "common";  // 通用报表id

    public static final String[] POINT_LEVEL_KEYS
            = new String[]{"双向细目", "知识点", "能力层级"};    // 知识点-能力层级统计类关键字

    public static final String ENTRY_LEVEL_REPORT = "上线预测"; //上线预测报表关键字

    @Autowired
    MongoDatabase scoreDatabase;

    @Autowired
    PointService pointService;

    @Autowired
    AverageService averageService;

    @Autowired
    SimpleCache instantCache;

    @Autowired
    ProjectConfigService projectConfigService;

    /**
     * 查询指定报表条目明细
     *
     * @param id 条目id
     * @return 条目明细
     */
    public Document queryReportItemById(String id) {
        String cacheKey = "report_item_id" + id;
        return instantCache.get(cacheKey, () -> {
            MongoCollection<Document> collection = scoreDatabase.getCollection("report_item_list");
            return collection.find(doc("_id", new ObjectId(id))).first();
        });
    }

    /**
     * 查询指定项目报表条目信息
     *
     * @param projectId 考试项目id
     * @return 报表条目信息
     */
    public Map<String, Object> querySchoolReportItems(String projectId) {
        String cacheKey = "school_report_items:" + projectId;
        return instantCache.get(cacheKey, () -> {
            HashMap<String, Object> reportItems = new LinkedHashMap<>();
            for (ReportRange reportRange : ReportRange.values()) {
                Map<String, Object> map = new LinkedHashMap<>();
                String rangeName = reportRange.name();

                Range range = new Range(rangeName, projectId);
                Map<String, List<Document>> reportItemMap = queryReportItems(range);
                for (String type : reportItemMap.keySet()) {
                    map.put(type, checkAndGetItemsData(projectId, type, reportItemMap));
                }

                reportItems.put(rangeName, map);
            }

            return reportItems;
        });
    }

    @SuppressWarnings("unchecked")
    private List<Map<String, Object>> checkAndGetItemsData(String projectId, String type,
                                                           Map<String, List<Document>> reportItemMap) {
        List<Map<String, Object>> list = new ArrayList<>();

        List<Document> reportItemList = reportItemMap.get(type);

        ProjectConfig projectConfig = projectConfigService.getProjectConfig(projectId);
        for (Document document : reportItemList) {
            Map<String, Object> reportItem = new HashMap<>();
            String name = document.getString("name");
            reportItem.put("type", document.getString("type"));
            reportItem.put("name", name);
            reportItem.put("id", document.getObjectId("_id").toHexString());
            reportItem.put("tag", document.getString("tag"));
            Map<String, Object> rangeMap = (Map<String, Object>)document.get("range");
            //默认允许下载
            boolean downloadAllowed = true;
            String rangeName = MapUtils.getString(rangeMap, "name");

            if (isPointOrLevelItem(name)) {
                reportItem.put("dataStatus", averageService.isExistAverage(projectId, Target.POINT));
            } else {
                reportItem.put("dataStatus", checkItemDate(projectId, document));
                //根据联考开关判断该报表是否允许下载
                if(projectConfig.isShareSchoolReport() && rangeName.equals("province")){
                    downloadAllowed = false;
                }
            }

            //上线预测报表需要根据project_config的配置参数来确定是否在页面显示
            if (name.equals(ENTRY_LEVEL_REPORT)) {
                reportItem.put("dataStatus", checkItemDate(projectId, document) && projectConfig.isEntryLevelEnable());
            }
            reportItem.put("downloadAllowed", downloadAllowed);
            list.add(reportItem);
        }

        return list;
    }

    // 是否是知识点与能力层级相关条目
    private boolean isPointOrLevelItem(String itemName) {
        if (StringUtil.isBlank(itemName)) {
            return false;
        }

        for (String pointLevelKey : POINT_LEVEL_KEYS) {
            if (itemName.contains(pointLevelKey)) {
                return true;
            }
        }

        return false;
    }

    // 检查指定学校指定项目报表条目是否有数据
    @SuppressWarnings("unchecked")
    private boolean checkItemDate(String projectId, Document document) {
        List<String> collectionNames = document.get("collection_names", List.class);
        if (collectionNames == null || collectionNames.isEmpty()) {
            return false;
        }

        for (String collectionName : collectionNames) {
            boolean status = checkCollectionData(projectId, collectionName);
            if (status) {
                return true;
            }
        }

        return false;
    }

    // 检查集合指定项目是否有数据
    public boolean checkCollectionData(String projectId, String collectionName) {
        String cacheKey = "collection_data_status:" + projectId + ":" + collectionName;
        return instantCache.get(cacheKey, () -> {
            MongoCollection<Document> collection = scoreDatabase.getCollection(collectionName);
            Document query = doc("project", projectId);
            return collection.find(query).first() != null;
        });
    }

    /**
     * 查询指定范围的报表条目列表(包括通用报表与自定义报表)
     *
     * @param range 范围
     * @return 报表分类列表
     */
    public Map<String, List<Document>> queryReportItems(Range range) {
        String cacheKey = "report_item_list:" + range;
        return instantCache.get(cacheKey, () -> {
            MongoCollection<Document> collection = scoreDatabase.getCollection("report_item_list");

            Range commonRange = new Range(range.getName(), COMMON_RANGE_ID);
            Document query = new Document($or(doc("range", range2Doc(range)), doc("range", range2Doc(commonRange))));

            HashMap<String, List<Document>> map = new LinkedHashMap<>();
            FindIterable<Document> iterable = collection.find(query).sort(doc("position", 1));
            for (Document document : iterable) {
                String type = document.getString("type");
                if (!map.containsKey(type)) {
                    map.put(type, new ArrayList<>());
                }

                map.get(type).add(document);
            }

            return map;
        });
    }

    /**
     * 获取排序最大的位置
     *
     * @return 最大的位置
     */
    public int queryMaxPosition() {
        MongoCollection<Document> collection = scoreDatabase.getCollection("report_item_list");
        Document document = collection.find().sort(doc("position", -1)).first();
        if (document == null) {
            return 0;
        }

        return DocumentUtils.getInt(document, "position", 0);
    }

    /**
     * 添加一个报表条目
     *
     * @param range           范围
     * @param type            类型
     * @param name            报表名称
     * @param collectionNames 报表数据来源的集合名称
     * @param serverName      报表接口名称
     * @param tag             报表文件保存路径标识
     */
    public void addReportItem(Range range, String type, String name,
                              String[] collectionNames, String serverName, String tag) {

        Document document = new Document();
        document.append("range", range2Doc(range));
        document.append("type", type);
        document.append("name", name);
        addList(document, "collection_names", collectionNames);
        document.append("server_name", serverName);
        document.append("tag", tag);
        document.append("position", queryMaxPosition() + 1)
                .append("md5", MD5.digest(UUID.randomUUID().toString()));

        scoreDatabase.getCollection("report_item_list").insertOne(document);
    }

    /**
     * 更新报表属性
     *
     * @param id              条目id
     * @param type            报表类型
     * @param name            条目名称
     * @param collectionNames 集合名称列表
     * @param serverName      报表接口名称
     * @param position        排序的位置
     * @param tag             报表文件保存路径标识
     */
    public void updateReportItem(String id, String type, String name, String[] collectionNames,
                                 String serverName, String position, String tag) {
        MongoCollection<Document> collection = scoreDatabase.getCollection("report_item_list");
        Document query = doc("_id", new ObjectId(id));

        Document document = new Document();
        DocUtils.addTo(document, "type", type);
        DocUtils.addTo(document, "name", name);
        DocumentUtils.addList(document, "collection_names", collectionNames);
        DocUtils.addTo(document, "server_name", serverName);
        DocUtils.addTo(document, "tag", tag);
        DocUtils.addTo(document, "md5", MD5.digest(UUID.randomUUID().toString()));

        if (StringUtil.isNotBlank(position)) {
            document.put("position", NumberUtils.toInt(position));
        }

        Document update = new Document("$set", document);
        collection.updateOne(query, update);
    }

    /**
     * 删除指定条目
     *
     * @param id 条目id
     */
    public void deleteReportItem(String id) {
        MongoCollection<Document> collection = scoreDatabase.getCollection("report_item_list");
        collection.deleteOne(doc("_id", new ObjectId(id)));
    }

    ///////////////////////////////////////////////////////////     报表类型

    @SuppressWarnings("unused")
    public enum ReportType {
        basics,     // 基础报表
        paper,      // 试卷分析报表
        topStudent, // 尖子生分析
        compare     // 比较类报表
    }

    //////////////////////////////////////////////////////////      报表范围

    public enum ReportRange {
        province,   // 总体报表
        school,     // 学校报表
        clazz       // 班级报表
    }
}
