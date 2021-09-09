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
package com.impetus.kundera.persistence;

import com.impetus.kundera.cache.Cache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The Class EntityManagerSession.
 */
public class EntityManagerSession {

    /**
     * The Constant log.
     */
    private static final Logger LOG = LoggerFactory.getLogger(EntityManagerSession.class);

    /**
     * cache is used to store objects retrieved in this EntityManager session.
     */
    private Map<Object, Object> sessionCache;

    /**
     * The l2 cache.
     */
    private Cache l2Cache; // L2 Cache

    /**
     * Instantiates a new entity manager cache.
     *
     * @param cache the cache
     */
    public EntityManagerSession(Cache cache) {
        this.sessionCache = new ConcurrentHashMap<Object, Object>();
        setL2Cache(cache);
    }

    /**
     * Find in cache.
     *
     * @param <T>         the generic type
     * @param entityClass the entity class
     * @param id          the id
     * @return the t
     */
    @SuppressWarnings("unchecked")
    protected <T> T lookup(Class<T> entityClass, Object id) {
        String key = cacheKey(entityClass, id);
        LOG.debug("Reading from L1 >> " + key);
        T o = (T) sessionCache.get(key);

        // go to second-level cache
        if (o == null) {
            LOG.debug("Reading from L2 >> " + key);
            Cache c = (Cache) getL2Cache();
            if (c != null) {
                o = (T) c.get(key);
                if (o != null) {
                    LOG.debug("Found item in second level cache!");
                }
            }
        }
        return o;
    }

    /**
     * Store in L1 only.
     *
     * @param id     the id
     * @param entity the entity
     */
    protected void store(Object id, Object entity) {
        store(id, entity, Boolean.TRUE);
    }

    /**
     * Save to cache.
     *
     * @param id            the id
     * @param entity        the entity
     * @param spillOverToL2 the spill over to l2
     */
    protected void store(Object id, Object entity, boolean spillOverToL2) {
        String key = cacheKey(entity.getClass(), id);
        LOG.debug("Writing to L1 >> " + key);
        sessionCache.put(key, entity);

        if (spillOverToL2) {
            LOG.debug("Writing to L2 >>" + key);
            // save to second level cache
            Cache c = (Cache) getL2Cache();
            if (c != null) {
                c.put(key, entity);
            }
        }
    }

    /**
     * Removes the.
     *
     * @param <T>         the generic type
     * @param entityClass the entity class
     * @param id          the id
     */
    protected <T> void remove(Class<T> entityClass, Object id) {
        remove(entityClass, id, Boolean.TRUE);
    }

    /**
     * Removes the from cache.
     *
     * @param <T>           the generic type
     * @param entityClass   the entity class
     * @param id            the id
     * @param spillOverToL2 the spill over to l2
     */
    protected <T> void remove(Class<T> entityClass, Object id, boolean spillOverToL2) {
        String key = cacheKey(entityClass, id);
        LOG.debug("Removing from L1 >> " + key);
        Object o = sessionCache.remove(key);

        if (spillOverToL2) {
            LOG.debug("Removing from L2 >> " + key);
            Cache c = (Cache) getL2Cache();
            if (c != null) {
                c.evict(entityClass, key);
            }
        }
    }

    /**
     * Cache key.
     *
     * @param clazz the clazz
     * @param id    the id
     * @return the string
     */
    private String cacheKey(Class<?> clazz, Object id) {
        return clazz.getName() + "_" + id;
    }

    /**
     * Clear.
     */
    public final void clear() {
        sessionCache = new ConcurrentHashMap<Object, Object>();

        // Clear L2 Cahce
        if (getL2Cache() != null) {
            getL2Cache().evictAll();
        }
    }

    /**
     * Gets the l2 cache.
     *
     * @return the l2Cache
     */
    public Cache getL2Cache() {
        return l2Cache;
    }

    /**
     * Sets the l2 cache.
     *
     * @param l2Cache the l2Cache to set
     */
    public void setL2Cache(Cache l2Cache) {
        this.l2Cache = l2Cache;
    }

}
