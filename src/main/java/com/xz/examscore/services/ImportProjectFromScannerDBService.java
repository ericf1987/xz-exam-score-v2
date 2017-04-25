package com.xz.examscore.services;

import com.hyd.simplecache.utils.MD5;
import com.mongodb.MongoClient;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.xz.ajiaedu.common.beans.exam.ExamProject;
import com.xz.ajiaedu.common.lang.Context;
import com.xz.ajiaedu.common.mongo.MongoUtils;
import com.xz.examscore.scanner.ScannerDBService;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Consumer;

/**
 * 从网阅数据库导入学生基础信息
 *
 * @author by fengye on 2017/4/14.
 */
@Service
public class ImportProjectFromScannerDBService {
    static final Logger LOG = LoggerFactory.getLogger(ImportProjectFromScannerDBService.class);

    @Autowired
    MongoClient scannerMongoClient;

    @Autowired
    MongoClient scannerMongoClient_g10;

    @Autowired
    MongoDatabase scoreDatabase;

    @Autowired
    ScannerDBService scannerDBService;

    @Autowired
    StudentService studentService;

    @Autowired
    ClassService classService;

    @Autowired
    SchoolService schoolService;

    @Autowired
    ProjectService projectService;

    @Autowired
    ProvinceService provinceService;

    @Autowired
    CityService cityService;

    @Autowired
    AreaService areaService;

    public MongoCollection<Document> getCollection(String projectId, String collectionName) {
        MongoClient mongoClient = scannerDBService.getMongoClient(projectId);
        MongoDatabase mongoDatabase = mongoClient.getDatabase(projectId);
        return mongoDatabase.getCollection(collectionName);
    }

    public void importSchools(String projectId, Context context) {
        LOG.info("从网阅数据库导入项目" + projectId + "学校信息");

        //获取网阅数据源，数据库
        MongoCollection<Document> collection = getCollection(projectId, "schools");

        List<Document> schoolList = new ArrayList<>();
        Set<String> areas = new HashSet<>();
        Set<String> cities = new HashSet<>();
        Set<String> provinces = new HashSet<>();

        List<Document> documents = MongoUtils.toList(collection.find());
        documents.forEach(s -> {
            Document schoolDoc = new Document();
            schoolDoc.put("project", projectId);
            schoolDoc.put("school", s.getString("schoolId"));
            schoolDoc.put("name", s.getString("schoolName"));
            schoolDoc.put("area", s.getString("areaId"));
            schoolDoc.put("city", s.getString("cityId"));
            schoolDoc.put("province", s.getString("provinceId"));
            schoolDoc.put("md5", MD5.digest(UUID.randomUUID().toString()));

            areas.add(s.getString("areaId"));
            cities.add(s.getString("cityId"));
            provinces.add(s.getString("provinceId"));

            schoolList.add(schoolDoc);
        });

        context.put("schools", schoolList);

        schoolService.saveProjectSchool(projectId, schoolList);
        projectService.updateProjectSchools(projectId, schoolList);
        provinceService.saveProjectProvince(projectId, provinces.isEmpty() ? null : provinces.iterator().next());
        cityService.saveProjectCities(projectId, cities);
        areaService.saveProjectAreas(projectId, areas);
    }

    public void importClasses(String projectId, Context context) {
        List<Document> classes = new ArrayList<>();
        List<Document> schools = context.get("schools");
        ExamProject project = context.get("project");

        Document group = MongoUtils.doc("$group",
                new Document().append("_id",
                        MongoUtils.doc("classId", "$classId").append("className", "$className")
                )
                        .append("totalCount", MongoUtils.doc("$sum", 1)));

        for (Document school : schools) {
            String schoolId = school.getString("school");
            List<Document> schoolClasses = new ArrayList<>();

            LOG.info("从网阅数据库导入学校" + schoolId + "(" + school.getString("name") + ") 班级信息...");

            Document match = MongoUtils.doc("$match", new Document().append("schoolId", schoolId));
            MongoCollection<Document> studentForProject = getCollection(projectId, "studentForProject");
            AggregateIterable<Document> aggregate = studentForProject.aggregate(Arrays.asList(
                    match, group
            ));
            aggregate.forEach((Consumer<Document>) c -> {
                Document schoolClass = new Document()
                        .append("project", projectId)
                        .append("class", c.getString("classId"))
                        .append("name", c.getString("className"))
                        .append("grade", project.getGrade())
                        .append("school", schoolId)
                        .append("area", school.getString("area"))
                        .append("city", school.getString("city"))
                        .append("province", school.getString("province"))
                        .append("md5", MD5.digest(UUID.randomUUID().toString()));
                schoolClasses.add(schoolClass);
                classes.add(schoolClass);
            });
            classService.saveProjectSchoolClasses(projectId, schoolId, schoolClasses);
        }
        context.put("classes", classes);
    }


    public void importStudents(String projectId, Context context){
        List<Document> classes = context.get("classes");
        int classCount = classes.size();
        int index = 0;

        for(Document classDoc : classes){
            String classId = classDoc.getString("class");
            index++;
            LOG.info("从网阅数据库导入班级 " + classId + " 的考生信息(" + index + "/" + classCount + ")...");

            List<Document> classStudents = new ArrayList<>();

            MongoCollection<Document> studentForProject = getCollection(projectId, "studentForProject");
            FindIterable<Document> students = studentForProject.find(MongoUtils.doc("classId", classId));
            students.forEach((Consumer<Document>)s -> {
                Document studentDoc = new Document()
                        .append("project", projectId)
                        .append("examNo", s.getString("examNo"))
                        .append("customExamNo", s.getString("customExamNo"))
                        .append("student", s.getString("studentId"))
                        .append("name", s.getString("studentName"))
                        .append("class", s.getString("classId"))
                        .append("school", s.getString("schoolId"))
                        .append("area", classDoc.getString("area"))
                        .append("city", classDoc.getString("city"))
                        .append("province", classDoc.getString("province"))
                        .append("md5", MD5.digest(UUID.randomUUID().toString()));

                classStudents.add(studentDoc);
            });
            studentService.saveProjectClassStudents(projectId, classId, classStudents);
        }
    }
}
