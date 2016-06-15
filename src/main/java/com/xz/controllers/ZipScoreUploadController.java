package com.xz.controllers;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.xz.ajiaedu.common.io.FileUtils;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.UUID;
import java.util.zip.ZipEntry;

import static com.xz.ajiaedu.common.mongo.MongoUtils.doc;

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

    @RequestMapping(value = "/import-exam-score", method = RequestMethod.POST)
    @ResponseBody
    public Result uploadExamScore(
            @RequestParam("project") String project,
            @RequestParam MultipartFile file) throws Exception {

        String filename = UUID.randomUUID().toString() + ".zip";
        File saveFile = FileUtils.getOrCreateFile(zipSaveLocation, filename);
        file.transferTo(saveFile);

        LOG.info("保存 zip 到 " + saveFile.getAbsolutePath());

        ZipFileReader zipFileReader = new ZipFileReader(saveFile);
        zipFileReader.readZipEntries("score.json", entry -> readScore(project, zipFileReader, entry));

        return Result.success();
    }

    private void readScore(String project, ZipFileReader zipFileReader, ZipEntry entry) {
        MongoCollection<Document> collection = scoreDatabase.getCollection("score");
        zipFileReader.readEntryByLine(entry, "UTF-8", line -> readScoreLine(project, line, collection));
    }

    private void readScoreLine(String project, String line, MongoCollection<Document> collection) {
        Document scoreDoc = Document.parse(line);

        // 查询题目
        String subject = scoreDoc.getString("subject");
        String questNo = scoreDoc.getString("questNo");
        Document quest = questService.findQuest(project, subject, questNo);
        if (quest == null) {
            throw new IllegalStateException("找不到题目: " + project + ", " + subject + ", " + questNo);
        }

        // 查询学校
        String schoolId = scoreDoc.getString("school");
        Document school = schoolService.findSchool(project, schoolId);
        if (school == null) {
            throw new IllegalStateException("找不到学校: " + project + ", " + schoolId);
        }

        // 补完成绩记录
        scoreDoc.put("quest", quest.get("questId"));
        scoreDoc.put("isObjective", quest.get("isObjective"));
        scoreDoc.put("area", school.get("area"));
        scoreDoc.put("city", school.get("city"));
        scoreDoc.put("province", school.get("province"));

        // 保存成绩记录
        Document query = doc("project", scoreDoc.get("project"))
                .append("student", scoreDoc.get("student"))
                .append("subject", scoreDoc.get("subject"))
                .append("questNo", scoreDoc.get("questNo"));

        collection.deleteMany(query);
        collection.insertOne(scoreDoc);
    }
}
