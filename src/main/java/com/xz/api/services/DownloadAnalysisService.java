package com.xz.api.services;

import com.hyd.simplecache.utils.MD5;
import com.xz.ajiaedu.common.lang.Result;
import com.xz.ajiaedu.common.lang.StringUtil;
import com.xz.services.*;
import com.xz.util.ParamUtils;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;


/**
 * @author by fengye on 2016/6/14.
 */
@Service
public class DownloadAnalysisService {

    @Autowired
    @Value("${report.generator.savepath}")
    private String savePath;

    @Autowired
    @Value("${zip.save.location}")
    private String downloadPath;

    @Autowired
    SchoolService schoolService;

    @Autowired
    ClassService classService;

    public Result generateZipFiles(String projectId, String schoolId, String[] filePath) {
        String[] paths = ParamUtils.getFileName(filePath);
        List<Map<String, String>> pathList = new ArrayList<>();
        String zipFileName = schoolService.findSchool(projectId, schoolId).getString("name") + "-考试分析报表.zip";
        for (String path : paths) {
            String[] param = path.split("-");
            List<Map<String, String>> category = getFileCategory(projectId, schoolId, param);
            pathList.addAll(category);
        }
        String pathUrl = createZipFiles(pathList, zipFileName);
        return Result.success().set("downloadUrl", pathUrl);
    }

    public String createZipFiles(List<Map<String, String>> pathList, String zipFileName) {
        File file = new File(downloadPath + zipFileName);
        try {
            ZipOutputStream out = new ZipOutputStream(new FileOutputStream(file));
            FileInputStream fis;
            for (Map<String, String> filePath : pathList) {
                if(!new File(filePath.get("srcFile")).exists()){
                    continue;
                }
                fis = new FileInputStream(filePath.get("srcFile"));
                ZipEntry entity = new ZipEntry(filePath.get("zipFile"));
                out.putNextEntry(entity);
                int temp;
                while((temp = fis.read()) != -1){
                    out.write(temp);
                }
                fis.close();
            }
            out.closeEntry();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return file.getPath();
    }

    private Map<String, String> getOneFileCategory(String srcFile, String zipFile, String srcFileName){
        Map<String, String> para = new HashMap<>();
        para.put("srcFile", srcFile);
        para.put("zipFile", zipFile);
        para.put("srcFileName", srcFileName);
        return para;
    }

    //解析文件参数（学校成绩分析-基础分析-分数分析）
    private List<Map<String, String>> getFileCategory(String projectId, String schoolId, String[] param) {
        //源文件路径和压缩文件路径
        String srcFile, zipFile;
        String part0 = param[0];
        String filename = param[2];
        String filePath;
        List<Map<String, String>> fileCategory = new ArrayList<>();
        if (part0.startsWith("总体")) {
            srcFile = getSaveFilePath(projectId, savePath, StringUtil.joinPaths(param));
            zipFile = StringUtil.joinPaths(param);
            fileCategory.add(getOneFileCategory(srcFile,zipFile,filename));
        } else if (part0.startsWith("学校")) {
            Document school = schoolService.findSchool(projectId, schoolId);
            filePath = StringUtil.joinPaths(
                    param[0], param[1], "school", school.getString("school"), filename
            );
            srcFile = getSaveFilePath(projectId, savePath, filePath);
            zipFile = StringUtil.joinPaths(
                    param[0], param[1], "school", school.getString("name"), filename
            );
            fileCategory.add(getOneFileCategory(srcFile,zipFile,filename));
        } else if (part0.startsWith("班级")) {
            List<Document> classes = classService.listClasses(projectId, schoolId);
            for (Document d : classes) {
                filePath = StringUtil.joinPaths(
                        param[0], param[1], "class", d.getString("class"), filename
                );
                srcFile = getSaveFilePath(projectId, savePath, filePath);
                zipFile = StringUtil.joinPaths(
                        param[0], param[1], "class", d.getString("name"), filename
                );
                fileCategory.add(getOneFileCategory(srcFile,zipFile,filename));
            }
        }
        return fileCategory;
    }

    private String getSaveFilePath(String projectId, String savePath, String filePath) {
        String md5 = MD5.digest(projectId);

        return StringUtil.joinPaths(savePath,
                md5.substring(0, 2), md5.substring(2, 4), filePath);
    }


}
