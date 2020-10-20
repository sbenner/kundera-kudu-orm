/**
 * Copyright 2013 Impetus Infotech.
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
package com.impetus.kundera.metadata.processor;

import com.impetus.kundera.gis.geometry.Point;
import com.impetus.kundera.metadata.model.attributes.AttributeType;
import com.impetus.kundera.query.Person.Day;
import junit.framework.Assert;
import org.junit.Test;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author vivek.mishra
 * junit for {@link AttributeType}
 *
 */
public class AttributeTypeTest {

    @Test
    public void testGetType() {
        Assert.assertEquals(AttributeType.SET, AttributeType.getType(Set.class));
        Assert.assertEquals(AttributeType.LIST, AttributeType.getType(List.class));
        Assert.assertEquals(AttributeType.ENUM, AttributeType.getType(Day.class));
        Assert.assertEquals(AttributeType.MAP, AttributeType.getType(Map.class));
        Assert.assertEquals(AttributeType.POINT, AttributeType.getType(Point.class));
        Assert.assertEquals(AttributeType.PRIMITIVE, AttributeType.getType(int.class));
    }

}
