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

  Annotations added:  
    `@Hashable(buckets=9) ({ElementType.TYPE})`
        and
    `@Hash @Target({ElementType.METHOD, ElementType.FIELD})`

    ```
    @Entity
    @Table(schema = "kudu@kudu_pu", name = "file_info")
    @Data
    @Hashable(buckets=9)
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




Important Links
===============
* [Kundera in 5 minutes](https://github.com/impetus-opensource/Kundera/wiki/Getting-Started-in-5-minutes)
* [Data Store specific Configurations](https://github.com/impetus-opensource/Kundera/wiki/Data-store-Specific-Configuration)
* Features :
   * [Polyglot Persistence](https://github.com/impetus-opensource/Kundera/wiki/Polyglot-Persistence)
   * [JPQL](https://github.com/impetus-opensource/Kundera/wiki/JPQL) & [Native Query](https://github.com/impetus-opensource/Kundera/wiki/Native-queries) Support
   * [Schema Generation](https://github.com/impetus-opensource/Kundera/wiki/Schema-Generation)
   * [Transaction Management](https://github.com/impetus-opensource/Kundera/wiki/Transaction-Management)
   * [Rest Based Access](https://github.com/impetus-opensource/Kundera/wiki/REST-Based-Access)
   * [Aggregation over NoSQL](https://github.com/impetus-opensource/Kundera/wiki/How-to-perform-aggregation-over-data-stored-in-NoSQL%3F)
* Tutorials :
   * [Kundera with Openshift](https://github.com/impetus-opensource/Kundera/wiki/Deploying-Polyglot-(RDBMS---NoSQL)-Applications-on-Openshift)
   * [Kundera with Play Framework](https://github.com/impetus-opensource/Kundera/wiki/Using-Kundera-with-Play!-Framework)
   * [Kundera with GWT](https://github.com/impetus-opensource/Kundera/wiki/Using-Kundera-with-GWT)
   * [Kundera with JBoss](https://github.com/impetus-opensource/Kundera/wiki/Using-Kundera-with-Jboss)
   * [Kundera with Spring](https://github.com/impetus-opensource/Kundera/wiki/Building-Applications-with-Kundera-and-Spring)
   * [Kundera with Spark](https://github.com/impetus-opensource/Kundera/wiki/Kundera-with-Spark)
* [Kundera Tagged Questions on stackoverflow.com](http://stackoverflow.com/questions/tagged/kundera)
* [Releases](https://github.com/impetus-opensource/Kundera/blob/trunk/src/README.md)


Contribution
============
* [Contribution Ideas](https://github.com/impetus-opensource/Kundera/wiki/How-to-Contribute#contribution-ideas)
* [Contribution Guidelines](https://github.com/impetus-opensource/Kundera/wiki/How-to-Contribute#contribution-guidelines)

About Us
========
Kundera is backed by Impetus Labs - iLabs. iLabs is a R&D consulting division of [Impetus Technologies](http://www.impetus.com). iLabs focuses on innovations with next generation technologies and creates practice areas and new products around them. iLabs is actively involved working on High Performance computing technologies, ranging from distributed/parallel computing, Erlang, grid softwares, GPU based software, Hadoop, Hbase, Cassandra, CouchDB and related technologies. iLabs is also working on various other Open Source initiatives.

Follow us on [Twitter](https://twitter.com/kundera_impetus).
