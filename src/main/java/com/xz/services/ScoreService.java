package com.xz.services;

import com.xz.bean.Range;
import com.xz.bean.Target;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * (description)
 * created at 16/05/11
 *
 * @author yiding_he
 */
@Service
public class ScoreService {

    private static final Map<String, String> TARGET_MAP = new HashMap<>();

    private static final Map<String, String> RANGE_MAP = new HashMap<>();

    static {
        TARGET_MAP.put(Target.PROJECT, "projectId");
        TARGET_MAP.put(Target.SUBJECT, "subjectId");
        TARGET_MAP.put(Target.QUEST, "questNo");
        RANGE_MAP.put(Range.PROVINCE, "projectId");
    }
}
