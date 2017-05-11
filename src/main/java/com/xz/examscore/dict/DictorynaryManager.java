package com.xz.examscore.dict;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

/**
 * @author by fengye on 2017/5/10.
 */
@Component
public class DictorynaryManager {
    static final Logger LOG = LoggerFactory.getLogger(DictorynaryManager.class);

    public static final Map<String, String> SUBJECT_NAMES = new HashMap<>();

    @PostConstruct
    public void init(){

    }
}
