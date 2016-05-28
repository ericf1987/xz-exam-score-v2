package com.xz.extractor.processor;

import java.text.MessageFormat;

/**
 * 数据处理异常
 *
 * @author zhaorenwu
 */

public class ProcessorException extends RuntimeException {

    public ProcessorException(String messagePattern, Object... params) {
        super(MessageFormat.format(messagePattern, params));
    }
}
