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
package com.impetus.kundera.client;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.impetus.kundera.PersistenceProperties;
import com.impetus.kundera.loader.ClientFactory;
import com.impetus.kundera.loader.GenericClientFactory;
import com.impetus.kundera.metadata.model.PersistenceUnitMetadata;
import com.impetus.kundera.persistence.EntityManagerFactoryImpl.KunderaMetadata;

/**
 * Resolver class for client. It instantiates client factory and discover
 * specific client.
 * 
 * @author vivek.mishra
 */
public final class ClientResolver
{

    // /** The client factories. */
    static Map<String, ClientFactory> clientFactories = new ConcurrentHashMap<String, ClientFactory>();

    /** logger instance. */
    private static final Logger logger = LoggerFactory.getLogger(ClientResolver.class);

    /**
     * Gets the client factory.
     * 
     * @param persistenceUnit
     *            the persistence unit
     * @return the client factory
     */
    public static ClientFactory getClientFactory(String persistenceUnit, Map<String, Object> puProperties,final KunderaMetadata kunderaMetadata)
    {
        ClientFactory clientFactory = instantiateClientFactory(persistenceUnit, puProperties, kunderaMetadata);
        clientFactories.put(persistenceUnit, clientFactory);
        return clientFactory;
    }

    /**
     * Creates new instance of client factory for given persistence unit.
     * 
     * @param persistenceUnit
     * @param puProperties
     * @param clientFactory
     * @return new instance of clientFactory
     */
    private static ClientFactory instantiateClientFactory(String persistenceUnit, Map<String, Object> puProperties,
            final KunderaMetadata kunderaMetadata)
    {
        ClientFactory clientFactory = null;
        logger.info("Initializing client factory for: " + persistenceUnit);
        PersistenceUnitMetadata persistenceUnitMetadata = kunderaMetadata.getApplicationMetadata()
                .getPersistenceUnitMetadata(persistenceUnit);
        String kunderaClientFactory = puProperties != null ? (String) puProperties
                .get(PersistenceProperties.KUNDERA_CLIENT_FACTORY) : null;
        if (kunderaClientFactory == null)
        {
            kunderaClientFactory = persistenceUnitMetadata.getProperties().getProperty(
                    PersistenceProperties.KUNDERA_CLIENT_FACTORY);
        }

        if (kunderaClientFactory == null)
        {
            throw new ClientResolverException(
                    "<kundera.client.lookup.class> is missing from persistence.xml, please provide specific client factory. e.g., <property name=\"kundera.client.lookup.class\" value=\"com.impetus.client.cassandra.pelops.PelopsClientFactory\" />");
        }
        try
        {
            clientFactory = (ClientFactory) Class.forName(kunderaClientFactory).newInstance();

            Method m = GenericClientFactory.class.getDeclaredMethod("setPersistenceUnit", String.class);
            if (!m.isAccessible())
            {
                m.setAccessible(true);
            }

            m.invoke(clientFactory, persistenceUnit);

            m = GenericClientFactory.class.getDeclaredMethod("setExternalProperties", Map.class);
            if (!m.isAccessible())
            {
                m.setAccessible(true);
            }

            m.invoke(clientFactory, puProperties);
            
            m = GenericClientFactory.class.getDeclaredMethod("setKunderaMetadata", KunderaMetadata.class);
            if (!m.isAccessible())
            {
                m.setAccessible(true);
            }

            m.invoke(clientFactory, kunderaMetadata);
            
        }
        catch (InstantiationException e)
        {
            onError(e);
        }
        catch (IllegalAccessException e)
        {
            onError(e);
        }
        catch (ClassNotFoundException e)
        {
            onError(e);
        }
        catch (SecurityException e)
        {
            onError(e);
        }
        catch (NoSuchMethodException e)
        {
            onError(e);
        }
        catch (IllegalArgumentException e)
        {
            onError(e);
        }
        catch (InvocationTargetException e)
        {
            onError(e);
        }

        if (clientFactory == null)
        {
            logger.error("Client Factory Not Configured For Specified Client Type : ");
            throw new ClientResolverException("Client Factory Not Configured For Specified Client Type.");
        }
        logger.info("Finishing factory initialization");
        return clientFactory;
    }

    public static ClientFactory getClientFactory(String pu)
    {
        ClientFactory clientFactory = clientFactories.get(pu);
        if (clientFactory != null)
        {
            return clientFactory;
        }
        logger.error("Client Factory Not Configured For Specified Client Type : ");
        throw new ClientResolverException("Client Factory Not Configured For Specified Client Type.");
    }

    /**
     * @param e
     */
    private static void onError(Exception e)
    {
        logger.error("Error while initializing client factory, Caused by: .", e.getMessage());
        throw new ClientResolverException(e);
    }
}
