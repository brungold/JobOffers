JobOffers
"JobOffers" is a Spring Boot web application that allows users to efficiently browse job offers from popular platforms such as NoFluffJobs and Pracuj.pl. The application, using a scheduler every three hours, checks whether new job offers have appeared. Users are required to register in the application, and upon registration, a token will be generated for them, providing access to job offers and the ability to add their own listings.

Key Features
Fetching Offers from NoFluffJobs and Pracuj.pl:

The program employs two HTTP clients to retrieve job offers from NoFluffJobs and Pracuj.pl platforms.
Authorization:

Clients must use a token to gain access to job offers. The option to register a new user is also available.
Offer Updates:

The database is regularly updated every 3 hours by querying a remote server from point 1.
Offer Uniqueness:

Job offers in the database are unique and do not repeat. This is determined by the offer's URL.
Fetching Offers by Id:

The client can retrieve a single job offer using a unique identifier.
Fetching All Available Offers:

Authorized clients have the capability to retrieve all available job offers.
Cache for Requests More Frequent than Every 60 Minutes:

If a client makes more than one request within 60 minutes, data is retrieved from the cache to minimize costs associated with fetching from the database.
Manual Addition of Job Offers:

Clients can manually add a new job offer to the database.
Job Offer Attributes:

Each job offer includes a link to the listing, job position name, company name, and salary information (which can be provided in a range).
Endpoints
/offers - GET: Retrieves job offers.
/offers/{id} - GET: Finds a job offer by its identifier.
/register - POST: Registers a new user.
/token - POST: Provides a token to the user.
/offers - POST: Creates a new job offer.
Technology
Java 17, Log4j2, Lombok, Maven
RestTemplate, JSON, HTTP, MockMvc:
RestTemplate: Tool for communication with REST services.
JSON: Data format for transmitting information between server and client.
HTTP: Communication protocol used in the application.
MockMvc: Framework for testing controllers without the need to start the server.
Spring Boot (Various modules used in the project):
Web (RestControllers): Implementation of the controller layer handling HTTP requests.
Test: Support for unit and integration testing.
Data Mongodb: Integration with MongoDB for database operation handling.
Validation: Input data validation.
Security + JWT: Ensuring security and authorization using JWT tokens.
Spring Scheduler:
Task scheduling mechanism used for cyclically checking for new job offers.
Database:
MongoDB + MongoExpress: NoSQL database, including a tool for managing MongoDB (MongoExpress).
Docker & Docker-Compose & Docker Desktop:
Containerization of the application and environment.
Redis & Jedis & Redis-Commander:
Key-value database used for caching data in memory (cache).
Unit Tests:
JUnit5, Mockito, AssertJ: Frameworks for unit testing, object mocking, and assertions.
Integration Tests:
Wiremock: Tool for creating artificial endpoints for integration testing.
Testcontainers: Library supporting integration testing, allowing for running Docker containers for testing resources.
SpringBootTest, MockMvc, SpringSecurityTest: Tests that verify the interaction between different components of the system.
