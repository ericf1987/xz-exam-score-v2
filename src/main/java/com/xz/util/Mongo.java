package com.xz.util;

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

    public static Document query(String projectId, Range range) {
        return query(projectId, range, null);
    }

    public static Document query(String projectId, Target target) {
        return query(projectId, null, target);
    }

    /**
     * 根据三个参数生成一个 Document，可用于分片集合的 key。
     *
     * @param projectId 项目ID
     * @param range     范围（可选）
     * @param target    目标（可选）
     *
     * @return 生成的 key 对象
     */
    public static Document query(String projectId, Range range, Target target) {
        Document document = doc("project", projectId);

        if (range != null) {
            document.append("range", range2Doc(range));
        }

        if (target != null) {
            document.append("target", target2Doc(target));
        }

        return document;
    }

    /**
     * 将 Target 对象转换为 Document 对象
     *
     * @param target 转换之前的对象
     *
     * @return 转换之后的对象
     */
    public static Document target2Doc(Target target) {
        return doc("name", target.getName()).append("id", target.idToParam());
    }

    /**
     * 将 Range 对象转换为 Document 对象
     *
     * @param range 转换之前的对象
     *
     * @return 转换之后的对象
     */
    public static Document range2Doc(Range range) {
        return doc("name", range.getName()).append("id", range.getId());
    }
}


