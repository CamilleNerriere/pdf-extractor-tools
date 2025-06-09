# PDF Extractor Tools - Noësis project

This app is part of a larger project called *Noësis* (information soon to come). It aims (among other goals) to provide effective tools for researchers and students. 

## Actual key functionalities 
* **Academic citations extraction**

From a PDF, you can export (PDF, Word or txt handled) all the citations in the original document (traditional citations between quotation marks, harvard citations, and block citations). 

You can find it as an individual tool [here](https://github.com/CamilleNerriere/citation-extractor) - with also more informations 

* **Annotations extraction**

From a PDF, you can export all the annotations added (PDF, Word and txt export). 

You can find it as an individual tool [here](https://github.com/CamilleNerriere/java-annotation-extractor/tree/main).

* **A basic authentication service**

As it is still a demo, registration is disabled. A basic demo user is provided to try the main functionalities. 

## Technical details

### Tech Stack

* Java 17
* Spring Boot 3.5
* JWT Authentication (access token only in demo mode -> refresh token planned in prod)
* Bucket4j for rate limiting
* Maven
* H2 (demo) -> PostgreSQL (prod)

### Security and Middleware

* JWT Authentication with Authorization: Bearer token
* Rate limiting with Bucket4j
    * General: 100 requests / 15 min
    * Auth : 5 requests / 1 min
    * Critical (PDF extraction): stricter limits - 3 requests / min
* Custom middlewares using Spring Interceptors
* Custom error handling 

### Tests

* Password hashing with Argon2 verified
* Jwt Service tested
* Authenticated endpoints tested with MockMvc
* Extraction endpoints tested: valid ZIP response with dummy files
* Rate limits tested

### Features in Code

| Feature                | Path                      | Description                          |
|------------------------|---------------------------|--------------------------------------|
| **Register**           | `GET /auth/register`      | Returns new user non sensitive infos  - disactivated in demo mode                       |
| **Login**              | `POST /auth/login`        | Returns JWT                          |
| **User Info**          | `GET /user/me`            | Requires JWT                         |
| **Update User**        | `PUT /user/update`        | Auth required                        |
| **Citation Extraction**| `POST /extract/citations` | File + formats, returns ZIP          |
| **Annotation Extraction** | `POST /extract/annotations` | File + formats, returns ZIP      |


