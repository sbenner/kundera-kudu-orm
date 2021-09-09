package com.impetus.client.kudu.schemamanager;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Target({TYPE})
@Retention(RUNTIME)
public @interface Hashable {
    int buckets() default 1;

    int replicationFactor() default 1;
}
