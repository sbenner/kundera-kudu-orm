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
package com.impetus.kundera.persistence.context;

import com.impetus.kundera.graph.Node;

/**
 * Logs CRUD events,
 * 
 * @author vivek.mishra
 * 
 */
public class EventLog
{

    private EventType eventType;

    private long timeinMillies;

    private Node node;

    EventLog(EventType eventType, Node transactional)
    {
        this.node = transactional;
        this.eventType = eventType;
        this.timeinMillies = System.currentTimeMillis();
    }

    Node getSavePointData()
    {
        return node.getOriginalNode();
    }

    /**
     * @return the entityId
     */
    Object getEntityId()
    {
        return node.getNodeId();
    }

    /**
     * @return the eventType
     */
    EventType getEventType()
    {
        return eventType;
    }

    /**
     * @return the timeinMillies
     */
    long getTimeinMillies()
    {
        return timeinMillies;
    }

    /**
     * @return the node
     */
    Node getNode()
    {
        return node;
    }

    public enum EventType
    {
        INSERT, UPDATE, DELETE;
    }
}
