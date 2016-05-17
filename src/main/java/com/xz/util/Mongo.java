package com.xz.util;

import com.alibaba.fastjson.JSON;
import com.xz.bean.Range;
import com.xz.bean.Target;
import org.bson.Document;

import static com.xz.ajiaedu.common.mongo.MongoUtils.doc;

/**
 * (description)
 * created at 16/05/13
 *
 * @author yiding_he
 */
public class Mongo {

    /**
     * 根据三个参数生成一个 Document，可用于分片集合的 key。
     *
     * @param projectId 项目ID
     * @param range     范围
     * @param target    目标
     *
     * @return 生成的 key 对象
     */
    public static Document generateId(String projectId, Range range, Target target) {

        Object targetId = target.getId();
        if (!(targetId instanceof String)) {
            targetId = Document.parse(JSON.toJSONString(targetId));
        }

        return doc("project", projectId)
                .append("range", doc().append("name", range.getName()).append("id", range.getId()))
                .append("target", doc().append("name", target.getName()).append("id", targetId));
    }

    /**
     * 将 Target 对象转换为 Document 对象
     *
     * @param target 转换之前的对象
     *
     * @return 转换之后的对象
     */
    public static Document target(Target target) {
        return doc("name", target.getName()).append("id", target.idToParam());
    }

    /**
     * 将 Range 对象转换为 Document 对象
     *
     * @param range 转换之前的对象
     *
     * @return 转换之后的对象
     */
    public static Document range(Range range) {
        return doc("name", range.getName()).append("id", range.getId());
    }
}


