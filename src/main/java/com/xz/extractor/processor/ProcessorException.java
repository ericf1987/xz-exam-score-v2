package com.xz.extractor.processor;

import java.text.MessageFormat;

/**
 * ${描述}
 *
 * @author zhaorenwu
 */

public class ProcessorException extends RuntimeException {

    public ProcessorException(String messagePattern, Object... params) {
        super(MessageFormat.format(messagePattern, params));
    }
}
