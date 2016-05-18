package com.xz.util;

import com.xz.ajiaedu.common.lang.StringUtil;
import com.xz.bean.Target;

/**
 * (description)
 * created at 16/05/18
 *
 * @author yiding_he
 */
public class SubjectUtil {

    // 是否是综合科目
    public static boolean isCombinedSubject(Target target) {
        return target.match(Target.SUBJECT) && isCombinedSubject(target.getId().toString());
    }

    // 是否是综合科目
    public static boolean isCombinedSubject(String subjectId) {
        return StringUtil.isOneOf(subjectId, "004005006", "007008009");
    }
}
