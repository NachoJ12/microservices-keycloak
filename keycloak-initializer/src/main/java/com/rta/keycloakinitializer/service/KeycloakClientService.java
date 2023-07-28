package com.rta.keycloakinitializer.service;

import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.*;
import org.keycloak.representations.idm.*;
import org.springframework.stereotype.Service;

import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


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
        String clientBackendId = getClientId(realmName, BACKEND_CLIENT_ID);
        compositeClientRoleWithRealmRole(realmName, clientBackendId, "USER", "APP_USER");

        List<String> rolesToAssign = Arrays.asList("view-users", "query-users");
        addServiceAccountsRoles(realmName, BACKEND_CLIENT_ID, rolesToAssign);

        addGroupsToToken(realmName, "profile");

        createGroup(realmName, "PROVIDERS");

        createUser(realmName, "beliquasa", "password123", "beliqua-sa@beliquasa.com");
        assignGroupToUser(realmName, "beliquasa", "PROVIDERS");

        createUser(realmName, "user1", "password123", "user1@gmail.com");
        assignRoleToUser(realmName, "user1", clientBackendId,"USER");

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
        RealmResource realmResource = getRealmResource(realmName);
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

    private String getClientId(String realmName, String clientName){
        RealmResource realmResource = getRealmResource(realmName);
        ClientsResource clientsResource = realmResource.clients();
        String clientResource = clientsResource.findByClientId(clientName).get(0).getId();

        return clientResource;
    }

    /** MAKE A CLIENT ROLE COMPOSITE OF A REALM LEVEL ROLE **/
    public void compositeClientRoleWithRealmRole(String realmName, String clientId, String clientRole, String realmRole){
        RealmResource realmResource = getRealmResource(realmName);
        ClientsResource clientsResource = realmResource.clients();
        ClientResource clientResource = clientsResource.get(clientId);

        RolesResource rolesResource = realmResource.roles();

        // Get client role (example: USER)
        RoleResource clientRoleResource = clientResource.roles().get(clientRole);

        // Get ID realm role (example: APP_USER)
        RoleResource realmRoleResource = rolesResource.get(realmRole);
        String realmRoleId = realmRoleResource.toRepresentation().getId();

        // Create a list of composites roles
        List<RoleRepresentation> composites = new ArrayList<>();
        RoleRepresentation realmRoleRepresentation = new RoleRepresentation();
        realmRoleRepresentation.setId(realmRoleId);
        realmRoleRepresentation.setName(realmRole);
        composites.add(realmRoleRepresentation);

        // Add the composite roles to the client role
        clientRoleResource.addComposites(composites);
    }


    private RealmResource getRealmResource(String realmName) {
        return keycloak.realm(realmName);
    }

    /** ASSIGN A SERVICE ACCOUNTS ROLE **/
    private void addServiceAccountsRoles(String realmName, String clientIdName, List<String> rolesToAssign){
        RealmResource realm = getRealmResource(realmName);
        String realmManagementId = realm.clients().findByClientId("realm-management").get(0).getId();

        String clientId = realm.clients().findByClientId(clientIdName).get(0).getId();
        String serviceAccountUserId = realm.clients().get(clientId).getServiceAccountUser().getId();

        List<RoleRepresentation> availableRoles = realm.users().get(serviceAccountUserId).roles().clientLevel(realmManagementId).listAvailable();

        List<RoleRepresentation> filteredRolesToAssign = availableRoles.stream()
                .filter(r -> rolesToAssign.contains(r.getName().toLowerCase()))
                .collect(Collectors.toList());

        realm.users().get(serviceAccountUserId).roles().clientLevel(realmManagementId).add(filteredRolesToAssign);

        System.out.println("The following service accounts roles " + filteredRolesToAssign + " have been assigned to the " + clientIdName + " client");
    }


    /** ADD GROUPS TO TOKEN **/
    // This method allows adding the Group Membership mapper to the Client Scope received by parameter
    private void addGroupsToToken(String realmName, String scope) {
        RealmResource realmResource = getRealmResource(realmName);
        List<ClientScopeRepresentation> scopes = realmResource.clientScopes().findAll();

        ClientScopeRepresentation clientScope = scopes.stream()
                .filter(cs -> cs.getName().equals(scope))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Client scope not found: " + scope));

        String clientScopeId = clientScope.getId();

        ProtocolMapperRepresentation groupMembership = new ProtocolMapperRepresentation();
        groupMembership.setName("group");
        groupMembership.setProtocol("openid-connect");
        groupMembership.setProtocolMapper("oidc-group-membership-mapper");
        groupMembership.getConfig().put("claim.name", "groups");
        groupMembership.getConfig().put("full.path", "false");
        groupMembership.getConfig().put("id.token.claim", "true");
        groupMembership.getConfig().put("access.token.claim", "true");
        groupMembership.getConfig().put("userinfo.token.claim", "true");

        ClientScopeResource clientScopeResource = realmResource.clientScopes().get(clientScopeId);

        try {
            clientScopeResource.getProtocolMappers().createMapper(groupMembership);
        } catch (Exception e) {
            throw new RuntimeException("Error adding group mapper to client scope: " + e.getMessage(), e);
        }

        ClientScopeRepresentation updatedClientScope = clientScopeResource.toRepresentation();

        try {
            clientScopeResource.update(updatedClientScope);
        } catch (Exception e) {
            throw new RuntimeException("Error updating client scope: " + e.getMessage(), e);
        }

    }


    private String getCreatedId(Response response) {
        String createdId = CreatedResponseUtil.getCreatedId(response);
        response.close();
        return createdId;
    }

    /** CREATE GROUP **/
    private void createGroup(String realmName, String groupName) {
        RealmResource realmResource = getRealmResource(realmName);

        GroupsResource groupsResource = realmResource.groups();

        GroupRepresentation groupRepresentation = new GroupRepresentation();
        groupRepresentation.setName(groupName);
        //groupRepresentation.singleAttribute("Title", "attributeTest");

        String groupId = getCreatedId(groupsResource.add(groupRepresentation));

        System.out.println("\nGroupId: " + groupId + " - name: " + groupName + ", created succesfully");
    }

    /** CREATE A CREDENTIAL REPRESENTATION THAT ALLOWS YOU TO SET PASSWORDS **/
    private CredentialRepresentation createPasswordCredentials(String password) {
        CredentialRepresentation passwordCredentials = new CredentialRepresentation();
        passwordCredentials.setTemporary(false);
        passwordCredentials.setType(CredentialRepresentation.PASSWORD);
        passwordCredentials.setValue(password);
        return passwordCredentials;
    }

    /** CHECK IF THERE IS A USER WITH THE SAME NAME **/
    private boolean userExists(String realmName, String username) {
        List<UserRepresentation> userRepresentationList = getRealmResource(realmName)
                .users()
                .searchByUsername(username,true);

        return !userRepresentationList.isEmpty();
    }

    /** CREATE NEW USER **/
    private void createUser(String realmName, String username, String password, String email) {
        RealmResource realmResource = getRealmResource(realmName);
        UsersResource usersResource = realmResource.users();

        // Create credentials (password)
        CredentialRepresentation passwordCredentials = createPasswordCredentials(password);

        if (!userExists(realmName, username)) {
            UserRepresentation newUser = new UserRepresentation();
            newUser.setUsername(username);
            newUser.setEmail(email);
            newUser.setCredentials(Collections.singletonList(passwordCredentials));
            newUser.setEnabled(true);

            usersResource.create(newUser);

            String userId = usersResource.searchByUsername(username, true).get(0).getId();
            System.out.println("\nUser created successfully. UserID: " + userId + " - username: " + username);
        } else {
            System.out.println("The user '" + username + "' already exists. It was not created.");
        }
    }

    /** ASSIGN GROUP TO A USER (both previously created) **/
    private void assignGroupToUser(String realmName, String username, String groupName){
        RealmResource realmResource = getRealmResource(realmName);

        UserRepresentation userRepresentation = realmResource.users().searchByUsername(username,true).get(0);
        UserResource userResource = realmResource.users().get(userRepresentation.getId());

        GroupsResource groupsResource = realmResource.groups();

        GroupRepresentation groupRepresentation = groupsResource.groups()
                .stream()
                .filter(groupR -> groupR.getName().equals(groupName))
                .findFirst()
                .orElse(null);

        if(groupRepresentation != null){
            userResource.joinGroup(groupRepresentation.getId());

            System.out.printf("Assigned to user '%s' the group '%s' with groupId '%s'",
                    userRepresentation.getUsername(), groupRepresentation.getName(), groupRepresentation.getId() );
        } else {
            System.out.println("Group '" + groupName + "' not found");
        }
    }

    /** ASSING ROLE TO A USER (both previously created) **/
    private void assignRoleToUser(String realmName, String username, String clientId, String roleName){
        RealmResource realmResource = getRealmResource(realmName);

        UserRepresentation userRepresentation = realmResource.users().searchByUsername(username,true).get(0);
        UserResource userResource = realmResource.users().get(userRepresentation.getId());

        // Search for the client's role
        ClientsResource clientsResource = realmResource.clients();
        ClientResource clientResource = clientsResource.get(clientId);
        RoleResource userRoleResource = clientResource.roles().get(roleName);
        RoleRepresentation roleRepresentation = userRoleResource.toRepresentation();

        if(roleRepresentation != null){
            userResource.roles().clientLevel(clientId).add(Collections.singletonList(roleRepresentation));

            System.out.printf("Assigned to user '%s' the role '%s' with roleId '%s'",
                    userRepresentation.getUsername(), roleRepresentation.getName(), roleRepresentation.getId());
        } else {
            System.out.println("Role '" + roleName + "' not found");
        }
    }


}