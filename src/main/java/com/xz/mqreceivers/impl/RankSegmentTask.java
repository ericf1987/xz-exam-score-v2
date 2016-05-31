package com.xz.mqreceivers.impl;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.xz.ajiaedu.common.mongo.MongoUtils;
import com.xz.bean.Range;
import com.xz.bean.Target;
import com.xz.mqreceivers.AggrTask;
import com.xz.mqreceivers.Receiver;
import com.xz.mqreceivers.ReceiverInfo;
import com.xz.services.ClassService;
import com.xz.services.RankService;
import com.xz.services.StudentService;
import com.xz.util.Mongo;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author by fengye on 2016/5/27.
 */
@ReceiverInfo(taskType = "rank_segment")
@Component
public class RankSegmentTask extends Receiver {

    //分段
    public static final double[] PIECE_WISE = new double[]{
            0.05, 0.1, 0.15, 0.2,
            0.25, 0.3, 0.35, 0.4,
            0.45, 0.5, 0.55, 0.6,
            0.65, 0.7, 0.75, 0.8,
            0.85, 0.9, 0.95, 1.0
    };

    @Autowired
    MongoDatabase scoreDataBase;

    @Autowired
    StudentService studentService;

    @Autowired
    RankService rankService;

    @Autowired
    ClassService classService;

    @Override
    protected void runTask(AggrTask aggrTask) {
        //1.获取该学校下面的所有班级
        //2.查询每个班级的的所有学生
        //3.家算出学生在学校的排名和排名段
        //4.在该学生班级和排名段单元格数据+1
        String projectId = aggrTask.getProjectId();
        Range range = aggrTask.getRange();
        Target target = aggrTask.getTarget();

        MongoCollection<Document> scoreMapCol = scoreDataBase.getCollection("score_map");
        MongoCollection<Document> rankSegmentCol = scoreDataBase.getCollection("rank_segment");
        List<Document> classDocs = classService.listClasses(projectId, range.getId());
        //System.out.println(classDocs);
        for (Document doc : classDocs) {
            //查询出每个班级在每个分段中的学生数，并算出占总班级的比率
            String classId = doc.getString("class");

            //查询出班级的总人数
            Document query = new Document("project", projectId).
                    append("range", Mongo.range2Doc(Range.clazz(classId))).
                    append("target", Mongo.target2Doc(target));
            int count = scoreMapCol.find(query).first().getInteger("count");
            Map<String, List<Document>> resultMap = generateSectionRate(count, Range.clazz(classId), range, projectId, target);

            Document condition = new Document("project", projectId).
                    append("range", Mongo.range2Doc(Range.clazz(classId))).
                    append("target", Mongo.target2Doc(target));
            rankSegmentCol.deleteMany(condition);
            rankSegmentCol.updateMany(
                    condition,
                    MongoUtils.$set("rankSegments", resultMap.get("rankSegments")),
                    MongoUtils.UPSERT
            );
        }

    }

    private Map<String, List<Document>> generateSectionRate(int count, Range range, Range schoolRange, String projectId, Target target) {
        List<Document> docs = listBySection(PIECE_WISE);
        Map<String, List<Document>> classSectionRate = new LinkedHashMap<String, List<Document>>();
        List<String> studentIds = studentService.getStudentList(projectId, range, target);
        for(String studentId : studentIds){
            //获取学生在学校的排名
            int rank = rankService.getRank(projectId, schoolRange, target, studentId);
            double section = getSection(rank, count, PIECE_WISE);
            //System.out.println("当前学生的排名段-->" + section);
            //对每个分段的人数进行累加
            addCount(section, docs);
        }
        //计算出每个分段的人数占班级总数的比率
        calculateRates(count, docs);
        classSectionRate.put("rankSegments", docs);
        return classSectionRate;
    }

    private List<Document> listBySection(double[] pieceWise) {
        List<Document> items = new ArrayList<Document>();
        for(int i = 0; i < pieceWise.length; i++){
            items.add(new Document("rankPercent", pieceWise[i]));
        }
        return items;
    }

    private void calculateRates(int count, List<Document> docs) {
        for(Document doc : docs){
            if(doc.getInteger("rate") != null){
                int num = doc.getInteger("rate");
                doc.put("rate", Double.valueOf(num) / Double.valueOf(count));
            }else{
                doc.put("rate", 0);
            }
        }
    }

    private void addCount(double section, List<Document> docs) {
        for(Document doc : docs){
            if(doc.getDouble("rankPercent") == section){
                if(doc.getInteger("rate") != null){
                    int num = doc.getInteger("rate");
                    doc.put("rate", num + 1);
                }else{
                    doc.append("rate", 0);
                }
            }
        }
    }

    private double getSection(int rank, int count, double[] PIECE_WISE){
        double rate = Double.valueOf(rank) / Double.valueOf(count);
        for(int i = 0; i < PIECE_WISE.length; i++){
            if(rate > PIECE_WISE[i]){
                continue;
            }else{
                return PIECE_WISE[i];
            }
        }
        return 0;
    }


}
