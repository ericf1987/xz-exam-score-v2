package com.xz.services;

import com.xz.AppException;
import com.xz.ajiaedu.common.aliyun.OSSFileClient;
import com.xz.ajiaedu.common.io.FileUtils;
import com.xz.ajiaedu.common.lang.StringUtil;
import com.xz.ajiaedu.common.lang.Value;
import com.xz.bean.Range;
import com.xz.intclient.InterfaceClient;
import com.xz.score.bean.Score;
import com.xz.score.creator.ScoreDataPackCreator;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * 导出成绩到阿里云
 * created at 16/07/04
 *
 * @author yiding_he
 */
@Service
public class ExportScoreService {

    static final Logger LOG = LoggerFactory.getLogger(ExportScoreService.class);

    @Autowired
    ScoreService scoreService;

    @Autowired
    StudentService studentService;

    @Autowired
    ProvinceService provinceService;

    @Autowired
    InterfaceClient interfaceClient;

    @Autowired
    OSSFileClient scorePackOssFileClient;

    /**
     * 导出成绩到阿里云
     *
     * @param projectId       项目ID
     * @param notifyInterface 是否要通知接口导入成绩
     *
     * @return 上传后的 oss 文件路径
     */
    public String exportScore(String projectId, boolean notifyInterface) {

        // 本地文件路径
        String filePath = "score-archives/" + UUID.randomUUID().toString() + ".zip";

        try {
            LOG.info("对项目 " + projectId + " 开始进行打包...");
            createPack(projectId, filePath);        // 创建成绩包文件

            LOG.info("对项目 " + projectId + " 打包完毕，大小 " + new File(filePath).length() + "，开始上传...");
            String ossPath = uploadPack(projectId, filePath);        // 上传成绩包文件

            if (notifyInterface) {
                LOG.info("对项目 " + projectId + " 打包上传完毕，接口正在导入...");
                notifyInterface(ossPath);              // 通知业务接口导入成绩
            }

            LOG.info("项目导出完毕。");
            return ossPath;
        } catch (IOException e) {
            throw new AppException(e);
        }
    }

    private String uploadPack(String projectId, String filePath) {
        String ossPath = "webmarking-score-pack/" + projectId + ".zip";
        scorePackOssFileClient.uploadFile(new File(filePath), ossPath);
        return ossPath;
    }

    private void notifyInterface(String ossPath) {
        interfaceClient.importExamScoreFromOSS(ossPath);
    }

    public void createPack(String projectId, String filePath) throws IOException {
        ScoreDataPackCreator packCreator = new ScoreDataPackCreator();
        packCreator.setProjectId(projectId);
        packCreator.setDeviceId("cms");
        packCreator.setChannel("cms");

        String province = provinceService.getProjectProvince(projectId);
        Value<Integer> counter = Value.of(0);
        studentService.getProjectStudentList(projectId, Range.province(province), -1, null)
                .forEach((Consumer<Document>) studentDoc -> addStudentScores(projectId, packCreator, studentDoc, counter));

        FileUtils.writeFile(packCreator.createZipArchive(), new File(filePath));
    }

    private void addStudentScores(
            String projectId, ScoreDataPackCreator packCreator, Document studentDoc, Value<Integer> counter) {

        counter.set(counter.get() + 1);
        if (counter.get() % 100 == 0) {
            LOG.info("读取项目 " + projectId + " 的第 " + counter.get() + " 条学生成绩...");
        }

        packCreator.addStudent(studentDoc.getString("school"), studentDoc.getString("student"), "");

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
        String answer = scoreDoc.getString("answer");
        return StringUtil.isBlank(answer) ? Collections.emptyList() : Arrays.asList(answer.split(","));
    }
}
