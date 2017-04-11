package com.xz.examscore.paperScreenShot.service;

import com.hyd.appserver.utils.StringUtils;
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
@SuppressWarnings("unchecked")
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

    /**
     * 保存试卷截图对象到本地文件系统
     *
     * @param paperScreenShotBean 试卷截图对象
     */
    public void saveScreenShot(PaperScreenShotBean paperScreenShotBean, Map<String, Object> subjectRuleMap) {
        String projectId = paperScreenShotBean.getProjectId();
        String schoolId = paperScreenShotBean.getSchoolId();
        String classId = paperScreenShotBean.getClassId();
        String subjectId = paperScreenShotBean.getSubjectId();

        //排名显示规则配置
        Map<String, Object> rankRuleMap = null == subjectRuleMap ? Collections.emptyMap() : (Map<String, Object>) subjectRuleMap.get(subjectId);

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
                List<SubjectiveQuestZone> subjectiveQuestZoneList = new ArrayList<>();

                subjectiveList.forEach(subjective -> {
                    //题号
                    String questNo = MapUtils.getString(subjective, "questionNo");
                    //获取主观题每个题目的坐标信息
                    List<SubjectiveQuestZone> subjectiveQuestZones = convertToRectsObj(projectId, schoolId, classId, subjectId, subjective, paper_positive, paper_reverse, questNo, rankRuleMap);
                    //获取主观题最靠前的区域
                    subjectiveQuestZoneList.add(subjectiveQuestZones.get(0));
                });

                //获取第一个客观题的高度
                double firstObjectiveHeight = getFirstObjectiveHeight(student);

                //获取第一个主观题的宽度
                double firstSubjectiveWidth = getFirstSubjectiveWidth(student);

                //总分标记区域
                TotalScoreZone totalScoreZone = getTotalScoreZone(projectId, student.get("studentId").toString(), subjectId, schoolId,
                        classId, firstSubjectiveWidth, rankRuleMap);

                //客观题标记区域
                ObjectiveQuestZone objectiveQuestZone = getObjectiveQuestZone(projectId, student.get("studentId").toString(), subjectId, schoolId,
                        classId, firstObjectiveHeight, firstSubjectiveWidth, rankRuleMap);

                try {
                    saveOneStudentScreenShot(paperScreenShotBean, studentId, paper_positive, paper_reverse,
                            totalScoreZone, objectiveQuestZone, subjectiveQuestZoneList);
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
                return MapUtils.getDoubleValue(rect, "coordinateX");
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
                return MapUtils.getDoubleValue(rect, "coordinateY");
            }
        }

        return 0;
    }

    private TotalScoreZone getTotalScoreZone(String projectId, String studentId, String subjectId, String schoolId, String classId,
                                             double firstSubjectiveWidth, Map<String, Object> rankRuleMap) {

        Map<String, Object> rankInClass = MapUtils.getMap(rankRuleMap, "rankInClass");
        Map<String, Object> rankInSchool = MapUtils.getMap(rankRuleMap, "rankInSchool");
        Map<String, Object> rankInProvince = MapUtils.getMap(rankRuleMap, "rankInProvince");

        boolean isClassOn = MapUtils.getBooleanValue(rankInClass, "total");
        boolean isSchoolOn = MapUtils.getBooleanValue(rankInSchool, "total");
        boolean isProvinceOn = MapUtils.getBooleanValue(rankInProvince, "total");

        double totalScore = scoreService.getScore(projectId, Range.student(studentId), Target.subject(subjectId));
        List<TextRect> rectList = new LinkedList<>();
        TextRect totalScoreRect = new TextRect((float) firstSubjectiveWidth, 50, "得分：" + packScoreData(totalScore), TOTAL_SCORE_FONT);
        rectList.add(totalScoreRect);

        fillTotalRanks(projectId, subjectId, schoolId, classId, (float) firstSubjectiveWidth, totalScore, rectList,
                isClassOn, isSchoolOn, isProvinceOn);

        return new TotalScoreZone(totalScore, rectList);
    }

    /**
     * 填充总分区域的排名信息
     *
     * @param projectId            考试项目ID
     * @param subjectId            科目ID
     * @param schoolId             学校ID
     * @param classId              班级ID
     * @param firstSubjectiveWidth 主观题高度
     * @param totalScore           总分
     * @param rectList             主观题答题区域
     * @param isClassOn            班级排名显示开关
     * @param isSchoolOn           学校排名显示开关
     * @param isProvinceOn         总体排名显示开关
     */
    private void fillTotalRanks(String projectId, String subjectId, String schoolId, String classId, float firstSubjectiveWidth, double totalScore, List<TextRect> rectList, boolean isClassOn, boolean isSchoolOn, boolean isProvinceOn) {
        //垂直间距
        int verticalInterval = TOTAL_SCORE_FONT.getSize() + 5;

        if (isClassOn) {
            int rankInClass = rankService.getRank(projectId, Range.clazz(classId), Target.subject(subjectId), totalScore);
            TextRect rankInClassRect = new TextRect(firstSubjectiveWidth, 50 + verticalInterval, "班级排名：" + rankInClass, TOTAL_SCORE_FONT);
            rectList.add(rankInClassRect);
        }

        if (isSchoolOn) {
            int rankInSchool = rankService.getRank(projectId, Range.school(schoolId), Target.subject(subjectId), totalScore);
            TextRect rankInSchoolRect = new TextRect(firstSubjectiveWidth, 50 + verticalInterval * 2, "学校排名：" + rankInSchool, TOTAL_SCORE_FONT);
            rectList.add(rankInSchoolRect);
        }

        if (isProvinceOn) {
            int rankInProvince = rankService.getRank(projectId, Range.province(provinceService.getProjectProvince(projectId)), Target.subject(subjectId), totalScore);
            TextRect rankInProvinceRect = new TextRect(firstSubjectiveWidth, 50 + verticalInterval * 3, "总体排名：" + rankInProvince, TOTAL_SCORE_FONT);
            rectList.add(rankInProvinceRect);
        }

        //调整总分排名位置的各项排名的高度
        for (int i = 1; i <= rectList.size(); i++) {
            TextRect textRect = rectList.get(i - 1);
            textRect.setCoordinateY(50 + verticalInterval * i);
        }

    }

    /**
     * 客观题答题区域
     *
     * @param projectId            项目ID
     * @param studentId            学生ID
     * @param subjectId            科目ID
     * @param schoolId             学校ID
     * @param classId              班级ID
     * @param firstObjectiveHeight 第一个客观题的高度
     * @param firstSubjectiveWidth 第一个主观题的宽度
     * @param rankRuleMap          排名显示规则
     * @return 返回结果
     */
    private ObjectiveQuestZone getObjectiveQuestZone(String projectId, String studentId, String subjectId, String schoolId, String classId,
                                                     double firstObjectiveHeight, double firstSubjectiveWidth, Map<String, Object> rankRuleMap) {

        Map<String, Object> rankInClass = MapUtils.getMap(rankRuleMap, "rankInClass");
        Map<String, Object> rankInSchool = MapUtils.getMap(rankRuleMap, "rankInSchool");
        Map<String, Object> rankInProvince = MapUtils.getMap(rankRuleMap, "rankInProvince");

        boolean isClassOn = MapUtils.getBooleanValue(rankInClass, "objective");
        boolean isSchoolOn = MapUtils.getBooleanValue(rankInSchool, "objective");
        boolean isProvinceOn = MapUtils.getBooleanValue(rankInProvince, "objective");

        long correctCount = scoreService.getQuestCorrectCount(projectId, studentId, subjectId, true);
        long totalCount = scoreService.getStudentSubjectScoresCount(projectId, studentId, subjectId, true);
        double totalScore = scoreService.getScore(projectId, Range.student(studentId), Target.subjectObjective(new SubjectObjective(subjectId, true)));
        ObjectiveQuestZone objectiveQuestZone = new ObjectiveQuestZone();
        objectiveQuestZone.setTotalScore(totalScore);
        objectiveQuestZone.setCoordinateX(firstSubjectiveWidth);
        objectiveQuestZone.setCoordinateY(firstObjectiveHeight);
        objectiveQuestZone.setTotalCount((int) totalCount);
        objectiveQuestZone.setCorrectCount((int) correctCount);
        //转化成双精度
        List<Double> errorQuestNo1 = scoreService.getErrorQuestNo(projectId, studentId, subjectId, true, false)
                .stream().map(Double::valueOf).collect(Collectors.toList());
        //降序
        Collections.sort(errorQuestNo1);

        //错题列表
        List<String> errorQuestNo = errorQuestNo1.stream().map(this::packScoreData).collect(Collectors.toList());
        objectiveQuestZone.setErrorQuests(errorQuestNo);

        //计算排名
        fillObjectiveRanks(projectId, studentId, subjectId, schoolId, classId, objectiveQuestZone.getTextRects(),
                firstObjectiveHeight, firstSubjectiveWidth,
                isClassOn, isSchoolOn, isProvinceOn);

        return objectiveQuestZone;
    }

    /**
     * 填充客观题排名
     *
     * @param projectId            项目ID
     * @param studentId            学生ID
     * @param subjectId            科目ID
     * @param schoolId             学校ID
     * @param classId              班级ID
     * @param firstObjectiveHeight 第一个客观题的高度
     * @param firstSubjectiveWidth 第一个主观题的宽度
     * @param isClassOn            班级排名显示开关
     * @param isSchoolOn           学校排名显示开关
     * @param isProvinceOn         总体排名显示开关
     */
    private void fillObjectiveRanks(String projectId, String studentId, String subjectId, String schoolId, String classId, List<TextRect> textRects, double firstObjectiveHeight, double firstSubjectiveWidth, boolean isClassOn, boolean isSchoolOn, boolean isProvinceOn) {
        SubjectObjective subjectObjective = new SubjectObjective(subjectId, true);
        double totalScore = scoreService.getScore(projectId, Range.student(studentId), Target.subjectObjective(subjectObjective));

        StringBuilder builder = new StringBuilder();

        if (isClassOn) {
            int rankInProvince = rankService.getRank(projectId, Range.province(provinceService.getProjectProvince(projectId)), Target.subjectObjective(subjectObjective), totalScore);
            builder.append("班-").append(rankInProvince).append("名 ");
        }

        if (isSchoolOn) {
            int rankInSchool = rankService.getRank(projectId, Range.school(schoolId), Target.subjectObjective(subjectObjective), totalScore);
            builder.append("校-").append(rankInSchool).append("名 ");
        }

        if (isProvinceOn) {
            int rankInClass = rankService.getRank(projectId, Range.clazz(classId), Target.subjectObjective(subjectObjective), totalScore);
            builder.append("总-").append(rankInClass).append("名 ");
        }

        TextRect textRect = new TextRect((float) firstSubjectiveWidth, (float) firstObjectiveHeight, builder.toString(), TOTAL_SCORE_FONT);
        textRects.add(textRect);
    }

    /**
     * 保存单个学生的试卷留痕截图文件
     *
     * @param paperScreenShotBean     试卷截图对象
     * @param fileName                文件名
     * @param paper_positive          正面URL地址
     * @param paper_reverse           反面URL地址
     * @param totalScoreZone          总分区域
     * @param objectiveQuestZone      客观题区域
     * @param subjectiveQuestZoneList 切图列表
     */
    public void saveOneStudentScreenShot(PaperScreenShotBean paperScreenShotBean, String fileName, String paper_positive, String paper_reverse,
                                         TotalScoreZone totalScoreZone, ObjectiveQuestZone objectiveQuestZone, List<SubjectiveQuestZone> subjectiveQuestZoneList) throws Exception {
        String directory = StringUtil.joinPaths(paperScreenShotSavePath,
                getScreenShotFilePath(paperScreenShotBean));
        try {
            FileUtils.getOrCreateDir(directory);
        } catch (IOException e) {
            LOG.error("生成试卷留痕截图目录失败！");
            return;
        }
        String filePath = StringUtil.joinPaths(directory, fileName);
        paintPaper(totalScoreZone, objectiveQuestZone, subjectiveQuestZoneList, filePath, paper_positive, paper_reverse);
    }

    /**
     * 将图片修改后保存
     *
     * @param totalScoreZone       总分区域
     * @param objectiveQuestZone   客观题区域
     * @param subjectiveQuestZones 图片切图区域列表
     * @param path                 保存路径
     * @param paper_positive       正面URL地址
     * @param paper_reverse        反面URL地址
     */
    public void paintPaper(TotalScoreZone totalScoreZone, ObjectiveQuestZone objectiveQuestZone, List<SubjectiveQuestZone> subjectiveQuestZones,
                           String path, String paper_positive, String paper_reverse) throws Exception {

        if(!StringUtils.isBlank(paper_positive)){
            BufferedImage img_positive = paintPositive(totalScoreZone, objectiveQuestZone, paper_positive);
            //将正反面截图读取到内存中

            BufferedImage img_reverse = null;
            if(!StringUtils.isBlank(paper_reverse)){
                img_reverse = PaintUtils.loadImageUrl(paper_reverse);
            }

            //标记主观题区域
            for (SubjectiveQuestZone subjectiveQuestZone : subjectiveQuestZones) {
                int pageIndex = subjectiveQuestZone.getPageIndex();
                if (pageIndex == 0) {
                    img_positive = doPaint(img_positive, subjectiveQuestZone);
                } else {
                    img_reverse = doPaint(img_reverse, subjectiveQuestZone);
                }
            }

            //保存正面
            PaintUtils.writeImageLocal(renderSuffixByIndex(path, true, PaintUtils.SCREEN_SHOT_SUFFIX_PNG), img_positive, PaintUtils.PNG);
            //保存反面
            PaintUtils.writeImageLocal(renderSuffixByIndex(path, false, PaintUtils.SCREEN_SHOT_SUFFIX_PNG), img_reverse, PaintUtils.PNG);
        }

    }

    public BufferedImage paintPositive(TotalScoreZone totalScoreZone, ObjectiveQuestZone objectiveQuestZone, String paper_positive) throws Exception {
        BufferedImage img_positive = PaintUtils.loadImageUrl(paper_positive);
        //标记总分区域
        if (totalScoreZone != null) {
            img_positive = paintTotalScoreZone(img_positive, totalScoreZone);
        }

        //标记客观题区域
        if (objectiveQuestZone != null && objectiveQuestZone.getCoordinateY() != 0) {
            img_positive = paintObjectiveQuestZone(img_positive, objectiveQuestZone);
        }
        return img_positive;
    }

    /**
     * 绘制总分区域
     *
     * @param img_positive   图片缓存
     * @param totalScoreZone 客观题区域
     * @return 图片缓存
     */
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

    /**
     * 绘制客观题区域
     *
     * @param bufferedImage      图片缓存
     * @param objectiveQuestZone 客观题区域
     * @return 图片缓存
     */
    private BufferedImage paintObjectiveQuestZone(BufferedImage bufferedImage, ObjectiveQuestZone objectiveQuestZone) {

        int lineIntervalY = TOTAL_SCORE_FONT.getSize() + 10;

        //去小数点末尾0
        String scoreDesc = packScoreData(objectiveQuestZone.getTotalScore()) + "分";

        double coordinateX = objectiveQuestZone.getCoordinateX();

        //在客观题答题区域上方一个字体高度+10的位置开始绘制客观题正确率信息
        double coordinateY = objectiveQuestZone.getCoordinateY() - lineIntervalY * 2;

        //分数
        bufferedImage = PaintUtils.modifyImage(bufferedImage, scoreDesc, TOTAL_SCORE_FONT, (float) coordinateX, (float) coordinateY);

        List<TextRect> textRects = objectiveQuestZone.getTextRects();

        for (TextRect textRect : textRects) {
            bufferedImage = PaintUtils.modifyImage(bufferedImage, textRect.getTextContent(), textRect.getFont(), (float) coordinateX + TOTAL_SCORE_FONT.getSize() * scoreDesc.length(), (float) coordinateY);
        }

        //获取错误题号列表
        List<String> errorQuestList = objectiveQuestZone.getErrorQuests();

        String errorTitle = "错题：";

        bufferedImage = PaintUtils.modifyImage(bufferedImage, errorTitle, TOTAL_SCORE_FONT, (float) coordinateX, (float) (coordinateY + lineIntervalY));

        List<TextRect> lines = objectiveQuestZone.getLines(errorQuestList, (float) coordinateX + TOTAL_SCORE_FONT.getSize() * errorTitle.length(),
                (float) (coordinateY + lineIntervalY), TOTAL_SCORE_FONT);

        for (TextRect textRect : lines){
            bufferedImage = PaintUtils.modifyImage(bufferedImage, textRect.getTextContent(), TOTAL_SCORE_FONT, textRect.getCoordinateX(), textRect.getCoordinateY());
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

    private BufferedImage doPaint(BufferedImage bufferedImage, SubjectiveQuestZone subjectiveQuestZone) {
        String scoreContent = packScoreData(subjectiveQuestZone.getScore()) + "分";
        List<TextRect> textRects = subjectiveQuestZone.getTextRects();

        bufferedImage = PaintUtils.modifyImage(bufferedImage, scoreContent, TOTAL_SCORE_FONT,
                (float) (subjectiveQuestZone.getCoordinateX()),
                (float) (subjectiveQuestZone.getCoordinateY()));

        for (TextRect textRect : textRects) {
            PaintUtils.modifyImage(bufferedImage, textRect.getTextContent(), TOTAL_SCORE_FONT,
                    (float) (subjectiveQuestZone.getCoordinateX() + TOTAL_SCORE_FONT.getSize() * scoreContent.length()),
                    (float) (subjectiveQuestZone.getCoordinateY())
            );
        }

        return bufferedImage;
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
     * @param rankRuleMap    排名显示规则
     * @return 返回Rect对象
     */
    private List<SubjectiveQuestZone> convertToRectsObj(String projectId, String schoolId, String classId, String subjectId, Map<String, Object> subjective, String paper_positive, String paper_reverse, String questionNo, Map<String, Object> rankRuleMap) {

        Map<String, Object> rankInClassMap = MapUtils.getMap(rankRuleMap, "rankInClass");
        Map<String, Object> rankInSchoolMap = MapUtils.getMap(rankRuleMap, "rankInSchool");
        Map<String, Object> rankInProvinceMap = MapUtils.getMap(rankRuleMap, "rankInProvince");

        List<String> questInClass = (List<String>) MapUtils.getObject(rankInClassMap, "subjective");
        List<String> questInSchool = (List<String>) MapUtils.getObject(rankInSchoolMap, "subjective");
        List<String> questInProvince = (List<String>) MapUtils.getObject(rankInProvinceMap, "subjective");

        List<Map<String, Object>> rects = (List<Map<String, Object>>) subjective.get("rects");
        List<SubjectiveQuestZone> list = new ArrayList<>();
        for (Map<String, Object> rect : rects) {

            double score = MapUtils.getDouble(subjective, "score");

            Document quest = questService.findQuest(projectId, subjectId, questionNo);
            Target questTarget = Target.quest(quest.getString("questId"));

            StringBuilder builder = new StringBuilder();

            if (null != questInProvince && questInProvince.contains(questionNo)) {
                int rankInProvince = rankService.getRank(projectId, Range.province(provinceService.getProjectProvince(projectId)), questTarget, score);
                builder.append("总-").append(rankInProvince).append("名 ");
            }

            if (null != questInSchool && questInSchool.contains(questionNo)) {
                int rankInSchool = rankService.getRank(projectId, Range.school(schoolId), questTarget, score);
                builder.append("校-").append(rankInSchool).append("名 ");
            }

            if (null != questInClass && questInClass.contains(questionNo)) {
                int rankInClass = rankService.getRank(projectId, Range.clazz(classId), questTarget, score);
                builder.append("班-").append(rankInClass).append("名 ");
            }

            TextRect textRect = new TextRect();
            textRect.setTextContent(builder.toString());
            textRect.setFont(TOTAL_SCORE_FONT);

            SubjectiveQuestZone r = new SubjectiveQuestZone();
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

            r.setTextRects(Collections.singletonList(textRect));

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
