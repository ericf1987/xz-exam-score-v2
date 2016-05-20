package com.xz.extractor;

import com.xz.ajiaedu.common.lang.Context;
import com.xz.extractor.processor.ProcessorException;

/**
 * ${描述}
 *
 * @author zhaorenwu
 */
public class InvalidExamDataException extends ProcessorException {

    public InvalidExamDataException(String filename, String line, int lineCount, String message, Throwable t) {
        this("行 " + filename + ":" + lineCount + " 格式不正确：" + message + "\n\t" + line, t);
    }

    public InvalidExamDataException(Context context, String line, int lineCount, Throwable t) {
        this(context.get("filename"), line, lineCount, t.getMessage(), t);
    }

    public InvalidExamDataException(Context context, int lineCount, Throwable t) {
        this(context, context.get("currentline"), lineCount, t);
    }

    public InvalidExamDataException(String message, Throwable cause) {
        super(message, cause);
    }
}
