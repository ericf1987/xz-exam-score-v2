package com.xz.util;

import org.junit.Test;

/**
 * (description)
 * created at 16/05/26
 *
 * @author yiding_he
 */
public class ScoreSegmentCounterTest {

    @Test
    public void testAddScore() throws Exception {
        ScoreSegmentCounter counter = new ScoreSegmentCounter(10);
        counter.addScore(0.5);
        counter.addScore(10);
        counter.addScore(12.5);
        counter.addScore(22.5);
        counter.addScore(32.5);
        System.out.println(counter.toDocuments());
    }
}