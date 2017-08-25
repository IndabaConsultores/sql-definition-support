

[![Travis-CI](https://travis-ci.org/IndabaConsultores/sql-definition-support.svg?branch=master)](https://travis-ci.org/IndabaConsultores/sql-definition-support) [![Sonarcloud](https://sonarcloud.io/api/badges/gate?key=es.indaba:sql-definiton-support)](https://sonarcloud.io/dashboard?id=es.indaba:sql-definiton-support) [![SonarCloud Technical Debt](https://sonarcloud.io/api/badges/measure?key=es.indaba:sql-definiton-support&metric=sqale_debt_ratio)](https://sonarcloud.io/dashboard?id=es.indaba:sql-definiton-support) [![SonarCloud Coverage](https://sonarcloud.io/api/badges/measure?key=es.indaba:sql-definiton-support&metric=coverage)](https://sonarcloud.io/dashboard?id=es.indaba:sql-definiton-support)

(UNRELEASED) SQL Definition Support (LGPL)
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
	<version>0.0.1-SNAPSHOT</version>
</dependency>
```

Check tests for detailed use.

## References
* Keep SQL out of code - http://www.javapractices.com/topic/TopicAction.do?Id=105
* How to store and manage SQL staments - https://dzone.com/articles/how-to-store-and-manage-sql-statements-more-effect

## Contribute
Pull requests are welcomed!!

This is an open debate if you find this support unnecessary or you think there is a better way to manage the SQL queries in code. Please open an issue and we will be please to discuss about it. 
