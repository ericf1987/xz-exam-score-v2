package com.xz.api.services;

import com.xz.XzExamScoreV2ApplicationTests;
import com.xz.ajiaedu.common.lang.Result;
import com.xz.ajiaedu.common.lang.StringUtil;
import com.xz.services.ClassService;
import com.xz.services.DownloadAnalysisService;
import com.xz.services.SchoolService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * @author by fengye on 2016/6/14.
 */
public class DownloadAnalysisServiceTest extends XzExamScoreV2ApplicationTests {
    @Autowired
    DownloadAnalysisService downloadAnalysisService;

    @Test
    public void testMain() throws Exception {
        String[] arr = new String[]{
                "100-200-300", "100-200-301", "100-200-302", "100-200-303",
                "100-201-304", "100-201-305", "100-201-306", "100-201-307",
                "100-202-308", "100-202-310",

                "101-200-300", "101-200-301", "101-200-302", "101-200-303",
                "101-201-304", "101-201-305", "101-201-306", "101-201-307",
                "101-202-308", "101-202-309", "101-202-310",

                "102-200-311",
                "102-201-314"
        };
        Result result = downloadAnalysisService.generateZipFiles("431100-903288f61a5547f1a08a7e20420c4e9e",
                "b49b8e85-f390-4e09-a709-8ab1175b0c68", arr);
        System.out.println(result.getData());
/*        String s = downloadAnalysisService.getZipFilePrefix("431100-903288f61a5547f1a08a7e20420c4e9e",
                "b49b8e85-f390-4e09-a709-8ab1175b0c68");
        System.out.println(s);*/
    }
}