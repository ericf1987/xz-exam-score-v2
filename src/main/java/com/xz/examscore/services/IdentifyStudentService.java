package com.xz.examscore.services;

import com.alibaba.fastjson.JSON;
import com.xz.ajiaedu.common.aliyun.OSSFileClient;
import com.xz.ajiaedu.common.io.FileUtils;
import com.xz.ajiaedu.common.mongo.MongoUtils;
import com.xz.examscore.AppException;
import com.xz.examscore.bean.Range;
import com.xz.examscore.intclient.InterfaceAuthClient;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.xz.ajiaedu.common.mongo.MongoUtils.doc;
import static com.xz.ajiaedu.common.mongo.MongoUtils.toList;

/**
 * @author by fengye on 2017/5/11.
 */
@Service
public class IdentifyStudentService {

    @Autowired
    StudentService studentService;

    @Autowired
    InterfaceAuthClient interfaceAuthClient;

    @Autowired
    ProvinceService provinceService;

    @Autowired
    OSSFileClient scorePackOssFileClient;

    static final Logger LOG = LoggerFactory.getLogger(ExportScoreService.class);


    public String exportStudents(String projectId, boolean notifyInterface) {

        //本地学生数据文件路径
        String filePath = "students-archives/" + UUID.randomUUID().toString() + ".json";

        try {
            LOG.info("对项目 " + projectId + " 的学生信息开始打包...");

            createPack(projectId, filePath);

            LOG.info("对项目 " + projectId + " 打包完成， 大小" + new File(filePath).length() + ", 开始上传...");

            String ossPath = uploadPack(projectId, filePath);

            if (notifyInterface) {
                LOG.info("对项目 " + projectId + " 打包上传完成，正在通知CMS接口接收学生数据...");
                interfaceAuthClient.importExamScoreFromOSS(ossPath);
            }

            LOG.info("导出学生数据到CMS完成！");

            return ossPath;
        } catch (Exception e) {
            throw new AppException(e);
        }
    }

    private String uploadPack(String projectId, String filePath) {
        String ossPath = "webmarking-score-pack/" + projectId + "_" + "students.json";
        scorePackOssFileClient.uploadFile(new File(filePath), ossPath);
        LOG.info("项目 " + projectId + " 学生信息的上传路径为-->" + ossPath);
        return ossPath;
    }

    public void createPack(String projectId, String filePath) {
        String province = provinceService.getProjectProvince(projectId);
        List<Document> students = toList(studentService.getProjectStudentList(projectId, Range.province(province),
                -1, 0, MongoUtils.doc("province", 1).append("school", 1).append("class", 1).append("student", 1).append("_id", 0),
                doc("province", 1).append("school", 1).append("class", 1).append("student", 1)));
        List<String> studentJson = packJsonData(students);
        try {
            FileUtils.writeFile(toBytes(studentJson), new File(filePath));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private List<String> packJsonData(List<Document> students) {
        return students.stream().map(this::toJsonString).collect(Collectors.toList());
    }

    private String toJsonString(Document doc) {
        return JSON.toJSONString(doc);
    }

    private byte[] toBytes(List<?> list) {
        StringBuilder builder = new StringBuilder();
        Iterator<?> itr = list.iterator();

        try {
            while (itr.hasNext()) {
                Object obj = itr.next();
                if (obj instanceof String) {
                    builder.append((String) obj).append("\n");
                } else {
                    builder.append(JSON.toJSONString(obj)).append("\n");
                }
            }

            return builder.toString().getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            return new byte[0];
        }
    }
}
