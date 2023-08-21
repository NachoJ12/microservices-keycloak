# microservices-keycloak

Project to practice using keycloak together with microservices.

## Table of contents

- [microservices-keycloak](#microservices-keycloak)
  - [Table of contents](#table-of-contents)
  - [Documentation](#documentation)
  - [Technical documentation](#technical-documentation)
  - [Instructions for use](#instructions-for-use)
    - [Docker Compose configuration](#docker-compose-configuration)
    - [Keycloak](#keycloak)
    - [Start Microservices](#start-microservices)

## Documentation

This documentation details the components and microservices of this repository.

- [Keycloak Initializer](keycloak-initializer/README.md)
- [Microservice Discovery](ms-discovery/README.md)
- [Microservice Bills](ms-bills/README.md)

## Technical documentation

- Ports:
  - keycloak: 8090
  - keycloak-initializer: dynamic
  - ms-discovery(eureka): 8761
  - ms-bills: dynamic

## Instructions for use

1. **Clone this Repository:** Begin by cloning this repository to your local machine:

```
git clone https://github.com/NachoJ12/microservices-keycloak.git
```

### Docker Compose configuration

This project uses Docker Compose to simplify deployment and container management. To run the application with Docker Compose, follow these steps:

1. **Install Docker and Docker Compose:** Make sure you have Docker and Docker Compose installed on your system.

2. **Start Containers:** Located in the root folder where the docker-compose.yml file is located, you must execute the following command in your terminal for the creation and execution of the containers in the background.

```
docker-compose up -d
```

This command will create and run the containers defined in the `docker-compose.yml` file.

**_Stop and Delete Containers_**

When you have finished using the application, you can stop the containers in two ways:

- To **stop containers without deleting them**, run:
  `docker-compose stop`

- To **stop and delete containers**, run:
  `docker-compose down`

This will remove the containers and free the resources used by them.

### Keycloak

1. **Access Keycloak Console:** Once the containers are running, you can access the Keycloak console by opening [http://localhost:8090](http://localhost:8090) in your web browser. Ensure that Keycloak is running on port 8090.

2. **Initialize Keycloak Realm:** Inside the repository you will find the [keycloak-initializer](keycloak-initializer/) which will create the realm, clients, users and necessary configurations.
   First, execute the `KeycloakInitializerApplication` to create the realm and its configurations.
   **This must be executed only once**.

### Start Microservices

1. Start the [ms-discovery](ms-discovery/) microservice.

2. Then start the [ms-bills](ms-bills/) microservice. Once the microservice has been successfully initialized, it's ready to handle requests through its endpoints.
