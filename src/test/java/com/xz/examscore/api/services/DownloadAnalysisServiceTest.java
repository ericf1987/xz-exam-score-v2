package com.xz.examscore.api.services;

import com.hyd.simplecache.utils.MD5;
import com.xz.ajiaedu.common.lang.Result;
import com.xz.ajiaedu.common.lang.StringUtil;
import com.xz.examscore.XzExamScoreV2ApplicationTests;
import com.xz.examscore.services.DownloadAnalysisService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author by fengye on 2016/6/14.
 */
public class DownloadAnalysisServiceTest extends XzExamScoreV2ApplicationTests {
    @Autowired
    DownloadAnalysisService downloadAnalysisService;

    @Test
    public void testMain() throws Exception {
        String[] arr = new String[]{
/*                "100-200-300",
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
                "100-202-325",
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
                "101-202-325",
                "102-200-311",
                "102-204-323",
                "102-200-319",
                "102-202-325",
                "102-201-314",
                "102-201-315",*/
                "102-201-316"
        };
/*        Result result = downloadAnalysisService.generateZipFiles("431100-903288f61a5547f1a08a7e20420c4e9e",
                "b49b8e85-f390-4e09-a709-8ab1175b0c68", arr);*/
        Result result = downloadAnalysisService.generateZipFiles("430600-fafb16b81a394f369f0863bfaab766eb",
                "a8e50b61-547a-485d-87b1-9a1527993b80", arr);
        System.out.println(result.getData());
/*        String s = downloadAnalysisService.getZipFilePrefix("431100-903288f61a5547f1a08a7e20420c4e9e",
                "b49b8e85-f390-4e09-a709-8ab1175b0c68");
        System.out.println(s);*/
    }

    @Test
    public void testMain1() throws Exception{
        String projectId = "430300-9cef9f2059ce4a36a40a7a60b07c7e00";
        String[] param = new String[]{"总体成绩分析","基础分析","分数分析.xlsx"};
        String result = getSaveFilePath(projectId, "F://", StringUtil.joinPaths(param));
        System.out.println(result);
    }

    private String getSaveFilePath(String projectId, String savePath, String filePath) {
        String md5 = MD5.digest(projectId);
        // /E0/20/430100-2df3f3ad199042c39c5f4b69f5dc7840/总体成绩分析/基础分析/分数分析
        return StringUtil.joinPaths(savePath,
                md5.substring(0, 2), md5.substring(2, 4), projectId, filePath);
    }
}