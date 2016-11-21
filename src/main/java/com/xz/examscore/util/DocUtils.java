package com.xz.examscore.util;

import com.hyd.appserver.utils.StringUtils;
import org.bson.Document;

/**
 * @author by fengye on 2016/11/17.
 */
public class DocUtils {
    public static Document addTo(Document document, String key, Object o) {
        if (o != null) {
            if (o instanceof String && !StringUtils.isBlank((String) o)) {
                document.put(key, o);
            } else {
                document.put(key, o);
            }
        }
        return document;
    }
}
