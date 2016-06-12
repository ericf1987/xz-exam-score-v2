package com.xz.services;

import com.hyd.simplecache.SimpleCache;
import com.mongodb.client.MongoDatabase;
import com.xz.bean.Point;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    MongoDatabase scoreDatabase;

    @Autowired
    SimpleCache cache;

    public List<Point> getPoints(String projectId, String subjectId) {
        String cacheKey = "points:" + projectId + ":" + subjectId;
        return Collections.emptyList();
    }
}
