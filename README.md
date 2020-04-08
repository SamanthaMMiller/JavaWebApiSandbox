# JavaWebApiSandbox
Playing about with learning how to create a web API in Java and using Spring Boot. 

# Tech Stack
* Java 14
* Maven
* Spring Boot
* Spring MVC (via Spring Boot Web Starter)
* Spring Data JPA (via Spring Boot Data JPA Starter)
* SpringFox (for OpenAPI / Swagger)
* H2 SQL database (in-memory)
* Project Lombok
* JUnit unit tests

# Running this project

Open a terminal at the root of this project and run:
```
mvnw spring-boot:run
```
Once this is complete, open a browser and navigate to http://localhost:8080/swagger-ui.html. 

Here, you can add, update, and retrieve (very simple versions of) employee records. This application uses an H2 in-memory SQL database, so the database is reset to empty whenever you re-start the application. 

To query the in-memory H2 database, browse to http://localhost:8080/h2-console (and login to the database using the credentials in the application.properties file).
