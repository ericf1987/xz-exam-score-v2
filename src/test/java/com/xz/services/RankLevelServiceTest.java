package com.xz.services;

import com.xz.XzExamScoreV2ApplicationTests;
import com.xz.bean.Range;
import com.xz.bean.Target;
import org.apache.poi.ss.formula.functions.Rank;
import org.bson.Document;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * @author by fengye on 2016/7/18.
 */
public class RankLevelServiceTest extends XzExamScoreV2ApplicationTests{

    @Autowired
    RankLevelService rankLevelService;

    @Test
    public void testGetRankLevelMap() throws Exception {
        String projectId = "430200-b73f03af1d74484f84f1aa93f583caaa";
        Range schoolRange = Range.school("200f3928-a8bd-48c4-a2f4-322e9ffe3700");
        Range classRange = Range.clazz("0c738247-b62c-4c90-9016-1cc1163fd0b1");
        Target subjectTarget = Target.subject("001");
        Target projectTarget = Target.project(projectId);

        List<Map<String, Object>> list = rankLevelService.getRankLevelMap(projectId, schoolRange, subjectTarget);
        List<Map<String, Object>> list1 = rankLevelService.getRankLevelMap(projectId, schoolRange, projectTarget);
        List<Map<String, Object>> list2 = rankLevelService.getRankLevelMap(projectId, classRange, subjectTarget);
        List<Map<String, Object>> list3 = rankLevelService.getRankLevelMap(projectId, classRange, projectTarget);
        System.out.println(list.toString());
        System.out.println(list1.toString());
        System.out.println(list2.toString());
        System.out.println(list3.toString());
    }
}