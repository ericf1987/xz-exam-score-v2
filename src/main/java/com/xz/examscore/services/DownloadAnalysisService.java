package com.xz.examscore.services;

import com.hyd.simplecache.utils.MD5;
import com.xz.ajiaedu.common.lang.Result;
import com.xz.ajiaedu.common.lang.StringUtil;
import com.xz.examscore.bean.ProjectStatus;
import com.xz.examscore.paperScreenShot.service.DownloadScreenShotService;
import com.xz.examscore.util.ReportNameMappings;
import org.apache.commons.collections.MapUtils;
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
import java.util.stream.Collectors;
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

    @Value("${examAlliance.report.generator.savepath}")
    private String savePath2;

    @Value("${report.zip.location}")
    private String downloadPath;

    @Autowired
    SchoolService schoolService;

    @Autowired
    ClassService classService;

    @Autowired
    ProjectService projectService;

    @Autowired
    DownloadScreenShotService downloadScreenShotService;

    public static final Logger LOG = LoggerFactory.getLogger(DownloadAnalysisService.class);

    /**
     * 生成报表压缩包
     *
     * @param projectId 项目ID
     * @param schoolId  学校ID
     * @param filePath  下载报表项
     * @param isBureau  是否是教育局账号
     * @return 返回结果
     */
    public Result generateZipFile(String projectId, String schoolId, String[] filePath, boolean isBureau) {

        //查询报表生成状态，如果未生成完毕，不允许下载
        ProjectStatus projectStatus = projectService.getProjectStatus(projectId);
        if (!projectStatus.equals(ProjectStatus.ReportGenerated)) {
            Map<String, Object> resultMap = new HashMap<>();
            resultMap.put("failureList", "");
            resultMap.put("downloadURL", "");
            resultMap.put("status", projectStatus.name());
            resultMap.put("downloadFlag", false);
            resultMap.put("desc", "报表尚未生成好，请耐心等待");
            return Result.success().set("downloadInfo", resultMap);
        }

        //如果是教育局账号
        if (isBureau) {
            return generateBureauZipFile0(projectId, schoolId, filePath);
        } else {
            return generateZipFiles0(projectId, schoolId, filePath, projectStatus.name());
        }
    }

    public Result generateBureauZipFile0(String projectId, String schoolId, String[] filePath) {
        String zipFileName = StringUtil.trim(projectService.findProject(projectId).getString("name")) + "-教育局分析报表.zip";
        List<String> schoolIds = schoolService.getProjectSchools(projectId).stream().map(s -> s.getString("school")).collect(Collectors.toList());

        List<String> schoolFilePath = Arrays.asList(filePath).stream().filter(s -> !s.contains("100")).collect(Collectors.toList());

        List<String> totalFilePath = Arrays.asList(filePath).stream().filter(s -> s.contains("100")).collect(Collectors.toList());

        //发起线程
        List<SchoolReportZipPacker> schoolReportZipPackers = startSchoolReportZipPackers(projectId, schoolIds, schoolFilePath.toArray(new String[schoolFilePath.size()]));
        //等待线程执行结束
        List<Result> results = joinSchoolReportZipPackers(schoolReportZipPackers);

        //所有学校压缩包的源文件路径
        List<String> schoolZipSrc = new ArrayList<>();

        for (Result result : results) {
            Map<String, Object> resultMap = result.getData();
            Map downloadInfo = MapUtils.getMap(resultMap, "downloadInfo");
            String downloadURLString = MapUtils.getString(downloadInfo, "downloadURL");
            String downLoadPathString = downloadURLString.replaceAll(downloadURL, downloadPath);
            schoolZipSrc.add(downLoadPathString);
        }

        //加入总体报表
        String totalDownLoadPathString = startTotalReportZipPacker(projectId, schoolId, totalFilePath.toArray(new String[totalFilePath.size()]));

        schoolZipSrc.add(totalDownLoadPathString.replaceAll(downloadURL, downloadPath));

        Map<String, Object> resultData = createZipFilesByCMD(projectId, projectId, schoolZipSrc, zipFileName);

        return Result.success().set("downloadInfo", resultData);
    }

    private Map<String, Object> createZipFilesByCMD(String projectId, String projectId1, List<String> schoolZipSrc, String zipFileName) {
        String prefix = getZipFilePrefix(projectId, projectId1);
        //压缩文件的生成目录
        File dir = new File(downloadPath + prefix);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        //下载zip包的生成路径
        String directory = downloadPath + prefix + File.separator + zipFileName;
        File file = new File(directory);

        List<File> files = schoolZipSrc.stream().map(File::new).collect(Collectors.toList());

        return doExecLinuxZipCMD(files.toArray(new File[files.size()]), file);
    }

    public Map<String, Object> doExecLinuxZipCMD(File[] srcFiles, File targetFile) {

        //不压缩子目录
        String cmd = "zip -j " + targetFile.getPath() + " " + getSrcFileZippedItems(srcFiles);
        Runtime runtime = Runtime.getRuntime();
        Map<String, Object> map = new HashMap<>();
        try {
            LOG.info("执行压缩命令为：{}", cmd);
            Process process = runtime.exec(cmd);
            int result = process.waitFor();
            LOG.info("执行压缩命令结果：{}", process.waitFor());
            map.put("downloadFlag", result == 0 ? true : false);
        } catch (Exception e) {
            LOG.info("执行命令失败：{}", e.getMessage());
            map.put("downloadFlag", false);
        }
        int zipSize = DownloadAnalysisService.getZipSize(targetFile.getAbsolutePath());
        String targetFilePath = targetFile.getAbsolutePath();
        String url = targetFilePath.replaceAll(downloadPath, downloadURL);
//        String url = StringUtil.joinPaths(downloadURL, targetFilePath.substring(savePath.length(), targetFilePath.length()));
        map.put("downloadURL", zipSize == 0 ? "" : url);
        map.put("desc", "报表生成完毕！");
        map.put("status", ProjectStatus.ReportGenerated.name());
        map.put("failureList", Collections.emptyList());
        return map;
    }

    private String getSrcFileZippedItems(File[] srcFiles) {
        StringBuilder builder = new StringBuilder();
        for (File file : srcFiles) {
            builder.append(file.getPath());
            builder.append(" ");
        }
        return builder.toString();
    }

    public Result generateZipFiles0(String projectId, String schoolId, String[] filePath, String projectStatus) {

        //根据文件参数获取文件路径
        String[] paths = ReportNameMappings.getFileName(filePath);
        List<Map<String, String>> pathList = new ArrayList<>();
        //压缩文件名称（学校名称-考试分析报表）
        String zipFileName = schoolService.findSchool(projectId, schoolId).getString("name") + "-考试分析报表.zip";
        for (String path : paths) {
            String[] param = path.split("-->");
            List<Map<String, String>> category = getFileCategory(projectId, schoolId, param, false);
            pathList.addAll(category);
        }
        //追加考试id和学校id
        Map<String, Object> resultMap = createZipFiles(projectId, schoolId, pathList, zipFileName);
        resultMap.put("status", projectStatus);
        resultMap.put("downloadFlag", true);
        resultMap.put("desc", "报表生成完毕！");
        return Result.success().set("downloadInfo", resultMap);
    }

    //将文件列表中的文件添置至压缩包
    public Map<String, Object> createZipFiles(String projectId, String schoolId, List<Map<String, String>> pathList, String zipFileName) {
        String prefix = getZipFilePrefix(projectId, schoolId);
        //压缩文件的生成目录
        File dir = new File(downloadPath + prefix);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        //下载zip包的生成路径
        String directory = downloadPath + prefix + File.separator + zipFileName;
        File file = new File(directory);
//        File file1 = new File(dir, zipFileName);
        Map<String, Object> resultMap = new HashMap<>();
        List<String> failureList = new ArrayList<>();
        doZipOperation(pathList, file, failureList);
        //判断压缩文件中是否有文件条目
        int size = getZipSize(directory);
        if (size != 0) {
            //zip文件下载url
            resultMap.put("downloadURL", downloadURL + prefix + File.separator + zipFileName);
        } else {
            resultMap.put("downloadURL", "");
        }
        //不存在的文件列表
        resultMap.put("failureList", failureList);
        return resultMap;
    }

    public void doZipOperation(List<Map<String, String>> pathList, File file, List<String> failureList) {
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
    }

    public static int getZipSize(String fileName) {
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
    public List<Map<String, String>> getFileCategory(String projectId, String schoolId, String[] param, boolean isBureau) {
        //源文件路径和压缩文件路径
        String srcFile, zipFile;
        String part0 = param[0];
        String filename = param[2];
        String filePath;
        List<Map<String, String>> fileCategory = new ArrayList<>();
        Document school = schoolService.findSchool(projectId, schoolId);
        if (part0.startsWith("总体")) {
            // srcFile：savePath/E0/20/430100-2df3f3ad199042c39c5f4b69f5dc7840/总体成绩分析/基础分析/分数分析.xlsx
            // filePath： 总体成绩分析/基础分析/分数分析.xlsx
            srcFile = getSaveFilePath(projectId, savePath, StringUtil.joinPaths(param));
            // /总体成绩分析/基础分析/分数分析.xlsx
            zipFile = StringUtil.joinPaths(param);
            fileCategory.add(getOneFileCategory(srcFile, zipFile, filename));
        } else if (part0.startsWith("学校")) {
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
                // srcFile：savePath/E0/20/430100-2df3f3ad199042c39c5f4b69f5dc7840/班级成绩分析/classId/基础分析/分数分析.xlsx
                // filePath： 班级成绩分析/clasId/基础分析/分数分析.xlsx
                filePath = StringUtil.joinPaths(
                        param[0], d.getString("class"), param[1], filename
                );
                srcFile = getSaveFilePath(projectId, savePath, filePath);
                zipFile = StringUtil.joinPaths(
                        isBureau ? StringUtil.joinPaths(param[0], school.getString("name")) : param[0], d.getString("name"), param[1], filename

                );
                fileCategory.add(getOneFileCategory(srcFile, zipFile, filename));
            }
        } else if (part0.startsWith("个性化")) {
            filePath = StringUtil.joinPaths(
                    param[0], param[1], filename
            );
            srcFile = getSaveFilePath(projectId, savePath, filePath);
            zipFile = StringUtil.joinPaths(
                    isBureau ? StringUtil.joinPaths(param[0], school.getString("name")) : param[0], param[1], filename
            );
            fileCategory.add(getOneFileCategory(srcFile, zipFile, filename));
        } else if (part0.startsWith("联考")) {
            filePath = StringUtil.joinPaths(
                    param[0], param[1], filename
            );
            srcFile = getSaveFilePath(projectId, savePath2, filePath);
            zipFile = StringUtil.joinPaths(
                    param[0], param[1], filename
            );
            fileCategory.add(getOneFileCategory(srcFile, zipFile, filename));
        }
        return fileCategory;
    }

    public String getSaveFilePath(String projectId, String savePath, String filePath) {
        String md5 = MD5.digest(projectId);
        // /E0/20/430100-2df3f3ad199042c39c5f4b69f5dc7840/总体成绩分析/基础分析/分数分析
        return StringUtil.joinPaths(savePath,
                md5.substring(0, 2), md5.substring(2, 4), projectId, filePath);
    }

    public String getZipFilePrefix(String projectId, String schoolId) {
        //在zip包生成的目录下面追加一个文件路径，格式为projectId/schoolId/yyyy/mm/dd/
        Calendar cal = Calendar.getInstance();
        String year = String.valueOf(cal.get(Calendar.YEAR));
        String month = String.valueOf(cal.get((Calendar.MONTH)) + 1);
        String date = String.valueOf(cal.get(Calendar.DAY_OF_MONTH));
        return StringUtil.joinPaths(projectId, schoolId,
                year, month, date);
    }

    public List<SchoolReportZipPacker> startSchoolReportZipPackers(String projectId, List<String> schoolIds, String[] filePathCode) {
        return schoolIds.stream().map(schoolId -> {
            SchoolReportZipPacker schoolReportZipPacker = new SchoolReportZipPacker(projectId, schoolId, filePathCode);
            schoolReportZipPacker.start();
            return schoolReportZipPacker;
        }).collect(Collectors.toList());
    }

    public String startTotalReportZipPacker(String projectId, String schoolId, String[] totalFilePath) {
        String zipFileName = "总体分析报表.zip";

        String[] fileName = ReportNameMappings.getFileName(totalFilePath);

        List<Map<String, String>> pathList = new ArrayList<>();
        for (String path : fileName) {
            String[] param = path.split("-->");
            List<Map<String, String>> category = getFileCategory(projectId, schoolId, param, false);
            pathList.addAll(category);
        }
        Map<String, Object> resultMap = createZipFiles(projectId, schoolId, pathList, zipFileName);

        return MapUtils.getString(resultMap, "downloadURL");
    }

    public List<Result> joinSchoolReportZipPackers(List<SchoolReportZipPacker> schoolReportZipPackers) {
        List<Result> results = new ArrayList<>();
        for (SchoolReportZipPacker schoolReportZipPacker : schoolReportZipPackers) {
            String projectId = schoolReportZipPacker.getProjectId();
            String schoolId = schoolReportZipPacker.getSchoolId();
            try {
                schoolReportZipPacker.join();
                LOG.info("项目{}，学校{}的excel报表打包生成完成！", projectId, schoolId);
            } catch (InterruptedException e) {
                LOG.info("项目{}，学校{}的excel报表打包生成失败！", projectId, schoolId);
            } finally {
                results.add(schoolReportZipPacker.getResult());
            }
        }
        return results;
    }

    class SchoolReportZipPacker extends Thread {
        private String projectId;
        private String schoolId;
        private String[] filePathCode;
        private Result result;

        public SchoolReportZipPacker(String projectId, String schoolId, String[] filePathCode) {
            this.projectId = projectId;
            this.schoolId = schoolId;
            this.filePathCode = filePathCode;
        }

        public String getProjectId() {
            return projectId;
        }

        public void setProjectId(String projectId) {
            this.projectId = projectId;
        }

        public String getSchoolId() {
            return schoolId;
        }

        public void setSchoolId(String schoolId) {
            this.schoolId = schoolId;
        }

        public String[] getFilePathCode() {
            return filePathCode;
        }

        public void setFilePathCode(String[] filePathCode) {
            this.filePathCode = filePathCode;
        }

        public Result getResult() {
            return result;
        }

        public void setResult(Result result) {
            this.result = result;
        }

        @Override
        public void run() {
            //单个学校生成
            setResult(generateZipFile(this.getProjectId(), this.getSchoolId(), this.getFilePathCode(), false));
        }
    }

}
