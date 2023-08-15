# microservices-keycloak

Project to practice using keycloak together with microservices.

## Technical documentation

- Java version: 17
- Spring Boot version: 3.0.8
- Spring Cloud version: 2022.0.4
- Keycloak version: 21.1.1

- Ports:
  - keycloak: 8090
  - keycloak-initializer: dynamic
  - ms-discovery(eureka): 8761

## Instructions for use

1. Located in the root folder where the docker-compose.yml file is located, you must execute the following command in your terminal for the creation and execution of the containers in the background.

```
docker-compose up -d
```

2. Keycloak must be started, which must be on port 8090.

3. Inside the repository we will find the 'keycloak-initializer' which will create the realm, clients, users and necessary configurations.
   The 'KeycloakInitializerApplication' must be executed first, in this way the realm and all its configurations are created as mentioned above.
   **This must be executed only once**.
   It will be possible to visualize in the console the description of the actions performed as well as the IDs generated for the users automatically by keycloak (due to integrity issues keycloak does not allow to set the id directly in the creation of the users).

4. Subsequently, the ms-discovery microservice should be started.

## Keycloak

Creation of realm, clients, users, roles, groups and configurations from code.

You will need to set the environment variables that are required for initialization, which I present with example data below.

- Environment variables (example):
  - KEYCLOAK_PORT= 8090
  - KEYCLOAK_ADMIN= admin
  - KEYCLOAK_ADMIN_PASSWORD= admin
  - BACKEND_CLIENT_ID = backend
  - BACKEND_CLIENT_SECRET = c6zWnLUoesehl8RxQZsXgfjRh7ffNkww
  - GATEWAY_CLIENT_ID = gateway-client
  - GATEWAY_CLIENT_SECRET = gateway-secret

### Keycloak Initializer

- Creation of 'ecommerce-rta' realm.
- Creation of two clients:
  - gateway-client
  - backend
- Creation of realm role 'APP_USER'.
- Creation of a group 'PROVIDERS'.
- In the 'Client Scopes' section, inside profile add in your mappers the Type 'Group Membership' with the name 'group' so that the groups are sent by token.
- Creation of a user 'beliquasa' with credentials 'password123' and assignment of the group 'PROVIDERS'.
- Creation of a user 'user1' with credentials 'password123' and assignment of the role 'USER'.
- Creation of a user 'user2' with credentials 'password123' WITHOUT role assignment.

**backend**

- Creation of 'backend' client.
- In its 'Capability config' section the 'Client authentication' was configured for a confidential access type. The 'Direct access grants' was deactivated and the 'Service accounts roles' was activated.
- Created the 'USER' role which in turn was composed by a realm level role 'APP_USER'.
- Permissions were granted in 'Services accounts roles' of 'query-users' and 'view-users' to be able to make queries on users.

**gateway-client**

- Creation of client 'gateway-client' redirection to http://localhost:9090/\*
- In its 'Capability config' section the 'Client authentication' was configured for a confidential access type. The 'Direct access grants' was deactivated and the 'Service accounts roles' was activated.
- The 'USER' role was created, which in turn was composed by a realm level role 'APP_USER'.
