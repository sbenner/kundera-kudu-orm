package com.impetus.kundera.metadata.model;

import com.impetus.kundera.metadata.KunderaMetadataManager;
import com.impetus.kundera.metadata.validator.*;
import com.impetus.kundera.persistence.EntityManagerFactoryImpl;
import org.junit.*;

import javax.persistence.EntityManagerFactory;
import javax.persistence.GenerationType;
import javax.persistence.Persistence;
import javax.persistence.metamodel.Metamodel;

public class ProcessAnnotationsTest
{

    EntityManagerFactory emf;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception
    {
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception
    {
    }

    @Before
    public void setUp() throws Exception
    {
        emf = Persistence.createEntityManagerFactory("GeneratedValue,kunderatest");
    }

    @After
    public void tearDown() throws Exception
    {
        emf.close();
    }

    @Test
    public void testProcess() {
        Metamodel metamodel = KunderaMetadataManager.getMetamodel(
                ((EntityManagerFactoryImpl) emf).getKunderaMetadataInstance(), "GeneratedValue");

        // for entity GeneratedIdDefault.
        IdDescriptor keyValue = ((MetamodelImpl) metamodel).getKeyValue(GeneratedIdDefault.class.getName());
        Assert.assertNotNull(keyValue);
        Assert.assertEquals(GenerationType.AUTO, keyValue.getStrategy());
        Assert.assertNull(keyValue.getTableDescriptor());
        Assert.assertNull(keyValue.getSequenceDescriptor());
        keyValue = null;

        // for entity GeneratedIdStrategyAuto.
        keyValue = ((MetamodelImpl) metamodel).getKeyValue(GeneratedIdStrategyAuto.class.getName());
        Assert.assertNotNull(keyValue);
        Assert.assertEquals(GenerationType.AUTO, keyValue.getStrategy());
        Assert.assertNull(keyValue.getTableDescriptor());
        Assert.assertNull(keyValue.getSequenceDescriptor());
        keyValue = null;

        keyValue = ((MetamodelImpl) metamodel).getKeyValue(GeneratedIdStrategyIdentity.class.getName());
        Assert.assertNotNull(keyValue);
        Assert.assertEquals(GenerationType.IDENTITY, keyValue.getStrategy());
        Assert.assertNull(keyValue.getTableDescriptor());
        Assert.assertNull(keyValue.getSequenceDescriptor());
        keyValue = null;

        // for entity GeneratedIdStrategySequence.
        keyValue = ((MetamodelImpl) metamodel).getKeyValue(GeneratedIdStrategySequence.class.getName());
        Assert.assertNotNull(keyValue);
        Assert.assertEquals(GenerationType.SEQUENCE, keyValue.getStrategy());
        Assert.assertNull(keyValue.getTableDescriptor());
        Assert.assertNotNull(keyValue.getSequenceDescriptor());
        Assert.assertEquals(50, keyValue.getSequenceDescriptor().getAllocationSize());
        Assert.assertEquals(1, keyValue.getSequenceDescriptor().getInitialValue());
        Assert.assertEquals("KunderaTest", keyValue.getSequenceDescriptor().getSchemaName());
        Assert.assertEquals("sequence_name", keyValue.getSequenceDescriptor().getSequenceName());
        Assert.assertNull(keyValue.getSequenceDescriptor().getCatalog());
        keyValue = null;

        // for entity GeneratedIdStrategyTable.
        keyValue = ((MetamodelImpl) metamodel).getKeyValue(GeneratedIdStrategyTable.class.getName());
        Assert.assertNotNull(keyValue);
        Assert.assertEquals(GenerationType.TABLE, keyValue.getStrategy());
        Assert.assertNull(keyValue.getSequenceDescriptor());
        Assert.assertNotNull(keyValue.getTableDescriptor());
        Assert.assertEquals(50, keyValue.getTableDescriptor().getAllocationSize());
        Assert.assertEquals(1, keyValue.getTableDescriptor().getInitialValue());
        Assert.assertEquals("KunderaTest", keyValue.getTableDescriptor().getSchema());
        Assert.assertEquals("kundera_sequences", keyValue.getTableDescriptor().getTable());
        Assert.assertEquals("sequence_name", keyValue.getTableDescriptor().getPkColumnName());
        Assert.assertEquals("GeneratedIdStrategyTable", keyValue.getTableDescriptor().getPkColumnValue());
        Assert.assertEquals("sequence_value", keyValue.getTableDescriptor().getValueColumnName());
        Assert.assertNull(keyValue.getTableDescriptor().getCatalog());
        Assert.assertNull(keyValue.getTableDescriptor().getUniqueConstraints());
        keyValue = null;

        // for entity GeneratedIdWithSequenceGenerator.
        keyValue = ((MetamodelImpl) metamodel).getKeyValue(GeneratedIdWithSequenceGenerator.class.getName());
        Assert.assertNotNull(keyValue);
        Assert.assertEquals(GenerationType.SEQUENCE, keyValue.getStrategy());
        Assert.assertNull(keyValue.getTableDescriptor());
        Assert.assertNotNull(keyValue.getSequenceDescriptor());
        Assert.assertEquals(20, keyValue.getSequenceDescriptor().getAllocationSize());
        Assert.assertEquals(80, keyValue.getSequenceDescriptor().getInitialValue());
        Assert.assertEquals("KunderaTest", keyValue.getSequenceDescriptor().getSchemaName());
        Assert.assertEquals("newSequence", keyValue.getSequenceDescriptor().getSequenceName());
        Assert.assertNull(keyValue.getSequenceDescriptor().getCatalog());
        keyValue = null;

        // for entity GeneratedIdWithTableGenerator.
        keyValue = ((MetamodelImpl) metamodel).getKeyValue(GeneratedIdWithTableGenerator.class.getName());
        Assert.assertNotNull(keyValue);
        Assert.assertEquals(GenerationType.TABLE, keyValue.getStrategy());
        Assert.assertNull(keyValue.getSequenceDescriptor());
        Assert.assertNotNull(keyValue.getTableDescriptor());
        Assert.assertEquals(30, keyValue.getTableDescriptor().getAllocationSize());
        Assert.assertEquals(100, keyValue.getTableDescriptor().getInitialValue());
        Assert.assertEquals("KunderaTest", keyValue.getTableDescriptor().getSchema());
        Assert.assertEquals("kundera", keyValue.getTableDescriptor().getTable());
        Assert.assertEquals("sequence", keyValue.getTableDescriptor().getPkColumnName());
        Assert.assertEquals("kk", keyValue.getTableDescriptor().getPkColumnValue());
        Assert.assertEquals("sequenceValue", keyValue.getTableDescriptor().getValueColumnName());
        Assert.assertNull(keyValue.getTableDescriptor().getCatalog());
        Assert.assertNull(keyValue.getTableDescriptor().getUniqueConstraints());
        keyValue = null;

        // for entity GeneratedIdStrategySequence.
        keyValue = ((MetamodelImpl) metamodel).getKeyValue(GeneratedIdWithOutSequenceGenerator.class.getName());
        Assert.assertNotNull(keyValue);
        Assert.assertEquals(GenerationType.SEQUENCE, keyValue.getStrategy());
        Assert.assertNull(keyValue.getTableDescriptor());
        Assert.assertNotNull(keyValue.getSequenceDescriptor());
        Assert.assertEquals(50, keyValue.getSequenceDescriptor().getAllocationSize());
        Assert.assertEquals(1, keyValue.getSequenceDescriptor().getInitialValue());
        Assert.assertEquals("KunderaTest", keyValue.getSequenceDescriptor().getSchemaName());
        Assert.assertEquals("sequence_name", keyValue.getSequenceDescriptor().getSequenceName());
        Assert.assertNull(keyValue.getSequenceDescriptor().getCatalog());
        keyValue = null;

        // for entity GeneratedIdStrategyTable.
        keyValue = ((MetamodelImpl) metamodel).getKeyValue(GeneratedIdWithOutTableGenerator.class.getName());
        Assert.assertNotNull(keyValue);
        Assert.assertEquals(GenerationType.TABLE, keyValue.getStrategy());
        Assert.assertNull(keyValue.getSequenceDescriptor());
        Assert.assertNotNull(keyValue.getTableDescriptor());
        Assert.assertEquals(50, keyValue.getTableDescriptor().getAllocationSize());
        Assert.assertEquals(1, keyValue.getTableDescriptor().getInitialValue());
        Assert.assertEquals("KunderaTest", keyValue.getTableDescriptor().getSchema());
        Assert.assertEquals("kundera_sequences", keyValue.getTableDescriptor().getTable());
        Assert.assertEquals("sequence_name", keyValue.getTableDescriptor().getPkColumnName());
        Assert.assertEquals("GeneratedIdWithOutTableGenerator", keyValue.getTableDescriptor().getPkColumnValue());
        Assert.assertEquals("sequence_value", keyValue.getTableDescriptor().getValueColumnName());
        Assert.assertNull(keyValue.getTableDescriptor().getCatalog());
        Assert.assertNull(keyValue.getTableDescriptor().getUniqueConstraints());
        keyValue = null;
    }
}
