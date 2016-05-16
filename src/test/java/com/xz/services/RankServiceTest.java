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
        System.out.println("rank of 90: " +
                rankService.getRank(PROJECT_ID, Range.school("SCHOOL_001"), Target.subject("001"), 90));
    }

    @Test
    public void testGetRankLevel() throws Exception {
        System.out.println(
                rankService.getRankLevel(
                        PROJECT_ID, Range.school("SCHOOL_001"), Target.subject("001"), "SCHOOL_001_CLASS_01_01"));
    }
}