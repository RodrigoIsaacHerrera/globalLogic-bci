# GLOBAL LOGIC - BCI
Evaluation Java 11 / Spring 2.5.4 / Gradle 7.X

Run Book

application configuration file (properties):

# H2 / Hibernate
spring.application.name=UserClientGlBci 

server.port=8082 

spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE;

spring.datasource.driver-class-name=org.h2.Driver

spring.h2.console.path=/h2-console

spring.h2.console.enabled=true

#spring.datasource.username=sa

#spring.datasource.password=

# JPA / Hibernate

spring.jpa.database-platform=org.hibernate.dialect.H2Dialect

spring.jpa.hibernate.ddl-auto=create-drop

spring.jpa.show-sql=true

# Gradle 
gradlew bootRun >> RUN API
ctrl+c over shell terminal used >> STOP API

gradlew test >> RUN TESTING and Jacoco Collection Report 

TO SEE TEST GO TO "reports" file in project directory open in browser
document index.html or testsession.html

# DOCUMENTATION
root directory / documentation