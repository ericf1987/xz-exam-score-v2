package com.xz.examscore.paperScreenShot.service;

import com.xz.ajiaedu.common.io.FileUtils;
import com.xz.ajiaedu.common.lang.StringUtil;
import com.xz.examscore.bean.Range;
import com.xz.examscore.bean.Target;
import com.xz.examscore.paperScreenShot.bean.*;
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

                //获取第一个客观题的高度
                double firstObjectiveHeight = getFirstObjectiveHeight(student);

                //客观题标记区域
                ObjectiveQuestZone objectiveQuestZone = getObjectiveQuestZone(projectId, student.get("studentId").toString(), subjectId, firstObjectiveHeight);

                saveOneStudentScreenShot(paperScreenShotBean, fileName, paper_positive, paper_reverse, totalScoreZone, objectiveQuestZone, rectList);
            });
        }
    }

    public double getFirstObjectiveHeight(Map<String, Object> student) {
        List<Map<String, Object>> objectiveList = (List<Map<String, Object>>) student.get("objectiveList");

        if(null != objectiveList){
            Collections.sort(objectiveList, (o1, o2) -> {
                Double qNo1 = Double.parseDouble(o1.get("questionNo").toString());
                Double qNo2 = Double.parseDouble(o2.get("questionNo").toString());
                return qNo1.compareTo(qNo2);
            });

            Map<String, Object> obj1 = objectiveList.get(0);
            List<Map<String, Object>> rects = (List<Map<String, Object>>)obj1.get("rects");
            if(null != rects){
                Map<String, Object> rect = rects.get(0);
                double coordinateY = MapUtils.getDoubleValue(rect, "coordinateY");
                return coordinateY;
            }
        }

        return 0;
    }

    private TotalScoreZone getTotalScoreZone(String projectId, String studentId, String subjectId, String schoolId, String classId) {
        double totalScore = scoreService.getScore(projectId, Range.student(studentId), Target.subject(subjectId));
        int rankInClass = rankService.getRank(projectId, Range.clazz(classId), Target.subject(subjectId), totalScore);
        int rankInSchool = rankService.getRank(projectId, Range.school(schoolId), Target.subject(subjectId), totalScore);
        int rankInProvince = rankService.getRank(projectId, Range.province(provinceService.getProjectProvince(projectId)), Target.subject(subjectId), totalScore);

        Font font = new Font("华文彩云", Font.PLAIN, 35);

        int verticalInterval = font.getSize() + 5;

        TextRect totalScoreRect = new TextRect(50, 50, "得分：" + totalScore, font);
        TextRect rankInClassRect = new TextRect(50, 50 + verticalInterval, "班级排名：" + rankInClass, font);
        TextRect rankInSchoolRect = new TextRect(50, 50 + verticalInterval * 2, "学校排名：" + rankInSchool, font);
        TextRect rankInProvinceRect = new TextRect(50, 50 + verticalInterval * 3, "总体排名：" + rankInProvince, font);

        List<TextRect> rectList = new LinkedList<>();
        rectList.add(totalScoreRect);
        rectList.add(rankInClassRect);
        rectList.add(rankInSchoolRect);
        rectList.add(rankInProvinceRect);

        return new TotalScoreZone(100, 100, totalScore, rectList);
    }

    private ObjectiveQuestZone getObjectiveQuestZone(String projectId, String studentId, String subjectId, double firstObjectiveHeight) {
        long correctCount = scoreService.getQuestCorrectCount(projectId, studentId, subjectId, true);
        long totalCount = scoreService.getStudentSubjectScoresCount(projectId, studentId, subjectId, true);
        ObjectiveQuestZone objectiveQuestZone = new ObjectiveQuestZone();
        objectiveQuestZone.setCoordinateX(50);
        objectiveQuestZone.setCoordinateY(firstObjectiveHeight);
        objectiveQuestZone.setTotalCount((int)totalCount);
        objectiveQuestZone.setCorrectCount((int)correctCount);
        List<String> errorQuestNo = scoreService.getErrorQuestNo(projectId, studentId, subjectId, true, false);
        Collections.sort(errorQuestNo);
        objectiveQuestZone.setErrorQuestList(errorQuestNo);
        return objectiveQuestZone;
    }

    /**
     * 保存单个学生的试卷留痕截图文件
     *
     * @param paperScreenShotBean 试卷截图对象
     * @param fileName            文件名
     * @param paper_positive      正面URL地址
     * @param paper_reverse       反面URL地址
     * @param totalScoreZone      总分区域
     * @param objectiveQuestZone  客观题区域
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
     * @param totalScoreZone     总分区域
     * @param objectiveQuestZone 客观题区域
     * @param rects              图片切图区域列表
     * @param path               保存路径
     * @param paper_positive     正面URL地址
     * @param paper_reverse      反面URL地址
     */
    public void paintPaper(TotalScoreZone totalScoreZone, ObjectiveQuestZone objectiveQuestZone, List<Rect> rects, String path, String paper_positive, String paper_reverse) {
        //将正反面截图读取到内存中
        BufferedImage img_positive = PaintUtils.loadImageUrl(paper_positive);
        BufferedImage img_reverse = PaintUtils.loadImageUrl(paper_reverse);

        //标记总分区域
        if (totalScoreZone != null) {
            img_positive = paintTotalScoreZone(img_positive, totalScoreZone);
        }

        //标记客观题区域
        if (objectiveQuestZone != null) {
            img_positive = paintObjectiveQuestZone(img_positive, objectiveQuestZone);
        }

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

        Optional<TotalScoreZone> optional = Optional.of(totalScoreZone);

        if (optional.isPresent()) {
            for (TextRect textRect : optional.get().getTextRects()) {
                Font font = textRect.getFont();
                float coordinateX = textRect.getCoordinateX();
                float coordinateY = textRect.getCoordinateY();
                String textContent = textRect.getTextContent();
                img_positive = PaintUtils.modifyImage(img_positive, textContent, font, coordinateX, coordinateY + font.getSize());
            }
        }

        return img_positive;
    }

    private BufferedImage paintObjectiveQuestZone(BufferedImage img_positive, ObjectiveQuestZone objectiveQuestZone) {
        Font font = new Font("华文彩云", Font.BOLD, 50);
        String correctDecs = objectiveQuestZone.getCorrectDecs(objectiveQuestZone);
        double coordinateX = objectiveQuestZone.getCoordinateX();
        double coordinateY = objectiveQuestZone.getCoordinateY() - font.getSize() - 10;
        String errorDesc = objectiveQuestZone.getErrorDesc(objectiveQuestZone);
        return PaintUtils.modifyImage(img_positive, correctDecs + ", " + errorDesc, font, (float) coordinateX, (float) coordinateY);
    }

    /**
     * 生成试卷截图文件名
     *
     * @param path   保存路径
     * @param b      正面/反面
     * @param suffix 扩展名
     * @return 截图文件名
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
