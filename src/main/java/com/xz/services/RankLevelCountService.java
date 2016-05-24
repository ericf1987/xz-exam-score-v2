package com.xz.services;

import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.result.UpdateResult;
import com.xz.ajiaedu.common.mongo.MongoUtils;
import com.xz.bean.Range;
import com.xz.bean.Target;
import com.xz.util.Mongo;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static com.xz.ajiaedu.common.mongo.MongoUtils.doc;

/**
 * Created by fengye on 2016/5/20.
 */
@Service
@SuppressWarnings("unchecked")
public class RankLevelCountService {

    @Autowired
    MongoDatabase scoreDatabase;

    @Autowired
    StudentService studentService;

    public void generateRankLevelHZ(String projectId) {
/*        MongoClient mongoClient = new MongoClient( "localhost" , 27017 );
        scoreDatabase = mongoClient.getDatabase("project_scores");*/
        MongoCollection<Document> scoreCol = scoreDatabase.getCollection("total_score");
        MongoCollection<Document> scoreRankLevelCol = scoreDatabase.getCollection("score_rank_level_map");
        //查询分数表中所有学生的排名
        FindIterable<Document> total_scores = scoreCol.find(
                new Document("project", projectId).
                        append("range.name", "student").
                        append("target.name", "subject")
        );
        total_scores.forEach(
                (Consumer<Document>) total_score_item -> {
                    //System.out.println(total_score_item.toString());
                    Document studentRange = (Document) total_score_item.get("range");
                    //获取每个学生的班级，学校
                    //List<Range> Ranges = getClassAndSchoolRanges(scoreDatabase,studentRange.getString("id"));
                    List<Range> Ranges = getClassAndSchoolRanges(studentRange.getString("id"));
                    Target subjectTarget = doc2Target((Document) total_score_item.get("target"));
                    for (Range range : Ranges) {
                        //System.out.println(range.toString());
                        Document d = Mongo.generateId(projectId, range, subjectTarget);
                        //获取等第节点
                        Document rankLevel = (Document) total_score_item.get("rankLevel");
                        if (rankLevel != null) {
                            String dd = "";
                            if (range.getName().equals("class")) {
                                dd = rankLevel.getString("class");
                                //System.out.println("班级ID：" + range.getId() + "，班级等第-->" + dd);
                                //班级等第+1
                                scoreRankLevelCol.deleteOne(doc("_id", d));
                                UpdateResult updateResult = scoreRankLevelCol.updateOne(
                                        new Document("_id", d).append("scoreLevelMap", MongoUtils.$elemMatch("scoreLevel", dd)),
                                        new Document("$inc", new Document("scoreLevelMap.$.count", 1))
                                );

                                if (updateResult.getModifiedCount() == 0) {
                                    scoreRankLevelCol.updateOne(
                                            new Document("_id", d).append("scoreLevelMap.scoreLevel", MongoUtils.$ne(dd)),
                                            MongoUtils.$push("scoreLevelMap", new Document("scoreLevel", dd).append("count", 1)),
                                            new UpdateOptions().upsert(true)
                                    );
                                }
                            } else if (range.getName().equals("school")) {
                                dd = rankLevel.getString("school");
                                //System.out.println("学校ID：" + range.getId() + "，学校等第-->" + dd);
                                //学校等第+1
                                scoreRankLevelCol.deleteOne(doc("_id", d));
                                UpdateResult updateResult = scoreRankLevelCol.updateOne(
                                        new Document("_id", d).append("scoreLevelMap", MongoUtils.$elemMatch("scoreLevel", dd)),
                                        new Document("$inc", new Document("scoreLevelMap.$.count", 1))
                                );

                                if (updateResult.getModifiedCount() == 0) {
                                    scoreRankLevelCol.updateOne(
                                            new Document("_id", d).append("scoreLevelMap.scoreLevel", MongoUtils.$ne(dd)),
                                            MongoUtils.$push("scoreLevelMap", new Document("scoreLevel", dd).append("count", 1)),
                                            new UpdateOptions().upsert(true)
                                    );
                                }
                            }
                        }
                    }
                }
        );
    }

    //获取班级，学校信息
    private List<Range> getClassAndSchoolRanges(String studentId) {
        MongoCollection<Document> studentCol = scoreDatabase.getCollection("student_list");
        Document student = studentCol.find(new Document("student", studentId)).first();
        Range classRange = Range.clazz(student.getString("class"));
        Range schoolRange = Range.school(student.getString("school"));
        List<Range> ranges = new ArrayList<Range>();
        ranges.add(classRange);
        ranges.add(schoolRange);
        return ranges;
    }

    //获取课程信息
    private Target doc2Target(Document doc) {
        Target target = null;
        String tag = doc.getString("name");
        String id = doc.getString("id");
        switch (tag) {
            case Target.ABILITY_LEVEL:
                target = Target.abilityLevel(id);
                break;
            case Target.POINT:
                target = Target.point(id);
                break;
            case Target.PROJECT:
                target = Target.project(id);
                break;
            case Target.QUEST:
                target = Target.quest(id);
                break;
            case Target.QUEST_TYPE:
                target = Target.questType(id);
                break;
            case Target.SUBJECT:
                target = Target.subject(id);
                break;
        }
        return target;
    }

    public static void main(String[] args) {
        new RankLevelCountService().generateRankLevelHZ("FAKE_PROJECT_1");
    }
}
