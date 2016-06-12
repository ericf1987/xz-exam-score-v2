package com.xz.services;

import com.xz.bean.Point;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

/**
 * 知识点与能力层级
 *
 * @author yiding_he
 */
@Service
public class PointService {

    public List<Point> getPoints(String projectId, String subjectId) {
        return Collections.emptyList();
    }
}
