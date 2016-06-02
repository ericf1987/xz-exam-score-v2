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
import com.xz.services.SchoolService;
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

    @Autowired
    SchoolService schoolService;

    @Override
    protected void runTask(AggrTask aggrTask) {
        //1.获取该学校下面的所有班级
        //2.查询每个班级的的所有学生
        //3.家算出学生在学校的排名和排名段
        //4.计算出对应分段中学生学校排名人数/班级人数
        String projectId = aggrTask.getProjectId();
        Range range = aggrTask.getRange();
        Target target = aggrTask.getTarget();

        MongoCollection<Document> rankSegmentCol = scoreDataBase.getCollection("rank_segment");

        //计算当前范围的人数
        int rangeCount = studentService.getStudentCount(projectId, range, target);

        List<Document> docs = new ArrayList<Document>();

        String rangeName = range.getName();

        //学校范围或联考范围判断
        if(rangeName.equals("school")){
            docs = classService.listClasses(projectId, range.getId());
        }else{
            docs = schoolService.getProjectSchools(projectId, range.getId());
        }

        for (Document doc : docs) {
            //查询出每个班级在每个分段中的学生数，并算出占总班级的比率
            String id = "";
            Range currentRange;
            if(rangeName.equals("school")){
                id = doc.getString("class");
                currentRange = Range.clazz(id);
            }else{
                id = doc.getString("school");
                currentRange = Range.school(id);
            }

            Map<String, List<Document>> resultMap = generateSectionRate(
                    rangeCount, currentRange,
                    range, projectId, target
            );

            Document condition = new Document("project", projectId).
                    append("range", Mongo.range2Doc(currentRange)).
                    append("target", Mongo.target2Doc(target));
            rankSegmentCol.deleteMany(condition);
            rankSegmentCol.updateMany(
                    condition,
                    MongoUtils.$set("rankSegments", resultMap.get("rankSegments")),
                    MongoUtils.UPSERT
            );
        }

    }

    private Map<String, List<Document>> generateSectionRate(int rangeCount, Range currentRange, Range range, String projectId, Target target) {
        Map<String, List<Document>> classSectionRate = new LinkedHashMap<String, List<Document>>();
        List<String> studentIds = studentService.getStudentList(projectId, currentRange, target);
        int size = studentIds.size();
        List<Document> docs = listBySection(PIECE_WISE, size);
        for (String studentId : studentIds) {
            //获取学生在当前范围的排名
            int rank = rankService.getRank(projectId, range, target, studentId);
            //获取该排名所在的分段
            double section = getSection(rank, rangeCount, PIECE_WISE);
            //对每个分段的人数进行累加
            addCount(section, docs);
        }
        //计算出每个分段的人数占总数的比率
        calculateRates(size, docs);
        classSectionRate.put("rankSegments", docs);
        return classSectionRate;
    }

    private List<Document> listBySection(double[] pieceWise, int size) {
        List<Document> items = new ArrayList<Document>();
        for (int i = 0; i < pieceWise.length; i++) {
            items.add(new Document("rankPercent", pieceWise[i]).append("rate", 0).append("count", 0));
        }
        return items;
    }

    private void calculateRates(int classCount, List<Document> docs) {
        for (Document doc : docs) {
            //将addCount中统计的人数添加到count节点
            doc.put("count", doc.getInteger("rate"));
            if (doc.getInteger("rate") != null) {
                int num = doc.getInteger("rate");
                doc.put("rate", Double.valueOf(num) / Double.valueOf(classCount));
            } else {
                doc.put("rate", 0);
            }
        }
    }

    private void addCount(double section, List<Document> docs) {
        for (Document doc : docs) {
            if (doc.getDouble("rankPercent").equals(section)) {
                int num = doc.getInteger("rate");
                doc.put("rate", num + 1);
            }
        }
    }

    private double getSection(int rank, int schoolCount, double[] PIECE_WISE) {
        double rate = Double.valueOf(rank) / Double.valueOf(schoolCount);
        for (int i = 0; i < PIECE_WISE.length; i++) {
            if (rate > PIECE_WISE[i]) {
                continue;
            } else {
                return PIECE_WISE[i];
            }
        }
        return 0;
    }


}
