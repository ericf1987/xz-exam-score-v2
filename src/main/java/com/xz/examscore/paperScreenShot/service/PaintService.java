package com.xz.examscore.paperScreenShot.service;

import com.xz.ajiaedu.common.io.FileUtils;
import com.xz.ajiaedu.common.lang.StringUtil;
import com.xz.examscore.bean.Range;
import com.xz.examscore.paperScreenShot.bean.ObjectiveQuestZone;
import com.xz.examscore.paperScreenShot.bean.PaperScreenShotBean;
import com.xz.examscore.paperScreenShot.bean.Rect;
import com.xz.examscore.paperScreenShot.bean.TotalScoreZone;
import com.xz.examscore.paperScreenShot.utils.PaintUtils;
import com.xz.examscore.services.ProvinceService;
import com.xz.examscore.services.RankService;
import com.xz.examscore.services.ScoreService;
import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.*;
import java.util.List;

/**
 * @author by fengye on 2017/3/1.
 */
@Service
public class PaintService {

    static final Logger LOG = LoggerFactory.getLogger(PaintService.class);

    @Value("${paper.screenshot.savepath}")
    private String paperScreenShotSavePath;

    @Autowired
    ScoreService scoreService;

    @Autowired
    RankService rankService;

    @Autowired
    ProvinceService provinceService;

    /**
     * 保存试卷截图对象到本地文件系统
     *
     * @param paperScreenShotBean 试卷截图对象
     */
    public void saveScreenShot(PaperScreenShotBean paperScreenShotBean) {
        String projectId = paperScreenShotBean.getProjectId();
        String schoolId = paperScreenShotBean.getSchoolId();
        String classId = paperScreenShotBean.getClassId();
        String subjectId = paperScreenShotBean.getSubjectId();

        List<Map<String, Object>> studentCardSlices = paperScreenShotBean.getStudentCardSlices();
        if (studentCardSlices.isEmpty()) {
            LOG.info("学生试卷留痕为空！项目{}，学校{}，班级{}", paperScreenShotBean.getProjectId(), paperScreenShotBean.getSchoolId(), paperScreenShotBean.getClassId());
        } else {
            studentCardSlices.forEach(student -> {
                List<Map<String, Object>> subjectiveList = (List<Map<String, Object>>) student.get("subjectiveList");
                String fileName = MapUtils.getString(student, "studentId");
                //正面试卷截图
                String paper_positive = MapUtils.getString(student, "paper_positive");
                //反面试卷截图
                String paper_reverse = MapUtils.getString(student, "paper_reverse");
                List<Rect> rectList = new ArrayList<>();
                subjectiveList.forEach(subjective -> {
                    //题号
                    String questNo = MapUtils.getString(subjective, "questionNo");
                    //获取主观题每个题目的坐标信息
                    List<Rect> rects = convertToRectsObj(subjective, paper_positive, paper_reverse, questNo);
                    rectList.add(rects.get(0));
                });

                //总分标记区域
                TotalScoreZone totalScoreZone = getTotalScoreZone(projectId, student.get("studentId").toString(), subjectId, schoolId, classId);

                //客观题标记区域
                ObjectiveQuestZone objectiveQuestZone = getObjectiveQuestZone(projectId, student.get("studentId").toString(), subjectId, schoolId, classId);

                saveOneStudentScreenShot(paperScreenShotBean, fileName, paper_positive, paper_reverse, totalScoreZone, objectiveQuestZone, rectList);
            });
        }
    }

    private TotalScoreZone getTotalScoreZone(String projectId, String studentId, String subjectId, String schoolId, String classId) {
/*        double totalScore = scoreService.getScore(projectId, Range.student(studentId), Target.subject(subjectId));
        int rankInClass = rankService.getRank(projectId, Range.clazz(classId), Target.subject(subjectId), totalScore);
        int rankInSchool = rankService.getRank(projectId, Range.school(schoolId), Target.subject(subjectId), totalScore);
        int rankInProvince = rankService.getRank(projectId, Range.province(provinceService.getProjectProvince(projectId)), Target.subject(subjectId), totalScore);*/
        Map<String, Integer> rankMap = new HashMap<>();
        rankMap.put(Range.CLASS, 30);
        rankMap.put(Range.SCHOOL, 40);
        rankMap.put(Range.PROVINCE, 50);
        return new TotalScoreZone(100, 100, 100, rankMap);
    }

    private ObjectiveQuestZone getObjectiveQuestZone(String projectId, String studentId, String subjectId, String schoolId, String classId) {
/*        FindIterable<Document> scores = scoreService.getStudentSubjectScores(projectId, studentId, subjectId);
        long correctCount = scoreService.getObjectiveCorrectCount(projectId, studentId, subjectId, true);
        long totalCount = scoreService.getStudentSubjectScoresCount(projectId, studentId, subjectId);*/
        ObjectiveQuestZone objectiveQuestZone = new ObjectiveQuestZone();
        objectiveQuestZone.setCoordinateX(150);
        objectiveQuestZone.setCoordinateY(400);
        objectiveQuestZone.setTotalCount(30);
        objectiveQuestZone.setCorrectCount(15);
        objectiveQuestZone.setErrorQuestList(Arrays.asList("1", "2", "3"));
        return objectiveQuestZone;
    }

    /**
     * 保存单个学生的试卷留痕截图文件
     *
     * @param paperScreenShotBean 试卷截图对象
     * @param fileName            文件名
     * @param paper_positive      正面URL地址
     * @param paper_reverse       反面URL地址
     * @param totalScoreZone
     * @param objectiveQuestZone
     * @param rectList            切图列表
     */
    public void saveOneStudentScreenShot(PaperScreenShotBean paperScreenShotBean, String fileName, String paper_positive, String paper_reverse,
                                         TotalScoreZone totalScoreZone, ObjectiveQuestZone objectiveQuestZone, List<Rect> rectList) {
        String directory = StringUtil.joinPaths(paperScreenShotSavePath,
                getScreenShotFilePath(paperScreenShotBean));
        try {
            FileUtils.getOrCreateDir(directory);
        } catch (IOException e) {
            LOG.error("生成试卷留痕截图目录失败！");
            return;
        }
        String filePath = StringUtil.joinPaths(directory, fileName);
        paintPaper(totalScoreZone, objectiveQuestZone, rectList, filePath, paper_positive, paper_reverse);
    }

    /**
     * 将图片修改后保存
     *
     * @param totalScoreZone
     * @param objectiveQuestZone
     * @param rects              图片切图区域列表
     * @param path               保存路径
     * @param paper_positive     正面URL地址
     * @param paper_reverse      反面URL地址
     */
    public void paintPaper(TotalScoreZone totalScoreZone, ObjectiveQuestZone objectiveQuestZone, List<Rect> rects, String path, String paper_positive, String paper_reverse) {
        //将正反面截图读取到内存中
        BufferedImage img_positive = PaintUtils.loadImageUrl(paper_positive);
        BufferedImage img_reverse = PaintUtils.loadImageUrl(paper_reverse);

/*        //标记总分区域
        if (totalScoreZone != null) {
            img_positive = paintTotalScoreZone(img_positive, totalScoreZone);
        }

        //标记客观题区域
        if (objectiveQuestZone != null) {
            img_positive = paintObjectiveQuestZone(img_positive, objectiveQuestZone);
        }*/

        for (Rect rect : rects) {
            int pageIndex = rect.getPageIndex();
            if (pageIndex == 0) {
                img_positive = doPaint(img_positive, rect);
            } else {
                img_reverse = doPaint(img_reverse, rect);
            }
        }
        //保存正面
        PaintUtils.writeImageLocal(renderSuffixByIndex(path, true, PaintUtils.SCREEN_SHOT_SUFFIX_PNG), img_positive, PaintUtils.PNG);
        //保存反面
        PaintUtils.writeImageLocal(renderSuffixByIndex(path, false, PaintUtils.SCREEN_SHOT_SUFFIX_PNG), img_reverse, PaintUtils.PNG);
    }

    private BufferedImage paintTotalScoreZone(BufferedImage img_positive, TotalScoreZone totalScoreZone) {
        Font font = new Font("华文彩云", Font.BOLD, 40);
        double totalScore = totalScoreZone.getTotalScore();
        double coordinateX = totalScoreZone.getCoordinateX();
        double coordinateY = totalScoreZone.getCoordinateY();
        String rankDesc = totalScoreZone.getRankDesc(totalScoreZone);
        return PaintUtils.modifyImage(img_positive, "得分：" + totalScore + ", " + rankDesc, font, (float) coordinateX, (float) coordinateY + font.getSize());
    }

    private BufferedImage paintObjectiveQuestZone(BufferedImage img_positive, ObjectiveQuestZone objectiveQuestZone) {
        Font font = new Font("华文彩云", Font.BOLD, 50);
        String correctDecs = objectiveQuestZone.getCorrectDecs(objectiveQuestZone);
        double coordinateX = objectiveQuestZone.getCoordinateX();
        double coordinateY = objectiveQuestZone.getCoordinateY();
        String errorDesc = objectiveQuestZone.getErrorDesc(objectiveQuestZone);
        return PaintUtils.modifyImage(img_positive, correctDecs + ", " + errorDesc, font, (float) coordinateX, (float) coordinateY);
    }

    /**
     * 生成试卷截图文件名
     *
     * @param path   保存路径
     * @param b      正面/反面
     * @param suffix 扩展名
     * @return
     */
    private String renderSuffixByIndex(String path, boolean b, String suffix) {
        return b ? path + "_positive" + suffix : path + "_reverse" + suffix;
    }

    private BufferedImage doPaint(BufferedImage bufferedImage, Rect rect) {
        Font font = new Font("宋体", Font.PLAIN, 40);
        String content = "题号：" + rect.getQuestNo() + ", 得分" + rect.getScore() + "分， 满分（" + rect.getFullScore() + ")";
        return PaintUtils.modifyImage(bufferedImage, content, font,
                (float) (rect.getCoordinateX()),
                (float) (rect.getCoordinateY()));
    }

    /**
     * 将作答区域封装成rect对象
     */
    private List<Rect> convertToRectsObj(Map<String, Object> subjective, String paper_positive, String paper_reverse, String questionNo) {
        List<Map<String, Object>> rects = (List<Map<String, Object>>) subjective.get("rects");
        List<Rect> list = new ArrayList<>();
        for (Map<String, Object> rect : rects) {
            Rect r = new Rect();
            r.setQuestNo(questionNo);
            r.setCoordinateX(MapUtils.getDouble(rect, "coordinateX"));
            r.setCoordinateY(MapUtils.getDouble(rect, "coordinateY"));
            r.setWidth(MapUtils.getDouble(rect, "width"));
            r.setHeight(MapUtils.getDouble(rect, "height"));
            r.setFullScore(MapUtils.getDouble(subjective, "fullScore"));
            r.setScore(MapUtils.getDouble(subjective, "score"));
            r.setPaper_positive(paper_positive);
            r.setPaper_reverse(paper_reverse);
            r.setPageIndex(MapUtils.getInteger(rect, "pageIndex"));
            list.add(r);
        }
        return list;
    }

    /**
     * 根据试卷截图对象生成保存路径
     *
     * @param paperScreenShotBean 试卷截图对象
     * @return 保存路径
     */
    private String getScreenShotFilePath(PaperScreenShotBean paperScreenShotBean) {
        if (null != paperScreenShotBean) {
            return StringUtil.joinPaths(paperScreenShotBean.getProjectId(),
                    paperScreenShotBean.getSchoolId(), paperScreenShotBean.getClassId(),
                    paperScreenShotBean.getSubjectId());
        }
        return "";
    }

    /**
     * 查询操作系统可用字体
     */
    public List<String> getAvailableFontFamilyNames() {
        List<String> fonts = new ArrayList<>();
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        String[] availableFontFamilyNames = ge.getAvailableFontFamilyNames();
        for (String name : availableFontFamilyNames) {
            LOG.info("可用字体有：{}", name);
            fonts.add(name);
        }
        return fonts;
    }
}
