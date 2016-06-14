package com.xz.controllers;

import com.xz.ajiaedu.common.io.FileUtils;
import com.xz.ajiaedu.common.lang.Result;
import com.xz.ajiaedu.common.lang.Value;
import com.xz.bean.Range;
import com.xz.score.bean.Score;
import com.xz.score.creator.ScoreDataPackCreator;
import com.xz.services.OSSService;
import com.xz.services.ProvinceService;
import com.xz.services.ScoreService;
import com.xz.services.StudentService;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * (description)
 * created at 16/06/14
 *
 * @author yiding_he
 */
@Controller
public class ScoreExportController {

    static final Logger LOG = LoggerFactory.getLogger(ScoreExportController.class);

    @Autowired
    ScoreService scoreService;

    @Autowired
    StudentService studentService;

    @Autowired
    ProvinceService provinceService;

    @Autowired
    OSSService ossService;

    @RequestMapping(value = "export-score-to-oss", method = RequestMethod.POST)
    @ResponseBody
    public Result exportScore(@RequestParam("project") String projectId) {
        try {
            String filePath = "score-archives/" + UUID.randomUUID().toString() + ".zip";
            LOG.info("对项目 " + projectId + " 开始进行打包...");
            createPack(projectId, filePath);        // 创建成绩包文件
            LOG.info("对项目 " + projectId + " 打包完毕，大小 " + new File(filePath).length() + "，开始上传...");
            uploadPack(projectId, filePath);        // 上传成绩包文件
            LOG.info("对项目 " + projectId + " 打包上传完毕。");
            notifyInterface(projectId, filePath);   // 通知业务接口导入成绩
            return Result.success();
        } catch (IOException e) {
            return Result.fail(e.getMessage());
        }
    }

    private void uploadPack(String projectId, String filePath) {
        ossService.uploadFile(filePath, "webmarking-score-pack/" + projectId + ".zip");
    }

    private void notifyInterface(String projectId, String filePath) {

    }

    public void createPack(String projectId, String filePath) throws IOException {
        ScoreDataPackCreator packCreator = new ScoreDataPackCreator();
        packCreator.setProjectId(projectId);
        packCreator.setDeviceId("cms");
        packCreator.setChannel("cms");

        Value<Integer> counter = Value.of(0);
        String province = provinceService.getProjectProvince(projectId);
        studentService.getProjectStudentList(projectId, Range.province(province), -1)
                .forEach((Consumer<Document>) studentDoc -> addStudentScores(projectId, packCreator, studentDoc, counter));

        FileUtils.writeFile(packCreator.createZipArchive(), new File(filePath));
    }

    private void addStudentScores(
            String projectId, ScoreDataPackCreator packCreator, Document studentDoc, Value<Integer> counter) {

        counter.set(counter.get() + 1);
        if (counter.get() % 100 == 0) {
            LOG.info("读取项目 " + projectId + " 的第 " + counter.get() + " 条学生成绩...");
        }

        String studentId = studentDoc.getString("student");
        scoreService.getStudentQuestScores(projectId, studentId)
                .forEach((Consumer<Document>) scoreDoc -> addStudentScore(packCreator, scoreDoc));
    }

    private void addStudentScore(ScoreDataPackCreator packCreator, Document scoreDoc) {
        Score score = new Score();
        score.setAnswer(getAnswer(scoreDoc));
        score.setSubjectId(scoreDoc.getString("subject"));
        score.setExamId("");
        score.setIgnore(false);
        score.setPaper("A");
        score.setQuestNo(scoreDoc.getString("questNo"));
        score.setRight(scoreDoc.getBoolean("right"));
        score.setSchoolId(scoreDoc.getString("school"));
        score.setScore(scoreDoc.getDouble("score"));
        score.setStudentId(scoreDoc.getString("student"));
        packCreator.addScore(score);
    }

    private List<String> getAnswer(Document scoreDoc) {
        return Arrays.asList(scoreDoc.getString("answer").split(","));
    }
}
