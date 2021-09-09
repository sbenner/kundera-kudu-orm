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
package com.impetus.kundera.property.accessor;

import com.impetus.kundera.property.PropertyAccessException;
import com.impetus.kundera.property.PropertyAccessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;

/**
 * The Class LongAccessor.
 *
 * @author animesh.kumar
 */
public class LongAccessor implements PropertyAccessor<Long> {

    private final static Logger log = LoggerFactory.getLogger(LongAccessor.class);

    /*
     * (non-Javadoc)
     *
     * @see com.impetus.kundera.property.PropertyAccessor#fromBytes(byte[])
     */
    @Override
    public final Long fromBytes(Class targetClass, byte[] bytes) {
        if (bytes == null || bytes.length != 8) {
            if (log.isWarnEnabled()) {
                log.warn("Bytes length not equal to 8");
            }
            return null;
        }
        return (ByteBuffer.wrap(bytes).getLong());
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.impetus.kundera.property.PropertyAccessor#toBytes(java.lang.Object)
     */
    @Override
    public final byte[] toBytes(Object object) {
        if (object != null) {
            Long l = (Long) object;
            ByteBuffer buffer = ByteBuffer.allocate(8);
            buffer.putLong(l);
            return buffer.array();
        }
        if (log.isWarnEnabled()) {
            log.warn("Object is null.");
        }
        return null;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.impetus.kundera.property.PropertyAccessor#toString(java.lang.Object)
     */
    @Override
    public final String toString(Object object) {
        return object != null ? object.toString() : null;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.impetus.kundera.property.PropertyAccessor#fromString(java.lang.String
     * )
     */
    @Override
    public Long fromString(Class targetClass, String s) {
        try {
            if (s == null) {
                if (log.isWarnEnabled()) {
                    log.warn("String s is null.");
                }
                return null;
            }
            Long l = new Long(s);
            return l;
        } catch (NumberFormatException e) {
            log.error("Number format exception, Caused by {}.", e);
            throw new PropertyAccessException(e);
        }
    }

    @Override
    public Long getCopy(Object object) {
        return object != null ? (Long) object : null;
    }

    public Long getInstance(Class<?> clazz) {
        return Long.MAX_VALUE;
    }
}
