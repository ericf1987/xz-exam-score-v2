package com.xz.report.total;

import com.xz.XzExamScoreV2ApplicationTests;
import com.xz.bean.Range;
import com.xz.services.ClassService;
import org.bson.Document;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

/**
 * @author by fengye on 2016/7/30.
 */
public class TotalPointAbilityLevelReportTest extends XzExamScoreV2ApplicationTests {

    @Autowired
    TotalPointAbilityLevelReport totalPointAbilityLevelReport;

    @Autowired
    ClassService classService;

    @Test
    public void testGetSheetTasks() throws Exception {
        //totalPointAbilityLevelReport.generate("430100-e7bd093d92d844819c7eda8b641ab6ee", Range.province("430000"), "target/total-point-ability-level.xlsx");
        //totalPointAbilityLevelReport.generate("430100-e7bd093d92d844819c7eda8b641ab6ee", Range.school("d00faaa0-8a9b-45c4-ae16-ea2688353cd0"), "target/total-point-ability-level.xlsx");
        List<Document> listClasses = new ArrayList<>(classService.listClasses("430100-e7bd093d92d844819c7eda8b641ab6ee", "d00faaa0-8a9b-45c4-ae16-ea2688353cd0"));
        if (!listClasses.isEmpty()) {
            listClasses.sort((o1, o2) -> o1.getString("name").compareTo(o2.getString("name")));
            String classId = listClasses.get(0).getString("class");
            String className = listClasses.get(0).getString("name");
            System.out.println(classId + "-->" + className);
        }
    }
}