package com.xz.examscore.api.server.sys;

import com.hyd.simplecache.utils.MD5;
import com.xz.ajiaedu.common.lang.Result;
import com.xz.examscore.XzExamScoreV2ApplicationTests;
import com.xz.examscore.api.Param;
import com.xz.examscore.services.ReportItemService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

/**
 * @author by fengye on 2016/8/11.
 */
public class AddReportItemTest extends XzExamScoreV2ApplicationTests {

    @Autowired
    AddReportItem addReportItem;

    @Autowired
    ReportItemService reportItemService;

    @Test
    public void testExecute() throws Exception {

        String[] collectionNames = new String[]{
                //"score", "total_score"
                "college_entry_level"
        };

        Param param = new Param()
                .setParameter("name", "上线预测")
                .setParameter("type", "topStudent")
                .setParameter("rangeName", "clazz")
                .setParameter("rangeId", ReportItemService.COMMON_RANGE_ID)
                .setParameter("collectionNames", collectionNames)
                .setParameter("serverName", "ClassEntryLevelRateAnalysis")
                .setParameter("md5", MD5.digest(UUID.randomUUID().toString()))
                .setParameter("tag", "102-202-325");
        Result result = addReportItem.execute(param);
        System.out.println(result.getData());
    }

    @Test
    public void test1() throws Exception{
        int position = reportItemService.queryMaxPosition();
        System.out.println(position);
    }
}