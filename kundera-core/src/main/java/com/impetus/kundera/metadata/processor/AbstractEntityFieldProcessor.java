/*******************************************************************************
 * * Copyright 2012 Impetus Infotech.
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *      http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 ******************************************************************************/
package com.impetus.kundera.metadata.processor;

import com.impetus.kundera.metadata.MetadataProcessor;
import com.impetus.kundera.metadata.model.EntityMetadata;
import com.impetus.kundera.metadata.validator.EntityValidator;
import com.impetus.kundera.persistence.EntityManagerFactoryImpl.KunderaMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.PersistenceException;
import javax.persistence.Temporal;
import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.util.Date;

/**
 * The Class AbstractEntityFieldProcessor.
 *
 * @author animesh.kumar
 */
public abstract class AbstractEntityFieldProcessor implements MetadataProcessor {

    /**
     * The Constant log.
     */
    private static final Logger log = LoggerFactory.getLogger(AbstractEntityFieldProcessor.class);

    /**
     * The Validator.
     */
    protected EntityValidator validator;

    protected KunderaMetadata kunderaMetadata;

    public AbstractEntityFieldProcessor(KunderaMetadata kunderaMetadata) {
        this.kunderaMetadata = kunderaMetadata;
    }

    /**
     * Gets the valid jpa column.
     *
     * @param clazz
     *            the clazz
     * @return the valid jpa column
     * @throws PersistenceException
     *             the persistence exception
     */

    /**
     * Validate.
     *
     * @param clazz the clazz
     * @throws PersistenceException the persistence exception
     */
    public final void validate(Class<?> clazz) throws PersistenceException {
        validator.validate(clazz);
    }

    /**
     * Gets the valid jpa column name.
     *
     * @param entity the entity
     * @param f      the f
     * @return the valid jpa column name
     */
    protected final String getValidJPAColumnName(Class<?> entity, Field f) {

        String name = null;

        if (f.isAnnotationPresent(Column.class)) {
            Column c = f.getAnnotation(Column.class);
            if (!c.name().isEmpty()) {
                name = c.name();
            } else {
                name = f.getName();
            }
        } else if (f.isAnnotationPresent(Basic.class)) {
            name = f.getName();
        }

        if (f.isAnnotationPresent(Temporal.class)) {
            if (!f.getType().equals(Date.class)) {
                log.error("@Temporal must map to java.util.Date for @Entity(" + entity.getName() + "." + f.getName()
                        + ")");
                return name;
            }
            if (null == name) {
                name = f.getName();
            }
        }
        return name;
    }

    /**
     * Populates @Id accesser methods like, getId and setId of clazz to
     * metadata.
     *
     * @param metadata the metadata
     * @param clazz    the clazz
     * @param f        the f
     */
    protected final void populateIdAccessorMethods(EntityMetadata metadata, Class<?> clazz, Field f) {
        try {
            BeanInfo info = Introspector.getBeanInfo(clazz);

            for (PropertyDescriptor descriptor : info.getPropertyDescriptors()) {
                if (descriptor.getName().equals(f.getName())) {
                    metadata.setReadIdentifierMethod(descriptor.getReadMethod());
                    metadata.setWriteIdentifierMethod(descriptor.getWriteMethod());
                    return;
                }
            }
        } catch (IntrospectionException e) {
            throw new RuntimeException(e);
        }
    }

}
