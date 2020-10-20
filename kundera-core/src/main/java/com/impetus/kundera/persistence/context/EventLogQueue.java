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

import com.impetus.kundera.KunderaException;
import com.impetus.kundera.persistence.context.EventLog.EventType;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The Class EventLogQueue.
 *
 * @author vivek.mishra
 */
class EventLogQueue {

    private final static int logSize = 10000; //keep only this number for rollback
    /**
     * The insert events.
     */
    private static final Map<Object, EventLog> insertEvents = new ConcurrentHashMap<Object, EventLog>();

    /**
     * The update events.
     */
    private static final Map<Object, EventLog> updateEvents = new ConcurrentHashMap<Object, EventLog>();

    /**
     * The delete events.
     */
    private static final Map<Object, EventLog> deleteEvents = new ConcurrentHashMap<Object, EventLog>();

    /**
     * On event.
     *
     * @param log       the log
     * @param eventType the event type
     */
    void onEvent(EventLog log, EventType eventType) {

        switch (eventType) {
            case INSERT:
                if (insertEvents.size() < logSize) {
                    onInsert(log);
                } else {
                    insertEvents.clear();
                }
                break;

            case UPDATE:
                if (updateEvents.size() < logSize) {
                    onUpdate(log);
                } else {
                    updateEvents.clear();
                }

                break;

            case DELETE:
                if (deleteEvents.size() < logSize) {
                    onDelete(log);
                } else {
                    deleteEvents.clear();
                }
                break;

            default:

                throw new KunderaException("Invalid event type:" + eventType);
        }

    }

    /**
     * On delete.
     *
     * @param log the log
     */
    private void onDelete(EventLog log) {

        deleteEvents.put(log.getEntityId(), log);

    }

    /**
     * On update.
     *
     * @param log the log
     */
    private void onUpdate(EventLog log) {

        updateEvents.put(log.getEntityId(), log);
    }

    /**
     * On insert.
     *
     * @param log the log
     */
    private void onInsert(EventLog log) {

        insertEvents.put(log.getEntityId(), log);

    }

    /**
     * Clear.
     */
    void clear() {
        insertEvents.clear();
        updateEvents.clear();
        deleteEvents.clear();
    }

    /**
     * Gets the insert events.
     *
     * @return the insert events
     */
    Map<Object, EventLog> getInsertEvents() {
        return insertEvents;
    }

    /**
     * Gets the update events.
     *
     * @return the update events
     */
    Map<Object, EventLog> getUpdateEvents() {
        return updateEvents;
    }

    /**
     * Gets the delete events.
     *
     * @return the delete events
     */
    Map<Object, EventLog> getDeleteEvents() {
        return deleteEvents;
    }

}
