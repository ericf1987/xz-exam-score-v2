package com.xz.examscore.services;

import com.hyd.simplecache.SimpleCache;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.xz.examscore.bean.Range;
import com.xz.examscore.cache.ProjectCacheManager;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.xz.examscore.util.Mongo.range2Doc;

/**
 * 全科通过率与全科不通过率
 *
 * @author zhaorenwu
 */
@Service
public class PassAndUnPassService {

    static final Logger LOG = LoggerFactory.getLogger(PassAndUnPassService.class);

    @Autowired
    MongoDatabase scoreDatabase;

    @Autowired
    ProjectCacheManager projectCacheManager;

    /**
     * 查询全科及格率与全科不及格率
     *
     * @param projectId 项目ID
     * @param range     范围
     *
     * @return 全科及格率与不及格率
     */
    public double[] getAllSubjectPassAndUnPass(String projectId, Range range) {
        String cacheKey = "pass_rate:" + projectId + ":" + range;

        SimpleCache simpleCache = projectCacheManager.getProjectCache(projectId);

        return simpleCache.get(cacheKey, () -> {
            MongoCollection<Document> averageCollection = scoreDatabase.getCollection("all_subject_pass_rate");
            Document document = averageCollection.find(
                    new Document("project", projectId)
                            .append("range", range2Doc(range))
            ).first();

            if (document != null) {
                return new double[]{getDouble(document, "allPassRate"), getDouble(document, "allFailRate")};
            } else {
                LOG.error("找不到全科及格率与不及格率, project={}, range={}", projectId, range);
                return new double[]{0, 0};
            }
        });
    }

    private Double getDouble(Document document, String allPassRate) {
        Double d = document.getDouble(allPassRate);
        return d == null ? 0d : d;
    }
}
