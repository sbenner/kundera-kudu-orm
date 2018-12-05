package com.impetus.client.kudu.schemamanager;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;


@Documented
@Target({METHOD,FIELD})
@Retention(RUNTIME)
public @interface Hash {
    public abstract int buckets() default 1;
}