package com.rta.keycloakinitializer.service;

import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmsResource;
import org.keycloak.admin.client.resource.RolesResource;
import org.keycloak.representations.idm.RealmRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class KeycloakClientService {

    private final Keycloak keycloak;

    public KeycloakClientService(Keycloak keycloak) {
        this.keycloak = keycloak;
    }

    public void createRealmAndConfigs(String realmName) {
       createRealm(realmName);
       setRealmLevelRoles(realmName, List.of("APP_USER", "APP_ADMIN"));


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



}