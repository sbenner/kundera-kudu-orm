/*******************************************************************************
 *  * Copyright 2016 Impetus Infotech.
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
package com.impetus.client.kudu;

import com.impetus.client.kudu.query.KuduDBEntityReader;
import com.impetus.client.kudu.schemamanager.KuduDBSchemaManager;
import com.impetus.kundera.KunderaException;
import com.impetus.kundera.client.Client;
import com.impetus.kundera.configure.schema.api.SchemaManager;
import com.impetus.kundera.loader.GenericClientFactory;
import com.impetus.kundera.metadata.model.PersistenceUnitMetadata;
import org.apache.kudu.client.KuduClient;
import org.apache.kudu.client.KuduException;

import java.util.Map;
import java.util.Properties;

/**
 * A factory for creating KuduDBClient objects.
 *
 * @author karthikp.manchala
 */
public class KuduDBClientFactory extends GenericClientFactory {

    /**
     * The kudu client.
     */
    private KuduClient kuduClient;

    /*
     * (non-Javadoc)
     *
     * @see
     * com.impetus.kundera.loader.ClientFactory#getSchemaManager(java.util.Map)
     */
    @Override
    public SchemaManager getSchemaManager(Map<String, Object> puProperties) {
        if (schemaManager == null) {
            initializePropertyReader();
            setExternalProperties(puProperties);
            schemaManager = new KuduDBSchemaManager(KuduDBClientFactory.class.getName(), puProperties, kunderaMetadata);
        }
        return schemaManager;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.impetus.kundera.loader.ClientLifeCycleManager#destroy()
     */
    @Override
    public void destroy() {
        try {
            this.kuduClient.close();
        } catch (KuduException e) {
            e.printStackTrace();
        }

        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.impetus.kundera.loader.GenericClientFactory#initialize(java.util.Map)
     */
    @Override
    public void initialize(Map<String, Object> puProperties) {
        reader = new KuduDBEntityReader(kunderaMetadata);
        setExternalProperties(puProperties);
        initializePropertyReader();
        PersistenceUnitMetadata pum = kunderaMetadata.getApplicationMetadata()
                .getPersistenceUnitMetadata(getPersistenceUnit());

        Properties pumProps = pum.getProperties();

        if (puProperties != null) {
            pumProps.putAll(puProperties);
        }

        String kuduMasterHost = pumProps.getProperty("kundera.nodes");

        String kuduMasterPort = pumProps.getProperty("kundera.port");

        String read = pumProps.getProperty("kudu.read.timeout");
        String op = pumProps.getProperty("kudu.op.timeout");

        Long readTimeout = null;
        Long opTimeout = null;
        if (read != null)
            readTimeout = Long.valueOf(read);

        if (op != null)
            opTimeout = Long.valueOf(op);


        if (kuduMasterHost == null || kuduMasterPort == null) {
            throw new KunderaException("Hostname/IP or Port is null.");
        }
        KuduClient.KuduClientBuilder builder = new KuduClient.KuduClientBuilder(
                kuduMasterHost + ":" + kuduMasterPort);
        if (read != null) {
            builder = builder.defaultSocketReadTimeoutMs(readTimeout * 1000);
        }
        if (op != null) {
            builder = builder.defaultOperationTimeoutMs(opTimeout * 1000);
        }
        kuduClient = builder.build();


    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.impetus.kundera.loader.GenericClientFactory#createPoolOrConnection()
     */
    @Override
    protected Object createPoolOrConnection() {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.impetus.kundera.loader.GenericClientFactory#instantiateClient(java.
     * lang.String)
     */
    @Override
    protected Client instantiateClient(String persistenceUnit) {
        return new KuduDBClient(kunderaMetadata, indexManager, reader, externalProperties, persistenceUnit,
                this.kuduClient, this.clientMetadata);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.impetus.kundera.loader.GenericClientFactory#isThreadSafe()
     */
    @Override
    public boolean isThreadSafe() {
        // TODO Auto-generated method stub
        return false;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.impetus.kundera.loader.GenericClientFactory#initializeLoadBalancer(
     * java.lang.String)
     */
    @Override
    protected void initializeLoadBalancer(String loadBalancingPolicyName) {
        // TODO Auto-generated method stub

    }

    /**
     * Initialize property reader.
     */
    private void initializePropertyReader() {
        if (propertyReader == null) {
            propertyReader = new KuduDBPropertyReader(externalProperties,
                    kunderaMetadata.getApplicationMetadata().getPersistenceUnitMetadata(getPersistenceUnit()));
            propertyReader.read(getPersistenceUnit());
        }
    }

}
