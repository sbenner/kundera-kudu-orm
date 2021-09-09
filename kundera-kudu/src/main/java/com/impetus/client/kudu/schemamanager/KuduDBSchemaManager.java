/*******************************************************************************
 *  * Copyright 2016 Impetus Infotech.
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
package com.impetus.client.kudu.schemamanager;

import com.impetus.client.kudu.KuduDBDataHandler;
import com.impetus.client.kudu.KuduDBValidationClassMapper;
import com.impetus.kundera.KunderaException;
import com.impetus.kundera.configure.schema.ColumnInfo;
import com.impetus.kundera.configure.schema.EmbeddedColumnInfo;
import com.impetus.kundera.configure.schema.SchemaGenerationException;
import com.impetus.kundera.configure.schema.TableInfo;
import com.impetus.kundera.configure.schema.api.AbstractSchemaManager;
import com.impetus.kundera.configure.schema.api.SchemaManager;
import com.impetus.kundera.metadata.model.MetamodelImpl;
import com.impetus.kundera.metadata.model.attributes.AbstractAttribute;
import com.impetus.kundera.persistence.EntityManagerFactoryImpl.KunderaMetadata;
import com.impetus.kundera.utils.ReflectUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.kudu.ColumnSchema;
import org.apache.kudu.ColumnSchema.ColumnSchemaBuilder;
import org.apache.kudu.ColumnTypeAttributes;
import org.apache.kudu.Schema;
import org.apache.kudu.client.AlterTableOptions;
import org.apache.kudu.client.CreateTableOptions;
import org.apache.kudu.client.KuduClient;
import org.apache.kudu.client.KuduTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.Embeddable;
import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.EmbeddableType;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

/**
 * The Class KuduDBSchemaManager.
 *
 * @author karthikp.manchala
 */
public class KuduDBSchemaManager extends AbstractSchemaManager implements SchemaManager {

    /**
     * The Constant logger.
     */
    private static final Logger logger = LoggerFactory.getLogger(KuduDBSchemaManager.class);

    /**
     * The client.
     */
    private KuduClient client;

    /**
     * Instantiates a new kudu db schema manager.
     *
     * @param clientFactory      the client factory
     * @param externalProperties the external properties
     * @param kunderaMetadata    the kundera metadata
     */
    public KuduDBSchemaManager(String clientFactory, Map<String, Object> externalProperties,
                               KunderaMetadata kunderaMetadata) {
        super(clientFactory, externalProperties, kunderaMetadata);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.impetus.kundera.configure.schema.api.AbstractSchemaManager#
     * exportSchema(java.lang.String, java.util.List)
     */
    public void exportSchema(final String persistenceUnit, List<TableInfo> schemas) {
        super.exportSchema(persistenceUnit, schemas);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.impetus.kundera.configure.schema.api.SchemaManager#dropSchema()
     */
    @Override
    public void dropSchema() {
        for (TableInfo tableInfo : tableInfos) {
            try {
                client.deleteTable(tableInfo.getTableName());
            } catch (Exception ex) {
                logger.error("Error during deleting tables in kudu, Caused by: ", ex);
                throw new SchemaGenerationException(ex, "Kudu");
            }
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.impetus.kundera.configure.schema.api.SchemaManager#validateEntity(
     * java.lang.Class)
     */
    @Override
    public boolean validateEntity(Class clazz) {
        // TODO Auto-generated method stub
        return false;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.impetus.kundera.configure.schema.api.AbstractSchemaManager#
     * initiateClient()
     */
    @Override
    protected boolean initiateClient() {
        for (String host : hosts) {
            if (host == null || !StringUtils.isNumeric(port) || port.isEmpty()) {
                logger.error("Host or port should not be null / port should be numeric");
                throw new IllegalArgumentException("Host or port should not be null / port should be numeric");
            }
            try {
                client = new KuduClient.KuduClientBuilder(host + ":" + port).build();
            } catch (Exception e) {
                logger.error("Database host cannot be resolved, Caused by: " + e.getMessage());
                throw new SchemaGenerationException("Database host cannot be resolved, Caused by: " + e.getMessage());
            }
        }
        return true;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.impetus.kundera.configure.schema.api.AbstractSchemaManager#validate(
     * java.util.List)
     */
    @Override
    protected void validate(List<TableInfo> tableInfos) {
        for (TableInfo tableInfo : tableInfos) {
            try {
                if (!client.tableExists(tableInfo.getTableName())) {
                    throw new SchemaGenerationException("Table: " + tableInfo.getTableName() + " does not exist ");
                }
            } catch (Exception e) {
                logger.error("Error while validating tables, Caused by: " + e.getMessage());
                throw new KunderaException("Error while validating tables, Caused by: " + e.getMessage());
            }
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.impetus.kundera.configure.schema.api.AbstractSchemaManager#update(
     * java.util.List)
     */
    @Override
    protected void update(List<TableInfo> tableInfos) {
        for (TableInfo tableInfo : tableInfos) {
            try {


                if (!client.tableExists(tableInfo.getTableName())) {
                    createKuduTable(tableInfo);
                } else {
                    List<String> entityColumns = new ArrayList<String>();
                    KuduTable table = client.openTable(tableInfo.getTableName());
                    AlterTableOptions alterTableOptions = new AlterTableOptions();
                    AtomicBoolean updated = new AtomicBoolean(false);
                    Schema schema = table.getSchema();
                    // add modify columns
                    for (ColumnInfo columnInfo : tableInfo.getColumnMetadatas()) {
                        entityColumns.add(columnInfo.getColumnName());
                        alterColumn(alterTableOptions, schema, columnInfo, updated);
                    }
                    // update for embeddables logic
                    for (EmbeddedColumnInfo embColumnInfo : tableInfo.getEmbeddedColumnMetadatas()) {
                        for (ColumnInfo columnInfo : embColumnInfo.getColumns()) {
                            entityColumns.add(columnInfo.getColumnName());
                            alterColumn(alterTableOptions, schema, columnInfo, updated);
                        }
                    }

                    // delete columns
                    for (ColumnSchema columnSchema : schema.getColumns()) {
                        // if not in tableInfo and not a key then delete
                        if (!entityColumns.contains(columnSchema.getName()) && !columnSchema.isKey()) {
                            alterTableOptions.dropColumn(columnSchema.getName());
                            updated.set(true);
                        }
                    }

                    if (updated.get()) {
                        client.alterTable(tableInfo.getTableName(), alterTableOptions);
                    }
                }
            } catch (Exception e) {
                logger.error("Error while updating tables, Caused by: " + e.getMessage());
                throw new KunderaException("Error while updating tables, Caused by: " + e.getMessage());
            }
        }
    }

    /**
     * Alter column.
     *
     * @param alterTableOptions the alter table options
     * @param schema            the schema
     * @param columnInfo        the column info
     * @param updated           the updated
     */
    private void alterColumn(AlterTableOptions alterTableOptions, Schema schema, ColumnInfo columnInfo,
                             AtomicBoolean updated) {
        if (!KuduDBDataHandler.hasColumn(schema, columnInfo.getColumnName())) {
            // add if column is not in schema

            alterTableOptions.addNullableColumn(columnInfo.getColumnName(),
                    KuduDBValidationClassMapper.getValidTypeForClass(columnInfo.getType()));
            updated.set(true);
        } else {
            // check for type, drop and add if not consistent TODO: throw
            // exception or override?
            if (!schema.getColumn(columnInfo.getColumnName()).getType()
                    .equals(KuduDBValidationClassMapper.getValidTypeForClass(columnInfo.getType()))) {
                alterTableOptions.dropColumn(columnInfo.getColumnName());
                alterTableOptions.addNullableColumn(columnInfo.getColumnName(),
                        KuduDBValidationClassMapper.getValidTypeForClass(columnInfo.getType()));
                updated.set(true);
            }
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.impetus.kundera.configure.schema.api.AbstractSchemaManager#create(
     * java.util.List)
     */
    @Override
    protected void create(List<TableInfo> tableInfos) {

        for (TableInfo tableInfo : tableInfos) {
            try {
                if (client.tableExists(tableInfo.getTableName())) {
                    client.deleteTable(tableInfo.getTableName());
                }
            } catch (Exception e) {
                logger.error("Cannot check table existence for table " + tableInfo.getTableName() + ". Caused By: " + e);
                throw new KunderaException("Cannot check table existence for table " + tableInfo.getTableName()
                        + ". Caused By: " + e);
            }
            createKuduTable(tableInfo);
        }
    }

    /**
     * Creates the kudu table.
     *
     * @param tableInfo the table info
     */
    private void createKuduTable(TableInfo tableInfo) {
        Set<ColumnSchema> keys = new HashSet<ColumnSchema>();
        Set<ColumnSchema> columns = new HashSet<ColumnSchema>();
        // add key


        if (tableInfo.getTableIdType().isAnnotationPresent(Embeddable.class)) {
            // composite keys
            MetamodelImpl metaModel = (MetamodelImpl)
                    kunderaMetadata.getApplicationMetadata().getMetamodel(
                            puMetadata.getPersistenceUnitName());
            EmbeddableType embeddableIdType = metaModel.embeddable(tableInfo.getTableIdType());
            Field[] fields = tableInfo.getTableIdType().getDeclaredFields();

            addPrimaryKeyColumnsFromEmbeddable(keys, embeddableIdType, fields, metaModel);

        } else if (tableInfo.getIdFieldAnnotations().get("com.impetus.client.kudu.schemamanager.Hash") != null) {
            // simple key

            for (ColumnInfo c : tableInfo.getColumnMetadatas()) {
                if (c.getFieldAnnotations().containsKey("com.impetus.client.kudu.schemamanager.Hash")
                    //&&!c.getColumnName().equals(tableInfo.getIdColumnName())
                ) {
                    keys.add(new ColumnSchema.
                            ColumnSchemaBuilder(c.getColumnName(),
                            KuduDBValidationClassMapper
                                    .getValidTypeForClass(c.getType())).key(true).build());
                }
            }
        } else {
            // simple key
            columns.add(new ColumnSchema.
                    ColumnSchemaBuilder(tableInfo.getIdColumnName(),
                    KuduDBValidationClassMapper
                            .getValidTypeForClass(tableInfo.getTableIdType())).key(true).build());
        }

        if (!tableInfo.getTableIdType().isAnnotationPresent(Embeddable.class))
            keys.add(new ColumnSchema.
                    ColumnSchemaBuilder(tableInfo.getIdColumnName(),
                    KuduDBValidationClassMapper
                            .getValidTypeForClass(tableInfo.getTableIdType())).key(true).build());
        // add other columns
        for (ColumnInfo columnInfo : tableInfo.getColumnMetadatas()) {
            if (keys.stream().noneMatch(i -> i.getName().equalsIgnoreCase(columnInfo.getColumnName()))) {
                ColumnSchemaBuilder columnSchemaBuilder = new ColumnSchema.
                        ColumnSchemaBuilder(columnInfo.getColumnName(),
                        KuduDBValidationClassMapper
                                .getValidTypeForClass(columnInfo.getType()))
                        .nullable(columnInfo.isNullable());
                setColumnTypeAttributes(columnInfo, columnSchemaBuilder);
                columns.add(columnSchemaBuilder.build());
            }
        }

        // add embedded columns
        for (EmbeddedColumnInfo embColumnInfo : tableInfo.getEmbeddedColumnMetadatas()) {
            if (embColumnInfo.getEmbeddedColumnName().equals(tableInfo.getIdColumnName())) {
                // skip for embeddable ids
                continue;
            }
            buildColumnsFromEmbeddableColumn(embColumnInfo, columns);

        }

        try {
            CreateTableOptions builder = new CreateTableOptions();

            builder.setNumReplicas(replicationFactor);
            if (tableInfo.getTableType().isAnnotationPresent(Hashable.class)) {
                Hashable hash = tableInfo.getTableType().getAnnotation(Hashable.class);
                if (hash.replicationFactor() != replicationFactor)
                    builder.setNumReplicas(hash.replicationFactor());

                builder.addHashPartitions(keys.stream().map(ColumnSchema::getName).collect(Collectors.toList()), hash.buckets());

            } else if (tableInfo.getTableIdType().isAnnotationPresent(Embeddable.class)) {
                builder.setRangePartitionColumns(keys.stream().map(ColumnSchema::getName).collect(Collectors.toList()));
            } else {
                builder.setRangePartitionColumns(Arrays.asList(tableInfo.getIdColumnName()));
            }

            keys.addAll(columns);
//            ;
//            List<ColumnSchema> all = new ArrayList<>(keys);
//            all.sort(Comparator.comparing(i->i.isKey()));
            Schema schema = new Schema(keys.stream().sorted(Comparator.comparing(i -> !i.isKey())).collect(Collectors.toList()));

            client.createTable(tableInfo.getTableName(), schema, builder);
            logger.debug("Table: " + tableInfo.getTableName() + " created successfully");
        } catch (Exception e) {
            logger.error("Table: " + tableInfo.getTableName() + " cannot be created, Caused by: " + e.getMessage(), e);
            throw new SchemaGenerationException("Table: " + tableInfo.getTableName()
                    + " cannot be created, Caused by: " + e.getMessage(), e, "Kudu");
        }
    }

    private void addPrimaryKeyColumnsFromEmbeddable(Set<ColumnSchema> columns, EmbeddableType embeddable,
                                                    Field[] fields, MetamodelImpl metaModel) {
        for (Field f : fields) {
            if (!ReflectUtils.isTransientOrStatic(f)) {
                if (f.getType().isAnnotationPresent(Embeddable.class)) {
                    // nested
                    addPrimaryKeyColumnsFromEmbeddable(columns, metaModel.embeddable(f.getType()), f
                            .getType().getDeclaredFields(), metaModel);
                } else {
                    Attribute attribute = embeddable.getAttribute(f.getName());
                    columns.add(new ColumnSchema.ColumnSchemaBuilder(
                            ((AbstractAttribute) attribute).getJPAColumnName(),
                            KuduDBValidationClassMapper
                                    .getValidTypeForClass(f.getType())).key(true).build());
                }
            }
        }

    }

    private void buildColumnsFromEmbeddableColumn(EmbeddedColumnInfo embColumnInfo, Set<ColumnSchema> columns) {
        for (ColumnInfo columnInfo : embColumnInfo.getColumns()) {
            ColumnSchemaBuilder columnSchemaBuilder = new ColumnSchema.ColumnSchemaBuilder(columnInfo.getColumnName(),
                    KuduDBValidationClassMapper.getValidTypeForClass(columnInfo.getType()));
            setColumnTypeAttributes(columnInfo, columnSchemaBuilder);
            columns.add(columnSchemaBuilder.build());
        }

    }

    private void setColumnTypeAttributes(ColumnInfo columnInfo, ColumnSchemaBuilder columnSchemaBuilder) {
        if (columnInfo.getType().isAssignableFrom(BigDecimal.class)) {
            ColumnTypeAttributes typeAttributes = new ColumnTypeAttributes
                    .ColumnTypeAttributesBuilder().precision(columnInfo.getPrecision()
            ).scale(columnInfo.getScale()).build();
            columnSchemaBuilder.typeAttributes(typeAttributes);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see com.impetus.kundera.configure.schema.api.AbstractSchemaManager#
     * create_drop(java.util.List)
     */
    @Override
    protected void create_drop(List<TableInfo> tableInfos) {
        create(tableInfos);
    }

}
