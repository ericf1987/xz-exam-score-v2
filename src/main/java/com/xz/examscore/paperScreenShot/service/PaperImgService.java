package com.xz.examscore.paperScreenShot.service;

import com.xz.ajiaedu.common.lang.CollectionUtils;
import com.xz.ajiaedu.common.lang.StringUtil;
import com.xz.examscore.paperScreenShot.bean.*;
import com.xz.examscore.paperScreenShot.utils.PaintUtils;
import com.xz.examscore.scanner.ScannerDBService;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.*;

/**
 * @author by fengye on 2017/5/18.
 */
@Service
public class PaperImgService {

    static final Logger LOG = LoggerFactory.getLogger(PaperImgService.class);

    @Autowired
    PaintService paintService;

    @Autowired
    PaperScreenShotService paperScreenShotService;

    @Autowired
    ScannerDBService scannerDBService;

    public static final String BASE64_HEADER = "data:image/png;base64,";

    /**
     * 生成一个班级一个科目的试卷图片列表
     *
     * @param projectId      考试项目ID
     * @param schoolId       学校ID
     * @param classId        班级ID
     * @param subjectId      科目ID
     * @param subjectRuleMap 科目试卷截图参数显示配置规则
     * @return 试卷图片列表
     */
    public List<Map<String, Object>> generateOneClassOneSubject(String projectId, String schoolId, String classId, String subjectId, Map<String, Object> subjectRuleMap) {
        PaperScreenShotBean paperScreenShotBean = paperScreenShotService.packScreenShotTaskBean(projectId, schoolId, classId, subjectId, "");
        return generatePaperZone(paperScreenShotBean, subjectRuleMap);
    }

    /**
     * @param paperScreenShotBean 试卷截图对象
     * @param subjectRuleMap      科目区域显示规则
     * @return 返回结果
     */
    public List<Map<String, Object>> generatePaperZone(PaperScreenShotBean paperScreenShotBean, Map<String, Object> subjectRuleMap) {
        String projectId = paperScreenShotBean.getProjectId();
        String schoolId = paperScreenShotBean.getSchoolId();
        String classId = paperScreenShotBean.getClassId();
        String subjectId = paperScreenShotBean.getSubjectId();

        //排名显示规则配置
        Map<String, Object> rankRuleMap = MapUtils.isEmpty(subjectRuleMap) ?
                Collections.emptyMap() : (Map<String, Object>) subjectRuleMap.get(subjectId);

        //学生试卷截图参数列表
        List<Map<String, Object>> studentCardSlices = paperScreenShotBean.getStudentCardSlices();

        List<Map<String, Object>> paperZoneData = new ArrayList<>();
        if (studentCardSlices.isEmpty()) {
            LOG.info("学生试卷留痕为空！项目{}，学校{}，班级{}", paperScreenShotBean.getProjectId(), paperScreenShotBean.getSchoolId(), paperScreenShotBean.getClassId());
        } else {
            studentCardSlices.forEach(student -> {
                GetPaperZone getPaperZone = new GetPaperZone(projectId, schoolId, classId, subjectId, rankRuleMap, student).invoke();
                String paper_positive = getPaperZone.getPaper_positive();
                PaperZone paperZone = getPaperZone.getPaperZone();
                String paper_reverse = getPaperZone.getPaper_reverse();

                String studentId = getPaperZone.getStudentId();
                BufferedImage positive_page = getOnePage(paper_positive, paperZone);
                BufferedImage reverse_page = getOnePage(paper_reverse, paperZone);

                Map<String, Object> map = new HashMap<>();
                map.put(studentId + "_positive", convertImgToString(positive_page, "png"));
                map.put(studentId + "_reverse", convertImgToString(reverse_page, "png"));
                paperZoneData.add(map);
            });
        }
        return paperZoneData;
    }

    /**
     * 获取一个学生的单张截图编码成字符串
     *
     * @param projectId      考试ID
     * @param schoolId       学校ID
     * @param classId        班级ID
     * @param subjectId      科目ID
     * @param studentId      学生ID
     * @param isPositive     正面或反面
     * @param subjectRuleMap 显示规则
     * @return
     */
    public String getOneStuOnePage(String projectId, String schoolId, String classId, String subjectId, String studentId, boolean isPositive,
                                   Map<String, Object> subjectRuleMap) {
        Map<String, Object> studentCardSlices = scannerDBService.getStudentCardSlices(projectId, subjectId, studentId);

        BufferedImage img = getBufferedImage(projectId, schoolId, classId, subjectId, isPositive, subjectRuleMap, studentCardSlices);

        return convertImgToString(img, PaintUtils.PNG);
    }

    public BufferedImage getBufferedImage(String projectId, String schoolId, String classId, String subjectId, boolean isPositive, Map<String, Object> subjectRuleMap, Map<String, Object> studentCardSlices) {
        GetPaperZone getPaperZone = new GetPaperZone(projectId, schoolId, classId, subjectId, subjectRuleMap, studentCardSlices).invoke();
        String paper_positive = getPaperZone.getPaper_positive();
        PaperZone paperZone = getPaperZone.getPaperZone();
        String paper_reverse = getPaperZone.getPaper_reverse();

        return getOnePage(isPositive ? paper_positive : paper_reverse, paperZone);
    }

    /**
     * 获取单页试卷的图片对象
     *
     * @param imgUrl    图片URL
     * @param paperZone 试卷区域对象
     * @return 图片对象
     */
    public BufferedImage getOnePage(String imgUrl, PaperZone paperZone) {

        if (StringUtil.isBlank(imgUrl)) {
            LOG.error("图片URL不能为空！");
            return null;
        }

        try {
            BufferedImage bufferedImage = PaintUtils.loadImageUrl(imgUrl);

            if (null == paperZone) {
                LOG.info("无试卷截图区域坐标参数，返回原卷截图！");
                return bufferedImage;
            }

            TotalScoreZone totalScoreZone = paperZone.getTotalScoreZone();
            ObjectiveQuestZone objectiveQuestZone = paperZone.getObjectiveQuestZone();
            List<SubjectiveQuestZone> subjectiveQuestZones = paperZone.getSubjectiveQuestZones();

            //绘制总分区域留痕
            if (null != totalScoreZone) {
                bufferedImage = paintService.paintTotalScoreZone(bufferedImage, totalScoreZone);
            }

            //绘制客观题区域留痕
            if (null != objectiveQuestZone) {
                bufferedImage = paintService.paintObjectiveQuestZone(bufferedImage, objectiveQuestZone);
            }

            //绘制主观题区域留痕
            if (CollectionUtils.isEmpty(subjectiveQuestZones)) {
                for (SubjectiveQuestZone zone : subjectiveQuestZones) {
                    bufferedImage = paintService.doPaint(bufferedImage, zone);
                }
            }

            return bufferedImage;

        } catch (Exception e) {
            LOG.error("获取网络图片URL失败！URL:{}", imgUrl);
            return null;
        }

    }

    /**
     * 将图片转化为字符串
     *
     * @param bufferedImage 图片对象
     * @param formatName    图片格式类型
     * @return 返回结果
     */
    public String convertImgToString(BufferedImage bufferedImage, String formatName) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();

        try {
            ImageIO.write(bufferedImage, formatName, stream);
            byte[] bytes = stream.toByteArray();
//            BASE64Encoder encoder = new BASE64Encoder();
            Base64.Encoder encoder = Base64.getEncoder();
            return BASE64_HEADER + encoder.encodeToString(bytes);
        } catch (IOException e) {
            LOG.error("图片转化为字符串出现异常，图片格式为：{}", formatName);
            e.printStackTrace();
            return "";
        }
    }

    private class GetPaperZone {
        private String projectId;
        private String schoolId;
        private String classId;
        private String subjectId;
        private Map<String, Object> rankRuleMap;
        private Map<String, Object> student;
        private String studentId;
        private String paper_positive;
        private String paper_reverse;
        private PaperZone paperZone;

        public GetPaperZone(String projectId, String schoolId, String classId, String subjectId, Map<String, Object> rankRuleMap, Map<String, Object> student) {
            this.projectId = projectId;
            this.schoolId = schoolId;
            this.classId = classId;
            this.subjectId = subjectId;
            this.rankRuleMap = rankRuleMap;
            this.student = student;
        }

        public String getStudentId() {
            return studentId;
        }

        public String getPaper_positive() {
            return paper_positive;
        }

        public String getPaper_reverse() {
            return paper_reverse;
        }

        public PaperZone getPaperZone() {
            return paperZone;
        }

        public GetPaperZone invoke() {
            List<Map<String, Object>> subjectiveList = (List<Map<String, Object>>) student.get("subjectiveList");
            studentId = MapUtils.getString(student, "studentId");
            //正面试卷截图
            paper_positive = MapUtils.getString(student, "paper_positive");
            //反面试卷截图
            paper_reverse = MapUtils.getString(student, "paper_reverse");

            //获取第一个客观题的高度
            double firstObjectiveHeight = paintService.getFirstObjectiveHeight(student);

            //获取第一个主观题的宽度
            double firstSubjectiveWidth = paintService.getFirstSubjectiveWidth(student);
            //总分标记区域
            TotalScoreZone totalScoreZone = paintService.getTotalScoreZone(projectId, student.get("studentId").toString(), subjectId, schoolId,
                    classId, firstSubjectiveWidth, rankRuleMap);

            //客观题标记区域
            ObjectiveQuestZone objectiveQuestZone = paintService.getObjectiveQuestZone(projectId, student.get("studentId").toString(), subjectId, schoolId,
                    classId, firstObjectiveHeight, firstSubjectiveWidth, rankRuleMap);

            //主观题区域
            List<SubjectiveQuestZone> subjectiveQuestZoneList = paintService.getSubjectiveQuestZones(projectId, schoolId, classId, subjectId, rankRuleMap, subjectiveList, paper_positive, paper_reverse);

            //试卷区域对象
            paperZone = new PaperZone(totalScoreZone, objectiveQuestZone, subjectiveQuestZoneList);
            return this;
        }
    }
}
