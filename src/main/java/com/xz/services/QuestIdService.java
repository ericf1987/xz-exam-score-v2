package com.xz.services;

import org.springframework.stereotype.Service;

/**
 * 如果导入的题目没有 questId，则自动生成一个，并保证多次导入时同一个题目得到的是同样的 questId
 *
 * @author yiding_he
 */
@Service
public class QuestIdService {

    public Object getQuestId(String project, String subject, String questNo) {
        return null;
    }
}
