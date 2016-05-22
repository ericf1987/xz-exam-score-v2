package com.xz.services;

import com.mongodb.client.MongoDatabase;
import com.xz.ajiaedu.common.lang.StringUtil;
import com.xz.bean.ProjectConfig;
import com.xz.bean.Range;
import com.xz.bean.Target;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.xz.ajiaedu.common.mongo.MongoUtils.doc;
import static com.xz.util.Mongo.range2Doc;
import static com.xz.util.Mongo.target2Doc;

/**
 * (description)
 * created at 16/05/21
 *
 * @author yiding_he
 */
@Service
public class RankLevelService {

    static final Logger LOG = LoggerFactory.getLogger(RankLevelService.class);

    @Autowired
    ProjectConfigService projectConfigService;

    @Autowired
    MongoDatabase scoreDatabase;

    public String getRankLevel(
            String projectId, String studentId, Target target, String rankRange, String defaultValue) {

        String collectionName = "total_score";
        ProjectConfig projectConfig = projectConfigService.getProjectConfig(projectId);
        if (projectConfig.isCombineCategorySubjects() && isCombinedSubject(target)) {
            collectionName = "total_score_combined";
        }

        Document query = doc("project", projectId)
                .append("target", target2Doc(target))
                .append("range", range2Doc(Range.student(studentId)));

        Document document = scoreDatabase.getCollection(collectionName)
                .find(query).projection(doc("rankLevel", 1)).first();

        if (document != null) {
            return ((Document) document.get("rankLevel")).getString(rankRange);
        } else {
            LOG.warn("找不到排名等级（可能缺考）: query=" + query);
            return defaultValue;
        }
    }

    private boolean isCombinedSubject(Target target) {
        if (!target.match(Target.SUBJECT)) {
            return false;
        }

        String subjectId = target.getId().toString();
        return StringUtil.isOneOf(subjectId, "004005006", "007008009");
    }
}
