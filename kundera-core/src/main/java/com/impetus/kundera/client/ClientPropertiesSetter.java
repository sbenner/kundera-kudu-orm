/**
 * Copyright 2012 Impetus Infotech.
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
package com.impetus.kundera.client;

import java.util.Map;

/**
 * Defines methods for setting client properties
 *
 * @author amresh.singh
 */
public interface ClientPropertiesSetter {

    /**
     *
     * @param client
     * @param properties
     */
    void populateClientProperties(Client client, Map<String, Object> properties);

}
