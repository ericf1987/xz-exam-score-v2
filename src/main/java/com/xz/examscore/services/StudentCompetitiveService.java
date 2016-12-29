package com.xz.examscore.services;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.xz.examscore.bean.Range;
import com.xz.examscore.bean.Target;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.xz.ajiaedu.common.mongo.MongoUtils.$gte;
import static com.xz.ajiaedu.common.mongo.MongoUtils.$lte;
import static com.xz.ajiaedu.common.mongo.MongoUtils.doc;
import static com.xz.examscore.util.Mongo.range2Doc;
import static com.xz.examscore.util.Mongo.target2Doc;

/**
 * @author by fengye on 2016/12/29.
 */
@Service
public class StudentCompetitiveService {

    static final Logger LOG = LoggerFactory.getLogger(StudentCompetitiveService.class);

    @Autowired
    MongoDatabase scoreDatabase;

    public double getAverage(String projectId, Range range, Target target, int rank){
        MongoCollection<Document> collection = scoreDatabase.getCollection("student_competitive");
        Document query = doc("project", projectId)
                .append("range", range2Doc(range))
                .append("target", target2Doc(target))
                .append("startIndex", $lte(rank))
                .append("endIndex", $gte(rank));
        Document first = collection.find(query).first();
        return null == first ? 0 : first.getDouble("average");
    }

}
