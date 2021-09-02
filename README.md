# Case Study  

This is a Java 11 project.  

### Details  

This microservice is a Spring Boot Application which also includes AuthorizationServer for authorization and ResourceServer for endpoint security. In addition it uses a H2 database.  

An initial Oauth2 client defined with the credentials of "client_id" and "secret". It can be used to retrieve jwt token before interact with the endpoints.  

Because of the limited time some assumptions made like;  

* A customer can only order one piece of a book, but multiple books can be bought at the same time.  
* Validation are done only as clear as the case states. Further validations should be defined.  
   

### Usage  

To run the application gradle command or provided [Dockerfile](Dockerfile) can be used.  

##### Using Gradle  
From the root folder run: `./gradlew bootRun` The application will run on port 8080

##### Using Docker  
From the root folder run:  

`./gradlew bootJar`  
`docker build --tag readingisgood:1.0 .`  
`docker run --publish 8080:8080 -d --name readingisgood readingisgood:1.0`  
