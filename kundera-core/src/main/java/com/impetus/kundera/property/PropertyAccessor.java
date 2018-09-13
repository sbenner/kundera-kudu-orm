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
package com.impetus.kundera.property;

import java.lang.reflect.Field;

/**
 * Interface to access {@link Field} property of a java class.
 * 
 * @param <T>
 *            the generic type
 * @author animesh.kumar
 */
public interface PropertyAccessor<T>
{

    /**
     * From bytes.
     * 
     * @param b
     *            the b
     * 
     * @return the T
     * 
     * @throws PropertyAccessException
     *             the property access exception
     */
    T fromBytes(Class targetClass, byte[] b);

    /**
     * To bytes.
     * 
     * @param object
     *            the object
     * 
     * @return the byte[]
     * 
     * @throws PropertyAccessException
     *             the property access exception
     */
    byte[] toBytes(Object object);

    /**
     * Converts Object to String. Normally, this will be object.toString() But
     * in some cases, this might be different.
     * 
     * @param object
     *            the object
     * 
     * @return the string
     */
    String toString(Object object);

    /**
     * Converts string representation to the data object whose type is T.
     * 
     * @param s
     *            the s
     * @return the t
     * @throws PropertyAccessException
     *             When string can't be converted to specified type, usually as
     *             a result of NumberFormatException
     */
    T fromString(Class targetClass, String s);

    /**
     * Gets copy of object
     * 
     * @param object
     * @return
     */
    T getCopy(Object object);

    /**
     * @param clazz
     * 
     */
    Object getInstance(Class<?> clazz);

}
