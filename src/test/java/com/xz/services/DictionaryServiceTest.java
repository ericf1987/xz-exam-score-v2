package com.xz.services;

import com.xz.XzExamScoreV2ApplicationTests;
import org.bson.Document;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.Assert.*;

/**
 * @author by fengye on 2016/7/28.
 */
public class DictionaryServiceTest extends XzExamScoreV2ApplicationTests {
    @Autowired
    DictionaryService dictionaryService;

    @Test
    public void testListItems() throws Exception {
        List<Document> doc = dictionaryService.listItems("isGovernmental");
        System.out.println(doc.toString());
    }

    @Test
    public void testFindDictionary() throws Exception {
        Document doc = dictionaryService.findDictionary("isGovernmental", "0");
        System.out.println(doc.toString());
    }
}