package com.xz.controllers;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.xz.ajiaedu.common.io.ZipFileReader;
import com.xz.ajiaedu.common.lang.Result;
import com.xz.services.QuestService;
import com.xz.services.SchoolService;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.UUID;
import java.util.zip.ZipEntry;

import static com.xz.ajiaedu.common.mongo.MongoUtils.*;

/**
 * 导入成绩数据
 *
 * @author yiding_he
 */
@Controller
public class ZipScoreUploadController {

    static final Logger LOG = LoggerFactory.getLogger(ZipScoreUploadController.class);

    @Value("${zip.save.location}")
    private String zipSaveLocation;     // zip 保存位置

    @Autowired
    SchoolService schoolService;

    @Autowired
    QuestService questService;

    @Autowired
    MongoDatabase scoreDatabase;

    @ResponseBody
    @RequestMapping(name = "upload-exam-score", method = RequestMethod.POST)
    public Result uploadExamScore(String project, MultipartFile file) throws Exception {

        String filename = UUID.randomUUID().toString() + ".zip";
        File saveFile = new File(zipSaveLocation, filename);
        file.transferTo(saveFile);

        LOG.info("保存 zip 到 " + saveFile.getAbsolutePath());

        ZipFileReader zipFileReader = new ZipFileReader(saveFile);
        zipFileReader.readZipEntries("score.json", entry -> readEntry(project, zipFileReader, entry));

        return Result.success();
    }

    private void readEntry(String project, ZipFileReader zipFileReader, ZipEntry entry) {
        MongoCollection<Document> collection = scoreDatabase.getCollection("score");
        zipFileReader.readEntryByLine(entry, "UTF-8", line -> readScoreLine(project, line, collection));
    }

    private void readScoreLine(String project, String line, MongoCollection<Document> collection) {
        JSONObject scoreObj = JSON.parseObject(line);

        // 查询题目
        String subject = scoreObj.getString("subject");
        String questNo = scoreObj.getString("questNo");
        Document quest = questService.findQuest(project, subject, questNo);
        if (quest == null) {
            throw new IllegalStateException("找不到题目: " + project + ", " + subject + ", " + questNo);
        }

        // 查询学校
        String schoolId = scoreObj.getString("school");
        Document school = schoolService.findSchool(project, schoolId);
        if (school == null) {
            throw new IllegalStateException("找不到学校: " + project + ", " + schoolId);
        }

        // 补完成绩记录
        Document scoreDoc = new Document(scoreObj);
        scoreDoc.put("quest", quest.get("questId"));
        scoreDoc.put("isObjective", quest.get("isObjective"));
        scoreDoc.put("area", school.get("area"));
        scoreDoc.put("city", school.get("city"));
        scoreDoc.put("province", school.get("province"));

        // 保存成绩记录
        Document query = doc("project", scoreDoc.remove("project"))
                .append("student", scoreDoc.remove("student"))
                .append("subject", scoreDoc.remove("subject"))
                .append("questNo", scoreDoc.remove("questNo"));

        collection.updateOne(query, $set(scoreDoc), UPSERT);
    }
}
