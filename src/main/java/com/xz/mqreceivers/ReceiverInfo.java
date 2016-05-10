package com.xz.mqreceivers;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * (description)
 * created at 16/05/10
 *
 * @author yiding_he
 */
@Retention(RUNTIME)
@Target(TYPE)
public @interface ReceiverInfo {

    String taskType();
}
