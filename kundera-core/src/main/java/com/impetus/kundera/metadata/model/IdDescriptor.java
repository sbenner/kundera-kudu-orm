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
package com.impetus.kundera.metadata.model;

import javax.persistence.GenerationType;

/**
 * Class IdDescriptor holds all information about generating id.
 *
 * @author Kuldeep.kumar
 */
public class IdDescriptor {
    private GenerationType strategy;

    private TableGeneratorDescriptor tableDescriptor;

    private SequenceGeneratorDescriptor sequenceDescriptor;

    /**
     * @return the strategy
     */
    public GenerationType getStrategy() {
        return strategy;
    }

    /**
     * @param strategy the strategy to set
     */
    public void setStrategy(GenerationType strategy) {
        this.strategy = strategy;
    }

    /**
     * @return the tableDescriptor
     */
    public TableGeneratorDescriptor getTableDescriptor() {
        return tableDescriptor;
    }

    /**
     * @param tableDescriptor the tableDescriptor to set
     */
    public void setTableDescriptor(TableGeneratorDescriptor tableDescriptor) {
        this.tableDescriptor = tableDescriptor;
    }

    /**
     * @return the sequenceDescriptor
     */
    public SequenceGeneratorDescriptor getSequenceDescriptor() {
        return sequenceDescriptor;
    }

    /**
     * @param sequenceDescriptor the sequenceDescriptor to set
     */
    public void setSequenceDescriptor(SequenceGeneratorDescriptor sequenceDescriptor) {
        this.sequenceDescriptor = sequenceDescriptor;
    }

}
