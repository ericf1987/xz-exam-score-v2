package com.xz.services;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.xz.ajiaedu.common.mongo.MongoUtils;
import com.xz.bean.Range;
import com.xz.bean.Target;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.xz.ajiaedu.common.mongo.MongoUtils.doc;
import static com.xz.util.Mongo.query;

/**
 * (description)
 * created at 16/06/13
 *
 * @author yiding_he
 */
@Service
public class TopStudentListService {

    @Autowired
    MongoDatabase scoreDatabase;

    /**
     * 查询尖子生列表
     *
     * @param projectId 项目ID
     * @param range     排名范围
     * @param target    排名目标
     * @param minRank   最小名次
     * @param maxRank   最大名次
     *
     * @return 尖子生列表
     */
    public List<Document> getTopStudentList(
            String projectId, Range range, Target target, int minRank, int maxRank) {

        MongoCollection<Document> collection = scoreDatabase.getCollection("top_student_list");
        Document query = query(projectId, range, target)
                .append("rank", doc("$gte", minRank).append("$lte", maxRank));

        Document projection = doc("score", 1).append("rank", 1).append("student", 1);  // 查询结果包含属性

        return MongoUtils.toList(collection.find(query).projection(projection));
    }
}
