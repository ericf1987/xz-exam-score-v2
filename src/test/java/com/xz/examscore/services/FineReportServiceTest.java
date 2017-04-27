package com.xz.examscore.services;

import com.xz.examscore.XzExamScoreV2ApplicationTests;
import com.xz.examscore.bean.FineReportItem;
import org.bson.Document;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @author by fengye on 2017/4/26.
 */
public class FineReportServiceTest extends XzExamScoreV2ApplicationTests {

    @Autowired
    FineReportService fineReportService;

    @Test
    public void testInsertItem() throws Exception {
        FineReportItem item = new FineReportItem();
        item.setItemId("101");
        item.setItemName("腾讯");
        item.setItemType("basics");
        item.setItemUrl("http://www.qq.com");
        fineReportService.insertItem(item);
    }

    @Test
    public void testUpdateItem() throws Exception {
        FineReportItem item = new FineReportItem();
        item.setItemId("101");
        item.setItemName("腾讯");
        item.setItemType("basics");
        item.setItemUrl("http://www.qq.com");
        item.setPosition(2);
        fineReportService.updateItem(item);
    }

    @Test
    public void testDeleteItem() throws Exception {

    }

    @Test
    public void testItem2Doc() throws Exception {

    }

    @Test
    public void testGetItem() throws Exception {
        Document item = fineReportService.getItem("100");
        System.out.println(item.toString());
    }

    @Test
    public void testGetAllItem() throws Exception {
        List<Document> allItems = fineReportService.getAllItems();
        System.out.println(allItems.toString());
    }
}