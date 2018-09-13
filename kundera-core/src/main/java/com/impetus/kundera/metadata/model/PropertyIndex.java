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

import java.lang.reflect.Field;

/**
 * Contains Index information of a field.
 * 
 * @author animesh.kumar
 */
public final class PropertyIndex
{

    /** The name. */
    private String name;

    /** The property. */
    private Field property;

    private String indexType;

    /** The boost. */
    private float boost = 1.0f;

    private Integer max;

    private Integer min;

    /**
     * Instantiates a new property index.
     * 
     * @param property
     *            the property
     * @param name
     *            the name
     */
    public PropertyIndex(Field property, String name, String indexType)
    {
        this.property = property;
        this.name = name;
        this.indexType = indexType;
    }

    /**
     * Gets the name.
     * 
     * @return the name
     */
    public String getName()
    {
        return name;
    }

    /**
     * Gets the property.
     * 
     * @return the property
     */
    public Field getProperty()
    {
        return property;
    }

    /**
     * Gets the boost.
     * 
     * @return the boost
     */
    public float getBoost()
    {
        return boost;
    }

    /**
     * Sets the boost.
     * 
     * @param boost
     *            the new boost
     */
    public void setBoost(float boost)
    {
        this.boost = boost;
    }

    /**
     * @return the indexType
     */
    public String getIndexType()
    {
        return indexType;
    }

    /**
     * @return the max
     */
    public Integer getMax()
    {
        return max;
    }

    /**
     * @param max
     *            the max to set
     */
    public void setMax(Integer max)
    {
        this.max = max;
    }

    /**
     * @return the min
     */
    public Integer getMin()
    {
        return min;
    }

    /**
     * @param min
     *            the min to set
     */
    public void setMin(Integer min)
    {
        this.min = min;
    }

}