package com.xz.examscore.paperScreenShot.service;

import com.xz.ajiaedu.common.io.FileUtils;
import com.xz.ajiaedu.common.lang.StringUtil;
import com.xz.examscore.bean.Range;
import com.xz.examscore.bean.SubjectObjective;
import com.xz.examscore.bean.Target;
import com.xz.examscore.paperScreenShot.bean.*;
import com.xz.examscore.paperScreenShot.utils.PaintUtils;
import com.xz.examscore.services.ProvinceService;
import com.xz.examscore.services.QuestService;
import com.xz.examscore.services.RankService;
import com.xz.examscore.services.ScoreService;
import org.apache.commons.collections.MapUtils;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

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
    ProvinceService provinceService;

    @Autowired
    RankService rankService;

    @Autowired
    QuestService questService;

    @Autowired
    MonitorService monitorService;

    public static final Font TOTAL_SCORE_FONT = new Font("华文彩云", Font.BOLD, 35);

    public static final Font OBJECTIVE_DESC_FONT = new Font("华文彩云", Font.BOLD, 25);

    public static final Font SUBJECTIVE_DESC_FONT = new Font("华文彩云", Font.BOLD, 25);

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
                String studentId = MapUtils.getString(student, "studentId");
                //正面试卷截图
                String paper_positive = MapUtils.getString(student, "paper_positive");
                //反面试卷截图
                String paper_reverse = MapUtils.getString(student, "paper_reverse");
                List<Rect> rectList = new ArrayList<>();

                subjectiveList.forEach(subjective -> {
                    //题号
                    String questNo = MapUtils.getString(subjective, "questionNo");
                    //获取主观题每个题目的坐标信息
                    List<Rect> rects = convertToRectsObj(projectId, schoolId, classId, subjectId, subjective, paper_positive, paper_reverse, questNo);
                    rectList.add(rects.get(0));
                });

                //获取第一个客观题的高度
                double firstObjectiveHeight = getFirstObjectiveHeight(student);

                //获取第一个主观题的宽度
                double firstSubjectiveWidth = getFirstSubjectiveWidth(student);

                //总分标记区域
                TotalScoreZone totalScoreZone = getTotalScoreZone(projectId, student.get("studentId").toString(), subjectId, schoolId, classId, firstSubjectiveWidth);

                //客观题标记区域
                ObjectiveQuestZone objectiveQuestZone = getObjectiveQuestZone(projectId, student.get("studentId").toString(), subjectId, firstObjectiveHeight, firstSubjectiveWidth);

                try {
                    saveOneStudentScreenShot(paperScreenShotBean, studentId, paper_positive, paper_reverse, totalScoreZone, objectiveQuestZone, rectList);
                } catch (Exception e) {
                    LOG.error("生成学生试卷截图出现异常，项目ID:{}， 学生ID:{}, 科目ID:{}", projectId, studentId, subjectId);
                    monitorService.recordFailedStudent(projectId, schoolId, classId, studentId, subjectId);
                }
            });
        }
    }

    private double getFirstSubjectiveWidth(Map<String, Object> student) {
        List<Map<String, Object>> subjectiveList = (List<Map<String, Object>>) student.get("subjectiveList");
        if (null != subjectiveList) {
            Collections.sort(subjectiveList, (o1, o2) -> {
                Double qNo1 = Double.parseDouble(o1.get("questionNo").toString());
                Double qNo2 = Double.parseDouble(o2.get("questionNo").toString());
                return qNo1.compareTo(qNo2);
            });

            Map<String, Object> obj1 = subjectiveList.get(0);
            List<Map<String, Object>> rects = (List<Map<String, Object>>) obj1.get("rects");
            if (null != rects) {
                Map<String, Object> rect = rects.get(0);
                double coordinateX = MapUtils.getDoubleValue(rect, "coordinateX");
                return coordinateX;
            }
        }
        return 0;
    }

    //获取试卷第一个客观题的高度坐标，作为客观题描述信息的绘制高度坐标
    public double getFirstObjectiveHeight(Map<String, Object> student) {
        List<Map<String, Object>> objectiveList = (List<Map<String, Object>>) student.get("objectiveList");

        if (null != objectiveList) {
            Collections.sort(objectiveList, (o1, o2) -> {
                Double qNo1 = Double.parseDouble(o1.get("questionNo").toString());
                Double qNo2 = Double.parseDouble(o2.get("questionNo").toString());
                return qNo1.compareTo(qNo2);
            });

            Map<String, Object> obj1 = objectiveList.get(0);
            List<Map<String, Object>> rects = (List<Map<String, Object>>) obj1.get("rects");
            if (null != rects) {
                Map<String, Object> rect = rects.get(0);
                double coordinateY = MapUtils.getDoubleValue(rect, "coordinateY");
                return coordinateY;
            }
        }

        return 0;
    }

    private TotalScoreZone getTotalScoreZone(String projectId, String studentId, String subjectId, String schoolId, String classId, double firstSubjectiveWidth) {
        double totalScore = scoreService.getScore(projectId, Range.student(studentId), Target.subject(subjectId));
        int rankInClass = rankService.getRank(projectId, Range.clazz(classId), Target.subject(subjectId), totalScore);
        int rankInSchool = rankService.getRank(projectId, Range.school(schoolId), Target.subject(subjectId), totalScore);
        int rankInProvince = rankService.getRank(projectId, Range.province(provinceService.getProjectProvince(projectId)), Target.subject(subjectId), totalScore);

        //垂直间距
        int verticalInterval = TOTAL_SCORE_FONT.getSize() + 5;

        TextRect totalScoreRect = new TextRect((float) firstSubjectiveWidth, 50, "得分：" + packScoreData(totalScore), TOTAL_SCORE_FONT);
        TextRect rankInClassRect = new TextRect((float) firstSubjectiveWidth, 50 + verticalInterval, "班级排名：" + rankInClass, TOTAL_SCORE_FONT);
        TextRect rankInSchoolRect = new TextRect((float) firstSubjectiveWidth, 50 + verticalInterval * 2, "学校排名：" + rankInSchool, TOTAL_SCORE_FONT);
        TextRect rankInProvinceRect = new TextRect((float) firstSubjectiveWidth, 50 + verticalInterval * 3, "总体排名：" + rankInProvince, TOTAL_SCORE_FONT);

        List<TextRect> rectList = new LinkedList<>();
        rectList.add(totalScoreRect);
        rectList.add(rankInClassRect);
        rectList.add(rankInSchoolRect);
        rectList.add(rankInProvinceRect);

        return new TotalScoreZone(totalScore, rectList);
    }

    private ObjectiveQuestZone getObjectiveQuestZone(String projectId, String studentId, String subjectId, double firstObjectiveHeight, double firstSubjectiveWidth) {
        long correctCount = scoreService.getQuestCorrectCount(projectId, studentId, subjectId, true);
        long totalCount = scoreService.getStudentSubjectScoresCount(projectId, studentId, subjectId, true);
        double totalScore = scoreService.getScore(projectId, Range.student(studentId), Target.subjectObjective(new SubjectObjective(subjectId, true)));
        ObjectiveQuestZone objectiveQuestZone = new ObjectiveQuestZone();
        objectiveQuestZone.setTotalScore(totalScore);
        objectiveQuestZone.setCoordinateX((float) firstSubjectiveWidth);
        objectiveQuestZone.setCoordinateY(firstObjectiveHeight);
        objectiveQuestZone.setTotalCount((int) totalCount);
        objectiveQuestZone.setCorrectCount((int) correctCount);
        //转化成双精度
        List<Double> errorQuestNo1 = scoreService.getErrorQuestNo(projectId, studentId, subjectId, true, false)
                .stream().map(Double::valueOf).collect(Collectors.toList());
        //降序
        Collections.sort(errorQuestNo1);

        List<String> errorQuestNo = errorQuestNo1.stream().map(this::packScoreData).collect(Collectors.toList());
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
                                         TotalScoreZone totalScoreZone, ObjectiveQuestZone objectiveQuestZone, List<Rect> rectList) throws Exception{
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
    public void paintPaper(TotalScoreZone totalScoreZone, ObjectiveQuestZone objectiveQuestZone, List<Rect> rects, String path, String paper_positive, String paper_reverse) throws Exception{
        //将正反面截图读取到内存中
        BufferedImage img_positive = PaintUtils.loadImageUrl(paper_positive);
        BufferedImage img_reverse = PaintUtils.loadImageUrl(paper_reverse);

        //标记总分区域
        if (totalScoreZone != null) {
            img_positive = paintTotalScoreZone(img_positive, totalScoreZone);
        }

        //标记客观题区域
        if (objectiveQuestZone != null && objectiveQuestZone.getCoordinateY() != 0) {
            img_positive = paintObjectiveQuestZone(img_positive, objectiveQuestZone);
        }

        //标记主观题区域
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

        BufferedImage bufferedImage;

        String scoreDesc = packScoreData(objectiveQuestZone.getTotalScore()) + "分";

        double coordinateX = objectiveQuestZone.getCoordinateX();

        //在客观题答题区域上方一个字体高度+10的位置开始绘制客观题正确率信息
        double coordinateY = objectiveQuestZone.getCoordinateY() - TOTAL_SCORE_FONT.getSize() - 10;

        //错题列表的起始位置
        double scoreDescX = coordinateX + TOTAL_SCORE_FONT.getSize() * scoreDesc.length();

        //分数
        bufferedImage = PaintUtils.modifyImage(img_positive, scoreDesc, TOTAL_SCORE_FONT, (float) coordinateX, (float) coordinateY);

        //错题
        bufferedImage = PaintUtils.modifyImage(bufferedImage, "错题：", TOTAL_SCORE_FONT, (float) scoreDescX, (float) coordinateY);

        //获取错误题号列表
        List<String> errorQuestList = objectiveQuestZone.getErrorQuestList();

        //将题号封装成多个文字区域
        List<TextRect> textRects = objectiveQuestZone.getTextRects(errorQuestList, scoreDescX + TOTAL_SCORE_FONT.getSize() * 3, coordinateY, OBJECTIVE_DESC_FONT);

        for (TextRect textRect : textRects) {
            bufferedImage = PaintUtils.modifyImage(bufferedImage, textRect.getTextContent(), OBJECTIVE_DESC_FONT, textRect.getCoordinateX(), textRect.getCoordinateY());
        }

        return bufferedImage;
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
        String scoreContent = packScoreData(rect.getScore()) + "分";
        String rankContent = "排名：";
        String descContent = "总-" + rect.getRankInProvince() + "名，校-" + rect.getRankInSchool() + "名，班-" + rect.getRankInClass() + "名";
        bufferedImage = PaintUtils.modifyImage(bufferedImage, scoreContent, TOTAL_SCORE_FONT,
                (float) (rect.getCoordinateX()),
                (float) (rect.getCoordinateY()));
        bufferedImage = PaintUtils.modifyImage(bufferedImage, rankContent, TOTAL_SCORE_FONT,
                (float) (rect.getCoordinateX() + TOTAL_SCORE_FONT.getSize() * scoreContent.length()),
                (float) (rect.getCoordinateY()));
        return PaintUtils.modifyImage(bufferedImage, descContent, SUBJECTIVE_DESC_FONT,
                (float) (rect.getCoordinateX() + TOTAL_SCORE_FONT.getSize() * scoreContent.length() + TOTAL_SCORE_FONT.getSize() * 3),
                (float) (rect.getCoordinateY()) + TOTAL_SCORE_FONT.getSize() - SUBJECTIVE_DESC_FONT.getSize());
    }

    /**
     * 将作答区域封装成rect对象
     *
     * @param projectId      项目ID
     * @param schoolId       学校ID
     * @param classId        班级ID
     * @param subjectId      科目ID
     * @param subjective     主观题列表
     * @param paper_positive 试卷正面URL
     * @param paper_reverse  试卷反面URL
     * @param questionNo     试卷题号
     * @return 返回Rect对象
     */
    private List<Rect> convertToRectsObj(String projectId, String schoolId, String classId, String subjectId, Map<String, Object> subjective, String paper_positive, String paper_reverse, String questionNo) {
        List<Map<String, Object>> rects = (List<Map<String, Object>>) subjective.get("rects");
        List<Rect> list = new ArrayList<>();
        for (Map<String, Object> rect : rects) {

            double score = MapUtils.getDouble(subjective, "score");

            Document quest = questService.findQuest(projectId, subjectId, questionNo);
            Target questTarget = Target.quest(quest.getString("questId"));

            int rankInProvince = rankService.getRank(projectId, Range.province(provinceService.getProjectProvince(projectId)), questTarget, score);
            int rankInSchool = rankService.getRank(projectId, Range.school(schoolId), questTarget, score);
            int rankInClass = rankService.getRank(projectId, Range.clazz(classId), questTarget, score);

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
            r.setRankInProvince(rankInProvince);
            r.setRankInSchool(rankInSchool);
            r.setRankInClass(rankInClass);
            list.add(r);
        }
        return list;
    }

    public String packScoreData(double score) {
        DecimalFormat df = new DecimalFormat("###.####");
        return df.format(score);
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
