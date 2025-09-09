# Byte Code Incremental Experimental Login

<h2>RUN BOOK</h2> 

<h3>SPECIFICATION:</h3>

<ol>
<li> 
<ul> 
<p> STACK: </p>
<li>Java 11</li> 
<li>Spring 2.5.4</li>
<li>Gradle 7.4</li>
</ul>
</li>

<li> 
<ul><p> System: </p>
<li>Windows 11</li> 
<li>Mac OS</li>
<li>Linux</li>
</ul>
</li>

</ol>

<h3> APPLICATION CONFIGURATION FILE (properties): </h3>

<h4># H2 / Hibernate </h4>
spring.application.name=UserClientGlBci 

server.port=8082 

spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE;

spring.datasource.driver-class-name=org.h2.Driver

spring.h2.console.path=/h2-console

spring.h2.console.enabled=true

#spring.datasource.username=sa

#spring.datasource.password=

<h4># JPA / Hibernate</h4>

spring.jpa.database-platform=org.hibernate.dialect.H2Dialect

spring.jpa.hibernate.ddl-auto=create-drop

spring.jpa.show-sql=true

<h3>GRADLE COMMANDS:<h3> 

Take Considerations on Linux shell use ./gradlew

<ol>
<li>gradlew bootRun >> RUN API</li>
<li>ctrl + C, over shell terminal used >> STOP API</li>
<li>gradlew test >> RUN TESTING and Jacoco Collection Report </li>
<li>TO SEE Coverage Test Jacoco GO TO "build/reports/jacoco/index.html" file, open in browser</li>
<li>document index.html or testsession.html</li>
</ol>

<h3># ClIENT API HTTPie </h3>

- <h4> ON APP open import collection and select: </h4> .../documentation [httpie-collection-httpie-ejercicio-global-logic.json](docs/httpie-collection-httpie-ejercicio-global-logic.json)

<h3># DOCUMENTATION</h3>
root directory / documentation

../documentation [AuthController_login.png](docs/AuthController_login.png)


