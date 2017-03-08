package com.xz.examscore.services;

import com.hyd.simplecache.SimpleCache;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.xz.ajiaedu.common.lang.CollectionUtils;
import com.xz.examscore.bean.Range;
import com.xz.examscore.cache.ProjectCacheManager;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.xz.examscore.util.Mongo.range2Doc;

/**
 * 科目贡献率
 *
 * @author zhaorenwu
 */
@Service
public class SubjectRateService {

    static final Logger LOG = LoggerFactory.getLogger(SubjectRateService.class);

    @Autowired
    ProjectCacheManager projectCacheManager;

    @Autowired
    MongoDatabase scoreDatabase;

    /**
     * 查询科目贡献率
     *
     * @param projectId 考试项目id
     * @param range     范围
     *
     * @return  科目贡献率
     */
    @SuppressWarnings("unchecked")
    public Map<String, Document> querySubjectRateMap(String projectId, Range range) {
        String cacheKey = "subject_rate_map:" + projectId + ":" + range;

        SimpleCache simpleCache = projectCacheManager.getProjectCache(projectId);

        return simpleCache.get(cacheKey, () -> {
            List<Document> subjectRates = querySubjectRate(projectId, range);
            return new HashMap<>(CollectionUtils.toMap(subjectRates,
                    subjectRate -> subjectRate.getString("subject")));
        });
    }

    /**
     * 查询科目贡献率
     *
     * @param projectId 考试项目id
     * @param range     范围
     *
     * @return  科目贡献率
     */
    @SuppressWarnings("unchecked")
    public List<Document> querySubjectRate(String projectId, Range range) {
        String cacheKey = "subject_rate:" + projectId + ":" + range;

        SimpleCache simpleCache = projectCacheManager.getProjectCache(projectId);

        return simpleCache.get(cacheKey, () -> {
            MongoCollection<Document> collection = scoreDatabase.getCollection("subject_rate");
            Document document = collection.find(
                    new Document("project", projectId)
                            .append("range", range2Doc(range))
            ).first();

            if (document != null) {
                return new ArrayList<>(document.get("subjectRates", List.class));
            } else {
                LOG.error("找不到科目贡献率, project={}, range={}", projectId, range);
                return new ArrayList<>();
            }
        });
    }
}
