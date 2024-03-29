/*******************************************************************************
 * * Copyright 2015 Impetus Infotech.
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
package com.impetus.kundera.client.crud.associations;

import javax.persistence.*;
import java.util.Set;

/**
 * @author Pragalbh Garg
 */
@Entity
@Table(name = "operating_system")
public class MobileOperatingSystem {
    @Id
    @Column(name = "os_id")
    private String id;

    @Column(name = "os_name")
    private String name;

    @OneToMany(cascade = {CascadeType.ALL}, fetch = FetchType.EAGER, mappedBy = "os")
    private Set<MobileHandset> handsets;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<MobileHandset> getHandsets() {
        return handsets;
    }

    public void setHandsets(Set<MobileHandset> handsets) {
        this.handsets = handsets;
    }

}
