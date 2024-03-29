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
package com.impetus.kundera.metadata.model.type;

import javax.persistence.metamodel.MappedSuperclassType;
import javax.persistence.metamodel.Type;

/**
 * Default implementation of {@link MappedSuperclassType}
 *
 * <code> DefaultMappedSuperClass</code> implements
 * <code>MappedSuperclassType</code> interface, invokes constructor with
 * PersistenceType.MAPPED_SUPERCLASS. Default implementation of {@link Type}
 * interface is provided by {@link AbstractType}
 *
 * @param <X> Embeddable generic java type.
 * @author vivek.mishra
 */
public class DefaultMappedSuperClass<X> extends AbstractIdentifiableType<X> implements MappedSuperclassType<X> {

    /**
     * Default constructor using fields.
     */
    public DefaultMappedSuperClass(Class<X> clazz, javax.persistence.metamodel.Type.PersistenceType persistenceType,
                                   AbstractIdentifiableType<? super X> superClazzType) {
        super(clazz, persistenceType, superClazzType);
    }

}
