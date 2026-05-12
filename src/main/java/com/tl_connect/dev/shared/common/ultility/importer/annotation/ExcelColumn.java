package com.tl_connect.dev.shared.common.ultility.importer.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ExcelColumn {
    String header();

    int order() default 0;

    boolean exportable() default true;

    boolean importable() default true;
}
