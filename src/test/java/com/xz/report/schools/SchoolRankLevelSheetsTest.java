package com.xz.report.schools;

import com.xz.XzExamScoreV2ApplicationTests;
import com.xz.bean.Range;
import com.xz.bean.Target;
import com.xz.services.*;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

/**
 * @author by fengye on 2016/7/18.
 */
public class SchoolRankLevelSheetsTest extends XzExamScoreV2ApplicationTests {
    @Autowired
    SchoolRankLevelReport schoolRankLevelReport;

    @Autowired
    SchoolService schoolService;

    @Autowired
    ClassService classService;

    @Autowired
    StudentService studentService;

    @Autowired
    RankLevelService rankLevelService;

    @Autowired
    TargetService targetService;

    @Test
    public void testGenerateSheet() throws Exception {
        String projectId = "430100-e7bd093d92d844819c7eda8b641ab6ee";
        Range range = Range.school("d00faaa0-8a9b-45c4-ae16-ea2688353cd0");
        schoolRankLevelReport.generate(projectId, range, "target/school-rank-level.xlsx");
    }

    @Test
    public void testStudentCount() throws Exception {
        String projectId = "430200-b73f03af1d74484f84f1aa93f583caaa";
        String schoolId = "200f3928-a8bd-48c4-a2f4-322e9ffe3700";
        String subjectId = "001";
        String rankLevel = "A";
        Range schoolRange = Range.school(schoolId);

        //统计学校人数

        Target target = targetService.getTarget(projectId, subjectId);

        //查询等第人数
        List<Map<String, Object>> schoolRankLevels = sort(rankLevelService.getRankLevelMap(projectId, schoolRange, target));

        int schoolRankCount = 0;
        int classRankCount = 0;
        for(Map<String, Object> rank : schoolRankLevels){
            if(rank.get("rankLevel").equals(rankLevel)){
                schoolRankCount += Integer.parseInt(rank.get("count").toString());
            }
        }
        System.out.println("当前等级为" + rankLevel + ", 学校人数为" + schoolRankCount);
        List<String> classIds = classService.listClasses(projectId, schoolId).stream()
                .map(document -> document.getString("class")).collect(Collectors.toList());

        for(String classId : classIds){
            Range classRange = Range.clazz(classId);
            List<Map<String, Object>> classRankLevel = sort(rankLevelService.getRankLevelMap(projectId, classRange, target));
            for(Map<String, Object> rank : classRankLevel){
                if(rank.get("rankLevel").equals(rankLevel)){
                    int count =  Integer.parseInt(rank.get("count").toString());
                    System.out.println("当前等级为" + rankLevel + ", 班级当前人数为" + count);
                    classRankCount += count;
                }
            }
        }
        System.out.println("当前等级为" + rankLevel + ", 班级总人数为" + classRankCount);

    }

    private List<Map<String, Object>> sort(List<Map<String, Object>> list){
        Collections.sort(list, (Map<String, Object> m1, Map<String, Object> m2) ->
                m1.get("rankLevel").toString().compareTo(m2.get("rankLevel").toString())
        );
        return list;
    }
}