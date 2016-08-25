package com.xz.examscore.services;

import com.hyd.simplecache.SimpleCache;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static com.xz.ajiaedu.common.mongo.MongoUtils.*;

/**
 * 地市字典信息服务
 *
 * @author zhaorenwu
 */
@Service
public class CityService {

    @Autowired
    MongoDatabase scoreDatabase;

    @Autowired
    SimpleCache cache;

    /**
     * 查询指定父节点的子节点列表
     *
     * @param parent 父节点id
     *
     * @return 子节点列表
     */
    public List<Document> listItems(String parent) {
        String cacheKey = "cities_of:" + parent;

        return new ArrayList<>(cache.get(cacheKey, () -> {
            ArrayList<Document> citys = new ArrayList<>();

            MongoCollection<Document> collection = scoreDatabase.getCollection("cities");
            Document query = doc("parent_id", parent);
            citys.addAll(toList(collection.find(query).sort(doc("id", 1)).projection(WITHOUT_INNER_ID)));

            return citys;
        }));
    }

    /**
     * 查询指定地市信息
     *
     * @param id 地市id
     *
     * @return 地市信息
     */
    public Document findCity(String id) {
        String cacheKey = "city_by_id:" + id;

        return cache.get(cacheKey, () -> {
            MongoCollection<Document> collection = scoreDatabase.getCollection("cities");

            Document query = doc("id", id);
            return collection.find(query).projection(WITHOUT_INNER_ID).first();
        });
    }

    /**
     * 查询指定地市名称
     *
     * @param id 地市id
     *
     * @return 地市名称
     */
    public String getCityName(String id) {
        Document city = findCity(id);
        return city == null ? "" : city.getString("name");
    }

    /**
     * 保存项目所属城市列表
     *
     * @param projectId 项目
     * @param cities    城市列表
     */
    public void saveProjectCities(String projectId, Collection<String> cities) {
        List<Document> documents = cities.stream()
                .map(city -> doc("project", projectId).append("city", city))
                .collect(Collectors.toList());

        MongoCollection<Document> c = scoreDatabase.getCollection("city_list");
        c.deleteMany(doc("project", projectId));
        c.insertMany(documents);
    }
}
