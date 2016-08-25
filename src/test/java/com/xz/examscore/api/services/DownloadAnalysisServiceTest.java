package com.xz.examscore.api.services;

import com.xz.ajiaedu.common.lang.Result;
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
/*
                "100-200-300", "100-200-301", "100-200-302", "100-200-303",
                "100-201-304", "100-201-305", "100-201-306", "100-201-307",
                "100-202-308", "100-202-310",

                "101-200-300", "101-200-301", "101-200-302", "101-200-303",
                "101-201-304", "101-201-305", "101-201-306", "101-201-307",
                "101-202-308", "101-202-309", "101-202-310",

                "102-200-311",
                "102-201-314"
*/

                "100-200-302","101-200-302","102-200-313"
        };
/*        Result result = downloadAnalysisService.generateZipFiles("431100-903288f61a5547f1a08a7e20420c4e9e",
                "b49b8e85-f390-4e09-a709-8ab1175b0c68", arr);*/
        Result result = downloadAnalysisService.generateZipFiles("430100-8d805ef37b2f4bc7ad9808a9a109dc22",
                "0835e05b-4d01-4944-9a0a-b8a77f201933", arr);
        System.out.println(result.getData());
/*        String s = downloadAnalysisService.getZipFilePrefix("431100-903288f61a5547f1a08a7e20420c4e9e",
                "b49b8e85-f390-4e09-a709-8ab1175b0c68");
        System.out.println(s);*/
    }
}