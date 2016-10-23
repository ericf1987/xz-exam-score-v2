package com.xz.examscore.util;

import com.xz.examscore.XzExamScoreV2ApplicationTests;
import org.junit.Test;

import java.util.Arrays;

/**
 * @author by fengye on 2016/10/23.
 */
public class ReportNameMappingsTest extends XzExamScoreV2ApplicationTests {

    @Test
    public void testGetFileName() throws Exception {
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
        Arrays.asList(ReportNameMappings.getFileName(arr)).forEach(System.out::println);
    }
}