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
package com.impetus.kundera.persistence.event;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.impetus.kundera.metadata.model.EntityMetadata;

/**
 * The Class EntityEventDispatcher.
 */
public class EntityEventDispatcher
{

    /** The Constant log. */
    private static final Logger log = LoggerFactory.getLogger(EntityManager.class);

    /**
     * Fire event listeners.
     * 
     * @param metadata
     *            the metadata
     * @param entity
     *            the entity
     * @param event
     *            the event
     * @throws PersistenceException
     *             the persistence exception
     */
    public void fireEventListeners(EntityMetadata metadata, Object entity, Class<?> event)
    {

        // handle external listeners first
        List<? extends CallbackMethod> callBackMethods = metadata.getCallbackMethods(event);

        if (null != callBackMethods && !callBackMethods.isEmpty() && null != entity)
        {
            log.debug("Callback >> " + event.getSimpleName() + " on " + metadata.getEntityClazz().getName());
            for (CallbackMethod callback : callBackMethods)
            {
                log.debug("Firing >> " + callback);

                callback.invoke(entity);

            }
        }
    }
}
