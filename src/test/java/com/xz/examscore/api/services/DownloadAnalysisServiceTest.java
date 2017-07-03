package com.xz.examscore.api.services;

import com.xz.ajiaedu.common.lang.Result;
import com.xz.ajiaedu.common.lang.StringUtil;
import com.xz.examscore.XzExamScoreV2ApplicationTests;
import com.xz.examscore.services.DownloadAnalysisService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

/**
 * @author by fengye on 2016/6/14.
 */
public class DownloadAnalysisServiceTest extends XzExamScoreV2ApplicationTests {
    @Autowired
    DownloadAnalysisService downloadAnalysisService;

    @Value("${report.zip.location}")
    private String downloadPath;

    @Value("${zip.download.url}")
    private String downloadURL;

    public static final String[] REPORT_PARAMS = new String[]{
            "100-200-300",
            "100-200-301",
            "100-200-302",
            "100-200-303",
            "100-201-304",
            "101-201-305",
            "100-201-306",
            "100-201-307",
            "100-202-308",
            "100-202-309",
            "100-202-310",
            "101-200-300",
            "101-200-301",
            "101-200-302",
            "101-200-303",
            "101-200-319",
            "101-204-324",
            "101-201-304",
            "102-201-305",
            "101-201-306",
            "101-201-307",
            "101-202-308",
            "101-202-309",
            "101-202-310",
            "102-200-311",
            "102-204-323",
            "102-200-319",
            "102-201-314",
            "102-201-315",
            "102-201-316"
    };

    @Test
    public void testMain() throws Exception {
        String projectId = "430100-e7bd093d92d844819c7eda8b641ab6ee";
        String schoolId = "d00faaa0-8a9b-45c4-ae16-ea2688353cd0";
        Result result = downloadAnalysisService.generateZipFile(projectId, schoolId, REPORT_PARAMS, true);
        System.out.println(result.getData());
    }

    @Test
    public void testMain1() throws Exception {
        String projectId = "430100-c6da4bfd10134ddb9c87c601d51eb631";
        String[] param = new String[]{"总体成绩分析", "基础分析", "分数分析.xlsx"};
        String result = downloadAnalysisService.getSaveFilePath(projectId, "F://", StringUtil.joinPaths(param));
        System.out.println(result);
    }

    @Test
    public void testgenerateBureauZipFile() throws Exception {
        String projectId = "430100-e7bd093d92d844819c7eda8b641ab6ee";
        String schoolId = "d00faaa0-8a9b-45c4-ae16-ea2688353cd0";
        Result result = downloadAnalysisService.generateZipFile(projectId, schoolId, REPORT_PARAMS, true);
        System.out.println(result.getData());
/*        String s = "http://report.ajia.cn/download-zip/430100-e7bd093d92d844819c7eda8b641ab6ee/430100-e7bd093d92d844819c7eda8b641ab6ee/2017/4/5\\中南迅智中学2016年6月初二期末考试（演示）-教育局分析报表.zip,";
        System.out.println(s.replaceAll(downloadURL, downloadPath));*/
    }

    @Test
    public void testgenerateBureauZipFile0() throws Exception {
        String projectId = "431100-a1dc056391744ef5afc296541ed4414f";
        String schoolId = "d00faaa0-8a9b-45c4-ae16-ea2688353cd0";
        Result result = downloadAnalysisService.generateBureauZipFile0(projectId, schoolId, REPORT_PARAMS);
        System.out.println(result.getData());
    }
}