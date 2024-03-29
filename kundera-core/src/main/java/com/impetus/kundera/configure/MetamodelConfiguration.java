package com.impetus.kundera.configure;

import com.impetus.kundera.PersistenceProperties;
import com.impetus.kundera.classreading.ClasspathReader;
import com.impetus.kundera.classreading.Reader;
import com.impetus.kundera.classreading.ResourceIterator;
import com.impetus.kundera.loader.MetamodelLoaderException;
import com.impetus.kundera.metadata.KunderaMetadataManager;
import com.impetus.kundera.metadata.MetadataBuilder;
import com.impetus.kundera.metadata.model.*;
import com.impetus.kundera.metadata.processor.GeneratedValueProcessor;
import com.impetus.kundera.persistence.EntityManagerFactoryImpl.KunderaMetadata;
import com.impetus.kundera.utils.KunderaCoreUtils;
import com.impetus.kundera.validation.ValidationFactory;
import com.impetus.kundera.validation.ValidationFactoryGenerator;
import com.impetus.kundera.validation.ValidationFactoryGenerator.ValidationFactoryType;
import com.impetus.kundera.validation.rules.RuleValidationException;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.ClassFile;
import org.apache.commons.lang.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Table;
import javax.persistence.metamodel.Metamodel;
import java.io.*;
import java.lang.reflect.Field;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarInputStream;
import java.util.stream.Collectors;

/**
 * The Metamodel configurer: a) Configure application meta data b) loads entity
 * metadata and maps metadata.
 *
 * @author vivek.mishra
 */
public class MetamodelConfiguration extends AbstractSchemaConfiguration implements Configuration {

    /**
     * The log.
     */
    private static Logger log = LoggerFactory.getLogger(MetamodelConfiguration.class);

    private ValidationFactory factory;


    /**
     * Constructor using persistence units as parameter.
     *
     * @param persistenceUnits persistence units.
     */
    public MetamodelConfiguration(Map properties, final KunderaMetadata metadata, String... persistenceUnits) {
        super(persistenceUnits, properties, metadata);
        ValidationFactoryGenerator generator = new ValidationFactoryGenerator();
        this.factory = generator.getFactory(ValidationFactoryType.BOOT_STRAP_VALIDATION);
    }

    /**
     * list classes
     *
     * @param jar the ClassLoader unit
     */


    public static List<JarEntry> listJarContent(String jar) throws Exception {

        URL url = new URL(jar);
        JarURLConnection conn = (JarURLConnection) url.openConnection();
        JarFile jarfile = conn.getJarFile();

        Enumeration<JarEntry> content = jarfile.entries();
        List<JarEntry> contents =
                new ArrayList<>();
        while (content.hasMoreElements()) {
            JarEntry je = content.nextElement();
            if (je.getName().endsWith(".jar") ||
                    je.getName().endsWith(".class")) {
                contents.add(je);
            }
        }
        return contents;

    }

    public static byte[] extractContentFromJar(String origFile, String file) throws Exception {
        InputStream in = null;
        OutputStream out = null;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            URL url = new URL(origFile + file);
            JarURLConnection conn = (JarURLConnection) url.openConnection();
            JarFile jarfile = conn.getJarFile();
            JarEntry jarEntry = conn.getJarEntry();
            in = new BufferedInputStream(jarfile.getInputStream(jarEntry));
            out = new BufferedOutputStream(baos);
            byte[] buffer = new byte[2048];
            for (; ; ) {
                int nBytes = in.read(buffer);
                if (nBytes <= 0) break;
                out.write(buffer, 0, nBytes);
            }
        } finally {
            if (in != null) {
                in.close();
            }
            if (out != null) {
                out.flush();
                out.close();
            }
        }
        return baos.toByteArray();
    }

    private static Class pickClassFromJarEntry(JarEntry je, String packageName) {
        if (je.isDirectory() || !je.getName().endsWith(".class")
        ) {
            return null;
        }
        Class c = null;
        try {
            String className = je.getName().substring(0, je.getName().length() - 6);
            className = className.replace('/', '.');
            className =
                    className.contains("-INF") ?
                            className.substring(
                                    className.lastIndexOf("-INF") + 13) :
                            className;

            if (className.startsWith(packageName)) {
                c = Class.forName(className);
            }
        } catch (NoClassDefFoundError | ClassNotFoundException e) {
            log.error(e.getMessage(), e);
        }
        return c;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.impetus.kundera.configure.Configuration#configure()
     */
    @Override
    public void configure() {
        log.debug("Loading Entity Metadata...");
        ApplicationMetadata appMetadata = kunderaMetadata.getApplicationMetadata();

        for (String persistenceUnit : persistenceUnits) {
            if (appMetadata.getMetamodelMap().get(persistenceUnit.trim()) != null) {
                if (log.isDebugEnabled()) {
                    log.debug("Metadata already exists for the Persistence Unit " + persistenceUnit + ". Nothing to do");
                }
            } else {
                loadEntityMetadata(persistenceUnit);
            }
        }
    }

//
//    /**
//     * scan jar for classes
//     *
//     * @param packageName package name to scan for classes in
//     */
////    public Iterable<Class> scanForClasses(String packageName) throws Exception {
////        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
////        String path = packageName.replace('.', '/');
////        Enumeration<URL> resources = classLoader.getResources(path);
////        List<File> dirs = new ArrayList<File>();
////        List<Class> classes = new ArrayList<Class>();
////        while (resources.hasMoreElements()) {
////            URL resource = resources.nextElement();
////            URI uri = new URI(resource.toString());
////            try {
////                if(uri.getScheme().equalsIgnoreCase("jar")){
////                    List<JarEntry> l = ((JarURLConnection) resource.openConnection())
////                            .getJarFile().stream().collect(Collectors.toList());
////                    l.stream().map(
////                            o -> this.pickClassFromJarEntry(o, packageName)
////
////                    ).filter(Objects::nonNull).forEach(classes::add);
////
////                } else if (uri.getPath() != null && !uri.getScheme().equalsIgnoreCase("jar")) {
////                    dirs.add(new File(uri.getPath()));
////                }
////            } catch (Exception e) {
////                log.error(e.getMessage(), e);
////            }
////        }
////
////        for (File directory : dirs) {
////            classes.addAll(findClasses(directory, packageName));
////        }
////
////        return classes;
////    }

    private synchronized List<Class> listVector(ClassLoader CL)
            throws NoSuchFieldException, SecurityException,
            IllegalArgumentException, IllegalAccessException {
        Class CL_class = CL.getClass();
        while (CL_class != java.lang.ClassLoader.class) {
            CL_class = CL_class.getSuperclass();
        }
        java.lang.reflect.Field ClassLoader_classes_field = CL_class
                .getDeclaredField("classes");
        ClassLoader_classes_field.setAccessible(true);
        return Collections.list(((Vector) ClassLoader_classes_field.get(CL)).elements());
    }

    private synchronized Iterator list(ClassLoader CL)
            throws NoSuchFieldException, SecurityException,
            IllegalArgumentException, IllegalAccessException {
        Class CL_class = CL.getClass();
        while (CL_class != java.lang.ClassLoader.class) {
            CL_class = CL_class.getSuperclass();
        }
        java.lang.reflect.Field ClassLoader_classes_field = CL_class
                .getDeclaredField("classes");
        ClassLoader_classes_field.setAccessible(true);
        Vector classes = (Vector) ClassLoader_classes_field.get(CL);
        return classes.listIterator();
    }

    @SuppressWarnings("resource")
    public JSONObject getCrunchifyClassNamesFromJar(String crunchifyJarName) {
        JSONArray listofClasses = new JSONArray();
        JSONObject crunchifyObject = new JSONObject();
        try {
            JarInputStream crunchifyJarFile = new JarInputStream(new FileInputStream(crunchifyJarName));
            JarEntry crunchifyJar;

            while (true) {
                crunchifyJar = crunchifyJarFile.getNextJarEntry();
                if (crunchifyJar == null) {
                    break;
                }
                if ((crunchifyJar.getName().endsWith(".class"))) {
                    String className = crunchifyJar.getName().replaceAll("/", "\\.");
                    String myClass = className.substring(0, className.lastIndexOf('.'));
                    listofClasses.put(myClass);
                }
            }
            crunchifyObject.put("jar_name", crunchifyJarName);
            crunchifyObject.put("classes_list", listofClasses);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return crunchifyObject;
    }

    public synchronized Iterable<Class> scanForClasses(String packageName) throws Exception {
        ClassLoader classLoader = ClassLoader.getSystemClassLoader();

        List<Class> classList = new ArrayList<>();
        Set<Class> out = new HashSet<>();


        while (classLoader != null) {
            // System.out.println("ClassLoader: " + myCL);
            try {
                out.addAll(listVector(classLoader).stream().filter(
                        o -> o.isAnnotationPresent(Entity.class) &&
                                o.getName().toLowerCase().startsWith(packageName)
                ).collect(Collectors.toList()));
            } catch (Exception e) {
                //e.printStackTrace();
            }
            classLoader = classLoader.getParent();
        }
//        for (Class c : classList) {
//            if (c.getName().toLowerCase().startsWith(packageName))
//                out.add(c);
//        }

        ///scan for other loaders
        URL u = Thread.currentThread().getContextClassLoader()
                .getResource("/");
        if (u != null) {
            String origJar = u.toString().split("!")[0] + "!/";
            try {
                List<JarEntry> entryList = listJarContent(origJar);
                if (entryList.size() > 0) {
                    for (JarEntry je : entryList) {


                        if (je != null && je.getName().endsWith(".class")) {
                            Class c = pickClassFromJarEntry(je, packageName);
                            if (c != null && c.isAnnotationPresent(Entity.class)) {
                                log.info("Loaded " + c.getName());
                                out.add(c);
                            }
                        } else {
                            if (je != null && je.getName().endsWith(".jar")) {
                                //log.info("JAR: "+je.getName());
                                byte[] b = extractContentFromJar(origJar,
                                        je.getName());
                                JarInputStream is = new JarInputStream(new ByteArrayInputStream(b));
                                JarEntry internalJarEntry = is.getNextJarEntry();
                                while (internalJarEntry != null) {
                                    internalJarEntry = is.getNextJarEntry();
                                    if (internalJarEntry != null && internalJarEntry.getName().endsWith(".class")) {
                                        Class c = pickClassFromJarEntry(internalJarEntry, packageName);
                                        if (c != null && c.isAnnotationPresent(Entity.class)) {
                                            log.info("Loaded " + c.getName());
                                            out.add(c);
                                        }
                                    }
                                }
                            }

                        }
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        return out;

    }

    private List<Class> findClasses(File directory, String packageName) throws Exception {
        List<Class> classes = new ArrayList<Class>();

        if (directory.isFile()) {
            JarFile jarFile = new JarFile(directory.getAbsolutePath());
            Enumeration e = jarFile.entries();
            try {
                while (e.hasMoreElements()) {
                    JarEntry je = (JarEntry) e.nextElement();
                    if (je.isDirectory() || !je.getName().endsWith(".class")) {
                        continue;
                    }
                    // -6 because of .class
                    String className = je.getName().substring(0, je.getName().length() - 6);
                    className = className.replace('/', '.');
                    log.info(className);
                    className =
                            className.contains("-INF") ?
                                    className.substring(
                                            className.lastIndexOf("-INF") + 13) :
                                    className;
                    log.info(className);
                    Class c = Class.forName(className);

                    if (c.isAnnotationPresent(Entity.class))
                        classes.add(Class.forName(className));


                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            return classes;
        }

        if (!directory.exists()) {
            return classes;
        }

        File[] files = directory.listFiles();
        if (files != null && files.length > 0)
            for (File file : files) {
                if (file.isDirectory()) {
                    classes.addAll(findClasses(file, packageName + "." + file.getName()));
                } else if (file.getName().endsWith(".class")) {
                    classes.add(Class.forName(packageName + '.' + file.getName().substring(0, file.getName().length() - 6)));
                }
            }
        return classes;
    }


//    private List<Class> findClasses(File directory, String packageName) throws Exception {
//        List<Class> classes = new ArrayList<Class>();
//
//        if (directory.isFile()) {
//            JarFile jarFile = new JarFile(directory.getAbsolutePath());
//            Enumeration e = jarFile.entries();
//            try {
//                while (e.hasMoreElements()) {
//                    JarEntry je = (JarEntry) e.nextElement();
//
//                    // -6 because of .class
//                    Class c = pickClassFromJarEntry(je, packageName);
//                    if (c != null && c.isAnnotationPresent(Entity.class))
//                        classes.add(c);
//
//
//                }
//            } catch (Exception ex) {
//                log.error(ex.getMessage(), ex);
//            }
//
//            return classes;
//        }
//
//        if (!directory.exists()) {
//            return classes;
//        }
//
//        File[] files = directory.listFiles();
//        if (files != null && files.length > 0)
//            for (File file : files) {
//                if (file.isDirectory()) {
//                    classes.addAll(findClasses(file, packageName + "." + file.getName()));
//                } else if (file.getName().endsWith(".class")) {
//                    classes.add(Class.forName(packageName + '.' + file.getName().substring(0, file.getName().length() - 6)));
//                }
//            }
//        return classes;
//    }

    private void loadEntityMetadata(String persistenceUnit) {
        if (persistenceUnit == null) {
            throw new IllegalArgumentException(
                    "Must have a persistenceUnitName in order to load entity metadata, you provided :"
                            + persistenceUnit);
        }

        Map<String, PersistenceUnitMetadata> persistentUnitMetadataMap = kunderaMetadata.getApplicationMetadata()
                .getPersistenceUnitMetadataMap();

        /** Classes to scan */
        List<String> classesToScan;
        URL[] resources = null;
        String client = null;
        List<URL> managedURLs = null;
        Iterable<Class> additionalClasses = null;
        if (persistentUnitMetadataMap == null || persistentUnitMetadataMap.isEmpty()) {
            log.error("It is necessary to load Persistence Unit metadata  for persistence unit " + persistenceUnit
                    + " first before loading entity metadata.");
            throw new MetamodelLoaderException("load Persistence Unit metadata  for persistence unit "
                    + persistenceUnit + " first before loading entity metadata.");
        } else {
            PersistenceUnitMetadata puMetadata = persistentUnitMetadataMap.get(persistenceUnit);
            classesToScan = puMetadata.getManagedClassNames();

            String packageToScan = puMetadata.getProperty("scan.package");
            if (StringUtils.isNotEmpty(packageToScan)) {
                try {
                    additionalClasses = scanForClasses(packageToScan);
                    additionalClasses.forEach(i ->
                    {
                        classesToScan.add(i.getName());
                    });

                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }

            }
            managedURLs = puMetadata.getManagedURLs();
            client = getClientFactoryName(persistenceUnit);
        }

        /*
         * Check whether Classes to scan was provided into persistence.xml If
         * yes, load them. Otherwise load them from classpath/ context path
         */
        Reader reader;
        ApplicationMetadata appMetadata = kunderaMetadata.getApplicationMetadata();
        if (classesToScan == null || classesToScan.isEmpty()) {
            log.info("No class to scan for persistence unit " + persistenceUnit
                    + ". Entities will be loaded from classpath/ context-path");
            // Entity metadata is not related to any PU, and hence will be
            // stored at common place
            // persistenceUnit = Constants.COMMON_ENTITY_METADATAS;

            // Check whether all common entity metadata have already been loaded
            if (appMetadata.getMetamodelMap().get(persistenceUnit) != null) {
                log.info("All common entitity metadata already loaded, nothing need to be done");
                return;
            }

            reader = new ClasspathReader();
            // resources = reader.findResourcesByClasspath();
        } else {
            reader = new ClasspathReader(classesToScan);
            // resources = reader.findResourcesByContextLoader();
        }

        InputStream[] iStreams = null;
        PersistenceUnitMetadata puMetadata = persistentUnitMetadataMap.get(persistenceUnit);
        if (this.getClass().getClassLoader() instanceof URLClassLoader && !puMetadata.getExcludeUnlistedClasses()) {
            URL[] managedClasses = reader.findResources();
            if (managedClasses != null) {
                List<URL> managedResources = Arrays.asList(managedClasses);
                managedURLs.addAll(managedResources);
            }
        } else {
            iStreams = reader.findResourcesAsStream();
        }

        if (managedURLs != null) {
            resources = managedURLs.toArray(new URL[]{});
        }


        // All entities to load should be annotated with @Entity
        reader.addValidAnnotations(Entity.class.getName());

        Metamodel metamodel = appMetadata.getMetamodel(persistenceUnit);
        if (metamodel == null) {
            metamodel = new MetamodelImpl();
        }

        Map<String, EntityMetadata> entityMetadataMap = ((MetamodelImpl) metamodel).getEntityMetadataMap();
        Map<String, Class<?>> entityNameToClassMap = ((MetamodelImpl) metamodel).getEntityNameToClassMap();
        Map<String, List<String>> puToClazzMap = new HashMap<String, List<String>>();
        Map<String, IdDescriptor> entityNameToKeyDescriptorMap = new HashMap<String, IdDescriptor>();
        List<Class<?>> classes = new ArrayList<Class<?>>();


        if (resources != null && resources.length > 0) {
            for (URL resource : resources) {
                InputStream is = null;
                try {
                    ResourceIterator itr = reader.getResourceIterator(resource, reader.getFilter());


                    while ((is = itr.next()) != null) {
                        classes.addAll(scanClassAndPutMetadata(is, reader, entityMetadataMap, entityNameToClassMap,
                                persistenceUnit, client, puToClazzMap, entityNameToKeyDescriptorMap));
                    }
                } catch (IOException e) {
                    log.error("Error while retrieving and storing entity metadata. Details:", e);
                    throw new MetamodelLoaderException("Error while retrieving and storing entity metadata");

                } finally {
                    if (is != null) {
                        try {
                            is.close();
                        } catch (Exception e) {
                            log.error("Error while retrieving and storing entity metadata. Details:", e);
                            throw new MetamodelLoaderException("cant close stream");

                        }
                    }
                }
            }
        } else if (iStreams != null) {
            try {
                for (InputStream is : iStreams) {
                    try {
                        classes.addAll(scanClassAndPutMetadata(is, reader, entityMetadataMap, entityNameToClassMap,
                                persistenceUnit, client, puToClazzMap, entityNameToKeyDescriptorMap));
                    } finally {
                        if (is != null) {
                            is.close();
                        }
                    }
                }
            } catch (IOException e) {
                log.error("Error while retrieving and storing entity metadata. Details:", e);
                throw new MetamodelLoaderException("Error while retrieving and storing entity metadata, Caused by : .",
                        e);

            }
        }

        if (additionalClasses != null) {
            for (Class clazz : additionalClasses) {
                try {
                    Class c = scanClassAndPutMetadata(clazz, entityMetadataMap, entityNameToClassMap,
                            persistenceUnit, client, puToClazzMap, entityNameToKeyDescriptorMap);
                    if (c != null) classes.add(c);

                } catch (Exception e) {
                    log.error("err", e);
                }
            }
        }


        ((MetamodelImpl) metamodel).setEntityMetadataMap(entityMetadataMap);
        appMetadata.getMetamodelMap().put(persistenceUnit, metamodel);
        appMetadata.setClazzToPuMap(puToClazzMap);
        ((MetamodelImpl) metamodel).addKeyValues(entityNameToKeyDescriptorMap);
        // assign JPA metamodel.
        ((MetamodelImpl) metamodel).assignEmbeddables(kunderaMetadata.getApplicationMetadata()
                .getMetaModelBuilder(persistenceUnit).getEmbeddables());
        ((MetamodelImpl) metamodel).assignManagedTypes(kunderaMetadata.getApplicationMetadata()
                .getMetaModelBuilder(persistenceUnit).getManagedTypes());
        ((MetamodelImpl) metamodel).assignMappedSuperClass(kunderaMetadata.getApplicationMetadata()
                .getMetaModelBuilder(persistenceUnit).getMappedSuperClassTypes());
    }

    /**
     * Scan class and put metadata.
     *
     * @param bits                         the bits
     * @param reader                       the reader
     * @param entityMetadataMap            the entity metadata map
     * @param entityNameToClassMap         the entity name to class map
     * @param entityNameToKeyDescriptorMap
     * @param persistenceUnit              persistence unit.
     * @throws IOException             Signals that an I/O exception has occurred.
     * @throws RuleValidationException
     */
    private List<Class<?>> scanClassAndPutMetadata(InputStream bits, Reader reader,
                                                   Map<String, EntityMetadata> entityMetadataMap,
                                                   Map<String, Class<?>> entityNameToClassMap,
                                                   String persistenceUnit,
                                                   String client,
                                                   Map<String, List<String>> clazzToPuMap,
                                                   Map<String, IdDescriptor> entityNameToKeyDescriptorMap) throws IOException {
        DataInputStream dstream = new DataInputStream(new BufferedInputStream(bits));
        ClassFile cf = null;
        String className = null;

        List<Class<?>> classes = new ArrayList<Class<?>>();

        try {
            cf = new ClassFile(dstream);

            className = cf.getName();

            List<String> annotations = new ArrayList<String>();

            reader.accumulateAnnotations(annotations,
                    (AnnotationsAttribute) cf.getAttribute(AnnotationsAttribute.visibleTag));
            reader.accumulateAnnotations(annotations,
                    (AnnotationsAttribute) cf.getAttribute(AnnotationsAttribute.invisibleTag));


            // iterate through all valid annotations
            for (String validAnn : reader.getValidAnnotations()) {


                // check if the current class has one?
                if (annotations.contains(validAnn)) {

                    Class<?> clazz = this.getClass().getClassLoader().loadClass(className);
                    this.factory.validate(clazz);

                    // get the name of entity to be used for entity to class map
                    // if or not annotated with name
                    String entityName = getEntityName(clazz);

                    if ((entityNameToClassMap.containsKey(entityName) && !entityNameToClassMap.get(entityName)
                            .getName().equals(clazz.getName()))) {
                        throw new MetamodelLoaderException("Name conflict between classes "
                                + entityNameToClassMap.get(entityName).getName() + " and " + clazz.getName()
                                + ". Make sure no two entity classes with the same name "
                                + " are specified for persistence unit " + persistenceUnit);
                    }
                    entityNameToClassMap.put(entityName, clazz);

                    EntityMetadata metadata = entityMetadataMap.get(clazz);
                    if (null == metadata) {
                        log.debug("Metadata not found in cache for " + clazz.getName());
                        // double check locking.
                        synchronized (clazz) {
                            if (null == metadata) {
                                MetadataBuilder metadataBuilder = new MetadataBuilder(persistenceUnit, client,
                                        KunderaCoreUtils.getExternalProperties(persistenceUnit, externalPropertyMap,
                                                persistenceUnits), kunderaMetadata);
                                metadata = metadataBuilder.buildEntityMetadata(clazz);

                                // in case entity's pu does not belong to parse
                                // persistence unit, it will be null.
                                if (metadata != null) {
                                    entityMetadataMap.put(clazz.getName(), metadata);
                                    mapClazztoPu(clazz, persistenceUnit, clazzToPuMap);
                                    processGeneratedValueAnnotation(clazz, persistenceUnit, metadata,
                                            entityNameToKeyDescriptorMap);
                                }
                            }
                        }
                    }

                    // TODO :
                    classes.add(clazz);
                    //onValidateClientProperties(classes, clazz, persistenceUnit);
                }
            }
        } catch (ClassNotFoundException e) {
            log.error("Class " + className + " not found, it won't be loaded as entity");
        } finally {
            if (dstream != null) {
                dstream.close();
            }
            if (bits != null) {
                bits.close();
            }
        }

        return classes;
    }

    private synchronized Class<?> scanClassAndPutMetadata(Class clazz, Map<String, EntityMetadata> entityMetadataMap, Map<String, Class<?>> entityNameToClassMap,
                                                          String persistenceUnit, String client, Map<String, List<String>> clazzToPuMap,
                                                          Map<String, IdDescriptor> entityNameToKeyDescriptorMap) throws IOException {

        //List<Class<?>> classes = new ArrayList<Class<?>>();
        try {

            // check if the current class has one?
            if (clazz.isAnnotationPresent(Entity.class) && clazz.isAnnotationPresent(Table.class)) {
                this.factory.validate(clazz);
                // get the name of entity to be used for entity to class map
                // if or not annotated with name
                String entityName = getEntityName(clazz);

                if ((entityNameToClassMap.containsKey(entityName) && !entityNameToClassMap.get(entityName)
                        .getName().equals(clazz.getName()))) {
                    throw new MetamodelLoaderException("Name conflict between classes "
                            + entityNameToClassMap.get(entityName).getName() + " and " + clazz.getName()
                            + ". Make sure no two entity classes with the same name "
                            + " are specified for persistence unit " + persistenceUnit);
                }
                entityNameToClassMap.put(entityName, clazz);

                EntityMetadata metadata = entityMetadataMap.get(clazz);
                if (null == metadata) {
                    log.debug("Metadata not found in cache for " + clazz.getName());
                    // double check locking.
                    synchronized (this) {
                        MetadataBuilder metadataBuilder = new MetadataBuilder(persistenceUnit, client,
                                KunderaCoreUtils.getExternalProperties(persistenceUnit, externalPropertyMap,
                                        persistenceUnits), kunderaMetadata);
                        metadata = metadataBuilder.buildEntityMetadata(clazz);
                        // in case entity's pu does not belong to parse
                        // persistence unit, it will be null.
                        if (metadata != null) {
                            entityMetadataMap.put(clazz.getName(), metadata);
                            mapClazztoPu(clazz, persistenceUnit, clazzToPuMap);
                            processGeneratedValueAnnotation(clazz, persistenceUnit, metadata,
                                    entityNameToKeyDescriptorMap);
                        }
                    }
                }
            } else {
                return null;
            }

        } catch (Exception e) {
            log.error("Class " + clazz.getName() + " not found, it won't be loaded as entity");
        }
        return clazz;
    }


    /**
     * @param clazz
     */
    private String getEntityName(Class<?> clazz) {
        return !StringUtils.isBlank(clazz.getAnnotation(Entity.class).name()) ? clazz.getAnnotation(Entity.class)
                .name() : clazz.getSimpleName();
    }

    /**
     * @param clazz
     */
    private List<Class<?>> onValidateClientProperties(List<Class<?>> classes, Class<?> clazz,
                                                      final String persistenceUnit) {
        if (clazz.isAnnotationPresent(Entity.class) && clazz.isAnnotationPresent(Table.class)) {
            classes.add(clazz);
        }
        return classes;
    }

    /**
     * Method to prepare class simple name to list of pu's mapping. 1 class can
     * be mapped to multiple persistence units, in case of RDBMS, in other cases
     * it will only be 1!
     *
     * @param clazz        entity class to be mapped.
     * @param pu           current persistence unit name
     * @param clazzToPuMap collection holding mapping.
     * @return map holding mapping.
     */
    private Map<String, List<String>> mapClazztoPu(Class<?> clazz, String pu, Map<String, List<String>> clazzToPuMap) {
        List<String> puCol = new ArrayList<String>(1);
        if (clazzToPuMap == null) {
            clazzToPuMap = new HashMap<String, List<String>>();
        } else {
            if (clazzToPuMap.containsKey(clazz.getName())) {
                puCol = clazzToPuMap.get(clazz.getName());
            }
        }

        if (!puCol.contains(pu)) {
            puCol.add(pu);
            clazzToPuMap.put(clazz.getName(), puCol);
            String annotateEntityName = clazz.getAnnotation(Entity.class).name();
            if (!StringUtils.isBlank(annotateEntityName)) {
                clazzToPuMap.put(annotateEntityName, puCol);
            }
        }

        return clazzToPuMap;
    }

    private void processGeneratedValueAnnotation(Class<?> clazz, String persistenceUnit, EntityMetadata m,
                                                 Map<String, IdDescriptor> entityNameToKeyDescriptorMap) {
        GeneratedValueProcessor processer = new GeneratedValueProcessor();
        String pu = m.getPersistenceUnit();

        String clientFactoryName = getClientFactoryName(persistenceUnit);

        if (pu != null && pu.equals(persistenceUnit)
                || clientFactoryName.equalsIgnoreCase("com.impetus.client.rdbms.RDBMSClientFactory")) {
            Field f = (Field) m.getIdAttribute().getJavaMember();

            if (f.isAnnotationPresent(GeneratedValue.class)) {
                processer.process(clazz, f, m, entityNameToKeyDescriptorMap);
            }
        }
    }

    private String getClientFactoryName(String persistenceUnit) {
        Map<String, Object> externalProperties = KunderaCoreUtils.getExternalProperties(persistenceUnit,
                externalPropertyMap, persistenceUnits);

        String clientFactoryName = externalProperties != null ? (String) externalProperties
                .get(PersistenceProperties.KUNDERA_CLIENT_FACTORY) : null;

        if (clientFactoryName == null) {
            clientFactoryName = KunderaMetadataManager.getPersistenceUnitMetadata(kunderaMetadata,
                    persistenceUnit).getClient();
        }
        return clientFactoryName;
    }
}