package com.xz.report;

import com.xz.report.sheet.SheetInfo;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * (description)
 * created at 16/05/30
 *
 * @author yiding_he
 */
@Retention(RUNTIME)
@Target(TYPE)
public @interface ReportInfo {

    String category();

    String fileName();

    SheetInfo[] sheets();
}
