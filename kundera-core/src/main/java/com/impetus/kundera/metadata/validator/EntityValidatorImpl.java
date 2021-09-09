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
package com.impetus.kundera.metadata.validator;

import com.impetus.kundera.client.ClientResolver;
import com.impetus.kundera.configure.schema.api.SchemaManager;
import com.impetus.kundera.metadata.KunderaMetadataManager;
import com.impetus.kundera.metadata.model.EntityMetadata;
import com.impetus.kundera.persistence.EntityManagerFactoryImpl.KunderaMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.*;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Validates entity for JPA rules.
 *
 * @author animesh.kumar
 */
public class EntityValidatorImpl implements EntityValidator {

    /**
     * The Constant log.
     */
    private static final Logger log = LoggerFactory.getLogger(EntityValidatorImpl.class);

    /**
     * cache for validated classes.
     */
    private List<Class<?>> classes = new ArrayList<Class<?>>();

    private Map<String, Object> puProperties;

    /**
     * @param puPropertyMap
     */
    public EntityValidatorImpl(Map puPropertyMap) {
        this.puProperties = puPropertyMap;
    }

    /**
     * @param externalPropertyMap
     */
    public EntityValidatorImpl() {
        this(null);
    }

    /**
     * Checks the validity of a class for Cassandra entity.
     *
     * @param clazz validates this class
     * @return returns 'true' if valid
     */
    @Override
    // TODO: reduce Cyclomatic complexity
    public final void validate(final Class<?> clazz) {

        if (classes.contains(clazz)) {
            return;
        }

        if (log.isDebugEnabled())
            log.debug("Validating " + clazz.getName());

        // Is Entity?
//        if (clazz.isAnnotationPresent(MappedSuperclass.class))
//        {
//            throw new InvalidEntityDefinitionException("JPA operation over MappedSuperclass are not allowed" + clazz);
//        }


        // Is Entity?
        if (!clazz.isAnnotationPresent(Entity.class)) {
            throw new InvalidEntityDefinitionException(clazz.getName() + " is not annotated with @Entity.");
        }


        // TODO:: add validation for MappedSuperClass.

//        // Must be annotated with @Table
//        if (!clazz.isAnnotationPresent(Table.class))
//        {
//            throw new InvalidEntityDefinitionException(clazz.getName() + " must be annotated with @Table.");
//        }

        // must have a default no-argument constructor
        try {
            clazz.getConstructor();
        } catch (NoSuchMethodException nsme) {
            throw new InvalidEntityDefinitionException(clazz.getName()
                    + " must have a default no-argument constructor.");
        }

        // Check for @Key and ensure that there is just 1 @Key field of String
        // type.
        List<Field> keys = new ArrayList<Field>();
        for (Field field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(Id.class) && field.isAnnotationPresent(EmbeddedId.class)) {
                throw new InvalidEntityDefinitionException(clazz.getName()
                        + " must have either @Id field or @EmbeddedId field");
            }

            if (field.isAnnotationPresent(Id.class)) {
                keys.add(field);
                // validate @GeneratedValue annotation if given
                if (field.isAnnotationPresent(GeneratedValue.class)) {
                    validateGeneratedValueAnnotation(clazz, field);
                }
            } else if (field.isAnnotationPresent(EmbeddedId.class)) {
                keys.add(field);
            }
        }

        if (keys.size() < 0) {
            throw new InvalidEntityDefinitionException(clazz.getName() + " must have an @Id field.");
        } else if (keys.size() > 1) {
            throw new InvalidEntityDefinitionException(clazz.getName() + " can only have 1 @Id field.");
        }

        // save in cache

        classes.add(clazz);
    }

    /**
     * validate generated value annotation if given.
     *
     * @param clazz
     * @param field
     */
    private void validateGeneratedValueAnnotation(final Class<?> clazz, Field field) {
        Table table = clazz.getAnnotation(Table.class);
        // Still we need to validate for this after post metadata population.
        if (table != null) {
            String schemaName = table.schema();
            if (schemaName != null && schemaName.indexOf('@') > 0) {
                schemaName = schemaName.substring(0, schemaName.indexOf('@'));
                GeneratedValue generatedValue = field.getAnnotation(GeneratedValue.class);
                if (generatedValue != null && generatedValue.generator() != null
                        && !generatedValue.generator().isEmpty()) {
                    if (!(field.isAnnotationPresent(TableGenerator.class)
                            || field.isAnnotationPresent(SequenceGenerator.class)
                            || clazz.isAnnotationPresent(TableGenerator.class) || clazz
                            .isAnnotationPresent(SequenceGenerator.class))) {
                        log.error("Unknown Id.generator{}: for class{}", generatedValue.generator(), clazz);
                        throw new IllegalArgumentException("Unknown Id.generator: " + generatedValue.generator());
                    } else {
                        checkForGenerator(clazz, field, generatedValue, schemaName);
                    }
                }
            }
        }
    }

    /**
     * Validate for generator.
     *
     * @param clazz
     * @param field
     * @param generatedValue
     * @param schemaName
     */
    private void checkForGenerator(final Class<?> clazz, Field field, GeneratedValue generatedValue, String schemaName) {
        TableGenerator tableGenerator = field.getAnnotation(TableGenerator.class);
        SequenceGenerator sequenceGenerator = field.getAnnotation(SequenceGenerator.class);
        if (tableGenerator == null || !tableGenerator.name().equals(generatedValue.generator())) {
            tableGenerator = clazz.getAnnotation(TableGenerator.class);
        }
        if (sequenceGenerator == null || !sequenceGenerator.name().equals(generatedValue.generator())) {
            sequenceGenerator = clazz.getAnnotation(SequenceGenerator.class);
        }

        if ((tableGenerator == null && sequenceGenerator == null)
                || (tableGenerator != null && !tableGenerator.name().equals(generatedValue.generator()))
                || (sequenceGenerator != null && !sequenceGenerator.name().equals(generatedValue.generator()))) {
            throw new IllegalArgumentException("Unknown Id.generator: " + generatedValue.generator());
        } else if ((tableGenerator != null && !tableGenerator.schema().isEmpty() && !tableGenerator.schema().equals(
                schemaName))
                || (sequenceGenerator != null && !sequenceGenerator.schema().isEmpty() && !sequenceGenerator.schema()
                .equals(schemaName))) {
            throw new InvalidEntityDefinitionException("Generator " + generatedValue.generator() + " in entity : "
                    + clazz.getName() + " has different schema name ,it should be same as entity have");
        }
    }

    @Override
    public void validateEntity(Class<?> clazz, final KunderaMetadata kunderaMetadata) {
        EntityMetadata metadata = KunderaMetadataManager.getEntityMetadata(kunderaMetadata, clazz);
        if (metadata != null) {
            SchemaManager schemaManager = ClientResolver.getClientFactory(metadata.getPersistenceUnit())
                    .getSchemaManager(puProperties);
            if (schemaManager != null && !schemaManager.validateEntity(clazz)) {
                log.warn("Validation for : " + clazz + " failed , any operation on this class will result in fail.");
            }
        }
    }
}
