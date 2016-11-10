package com.xz.examscore.services;

import com.hyd.simplecache.utils.MD5;
import com.xz.ajiaedu.common.lang.Result;
import com.xz.ajiaedu.common.lang.StringUtil;
import com.xz.examscore.util.ReportNameMappings;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;


/**
 * @author by fengye on 2016/6/14.
 */
@Service
public class DownloadAnalysisService {

    @Value("${zip.download.url}")
    private String downloadURL;

    @Value("${report.generator.savepath}")
    private String savePath;

    @Value("${report.zip.location}")
    private String downloadPath;

    @Autowired
    SchoolService schoolService;

    @Autowired
    ClassService classService;

    public static final Logger LOG = LoggerFactory.getLogger(DownloadAnalysisService.class);

    public Result generateZipFiles(String projectId, String schoolId, String[] filePath) {
        //根据文件参数获取文件路径
        String[] paths = ReportNameMappings.getFileName(filePath);
        List<Map<String, String>> pathList = new ArrayList<>();
        //压缩文件名称（学校名称-考试分析报表）
        String zipFileName = schoolService.findSchool(projectId, schoolId).getString("name") + "-考试分析报表.zip";
        for (String path : paths) {
            String[] param = path.split("-->");
            List<Map<String, String>> category = getFileCategory(projectId, schoolId, param);
            pathList.addAll(category);
        }
        //追加考试id和学校id
        Map<String, Object> resultMap = createZipFiles(projectId, schoolId, pathList, zipFileName);
        return Result.success().set("downloadInfo", resultMap);
    }

    //将文件列表中的文件添置至压缩包
    public Map<String, Object> createZipFiles(String projectId, String schoolId, List<Map<String, String>> pathList, String zipFileName) {
        String prefix = getZipFilePrefix(projectId, schoolId);
        //压缩文件的生成目录
        File dir = new File(downloadPath + prefix);
        if(!dir.exists()){
            dir.mkdirs();
        }
        //下载zip包的生成路径
        String directory = downloadPath + prefix + File.separator +zipFileName;
        File file = new File(directory);
//        File file1 = new File(dir, zipFileName);
        Map<String, Object> resultMap = new HashMap<>();
        List<String> failureList = new ArrayList<>();
        try {
            ZipOutputStream out = new ZipOutputStream(new FileOutputStream(file));
            FileInputStream fis;
            for (Map<String, String> filePath : pathList) {
                //源文件不存在，则将压缩文件名添加至失败列表
                if (!new File(filePath.get("srcFile")).exists()) {
                    failureList.add(filePath.get("zipFile"));
                    continue;
                }
                fis = new FileInputStream(filePath.get("srcFile"));
                ZipEntry entity = new ZipEntry(filePath.get("zipFile"));
                out.putNextEntry(entity);
                int temp;
                while ((temp = fis.read()) != -1) {
                    out.write(temp);
                }
                fis.close();
            }
            out.closeEntry();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        //判断压缩文件中是否有文件条目
        int size = getZipSize(directory);
        if (size != 0) {
            //zip文件下载url
            resultMap.put("downloadURL", downloadURL + prefix + File.separator +zipFileName);
        } else {
            resultMap.put("downloadURL", "");
        }
        //不存在的文件列表
        resultMap.put("failureList", failureList);
        return resultMap;
    }

    private int getZipSize(String fileName) {
        try {
            ZipFile zipFile = new ZipFile(fileName);
            return zipFile.size();
        } catch (IOException e) {
            e.printStackTrace();
            return 0;
        }
    }

    private Map<String, String> getOneFileCategory(String srcFile, String zipFile, String srcFileName) {
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
            // srcFile：savePath/E0/20/430100-2df3f3ad199042c39c5f4b69f5dc7840/总体成绩分析/基础分析/分数分析.xlsx
            // filePath： 总体成绩分析/基础分析/分数分析.xlsx
            srcFile = getSaveFilePath(projectId, savePath, StringUtil.joinPaths(param));
            // /总体成绩分析/基础分析/分数分析.xlsx
            zipFile = StringUtil.joinPaths(param);
            fileCategory.add(getOneFileCategory(srcFile, zipFile, filename));
        } else if (part0.startsWith("学校")) {
            Document school = schoolService.findSchool(projectId, schoolId);
            // srcFile：savePath/E0/20/430100-2df3f3ad199042c39c5f4b69f5dc7840/
            // filePath： 学校成绩分析/schoolId/基础分析/分数分析.xlsx
            filePath = StringUtil.joinPaths(
                    param[0], school.getString("school"), param[1], filename
            );
            srcFile = getSaveFilePath(projectId, savePath, filePath);
            //压缩文件中将id替换成name
            zipFile = StringUtil.joinPaths(
                    param[0], school.getString("name"), param[1], filename
            );
            fileCategory.add(getOneFileCategory(srcFile, zipFile, filename));
        } else if (part0.startsWith("班级")) {
            List<Document> classes = classService.listClasses(projectId, schoolId);
            for (Document d : classes) {
                // srcFile：savePath/E0/20/430100-2df3f3ad199042c39c5f4b69f5dc7840/班级成绩分析/clasId/基础分析/分数分析.xlsx
                // filePath： 班级成绩分析/clasId/基础分析/分数分析.xlsx
                filePath = StringUtil.joinPaths(
                        param[0], d.getString("class"), param[1], filename
                );
                srcFile = getSaveFilePath(projectId, savePath, filePath);
                zipFile = StringUtil.joinPaths(
                        param[0], d.getString("name"), param[1], filename

                );
                fileCategory.add(getOneFileCategory(srcFile, zipFile, filename));
            }
        } else if(part0.startsWith("个性化")){
            filePath = StringUtil.joinPaths(
                    param[0], param[1], filename
            );
            srcFile = getSaveFilePath(projectId, savePath, filePath);
            zipFile = StringUtil.joinPaths(
                    param[0], param[1], filename
            );
            fileCategory.add(getOneFileCategory(srcFile, zipFile, filename));
        }
        return fileCategory;
    }

    private String getSaveFilePath(String projectId, String savePath, String filePath) {
        String md5 = MD5.digest(projectId);
        // /E0/20/430100-2df3f3ad199042c39c5f4b69f5dc7840/总体成绩分析/基础分析/分数分析
        return StringUtil.joinPaths(savePath,
                md5.substring(0, 2), md5.substring(2, 4), projectId, filePath);
    }

    public String getZipFilePrefix(String projectId, String schoolId) {
/*        UUID uuid = UUID.randomUUID();
        String md5 = MD5.digest(uuid.toString());
        return StringUtil.joinPaths(md5.substring(0, 2), md5.substring(2, 4));*/
        //在zip包生成的目录下面追加一个文件路径，格式为projectId/schoolId/yyyy/mm/dd/
        Calendar cal = Calendar.getInstance();
        String year = String.valueOf(cal.get(Calendar.YEAR));
        String month = String.valueOf(cal.get((Calendar.MONTH)) + 1);
        String date = String.valueOf(cal.get(Calendar.DAY_OF_MONTH));
        return StringUtil.joinPaths(projectId, schoolId,
                year, month, date);
    }

}
