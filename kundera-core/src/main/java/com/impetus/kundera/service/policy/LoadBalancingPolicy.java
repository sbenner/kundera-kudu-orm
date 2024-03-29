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
package com.impetus.kundera.service.policy;

import java.util.Collection;

/**
 * LoadBalancing policy interface for {@link RoundRobinBalancingPolicy} and {@link LeastActiveBalancingPolicy}.
 *
 * @author Kuldeep.mishra
 *
 */
public interface LoadBalancingPolicy {

    /**
     * Returns pool on the basis of loadbalancing policy.
     *
     * @param pools
     * @param excludeHosts
     * @return pool object.
     */
    Object getPool(Collection<Object> pools);
}
