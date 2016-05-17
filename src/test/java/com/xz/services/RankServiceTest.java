package com.xz.services;

import com.xz.XzExamScoreV2ApplicationTests;
import com.xz.bean.Range;
import com.xz.bean.Target;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * (description)
 * created at 16/05/13
 *
 * @author yiding_he
 */
public class RankServiceTest extends XzExamScoreV2ApplicationTests {

    @Autowired
    RankService rankService;

    @Test
    public void testGetRank() throws Exception {
        System.out.println("rank of 99: " +
                rankService.getRank(PROJECT_ID, Range.school("SCHOOL_007"), Target.subject("003"), 99));
    }

    @Test
    public void testGetRank2() throws Exception {
        int rank = rankService.getRank(PROJECT_ID,
                Range.clazz("SCHOOL_002_CLASS_04"), Target.subject("003"), "SCHOOL_006_CLASS_04_05");

        System.out.println(rank);
    }

    @Test
    public void testGetRankLevel() throws Exception {
        System.out.println(
                rankService.getRankLevel(
                        PROJECT_ID, Range.school("SCHOOL_008"), Target.subject("002"), "SCHOOL_008_CLASS_08_01"));
    }
}