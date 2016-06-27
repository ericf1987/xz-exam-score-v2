package com.xz.mqreceivers.impl;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.xz.bean.Range;
import com.xz.bean.Target;
import com.xz.mqreceivers.AggrTask;
import com.xz.mqreceivers.Receiver;
import com.xz.mqreceivers.ReceiverInfo;
import com.xz.services.ClassService;
import com.xz.services.RankService;
import com.xz.services.SchoolService;
import com.xz.services.StudentService;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.xz.ajiaedu.common.mongo.MongoUtils.$set;
import static com.xz.ajiaedu.common.mongo.MongoUtils.UPSERT;
import static com.xz.util.Mongo.query;

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

        List<Document> docs;

        String rangeName = range.getName();

        //学校范围或联考范围判断
        if (rangeName.equals("school")) {
            docs = classService.listClasses(projectId, range.getId());
        } else {
            docs = schoolService.getProjectSchools(projectId, range.getId());
        }

        for (Document doc : docs) {
            //查询出每个班级在每个分段中的学生数，并算出占总班级的比率
            String id;
            Range currentRange;
            if (rangeName.equals("school")) {
                id = doc.getString("class");
                currentRange = Range.clazz(id);
            } else {
                id = doc.getString("school");
                currentRange = Range.school(id);
            }

            Map<String, List<Document>> resultMap =
                    generateSectionRate(rangeCount, currentRange, range, projectId, target);

            if (resultMap != null && !resultMap.isEmpty()) {
                rankSegmentCol.updateMany(
                        query(projectId, currentRange, target),
                        $set("rankSegments", resultMap.get("rankSegments")),
                        UPSERT
                );
            }
        }

    }

    private Map<String, List<Document>> generateSectionRate(
            int rangeCount, Range currentRange, Range range, String projectId, Target target) {

        Map<String, List<Document>> sectionRate = new LinkedHashMap<>();
        List<String> studentIds = studentService.getStudentIds(projectId, currentRange, target);
        int studentCount = studentIds.size();

        if (studentCount == 0) {
            return null;
        }

        List<Document> rankSegmentMap = listBySection(PIECE_WISE);

        for (String studentId : studentIds) {
            int rank = rankService.getRank(projectId, range, target, studentId);
            double section = getSection(rank, rangeCount, PIECE_WISE);

            //对每个分段的人数进行累加
            addCount(section, rankSegmentMap);
        }

        //计算出每个分段的人数占总数的比率
        calculateRates(studentCount, rankSegmentMap);
        sectionRate.put("rankSegments", rankSegmentMap);
        return sectionRate;
    }

    private List<Document> listBySection(double[] pieceWise) {
        List<Document> items = new ArrayList<>();
        for (double percent : pieceWise) {
            items.add(new Document("rankPercent", percent).append("rate", 0).append("count", 0));
        }
        return items;
    }

    private void calculateRates(int classCount, List<Document> docs) {
        for (Document doc : docs) {
            //将addCount中统计的人数添加到count节点
            doc.put("count", doc.getInteger("rate"));
            if (doc.getInteger("rate") != null) {
                int num = doc.getInteger("rate");
                doc.put("rate", (double) num / (double) classCount);
            } else {
                doc.put("rate", 0);
            }
        }
    }

    private void addCount(double section, List<Document> docs) {
        docs.stream()
                .filter(doc -> doc.getDouble("rankPercent").equals(section))
                .forEach(doc -> {
                    int num = doc.getInteger("rate");
                    doc.put("rate", num + 1);
                });
    }

    private double getSection(int rank, int schoolCount, double[] PIECE_WISE) {
        double rate = (double) rank / (double) schoolCount;

        for (double aPIECE_WISE : PIECE_WISE) {
            if (rate <= aPIECE_WISE) {
                return aPIECE_WISE;
            }
        }
        return 0;
    }


}
