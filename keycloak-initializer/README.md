# Keycloak Initializer Documentation

## Table of Contents

- [Keycloak Initializer Documentation](#keycloak-initializer-documentation)
  - [Table of Contents](#table-of-contents)
  - [Dependencies](#dependencies)
  - [Environment](#environment)
  - [Keycloak Initializer](#keycloak-initializer)
    - [Description](#description)
    - [gateway-client](#gateway-client)
    - [backend](#backend)
  - [Technical Documentation](#technical-documentation)
  - [Instructions for Use](#instructions-for-use)

## Dependencies

Keycloak Initializer uses the following technologies

- Java 17
- Spring Boot 3.0.8
- Keycloak version: 21.1.1
  - `keycloak-spring-boot-starter`
  - `keycloak-admin-client`

## Environment

To use Keycloak Initializer, you need to set the following environment variables:

- **KEYCLOAK_PORT**: Port number for Keycloak (Example: 8090)
- **KEYCLOAK_ADMIN**: Keycloak admin username (Example: admin)
- **KEYCLOAK_ADMIN_PASSWORD**: Keycloak admin password (Example: admin)
- **BACKEND_CLIENT_ID**: Client ID for the 'backend' client (Example: backend)
- **BACKEND_CLIENT_SECRET**: Client secret for the 'backend' client (Example: c6zWnLUoesehl8RxQZsXgfjRh7ffNkww)
- **GATEWAY_CLIENT_ID**: Client ID for the 'gateway-client' client (Example: gateway-client)
- **GATEWAY_CLIENT_SECRET**: Client secret for the 'gateway-client' client (Example: gateway-secret)

## Keycloak Initializer

### Description

Keycloak Initializer is a tool for programmatically creating realms, clients, users, roles, groups, and configurations in Keycloak.

- Creation of 'ecommerce-rta' realm.
- Creation of two clients:
  - [gateway-client](#gateway-client)
  - [backend](#backend)
- Creation of realm role 'APP_USER'.
- Creation of a group 'PROVIDERS'.
- In the 'Client Scopes' section, inside profile add in your mappers the Type 'Group Membership' with the name 'group' so that the groups are sent by token.
- Creation of a user 'beliquasa' with credentials 'password123' and assignment of the group 'PROVIDERS'.
- Creation of a user 'user1' with credentials 'password123' and assignment of the role 'USER'.
- Creation of a user 'user2' with credentials 'password123' WITHOUT role assignment.

### gateway-client

- Creation of client 'gateway-client' redirection to http://localhost:9090/\*
- In its 'Capability config' section the 'Client authentication' was configured for a confidential access type. The 'Direct access grants' was deactivated and the 'Service accounts roles' was activated.
- Create the role 'USER', composed of a role at realm level 'APP_USER'.

### backend

- Creation of 'backend' client.
- In its 'Capability config' section the 'Client authentication' was configured for a confidential access type. The 'Direct access grants' was deactivated and the 'Service accounts roles' was activated.
- Created the 'USER' role which in turn was composed by a realm level role 'APP_USER'.
- Permissions were granted in 'Services accounts roles' of 'query-users' and 'view-users' to be able to make queries on users.

## Technical Documentation

For a more detailed understanding, refer to the [full technical documentation](../README.md).

## Instructions for Use

1. Start Keycloak:

   Ensure that Keycloak is running and accessible on the correct port (Example: 8090).

2. Initialize Keycloak:

   Run the `KeycloakInitializerApplication` to create the realm, clients, users, roles, groups and configurations. **This must be executed only once**.

   It will be possible to visualize in the console the description of the actions performed as well as the IDs generated for the users automatically by keycloak (due to integrity issues keycloak does not allow to set the id directly in the creation of the users).
