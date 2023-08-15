# Microservice ms-bills

The `ms-bills` microservice handles bills-related functionality in the system. It provides endpoints to retrieve bills, save new bills and search for bills by customer id.

## Table of Contents

- [Microservice ms-bills](#microservice-ms-bills)
  - [Table of Contents](#table-of-contents)
  - [Dependencies](#dependencies)
  - [Configuration](#configuration)
    - [Security](#security)
    - [Eureka Client](#eureka-client)
  - [Endpoints](#endpoints)
  - [Technical Documentation](#technical-documentation)
  - [Instructions for Use](#instructions-for-use)

## Dependencies

This microservice is built using the following technologies and libraries:

- Java 17
- Spring Boot 3.0.9
- Spring Cloud 2022.0.4
- H2 Database for data storage

## Configuration

### Security

Security in this microservice is handled through OAuth2 and JWT. The resource server is configured to authenticate JWT tokens and authorization rules are defined on endpoints using `@PreAuthorize` annotations.

### Eureka Client

The microservice registers with the discovery server (Eureka) to allow communication between microservices dynamically.

## Endpoints

This microservice provides the following endpoints:

- `GET /bills/all`: Retrieves all bills. Requires the `USER` role.
- `POST /bills/save`: Saves a new bill. Requires the `PROVIDERS` authority.
- `GET /bills/findAllByCustomerBill/{customerBill}`: Retrieves bills by customer id.

## Technical Documentation

For a more detailed understanding of the workflow of this microservice, refer to the [full technical documentation](../DOCUMENTATION.md).

## Instructions for Use

1. Start Keycloak:

Before running the microservice, ensure that Keycloak is running and accessible on port 8090. This is essential for JWT token validation and for accessing the JWK set URI.

- Initialize Keycloak Realm (if applicable): \*\* If you have not already initialized the [keycloak-initializer](../keycloak-initializer/) for the first time, proceed to do so. The ms-bills microservice interacts with Keycloak for authentication or authorization, follow any specific steps required to initialize the Keycloak realm, clients, users, roles, and necessary configurations.

2. Run the ms-bills microservice:

Launch the ms-bills microservice. Ensure that the microservice is configured to connect to the Eureka discovery server and any other relevant components.

Please note that the Eureka server and Keycloak are integral components for the proper functioning of the ms-bills microservice. Make sure they are properly configured and operational before starting the microservice.

Remember to set any required environment variables for the microservice's operation, as described in the [full technical documentation](../DOCUMENTATION.md).
