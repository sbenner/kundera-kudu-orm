/**
 * Copyright 2013 Impetus Infotech.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.impetus.kundera.configure.schema.api;

import com.impetus.kundera.PersistenceProperties;
import junit.framework.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.HashMap;
import java.util.Map;

public class SchemaManagerTest {

    private String persistenceUnit = "metaDataTest";

    @Before
    public void setUp() {

    }

    @Test
    public void testCreate() {
        Map<String, Object> props = new HashMap<String, Object>();
        props.put(PersistenceProperties.KUNDERA_DDL_AUTO_PREPARE, "create");
        props.put(PersistenceProperties.KUNDERA_REPLICATION_FACTOR, "1");
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("metaDataTest", props);
        Assert.assertTrue(CoreSchemaManager.validateAction("create"));
        emf.close();
        Assert.assertTrue(CoreSchemaManager.validateAction("create"));
        Assert.assertFalse(CoreSchemaManager.validateAction("drop"));
    }

    @Test
    public void testCreateDrop() {
        Map<String, Object> props = new HashMap<String, Object>();
        props.put(PersistenceProperties.KUNDERA_DDL_AUTO_PREPARE, "create-drop");
        props.put(PersistenceProperties.KUNDERA_REPLICATION_FACTOR, "1");
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("metaDataTest", props);
        Assert.assertTrue(CoreSchemaManager.validateAction("create-drop"));
        Assert.assertFalse(CoreSchemaManager.validateAction("create"));

        emf.close();
        Assert.assertFalse(CoreSchemaManager.validateAction("create-drop"));
        Assert.assertTrue(CoreSchemaManager.validateAction("drop"));
    }

    @Test
    public void testUpdate() {
        Map<String, Object> props = new HashMap<String, Object>();
        props.put(PersistenceProperties.KUNDERA_DDL_AUTO_PREPARE, "update");
        props.put(PersistenceProperties.KUNDERA_REPLICATION_FACTOR, "1");
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("metaDataTest", props);
        Assert.assertFalse(CoreSchemaManager.validateAction("create-drop"));
        Assert.assertFalse(CoreSchemaManager.validateAction("create"));
        Assert.assertTrue(CoreSchemaManager.validateAction("update"));
        emf.close();
        Assert.assertTrue(CoreSchemaManager.validateAction("update"));
        Assert.assertFalse(CoreSchemaManager.validateAction("drop"));
    }

    @After
    public void tearDown() {

    }

}
