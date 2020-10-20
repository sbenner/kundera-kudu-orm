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

import com.impetus.kundera.metadata.model.EntityMetadata;
import com.impetus.kundera.metadata.model.IdDescriptor;
import com.impetus.kundera.metadata.model.SequenceGeneratorDescriptor;
import com.impetus.kundera.metadata.model.TableGeneratorDescriptor;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.SequenceGenerator;
import javax.persistence.TableGenerator;
import java.lang.reflect.Field;
import java.util.Map;

public class GeneratedValueProcessor {
    public void process(Class<?> clazz, Field idField, EntityMetadata m,
                        Map<String, IdDescriptor> entityNameToKeyDescriptorMap) {
        IdDescriptor keyValue = new IdDescriptor();

        GeneratedValue value = idField.getAnnotation(GeneratedValue.class);
        String generatorName = value.generator();
        GenerationType generationType = value.strategy();

        switch (generationType) {
            case TABLE:
                TableGeneratorDescriptor tgd = processTableGenerator(clazz, idField, m, generatorName);
                keyValue.setTableDescriptor(tgd);
                keyValue.setStrategy(GenerationType.TABLE);
                break;
            case SEQUENCE:
                SequenceGeneratorDescriptor sgd = processSequenceGenerator(clazz, idField, m, generatorName);
                keyValue.setSequenceDescriptor(sgd);
                keyValue.setStrategy(GenerationType.SEQUENCE);
                break;
            case IDENTITY:
                keyValue.setStrategy(GenerationType.IDENTITY);
                break;
            case AUTO:
                // No need of Any Generator
                keyValue.setStrategy(GenerationType.AUTO);
                break;
        }
        entityNameToKeyDescriptorMap.put(clazz.getName(), keyValue);
    }

    private SequenceGeneratorDescriptor processSequenceGenerator(Class<?> clazz, Field idField, EntityMetadata m,
                                                                 String generatorName) {
        SequenceGeneratorDescriptor sgd = null;
        if (!generatorName.isEmpty()) {
            SequenceGenerator sequenceGenerator = idField.getAnnotation(SequenceGenerator.class);
            if (sequenceGenerator == null || !sequenceGenerator.name().equals(generatorName)) {
                sequenceGenerator = clazz.getAnnotation(SequenceGenerator.class);
            }
            sgd = new SequenceGeneratorDescriptor(sequenceGenerator, m.getSchema());
        } else {
            sgd = new SequenceGeneratorDescriptor(m.getSchema());
        }
        return sgd;
    }

    private TableGeneratorDescriptor processTableGenerator(Class<?> clazz, Field idField, EntityMetadata m,
                                                           String generatorName) {
        TableGeneratorDescriptor tgd = null;
        if (!generatorName.isEmpty()) {
            TableGenerator tableGenerator = idField.getAnnotation(TableGenerator.class);
            if (tableGenerator == null || !tableGenerator.name().equals(generatorName)) {
                tableGenerator = clazz.getAnnotation(TableGenerator.class);
            }
            tgd = new TableGeneratorDescriptor(tableGenerator, m.getSchema(), m.getTableName());
        } else {
            tgd = new TableGeneratorDescriptor(m.getSchema(), m.getTableName());
        }
        return tgd;
    }
}
