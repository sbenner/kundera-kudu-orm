[![Join the chat at https://gitter.im/Impetus/Kundera](https://badges.gitter.im/Impetus/Kundera.svg)](https://gitter.im/Impetus/Kundera) [![Follow us on Twitter](http://i.imgur.com/wWzX9uB.png)](https://twitter.com/kundera_impetus)

Сloned from
====================
* [Kundera](https://github.com/Impetus/Kundera)
* [Kudu support docs](https://github.com/Impetus/Kundera/wiki/Kundera-with-Kudu)

Changes made
=====================

* Modified core orm to support `nullable` fields. 

  `SchemaConfiguration` previously ignored `javax.persistence.Column` attributes.

* Added `decimal` support along with dependency on `kudu-client 1.8.0`
   
   e.g.   
 `@Column(name = "price", precision = 18,scale = 4)
BigDecimal price;`

* `Hash partitioning` for key fields added on table create.

  Annotations added with  
  default values :
    `@Hashable(buckets=1,replicationFactor=1) ({ElementType.TYPE})`
        and
    `@Hash @Target({ElementType.METHOD, ElementType.FIELD})`

  Example: 
    ```
    @Entity
    @Table(schema = "kudu@kudu_pu", name = "file_info")
    @Data
    @Hashable(buckets=9,replicationFactor=3)
    public class FileInfo  {
      @Column(nullable = false, name = "date_added")
        @Hash
        private Timestamp dateAdded;
    ....
        @Id
        @Hash
        private String id;
    ....
        
    }
    ```
* `Default Replication Factor` added
  
 added for `hash partitioning` annotation as written above and as a global default value
 in `META-INF/persistence.xml` 
 ```
   <property name="kundera.replication.factor" value="1"/>
 ```
added `scan.package` property for external scans for entity presence
in other libraries or paths
 ```
   <property name="scan.package" value="com.test.persistence"/>
 ```
 added honoring of socket operation timeouts and socket read timeouts
 ```
   <property name="kudu.read.timeout" value="600"/>
   <property name="kudu.op.timeout" value="900"/>
 ```


