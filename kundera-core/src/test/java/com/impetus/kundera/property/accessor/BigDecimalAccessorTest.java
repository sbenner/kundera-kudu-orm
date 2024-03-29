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
package com.impetus.kundera.property.accessor;

import com.impetus.kundera.property.PropertyAccessor;
import junit.framework.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;

/**
 * @author amresh.singh
 */
public class BigDecimalAccessorTest {
    PropertyAccessor<BigDecimal> accessor;

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        accessor = new BigDecimalAccessor();
    }

    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {
        accessor = null;
    }

    /**
     * Test method for {@link com.impetus.kundera.property.accessor.BigDecimalAccessor#fromBytes(java.lang.Class, byte[])}.
     */
    @Test
    public void testFromBytes() {
        Assert.assertNull(accessor.fromBytes(BigDecimal.class, null));

        BigDecimal v1 = new BigDecimal(1111111111.11111);
        byte[] b = accessor.toBytes(v1);

        BigDecimal v2 = accessor.fromBytes(BigDecimal.class, b);
        Assert.assertEquals(v1, v2);

    }

    /**
     * Test method for {@link com.impetus.kundera.property.accessor.BigDecimalAccessor#toBytes(java.lang.Object)}.
     */
    @Test
    public void testToBytes() {
        Assert.assertNull(accessor.toString(null));
        BigDecimal v1 = new BigDecimal(1111111111);
        byte[] b = accessor.toBytes(v1);

        BigDecimal v2 = accessor.fromBytes(BigDecimal.class, b);
        Assert.assertEquals(v1, v2);
    }

    /**
     * Test method for {@link com.impetus.kundera.property.accessor.BigDecimalAccessor#toString(java.lang.Object)}.
     */
    @Test
    public void testToStringObject() {
        Assert.assertNull(accessor.toString(null));
        BigDecimal v1 = new BigDecimal(1111111111);
        String s1 = v1.toString();

        String s2 = accessor.toString(v1);
        Assert.assertTrue(s1.equals(s2));
    }

    /**
     * Test method for {@link com.impetus.kundera.property.accessor.BigDecimalAccessor#fromString(java.lang.Class, java.lang.String)}.
     */
    @Test
    public void testFromString() {
        Assert.assertNull(accessor.fromString(BigDecimal.class, null));

        BigDecimal v1 = new BigDecimal(1111111111);
        String s = v1.toString();

        BigDecimal v2 = accessor.fromString(BigDecimal.class, s);

        Assert.assertEquals(v1, v2);

    }

    /**
     * Test method for {@link com.impetus.kundera.property.accessor.BigDecimalAccessor#getCopy(java.lang.Object)}.
     */
    @Test
    public void testGetCopy() {
        Assert.assertNull(accessor.getCopy(null));

        BigDecimal v1 = new BigDecimal(1111111111);
        BigDecimal v2 = accessor.getCopy(v1);

        Assert.assertEquals(v1, v2);
    }

    /**
     * Test method for {@link com.impetus.kundera.property.accessor.BigDecimalAccessor#getInstance(java.lang.Class)}.
     */
    @Test
    public void testGetInstance() {
        Assert.assertEquals(BigDecimal.TEN, accessor.getInstance(BigDecimal.class));

    }

}
