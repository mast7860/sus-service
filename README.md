# sus-service
System Usability Scale (SUS)

The Service exposes endpoint for

1. generate unique id for each page load
2. collect user responses
3. global statistics

The collected data is stored in a MySql database.

## Technology Stack

* [Coretto](https://docs.aws.amazon.com/corretto/latest/corretto-17-ug/what-is-corretto-17.html) - Amazon OpenJDK 17
* [Gradle](https://gradle.org/) - Build tool
* [Micronaut](https://micronaut.io/) - Microservices framework

## Getting Started

These instructions will get you a copy of the project up and running on your local machine for development and testing
purposes. See deployment for notes on how to deploy the project on a live system.

### Prerequisites

This service requires Java version 17 or newer in order to run, and also requires Java annotation processing to be
enabled if you are using an IDE.

Enabling annotation processing in **IntelliJ IDEA** is done by opening
**Preferences->Build, Execution, Deployment -> Compiler > Annotation Processors** and then
selecting **Enable annotation processing**.

### Installing

The service can be built using the following command:

```
./gradlew build
```

### Running

The service can start running using the following command
Start mysql docker container before starting service
```
./gradlew run
```

## Running the tests

Unit tests can be run using the following command:

```
./gradlew test
```

## Useful docker commands
```
docker compose commands
docker compose ps : to see the state of all the containers
docker compose up : start
docker compose down : remove
docker compose build : build images
docker compose up -d --build : Rebuild and restart the containers
```

## Database Setup
```
mysql -h host -u username -p

CREATE DATABASE dbName;

Service user for application
CREATE USER 'userOne'@'%' IDENTIFIED BY 'password';
GRANT SELECT, INSERT, UPDATE ON `ocfauroradb`.* TO 'userOne'@'%';

# To verify user and see validate grants
select * from mysql.user;
show grants for userName;


```
##References
https://www.usability.gov/how-to-and-tools/methods/system-usability-scale.html

```
> 80.3 - A
68-80.3
68 C
51-68 - D
<51 - F
```
# How to Run Full setup locally

- get client n monitoring setup code in the same directory as service
- git clone https://github.com/mast7860/sus-client.git
- git clone https://github.com/mast7860/monitoring.git
- The project contains docker-sus , which runs mysql, client and server
- run ./gradlew build to build the project
- then docker compose build && docker compose up
- RUN readme of monitoring , make needed changes and start the container
- In the sus-service
  - grafana folder contains dashboard json which can  be imported to see in dashboard
  - the postman folder contains postman collection , used to test api from postman tool
- http://localhost:8080/swagger-ui#/ : can see the api contact
- java 17 , gradle 7.3.3 , docker are needed at minimum to run 