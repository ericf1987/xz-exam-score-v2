package com.xz.examscore.services;

import com.hyd.simplecache.utils.MD5;
import com.xz.ajiaedu.common.mongo.MongoUtils;
import com.xz.examscore.XzExamScoreV2ApplicationTests;
import org.bson.Document;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

/**
 * @author by fengye on 2016/10/18.
 */
public class SubjectCombinationServiceTest extends XzExamScoreV2ApplicationTests {
    @Autowired
    SubjectCombinationService subjectCombinationService;

    @Autowired
    SubjectService subjectService;

    @Test
    public void testGetAllSubjectCombinations() throws Exception {
        String projectId = "430200-01ef739fb0074d489f39e62a9be64629";
        System.out.println(subjectCombinationService.getAllSubjectCombinations(projectId));
    }

    @Test
    public void testGetSubjectCombinationName() throws Exception {
        String name = subjectCombinationService.getSubjectCombinationName("007008009");
        System.out.println(name);
    }

    @Test
    public void testSaveProjectSubjectCombinations() throws Exception {
        String projectId = "430300-672a0ed23d9148e5a2a31c8bf1e08e62";
        subjectCombinationService.saveProjectSubjectCombinations(projectId, Arrays.asList("007008009", "004005006"));
    }

    @Test
    public void testGetSubjectName() throws Exception {
        System.out.println(subjectService.getSubjectName0("001002"));
    }

    @Test
    public void testInsertSubjects2() throws Exception{
        List<Document> docs = Arrays.asList(
                MongoUtils.doc("subjectId", "000").append("subjectName", "全科").append("md5", MD5.digest(UUID.randomUUID().toString())),
                MongoUtils.doc("subjectId", "001").append("subjectName", "语文").append("md5", MD5.digest(UUID.randomUUID().toString())),
                MongoUtils.doc("subjectId", "002").append("subjectName", "数学").append("md5", MD5.digest(UUID.randomUUID().toString())),
                MongoUtils.doc("subjectId", "003").append("subjectName", "英语").append("md5", MD5.digest(UUID.randomUUID().toString())),
                MongoUtils.doc("subjectId", "004").append("subjectName", "物理").append("md5", MD5.digest(UUID.randomUUID().toString())),
                MongoUtils.doc("subjectId", "005").append("subjectName", "化学").append("md5", MD5.digest(UUID.randomUUID().toString())),
                MongoUtils.doc("subjectId", "006").append("subjectName", "生物").append("md5", MD5.digest(UUID.randomUUID().toString())),
                MongoUtils.doc("subjectId", "007").append("subjectName", "政治").append("md5", MD5.digest(UUID.randomUUID().toString())),
                MongoUtils.doc("subjectId", "008").append("subjectName", "历史").append("md5", MD5.digest(UUID.randomUUID().toString())),
                MongoUtils.doc("subjectId", "009").append("subjectName", "地理").append("md5", MD5.digest(UUID.randomUUID().toString())),
                MongoUtils.doc("subjectId", "010").append("subjectName", "社会").append("md5", MD5.digest(UUID.randomUUID().toString())),
                MongoUtils.doc("subjectId", "011").append("subjectName", "科学").append("md5", MD5.digest(UUID.randomUUID().toString())),
                MongoUtils.doc("subjectId", "012").append("subjectName", "技术与设计").append("md5", MD5.digest(UUID.randomUUID().toString())),
                MongoUtils.doc("subjectId", "013").append("subjectName", "思想品德").append("md5", MD5.digest(UUID.randomUUID().toString())),
                MongoUtils.doc("subjectId", "014").append("subjectName", "信息技术").append("md5", MD5.digest(UUID.randomUUID().toString())),
                MongoUtils.doc("subjectId", "015").append("subjectName", "体育").append("md5", MD5.digest(UUID.randomUUID().toString())),
                MongoUtils.doc("subjectId", "016").append("subjectName", "品生与品社").append("md5", MD5.digest(UUID.randomUUID().toString())),
                MongoUtils.doc("subjectId", "017").append("subjectName", "小学综合").append("md5", MD5.digest(UUID.randomUUID().toString())),
                MongoUtils.doc("subjectId", "018").append("subjectName", "学法知法").append("md5", MD5.digest(UUID.randomUUID().toString())),
                MongoUtils.doc("subjectId", "019").append("subjectName", "道德与法治").append("md5", MD5.digest(UUID.randomUUID().toString())),
                MongoUtils.doc("subjectId", "004005006").append("subjectName", "理科综合").append("md5", MD5.digest(UUID.randomUUID().toString())),
                MongoUtils.doc("subjectId", "007008009").append("subjectName", "文科综合").append("md5", MD5.digest(UUID.randomUUID().toString())),
                MongoUtils.doc("subjectId", "006009").append("subjectName", "生地综合").append("md5", MD5.digest(UUID.randomUUID().toString())),
                MongoUtils.doc("subjectId", "007008").append("subjectName", "政史综合").append("md5", MD5.digest(UUID.randomUUID().toString())),
                MongoUtils.doc("subjectId", "004005").append("subjectName", "理化综合").append("md5", MD5.digest(UUID.randomUUID().toString())),
                MongoUtils.doc("subjectId", "001002").append("subjectName", "语数综合").append("md5", MD5.digest(UUID.randomUUID().toString())),
                MongoUtils.doc("subjectId", "003004005").append("subjectName", "英物化综合").append("md5", MD5.digest(UUID.randomUUID().toString()))
        );
        subjectService.insertSubjects(docs);
    }
}