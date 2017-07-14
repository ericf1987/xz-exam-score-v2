package com.xz.examscore.services;

import com.hyd.simplecache.SimpleCache;
import com.hyd.simplecache.utils.MD5;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.UpdateResult;
import com.xz.examscore.cache.ProjectCacheManager;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static com.xz.ajiaedu.common.mongo.MongoUtils.$set;
import static com.xz.ajiaedu.common.mongo.MongoUtils.doc;

/**
 * (description)
 * created at 16/05/11
 *
 * @author yiding_he
 */
@Service
public class SubjectService {

    private static final Map<String, String> SUBJECT_NAMES = new HashMap<>();

    static {
        SUBJECT_NAMES.put("000", "全科");
        SUBJECT_NAMES.put("001", "语文");
        SUBJECT_NAMES.put("002", "数学");
        SUBJECT_NAMES.put("003", "英语");
        SUBJECT_NAMES.put("004", "物理");
        SUBJECT_NAMES.put("005", "化学");
        SUBJECT_NAMES.put("006", "生物");
        SUBJECT_NAMES.put("007", "政治");
        SUBJECT_NAMES.put("008", "历史");
        SUBJECT_NAMES.put("009", "地理");
        SUBJECT_NAMES.put("010", "社会");
        SUBJECT_NAMES.put("011", "科学");
        SUBJECT_NAMES.put("012", "技术与设计");
        SUBJECT_NAMES.put("013", "思想品德");
        SUBJECT_NAMES.put("014", "信息技术");
        SUBJECT_NAMES.put("015", "体育");
        SUBJECT_NAMES.put("016", "品生与品社");
        SUBJECT_NAMES.put("017", "小学综合");
        SUBJECT_NAMES.put("018", "学法知法");
        SUBJECT_NAMES.put("019", "道德与法治");
        SUBJECT_NAMES.put("020", "专业科目");
        SUBJECT_NAMES.put("021", "专业科目2");
        SUBJECT_NAMES.put("022", "音乐");
        SUBJECT_NAMES.put("023", "美术");
        SUBJECT_NAMES.put("024", "生命与健康");
        SUBJECT_NAMES.put("025", "生物A");
        SUBJECT_NAMES.put("026", "生物B");
        SUBJECT_NAMES.put("027", "地理A");
        SUBJECT_NAMES.put("028", "地理B");
        SUBJECT_NAMES.put("022023", "音乐美术");
        SUBJECT_NAMES.put("011016024", "科学-品生-生命");

        SUBJECT_NAMES.put("020021", "专业科目12综合");
        SUBJECT_NAMES.put("004005006", "理科综合");
        SUBJECT_NAMES.put("007008009", "文科综合");
        SUBJECT_NAMES.put("006009", "生地综合");
        SUBJECT_NAMES.put("007008", "政史综合");
        SUBJECT_NAMES.put("004005", "理化综合");
        SUBJECT_NAMES.put("001002", "语数综合");
        SUBJECT_NAMES.put("011013", "科学思品");
        SUBJECT_NAMES.put("003004005", "英物化综合");
        SUBJECT_NAMES.put("101", "种植专业");
        SUBJECT_NAMES.put("102", "英语专业");
        SUBJECT_NAMES.put("103", "医卫专业");
        SUBJECT_NAMES.put("104", "文秘专业");
        SUBJECT_NAMES.put("105", "师范专业");
        SUBJECT_NAMES.put("106", "商贸专业");
        SUBJECT_NAMES.put("107", "美术专业");
        SUBJECT_NAMES.put("108", "旅游专业");
        SUBJECT_NAMES.put("109", "计算机专业");
        SUBJECT_NAMES.put("110", "机电专业");
        SUBJECT_NAMES.put("111", "电子专业");
        SUBJECT_NAMES.put("112", "财会专业");

    }

    @Autowired
    MongoDatabase scoreDatabase;

    @Autowired
    ProjectCacheManager projectCacheManager;

    @Autowired
    ProjectConfigService projectConfigService;

    @Autowired
    StudentService studentService;

    /**
     * 查询考试项目的科目列表
     *
     * @param projectId 项目ID
     * @return 科目ID列表
     */
    @SuppressWarnings("unchecked")
    public List<String> querySubjects(String projectId) {
        String cacheKey = "subject_list:" + projectId;

        SimpleCache simpleCache = projectCacheManager.getProjectCache(projectId);

        return new ArrayList<>(simpleCache.get(cacheKey, () -> {
            ArrayList<String> targets = new ArrayList<>();
            MongoCollection<Document> collection = scoreDatabase.getCollection("subject_list");

            collection.find(doc("project", projectId)).forEach((Consumer<Document>) document -> {
                List<String> subjectIds = (List<String>) document.get("subjects");
                targets.addAll(subjectIds);
            });

            return targets;
        }));
    }

    public String getSubjectName0(String subjectId) {
        MongoCollection<Document> c = scoreDatabase.getCollection("subjects");
        Document query = doc("subjectId", subjectId);
        Document first = c.find(query).first();
        return null == first ? subjectId : first.getString("subjectName");
    }

    public static String getSubjectName(String subjectId) {
        return SUBJECT_NAMES.get(subjectId);
    }

    //////////////////////////////////////////////////////////////

    /**
     * 保存项目的科目列表
     *
     * @param projectId 项目ID
     * @param subjects  科目列表
     */
    public void saveProjectSubjects(String projectId, List<String> subjects) {
        MongoCollection<Document> c = scoreDatabase.getCollection("subject_list");
        Document query = doc("project", projectId);
        UpdateResult result = c.updateMany(query, $set(doc("subjects", subjects)));
        if (result.getMatchedCount() == 0) {
            c.insertOne(
                    query.append("subjects", subjects)
                            .append("md5", MD5.digest(UUID.randomUUID().toString()))
            );
        }
    }

    public void insertSubjects(String projectId, Document subjects) {
        MongoCollection<Document> c = scoreDatabase.getCollection("subjects");
        c.insertOne(doc("project", projectId).append("subjects", subjects).append("md5", MD5.digest(UUID.randomUUID().toString())));
    }

    public void clearSubjects(String projectId) {
        MongoCollection<Document> c = scoreDatabase.getCollection("subjects");
        c.deleteMany(doc("project", projectId));
    }

    /**
     * 查询学生缺考科目
     * @param projectId 项目ID
     * @param studentId 科目ID
     */
    public List<Map<String, String>> queryAbsentSubject(String projectId, String studentId){
        //查询科目表数据
        List<String> subjectIds = querySubjects(projectId);

        //查询学生参考科目
        Document student = studentService.findStudent(projectId, studentId);

        if(null != student){
            List subjects = student.get("subjects", List.class);

            subjectIds.removeIf(s -> null != subjects && subjects.contains(s));
            return subjectIds.stream().map(this::packSubject).collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    public Map<String, String> packSubject(String subjectId) {
        Map<String, String> m = new HashMap<>();
        m.put("subjectId", subjectId);
        m.put("subjectName", getSubjectName(subjectId));
        return m;
    }
}
