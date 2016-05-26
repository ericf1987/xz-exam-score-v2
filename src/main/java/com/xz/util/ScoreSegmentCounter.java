package com.xz.util;

import com.xz.ajiaedu.common.lang.CounterMap;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

/**
 * (description)
 * created at 16/05/26
 *
 * @author yiding_he
 */
public class ScoreSegmentCounter {

    private int interval;       // 分段间距（例如10分/50分）

    private CounterMap<Integer> counterMap = new CounterMap<>();

    public ScoreSegmentCounter(int interval) {
        this.interval = interval;
    }

    public void addScore(double score) {
        int segment = ((int) score / interval) * interval;
        this.counterMap.incre(segment);
    }

    public List<Document> toDocuments() {
        List<Document> result = new ArrayList<>();
        for (Integer segment : counterMap.keySet()) {
            Document document = new Document();
            document.append("segment", segment);
            document.append("count", counterMap.get(segment));
            result.add(document);
        }
        return result;
    }

}
