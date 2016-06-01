package com.xz.services;

import com.mongodb.client.MongoDatabase;
import com.xz.bean.Range;
import com.xz.bean.Target;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.xz.util.Mongo.query;

/**
 * (description)
 * created at 16/05/26
 *
 * @author yiding_he
 */
@Service
public class StdDeviationService {

    static final Logger LOG = LoggerFactory.getLogger(StdDeviationService.class);

    @Autowired
    MongoDatabase scoreDatabase;

    /**
     * 查询标准差
     *
     * @param projectId 项目ID
     * @param range     范围（不能是单个学生）
     * @param target    目标
     *
     * @return 标准差
     */
    public double getStdDeviation(String projectId, Range range, Target target) {
        Document query = query(projectId, range, target);
        Document document = scoreDatabase.getCollection("std_deviation").find(query).first();

        if (document != null) {
            return document.getDouble("stdDeviation");
        } else {
            LOG.error("找不到标准差数值: " + query.toJson());
            return 0d;
        }
    }
}
