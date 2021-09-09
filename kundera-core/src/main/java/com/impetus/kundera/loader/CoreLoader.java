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
package com.impetus.kundera.loader;

import com.impetus.kundera.metadata.model.CoreMetadata;
import com.impetus.kundera.persistence.EntityManagerFactoryImpl.KunderaMetadata;
import com.impetus.kundera.proxy.cglib.CglibLazyInitializerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class CoreLoader.
 *
 * @author amresh.singh
 */
public class CoreLoader {

    /**
     * The log.
     */
    private static Logger log = LoggerFactory.getLogger(CoreLoader.class);

    /**
     * Load.
     */
    public void load(final KunderaMetadata kunderaMetadata) {
        log.info("Loading Kundera Core Metdata ... ");

        CoreMetadata coreMetadata = new CoreMetadata();
        coreMetadata.setLazyInitializerFactory(new CglibLazyInitializerFactory());
        kunderaMetadata.setCoreMetadata(coreMetadata);
    }
}
