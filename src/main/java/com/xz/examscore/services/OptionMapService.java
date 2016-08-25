package com.xz.examscore.services;

import com.hyd.simplecache.SimpleCache;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.xz.ajiaedu.common.lang.CollectionUtils;
import com.xz.examscore.bean.Range;
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
 * 选项选率统计
 *
 * @author zhaorenwu
 */
@Service
public class OptionMapService {

    static final Logger LOG = LoggerFactory.getLogger(OptionMapService.class);

    @Autowired
    MongoDatabase scoreDatabase;

    @Autowired
    SimpleCache cache;

    /**
     * 试题选项选率统计（map 格式）
     *
     * @param projectId 项目ID
     * @param questId   范围
     * @param range     试题id
     *
     * @return  试题区分度
     */
    public Map<String, Document> getOptionMap(String projectId, String questId, Range range) {
        String cacheKey = "option_maps:" + projectId + ":" + questId + ":" + range;
        return cache.get(cacheKey, () -> {
            List<Document> optionList = getOptionList(projectId, questId, range);
            return new HashMap<>(CollectionUtils.toMap(optionList, document -> document.getString("answer")));
        });
    }

    /**
     * 试题选项选率统计（list 格式）
     *
     * @param projectId 项目ID
     * @param range     范围
     * @param questId   试题id
     *
     * @return 试题区分度
     */
    @SuppressWarnings("unchecked")
    public List<Document> getOptionList(String projectId, String questId, Range range) {
        String cacheKey = "option_lists:" + projectId + ":" + questId + ":" + range;
        return cache.get(cacheKey, () -> {
            MongoCollection<Document> collection = scoreDatabase.getCollection("option_map");
            Document document = collection.find(
                    new Document("project", projectId)
                            .append("quest", questId)
                            .append("range", range2Doc(range))
            ).first();

            if (document != null) {
                return new ArrayList<>(document.get("optionMap", List.class));
            } else {
                LOG.error("找不到试题选项选率数据, project={}, questId={}, range={}", projectId, questId, range);
                return new ArrayList<>();
            }
        });
    }
}
