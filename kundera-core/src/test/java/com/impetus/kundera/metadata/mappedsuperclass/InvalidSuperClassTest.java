/*******************************************************************************
 * * Copyright 2013 Impetus Infotech.
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
package com.impetus.kundera.metadata.mappedsuperclass;

import com.impetus.kundera.loader.MetamodelLoaderException;
import junit.framework.Assert;
import org.apache.commons.lang.StringUtils;
import org.junit.Test;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 * @author vivek.mishra
 * <p>
 * MappedSuper class junit.
 */
public class InvalidSuperClassTest {
    private String persistenceUnit = "invalidmappedsu";

    @Test
    public void setup() {
        try {
            EntityManagerFactory emf = Persistence.createEntityManagerFactory(persistenceUnit);
            Assert.fail("Should have gone to catch block!");
        } catch (MetamodelLoaderException mlex) {
            Assert.assertTrue(StringUtils.startsWith(mlex.getMessage(),
                    "Class:class com.impetus.kundera.metadata.mappedsuperclass.InvalidPersonEntityis annotated with @MappedSuperClass and @Entity not allowed"));
        }
    }

}
