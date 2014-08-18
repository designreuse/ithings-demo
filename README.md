
Based on jhipster.github.com


README for iThings Demo
==========================

it's a POC that's try to integrate multiple technologies....

* spring-data-jpa / hibernate + multi-tenancy
* spring-data-mongodb / with cross-store capabilitie with JPA
* spring-data-elasticsearch
* distributed cache: hazelcast
* spring-security / oauth2
* angularjs frontend


Start the backend
=================

* dev mode

mvn spring-boot:run

* prod mode

mvn -Pprod spring-boot:run

* package war

mvn clean package


Start the front-end
===================

grunt server

*  test / build / minified

grunt

