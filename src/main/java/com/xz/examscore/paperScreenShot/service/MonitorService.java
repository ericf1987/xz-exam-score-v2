package com.xz.examscore.paperScreenShot.service;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.UpdateResult;
import com.xz.ajiaedu.common.mongo.MongoUtils;
import com.xz.examscore.paperScreenShot.bean.TaskProcess;
import com.xz.examscore.services.ClassService;
import com.xz.examscore.services.SchoolService;
import com.xz.examscore.services.StudentService;
import com.xz.examscore.util.DoubleUtils;
import com.xz.examscore.util.Mongo;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static com.xz.ajiaedu.common.mongo.MongoUtils.*;

/**
 * @author by fengye on 2017/3/26.
 */
@Service
public class MonitorService {

    @Autowired
    private MongoDatabase scoreDatabase;

    @Autowired
    private ClassService classService;

    @Autowired
    private SchoolService schoolService;
    
    @Autowired
    private StudentService studentService;

    public void increaseFinished(String projectId, TaskProcess taskProcess) {
        MongoCollection<Document> paperScreenShotCounter = scoreDatabase.getCollection("paperScreenShot_counter");
        Document query = MongoUtils.doc("project", projectId).append("taskProcess", taskProcess.name());
        paperScreenShotCounter.updateMany(query, $inc("finishedCount", 1));
    }

    public int getTotal(String projectId) {
        AtomicInteger counter = new AtomicInteger(0);
        List<String> schoolIds = schoolService.getProjectSchools(projectId).stream().map(s -> s.getString("school")).collect(Collectors.toList());
        schoolIds.forEach(schoolId -> {
            List<Document> classes = classService.listClasses(projectId, schoolId);
            counter.addAndGet(classes.size());
        });

        return counter.get();
    }

    public int getFinishedCount(String projectId, TaskProcess taskProcess) {
        MongoCollection<Document> paperScreenShot_counter = scoreDatabase.getCollection("paperScreenShot_counter");
        Document query = MongoUtils.doc("project", projectId).append("taskProcess", taskProcess.name());
        Document finishedCount = paperScreenShot_counter.find(query).projection(doc("finishedCount", 1)).first();
        return null == finishedCount ? 0 : finishedCount.getLong("finishedCount").intValue();
    }

    public double getFinishRate(String projectId, TaskProcess taskProcess) {
        return DoubleUtils.round((double) (getFinishedCount(projectId, taskProcess)) / getTotal(projectId));
    }

    public void reset(String projectId, TaskProcess taskProcess) {
        int totalCount = getTotal(projectId);
        Document query = MongoUtils.doc("project", projectId).append("taskProcess", taskProcess.name())
                .append("totalCount", totalCount);
        Document update = MongoUtils.doc("finishedCount", 0L);
        MongoCollection<Document> paperScreenShot_counter = scoreDatabase.getCollection("paperScreenShot_counter");
        UpdateResult result = paperScreenShot_counter.updateMany(query, $set(update));
        if (result.getMatchedCount() == 0) {
            paperScreenShot_counter.insertOne(query.append("finishedCount", 0L).append("md5", Mongo.md5()));
        }
    }

    /**
     * 记录生成截图失败的学生信息
     * @param projectId
     * @param studentId
     * @param subjectId
     */
    public void recordFailedStudent(String projectId, String schoolId, String classId, String studentId, String subjectId) {
        MongoCollection<Document> collection = scoreDatabase.getCollection("paperScreenShot_fail_student");
        Document query = doc("project", projectId).append("school", schoolId).append("classId", classId)
                .append("subject", subjectId);
        UpdateResult result = collection.updateMany(query, $push("studentId", studentId));
        if(result.getMatchedCount() == 0){
            collection.insertOne(query.append("students", Collections.emptyList()).append("md5", Mongo.md5()));
        }
    }

    public List<String> getFailedStudents(String projectId, String subjectId){
        MongoCollection<Document> collection = scoreDatabase.getCollection("paperScreenShot_fail_student");
        Document query = doc("project", projectId).append("subject", subjectId);
        Document students = collection.find(query).projection(doc("students", 1)).first();

        if(null != students){
            List<String> studentIds = students.get("students", List.class);
            List<String> names = studentIds.stream().map(s -> studentService.findStudent(projectId, s).getString("name")).collect(Collectors.toList());
            return names;
        }

        return Collections.emptyList();
    }
}
