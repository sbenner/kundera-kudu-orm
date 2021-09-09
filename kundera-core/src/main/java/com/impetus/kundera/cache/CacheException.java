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
package com.impetus.kundera.cache;

import com.impetus.kundera.KunderaException;

/**
 * When something goes wrong in cache.
 *
 * @author animesh.kumar
 */
public class CacheException extends KunderaException {

    /**
     * The Constant serialVersionUID.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Instantiates a new cache exception.
     *
     * @param s the s
     */
    public CacheException(final String s) {
        super(s);
    }

    /**
     * Instantiates a new cache exception.
     *
     * @param s the s
     * @param e the e
     */
    public CacheException(final String s, final Throwable e) {
        super(s, e);
    }

    /**
     * Instantiates a new cache exception.
     *
     * @param e the e
     */
    public CacheException(final Throwable e) {
        super(e);
    }
}
