package com.xz.mqreceivers.impl;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.xz.ajiaedu.common.mongo.MongoUtils;
import com.xz.bean.Range;
import com.xz.bean.Target;
import com.xz.mqreceivers.AggrTask;
import com.xz.mqreceivers.Receiver;
import com.xz.mqreceivers.ReceiverInfo;
import com.xz.services.RangeService;
import com.xz.services.StudentService;
import com.xz.services.TargetService;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author by fengye on 2016/5/27.
 */
@ReceiverInfo(taskType = "rank_position")
@Component
public class RankSegmentTask extends Receiver{

    //分段
    public static final double[] PIECE_WISE = new double[]{
            0.05, 0.1, 0.15, 0.2,
            0.25, 0.3, 0.35, 0.4,
            0.45, 0.5, 0.55, 0.6,
            0.65, 0.7, 0.75, 0.8,
            0.85, 0.9, 0.95, 1.0
    };

    @Autowired
    RangeService rangeService;

    @Autowired
    TargetService targetService;

    @Autowired
    MongoDatabase scoreDataBase;

    @Autowired
    StudentService studentService;

    @Override
    protected void runTask(AggrTask aggrTask) {
        String projectId = aggrTask.getProjectId();
        Range range = aggrTask.getRange();
        Target target = aggrTask.getTarget();
        MongoCollection<Document> scoreCol = scoreDataBase.getCollection("score_map");
        MongoCollection<Document> totalScoreCol = scoreDataBase.getCollection("total_score");
        MongoCollection<Document> rankSegmentCol = scoreDataBase.getCollection("rank_segment");
        List<String> studentIds = studentService.getStudentList(projectId, range, target);
        //1.获取分数，关联每个学生的ID，查询total_score表查询到每个学生的分数

        for(String studentId : studentIds){
            Range studentRange = Range.student(studentId);
            Document studentQuery = new Document("project", projectId).
                    append("range", studentRange).
                    append("target", target);
            Document totalScoreDoc = totalScoreCol.find(studentQuery).first();
            //获取某次班级学校的总分
            double totalScore = totalScoreDoc.getDouble("totalScore");
            //2.根据分数取得score_map表中的排名
            Document query = new Document("project", projectId).
                    append("range", range).
                    append("target", target);
            Document scoreMap = scoreCol.find(query).first();
            List<Document> scoreMapDoc = (List<Document>)scoreMap.get("scoreMap");
            int count = scoreMap.getInteger("count");
            //按照分数排序
            Collections.sort(scoreMapDoc, (Document d1, Document d2) ->{
                return d2.getDouble("score").compareTo(d1.getDouble("score"));
            });

            List<Document> rankSegments = new ArrayList<Document>();

            //排名分布率
            for(int i = 0;i < PIECE_WISE.length;i++){
                //根据总分，分段，和所有明细分数，算出每个分段内的人数占
                double countInPiece = count * PIECE_WISE[i];
                double rate = sortByScore(totalScore, scoreMapDoc, countInPiece, count);
                Document rankSegment = new Document("rankPercent", PIECE_WISE[i]).append("rate", rate);
                rankSegments.add(rankSegment);
            }

            rankSegmentCol.deleteMany(query);
            rankSegmentCol.updateOne(
                    query,
                    MongoUtils.$push("rankSegments", rankSegments),
                    MongoUtils.UPSERT
            );
        }
    }

    private double sortByScore(double totalScore, List<Document> scoreMap, double piece, int count) {
        int countInPiece = 0;
        for(Document d : scoreMap){
            countInPiece += d.getInteger("count");
            if(countInPiece >= Double.valueOf(count * piece).intValue()){
                return countInPiece / count;
            }
        }
        return countInPiece / count;
    }

}
