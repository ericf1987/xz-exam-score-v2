package com.xz.services;

import com.mongodb.client.MongoDatabase;
import com.xz.bean.Range;
import com.xz.bean.Target;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MinMaxScoreService {

    @Autowired
    MongoDatabase scoreDatabase;

    /**
     * 查询最低分最高分
     *
     * @param projectId 项目ID
     * @param range     范围
     * @param target    目标
     *
     * @return 最低分、最高分
     */
    public double[] getMinMaxScore(String projectId, Range range, Target target) {

    }
}
