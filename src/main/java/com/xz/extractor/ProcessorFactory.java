package com.xz.extractor;

import com.xz.extractor.processor.*;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

/**
 * 数据处理器工厂类
 *
 * @author zhaorenwu
 */

@Component
public class ProcessorFactory {

    private Map<String, DataProcessor> readerMap = new HashMap<>();

    @Autowired
    ClassListProcessor classListProcessor;

    @Autowired
    FullScoreProcessor fullScoreProcessor;

    @Autowired
    QuestListProcessor questListProcessor;

    @Autowired
    SchoolListProcessor schoolListProcessor;

    @Autowired
    ScoreProcessor scoreProcessor;

    @Autowired
    StudentListProcessor studentListProcessor;

    @Autowired
    SubjectListProcessor subjectListProcessor;

    @PostConstruct
    public void initFactory() {
        registerProcessor(classListProcessor);
        registerProcessor(fullScoreProcessor);
        registerProcessor(questListProcessor);
        registerProcessor(schoolListProcessor);
//        registerProcessor(scoreProcessor);
        registerProcessor(studentListProcessor);
        registerProcessor(subjectListProcessor);
    }

    public void registerProcessor(DataProcessor processor) {
        this.readerMap.put(processor.getFilePattern(), processor);
    }

    public DataProcessor getDataProcessor(String filename) {

        DataProcessor matchResult = null, equalResult = null;

        for (String pattern : readerMap.keySet()) {
            if (pattern.equals(filename)) {
                equalResult = readerMap.get(pattern);
                break;
            } else if (FilenameUtils.wildcardMatch(filename, pattern)) {
                matchResult = readerMap.get(pattern);
            }
        }

        return equalResult != null ? equalResult : matchResult;
    }
}
