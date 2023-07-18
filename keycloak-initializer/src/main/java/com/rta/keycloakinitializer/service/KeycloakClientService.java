package com.rta.keycloakinitializer.service;

import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.RealmRepresentation;
import org.springframework.stereotype.Service;


@Service
public class KeycloakClientService {

    private final Keycloak keycloak;

    public KeycloakClientService(Keycloak keycloak) {
        this.keycloak = keycloak;
    }

    public void createRealmAndConfigs(String realmName) {
       createRealm(realmName);
    }

    private void createRealm(String realmName){
        RealmRepresentation realm = new RealmRepresentation();
        realm.setRealm(realmName);
        realm.setEnabled(true);
        keycloak.realms().create(realm);
        System.out.println("\nRealm " + realmName + " create successful");
    }

}