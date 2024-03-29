/*******************************************************************************
 *  * Copyright 2013 Impetus Infotech.
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
package com.impetus.kundera.persistence.event;

import javax.persistence.*;
import java.util.List;

/**
 * @author vivek.mishra
 * Address entity with internal call backs.
 */
@Entity
@Table(name = "ADDRESS", schema = "KunderaTest@kunderatest")
public class AddressEntityWithList {
    @Id
    private String addressId;

    @Column
    private String street;

    @Column
    private String city;

    @Column
    private String fullAddress;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<AddressEntityWithList> subaddresses;


    public AddressEntityWithList() {

    }

    public String getAddressId() {
        return addressId;
    }

    public void setAddressId(String addressId) {
        this.addressId = addressId;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    @PrePersist
    public void populateFullAddress() {
        this.fullAddress = street + "," + city;
    }

    public String getFullAddress() {
        return this.fullAddress;
    }

    public List<AddressEntityWithList> getSubaddresses() {
        return subaddresses;
    }

    public void setSubaddresses(List<AddressEntityWithList> subaddresses) {
        this.subaddresses = subaddresses;
    }


}
