
package com.impetus.client.kudu.schemamanager;

import java.lang.annotation.Target;
import java.lang.annotation.Retention;
import java.lang.annotation.Documented;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Target({TYPE}) 
@Retention(RUNTIME)
public @interface Hashable {
    public int buckets() default 1;
}
