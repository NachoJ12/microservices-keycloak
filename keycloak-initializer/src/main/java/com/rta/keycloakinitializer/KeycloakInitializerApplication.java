package com.rta.keycloakinitializer;

import com.rta.keycloakinitializer.service.KeycloakClientService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class KeycloakInitializerApplication implements CommandLineRunner {

	private KeycloakClientService keycloakClientService;

	public KeycloakInitializerApplication(KeycloakClientService keycloakClientService) {
		this.keycloakClientService = keycloakClientService;
	}

	public static void main(String[] args) {
		SpringApplication.run(KeycloakInitializerApplication.class, args);
	}


	@Override
	public void run(String... args) {
		keycloakClientService.createRealmAndConfigs("ecommerce-rta");

		System.exit(0);
	}
}
