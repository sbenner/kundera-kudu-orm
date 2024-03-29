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
package com.impetus.kundera.metadata.entities;

import javax.persistence.*;

/**
 * @author vivek.mishra
 */
@Entity
@Table(name = "table", schema = "testSchema@keyspace")
public class OToOOwnerEntity {

    @Id
    @Column(name = "ROW_KEY")
    private byte rowKey;

    @Column(name = "name")
    private String name;

    @Column(name = "AMOUNT")
    private int amount;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @Column(name = "Association_ID")
    private AssociationEntity association;

    /**
     * @return the rowKey
     */
    public byte getRowKey() {
        return rowKey;
    }

    /**
     * @param rowKey the rowKey to set
     */
    public void setRowKey(byte rowKey) {
        this.rowKey = rowKey;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the amount
     */
    public int getAmount() {
        return amount;
    }

    /**
     * @param amount the amount to set
     */
    public void setAmount(int amount) {
        this.amount = amount;
    }

    /**
     * @return the association
     */
    public AssociationEntity getAssociation() {
        return association;
    }

    /**
     * @param association the association to set
     */
    public void setAssociation(AssociationEntity association) {
        this.association = association;
    }

}
