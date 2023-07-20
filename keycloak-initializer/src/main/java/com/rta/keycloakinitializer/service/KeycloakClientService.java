package com.rta.keycloakinitializer.service;

import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.*;
import org.keycloak.representations.idm.ClientRepresentation;
import org.keycloak.representations.idm.RealmRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.springframework.stereotype.Service;

import javax.ws.rs.core.Response;
import java.util.List;


@Service
public class KeycloakClientService {

    private static final String BACKEND_CLIENT_ID = System.getenv("BACKEND_CLIENT_ID");
    private static final String BACKEND_CLIENT_SECRET = System.getenv("BACKEND_CLIENT_SECRET");

    private final Keycloak keycloak;

    public KeycloakClientService(Keycloak keycloak) {
        this.keycloak = keycloak;
    }

    public void createRealmAndConfigs(String realmName) {
        createRealm(realmName);
        setRealmLevelRoles(realmName, List.of("APP_USER", "APP_ADMIN"));

        // Create backend client representation
        ClientRepresentation backendClient = new ClientRepresentation();
        backendClient.setClientId(BACKEND_CLIENT_ID);
        backendClient.setSecret(BACKEND_CLIENT_SECRET);
        backendClient.setServiceAccountsEnabled(true);
        backendClient.setEnabled(true);

        createClient(realmName, backendClient, List.of("USER", "MANAGER"));

    }

    private void createRealm(String realmName){
        RealmRepresentation realm = new RealmRepresentation();
        realm.setRealm(realmName);
        realm.setEnabled(true);
        keycloak.realms().create(realm);
        System.out.println("\nRealm " + realmName + " create successful");
    }

    private void setRealmLevelRoles(String realmName, List<String> realmLevelRoles) {
        RealmsResource realmsResource = keycloak.realms();
        RolesResource rolesResource = realmsResource.realm(realmName).roles();

        for(String roleName: realmLevelRoles){
            RoleRepresentation role = new RoleRepresentation();
            role.setName(roleName);
            rolesResource.create(role);
        }
        System.out.println("\nRealm roles " + realmLevelRoles + " added successfully");
    }

    private void createClient(String realmName, ClientRepresentation clientRepresentation, List<String> roles){
        RealmResource realmResource = keycloak.realm(realmName);
        ClientsResource clientsResource = realmResource.clients();

        Response response = clientsResource.create(clientRepresentation);

        if (response.getStatus() == 201) {
            String createdClientId = response.getLocation().getPath().replaceAll(".*/([^/]+)$", "$1");

            addRolesToClient(clientsResource, createdClientId, roles);

            System.out.println("ClientId: " + createdClientId + " - Name: " + clientRepresentation.getClientId() + " - Roles: " + roles);
        }
    }

    private void addRolesToClient(ClientsResource clientsResource, String clientId, List<String> roles){
        ClientResource clientResource = clientsResource.get(clientId);

        for (String role: roles) {
            RoleRepresentation roleRepresentation = new RoleRepresentation();
            roleRepresentation.setName(role);
            roleRepresentation.setClientRole(true);
            roleRepresentation.setContainerId(clientId);

            clientResource.roles().create(roleRepresentation);
        }
    }




}