package com.xz.examscore.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 通过代码解析相应的报表名称
 *
 * @author by fengye on 2016/6/15.
 */
public class ReportNameMappings {

    public static final Map<String, String> PRIMARY_CLASSIFY_CODE_MAP = new HashMap<>();

    public static final Map<String, String> SECONDARY_CLASSIFY_CODE_MAP = new HashMap<>();

    public static final Map<String, String> FILE_NAME_CODE_MAP = new HashMap<>();

    public static final List<String> BASIC_REPORT_ITEM = new ArrayList<>();

    static {
        PRIMARY_CLASSIFY_CODE_MAP.put("100", "总体成绩分析");
        PRIMARY_CLASSIFY_CODE_MAP.put("101", "学校成绩分析");
        PRIMARY_CLASSIFY_CODE_MAP.put("102", "班级成绩分析");

        SECONDARY_CLASSIFY_CODE_MAP.put("200", "基础分析");
        SECONDARY_CLASSIFY_CODE_MAP.put("201", "试卷分析");
        SECONDARY_CLASSIFY_CODE_MAP.put("202", "尖子生情况");
        SECONDARY_CLASSIFY_CODE_MAP.put("203", "基础数据");
        SECONDARY_CLASSIFY_CODE_MAP.put("204", "历次考试对比");

        FILE_NAME_CODE_MAP.put("300", "分数分析.xlsx");
        FILE_NAME_CODE_MAP.put("301", "分数段统计.xlsx");
        FILE_NAME_CODE_MAP.put("302", "排名统计.xlsx");
        FILE_NAME_CODE_MAP.put("303", "学科分析.xlsx");
        FILE_NAME_CODE_MAP.put("304", "试卷题型分析.xlsx");
        FILE_NAME_CODE_MAP.put("305", "双向细目分析.xlsx");
        FILE_NAME_CODE_MAP.put("306", "客观题分析.xlsx");
        FILE_NAME_CODE_MAP.put("307", "主观题分析.xlsx");
        FILE_NAME_CODE_MAP.put("308", "尖子生统计.xlsx");
        FILE_NAME_CODE_MAP.put("309", "尖子生试卷情况分析.xlsx");
        FILE_NAME_CODE_MAP.put("310", "高分段竞争力分析.xlsx");
        FILE_NAME_CODE_MAP.put("311", "分数排名统计.xlsx");
        FILE_NAME_CODE_MAP.put("312", "S-P诊断.xlsx");
        FILE_NAME_CODE_MAP.put("313", "等级分析.xlsx");
        FILE_NAME_CODE_MAP.put("314", "题型分析.xlsx");
        FILE_NAME_CODE_MAP.put("315", "知识点分析.xlsx");
        FILE_NAME_CODE_MAP.put("316", "能力层级分析.xlsx");
        FILE_NAME_CODE_MAP.put("317", "S-P试卷诊断.xlsx");
        FILE_NAME_CODE_MAP.put("318", "学生各科成绩明细.xlsx");
        FILE_NAME_CODE_MAP.put("319", "等第报表.xlsx");
        FILE_NAME_CODE_MAP.put("320", "平均分对比.xlsx");
        FILE_NAME_CODE_MAP.put("321", "优秀率对比.xlsx");
        FILE_NAME_CODE_MAP.put("322", "及格率对比.xlsx");
        FILE_NAME_CODE_MAP.put("323", "学生历史成绩.xlsx");

        BASIC_REPORT_ITEM.add("-->基础数据-->学生各科成绩明细.xlsx");
        BASIC_REPORT_ITEM.add("-->基础数据-->各科试卷题目得分明细.xlsx");
    }

    public static String[] getFileName(String[] code) {
        List<String> fileNames = new ArrayList<>();
        for (String aCode : code) {
            String[] param = aCode.split("-");
            String filename = PRIMARY_CLASSIFY_CODE_MAP.get(param[0]) + "-->"
                    + SECONDARY_CLASSIFY_CODE_MAP.get(param[1]) + "-->"
                    + FILE_NAME_CODE_MAP.get(param[2]);
            fileNames.add(filename);
            //如果包含总体，学校，班级的任何一张报表，则将基础数据附带在对应文件夹生成zip包
        }
        String[] seeds = new String[]{
                "总体成绩分析", "学校成绩分析", "班级成绩分析"
        };

        for (String seed : seeds){
            addBySeed(fileNames, seed, BASIC_REPORT_ITEM);
        }
        return fileNames.toArray(new String[fileNames.size()]);
    }

    public static List<String> addBySeed(List<String> fileNames, String seed, List<String> items) {
        for(String item : items){
            String fileItem = seed + item;
            for (String fileName : fileNames) {
                if (fileName.contains(seed)) {
                    fileNames.add(fileItem);
                    break;
                }
            }
        }
        return fileNames;
    }

}
