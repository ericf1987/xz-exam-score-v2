package com.xz.services;

import com.xz.XzExamScoreV2ApplicationTests;
import com.xz.bean.Point;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * (description)
 * created at 16/06/28
 *
 * @author yiding_he
 */
public class PointServiceTest extends XzExamScoreV2ApplicationTests {

    @Autowired
    PointService pointService;

    @Test
    public void testGetPoints() throws Exception {
        List<Point> points = pointService.getPoints(UNION_PROJECT_ID, "001");
        points.forEach(System.out::println);
    }
}