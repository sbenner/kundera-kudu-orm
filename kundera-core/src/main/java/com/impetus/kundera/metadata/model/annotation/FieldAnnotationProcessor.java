package com.impetus.kundera.metadata.model.annotation;

import javax.persistence.metamodel.ManagedType;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

public interface FieldAnnotationProcessor extends JPAAnnotationProcessor {
    void validateFieldAnnotation(Annotation annotation, Field field, ManagedType managedType);
}
