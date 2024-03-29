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
package com.impetus.kundera.configure.schema;

import junit.framework.Assert;
import org.junit.Test;

import java.util.ArrayList;

/**
 * @author vivek.mishra
 * junit for {@link TableInfo}
 *
 */
public class TableInfoTest {

    @Test
    public void testWithSingleValueConstructor() {
        EmbeddedColumnInfo embeddedColumnInfo = new EmbeddedColumnInfo(null);

        ColumnInfo col1 = new ColumnInfo();
        col1.setColumnName("column1");

        ColumnInfo columnInfo = new ColumnInfo();
        columnInfo.setColumnName("column1");
        columnInfo.setIndexable(true);
        columnInfo.setType(Integer.class);

        java.util.List<ColumnInfo> columns = new ArrayList<ColumnInfo>();
        columns.add(columnInfo);
        columns.add(col1);

        embeddedColumnInfo.setColumns(columns);
        embeddedColumnInfo.setEmbeddedColumnName("embeddedColumn");

        Assert.assertEquals("embeddedColumn", embeddedColumnInfo.getEmbeddedColumnName());
        Assert.assertNull(embeddedColumnInfo.getEmbeddable());
        Assert.assertEquals(2, embeddedColumnInfo.getColumns().size());
        Assert.assertNotNull(embeddedColumnInfo.toString());

        EmbeddedColumnInfo embedded2 = new EmbeddedColumnInfo(null);
        Assert.assertNotSame(embeddedColumnInfo, embedded2);

        IndexInfo indexInfo = new IndexInfo("personName");
        indexInfo.setIndexType("KEYS");
        indexInfo.setMaxValue(12);
        indexInfo.setMinValue(1);

        TableInfo tableInfo = new TableInfo("table1", "column family", String.class, "address_id");
        tableInfo.addColumnInfo(columnInfo);
        tableInfo.addToIndexedColumnList(indexInfo);
        Assert.assertNotNull(tableInfo.getColumnMetadatas());

        Assert.assertEquals("address_id", tableInfo.getIdColumnName());
        Assert.assertEquals(String.class, tableInfo.getTableIdType());
        Assert.assertEquals("column family", tableInfo.getType());

        Assert.assertNotNull(tableInfo.toString());

    }

}
