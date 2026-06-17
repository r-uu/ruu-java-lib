/**
 * Keycloak Admin Client module for user and realm management.
 */
module de.ruu.lib.keycloak.admin
{
	// Keycloak Admin Client (automatic modules)
	requires keycloak.admin.client;
	requires keycloak.client.common.synced;
	
	// JAX-RS API
	requires jakarta.ws.rs;
	
	// JAXB - Required by Keycloak Admin Client
	requires jakarta.xml.bind;
	requires transitive org.glassfish.jaxb.runtime;

	// Jackson - Required by Keycloak Admin Client
	requires com.fasterxml.jackson.core;
	requires com.fasterxml.jackson.databind;
	requires com.fasterxml.jackson.annotation;
	requires com.fasterxml.jackson.datatype.jdk8;
	requires com.fasterxml.jackson.datatype.jsr310;

	// Lombok needs java.beans for @Builder
	requires static lombok;
	requires java.desktop; // For java.beans package (used by Lombok)
	
	requires org.slf4j;

	exports de.ruu.lib.keycloak.admin;
	exports de.ruu.lib.keycloak.admin.setup;
	exports de.ruu.lib.keycloak.admin.validation;
}
