/*******************************************************************************
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
package com.impetus.kundera.proxy;

import javax.persistence.PersistenceException;

import com.impetus.kundera.persistence.PersistenceDelegator;

/**
 * Handles fetching of the underlying entity for a proxy.
 * 
 * @author Gavin King
 * @author Steve Ebersole
 */
public interface LazyInitializer
{

    /**
     * Initialize the proxy, fetching the target entity if necessary.
     * 
     * @throws PersistenceException
     *             the persistence exception
     */
    public void initialize() throws PersistenceException;

    /**
     * Retrieve the identifier value for the enity our owning proxy represents.
     * 
     * @return The identifier value.
     */
    public Object getIdentifier();

    /**
     * Set the identifier value for the enity our owning proxy represents.
     * 
     * @param id
     *            The identifier value.
     */
    public void setIdentifier(Object id);

    /**
     * The entity-name of the entity our owning proxy represents.
     * 
     * @return The entity-name.
     */
    public String getEntityName();

    /**
     * Get the actual class of the entity. Generally, {@link #getEntityName()}
     * should be used instead.
     * 
     * @return The actual entity class.
     */
    public Class<?> getPersistentClass();

    /**
     * Is the proxy uninitialzed?.
     * 
     * @return True if uninitialized; false otherwise.
     */
    public boolean isUninitialized();

    /**
     * Get the session to which this proxy is associated, or null if it is not
     * attached.
     * 
     * @return The associated session.
     */
    public PersistenceDelegator getPersistenceDelegator();

    /**
     * Unset this initializer's reference to session. It is assumed that the
     * caller is also taking care or cleaning up the owning proxy's reference in
     * the persistence context.
     */
    public void unsetPersistenceDelegator();

    /**
     * Sets the unwrap.
     * 
     * @param unwrap
     *            the new unwrap
     */
    public void setUnwrap(boolean unwrap);

    /**
     * Checks if is unwrap.
     * 
     * @return true, if is unwrap
     */
    public boolean isUnwrap();

    public abstract Object getImplementation();

    public abstract void setImplementation(Object paramObject);

    public void setOwner(Object owner) throws PersistenceException;

    public Object getOwner() throws PersistenceException;

    public void setInitialized(boolean initialized);
}
