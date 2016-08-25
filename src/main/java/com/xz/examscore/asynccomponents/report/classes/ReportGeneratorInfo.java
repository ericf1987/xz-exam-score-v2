package com.xz.examscore.asynccomponents.report.classes;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * (description)
 * created at 16/06/06
 *
 * @author yiding_he
 */
@Retention(RUNTIME)
@Target(TYPE)
public @interface ReportGeneratorInfo {

    String range();
}
