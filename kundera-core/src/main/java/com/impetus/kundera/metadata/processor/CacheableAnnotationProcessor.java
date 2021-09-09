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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.Cacheable;

/**
 * The MetadataProcessor implementation to scan for EntityListener class/method
 * JPA Specifications: 1. EntityListeners classes must have a no-argument
 * constructor. 2. Callback methods can have any visibility. 3. Callback methods
 * must return void. 4. Callback methods must NOT throw any checked exception.
 * 5. ExternalCallback methods must accept only entity object. 6.
 * InternalCallback methods must NOT accept any parameter. 7. EntityListeners
 * are state-less. 8. EnternalCallbackMethods must be fired before
 * InternalCallbackMethods.
 *
 * @author animesh.kumar
 */

public class CacheableAnnotationProcessor implements MetadataProcessor {

    /**
     * the log used by this class.
     */
    private static Logger log = LoggerFactory.getLogger(CacheableAnnotationProcessor.class);

    /*
     * (non-Javadoc)
     *
     * @see
     * com.impetus.kundera.metadata.MetadataProcessor#process(java.lang.Class,
     * com.impetus.kundera.metadata.model.EntityMetadata)
     */
    @Override
    public final void process(final Class<?> entityClass, EntityMetadata metadata) {

        Cacheable cacheable = (Cacheable) entityClass.getAnnotation(Cacheable.class);

        if (null != cacheable) {
            metadata.setCacheable(cacheable.value());
        }
    }
}
