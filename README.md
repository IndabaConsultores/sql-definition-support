

[![Travis-CI](https://travis-ci.org/IndabaConsultores/sql-definition-support.svg?branch=master)](https://travis-ci.org/IndabaConsultores/sql-definition-support) [![Sonarcloud](https://sonarcloud.io/api/project_badges/measure?project=es.indaba:sql-definition-support&metric=alert_status)](https://sonarcloud.io/dashboard?id=es.indaba:sql-definition-support) [![SonarCloud Technical Debt](https://sonarcloud.io/api/project_badges/measure?project=es.indaba:sql-definition-support&metric=sqale_rating)](https://sonarcloud.io/dashboard?id=es.indaba:sql-definition-support) [![SonarCloud Coverage](https://sonarcloud.io/api/project_badges/measure?project=es.indaba:sql-definition-support&metric=coverage)](https://sonarcloud.io/dashboard?id=es.indaba:sql-definition-support)

SQL Definition Support (LGPL)
=============
Support library for externalizing SQL queries from Java code (LGPL)

A CDI Extension to call stored procedures or database functions declaratively using hibernate.

Feel free to use this library as you wish, make sure to quote the LGPL in all used sources.

## Using this project

Include Maven dependency on your pom.xml

```xml
<dependency>
	<groupId>es.indaba</groupId>
	<artifactId>sql-definition-support</artifactId>
	<version>1.1.0</version>
</dependency>
```

Place the queries in one or many text files with .sqld extension in the applications classpath. 
i.e. com/test/test-queries.sql

```
MY_QUERY_1 {
 SELECT * 
 FRON ANY_TABLE 
 WHERE A=? OR B=?
}
-- Second query
MY_QUERY_2 {
 -- This is my favorite query
 SELECT * 
 FRON FAVORITE_TABLE 
 WHERE C=?
}
...
```

YAML syntax is also supported for files with .ysqld extension

com/test/test.ysqld

```yaml
query1: |
 QUERY1_CONTENT
query2: |
 QUERY2_CONTENT
query5: |
 Select * 
 from table
 where a=1
 and b=?
```

Load the sqld definitions on your application startup, providing the classpath prefix for restricting the search

```java
...
QueryDefinitionsStaticHolder.loadQueryDefinitions("com.test");
...
```
Where you need to access to a query instantiate a QueryDefinition class with the query's key

```java
...
QueryDefinition query = new QueryDefinition("MY_QUERY_2");
-- Get the query as a String
String sqlQuery = query.getQueryAsString();
...
sqlQuery = QueryDefinitionsStaticHolder.getQueryAsString("query1");
...
```

Check tests for detailed use.

## References
* Keep SQL out of code - http://www.javapractices.com/topic/TopicAction.do?Id=105
* How to store and manage SQL statements - https://dzone.com/articles/how-to-store-and-manage-sql-statements-more-effect

## Contribute
Pull requests are welcomed!!

This is an open debate if you find this support unnecessary or you think there is a better way to manage the SQL queries in code. Please open an issue and we will be please to discuss about it. 

## Licenses
This work is distributed under LGPL v3.

The text file parser (es.indaba.sqld.parser.TextBlockReader) is inspired in the work done by the [WEB4J](http://www.web4j.com/) project that was released under the 3-Clause BSD License. See /LICENSES/BSD-LICENSE.txt. 
 

