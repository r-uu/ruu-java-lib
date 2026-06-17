package de.ruu.lib.keycloak.admin.validation;

import de.ruu.lib.keycloak.admin.validation.KeycloakConfigValidator.*;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.assertj.core.api.Assertions.*;

/**
 * Unit tests for {@link KeycloakConfigValidator}.
 * 
 * @author r-uu
 * @since 2025-12-27
 */
class KeycloakConfigValidatorTest
{
	@Test
	void testValidateRoles_AllPresent()
	{
		Set<String> tokenRoles = Set.of("task-read", "task-write", "task-delete");
		Set<String> requiredRoles = Set.of("task-read", "task-write");
		
		RoleValidationResult result = KeycloakConfigValidator.validateRoles(tokenRoles, requiredRoles);
		
		assertThat(result.isValid()).isTrue();
		assertThat(result.getMissingRoles()).isEmpty();
		assertThat(result.getExtraRoles()).hasSize(1);
		assertThat(result.getExtraRoles()).contains("task-delete");
	}
	
	@Test
	void testValidateRoles_MissingRoles()
	{
		Set<String> tokenRoles = Set.of("task-read");
		Set<String> requiredRoles = Set.of("task-read", "task-write", "task-delete");
		
		RoleValidationResult result = KeycloakConfigValidator.validateRoles(tokenRoles, requiredRoles);
		
		assertThat(result.isValid()).isFalse();
		assertThat(result.getMissingRoles()).hasSize(2);
		assertThat(result.getMissingRoles()).contains("task-write");
		assertThat(result.getMissingRoles()).contains("task-delete");
		assertThat(result.getExtraRoles()).isEmpty();

		assertThat(result.getRecommendations()).isNotEmpty();
		assertThat(result.getSummary()).contains("Missing");
	}
	
	@Test
	void testValidateAudience_Valid()
	{
		Set<String> tokenAudiences = Set.of("jeeeraaah-backend", "account");
		String expectedAudience = "jeeeraaah-backend";
		
		AudienceValidationResult result = KeycloakConfigValidator
				.validateAudience(tokenAudiences, expectedAudience);
		
		assertThat(result.isValid()).isTrue();
		assertThat(result.getExpectedAudience()).isEqualTo("jeeeraaah-backend");
	}
	
	@Test
	void testValidateAudience_Missing()
	{
		Set<String> tokenAudiences = Set.of("account");
		String expectedAudience = "jeeeraaah-backend";
		
		AudienceValidationResult result = KeycloakConfigValidator
				.validateAudience(tokenAudiences, expectedAudience);
		
		assertThat(result.isValid()).isFalse();
		assertThat(result.getRecommendations()).isNotEmpty();
		assertThat(result.getSummary()).contains("does not contain");
	}
	
	@Test
	void testValidateRoleNamingConsistency_Consistent()
	{
		Set<String> roles = Set.of("task-read", "task-write", "taskgroup-read", "taskgroup-write");
		
		NamingConsistencyResult result = KeycloakConfigValidator
				.validateRoleNamingConsistency(roles);
		
		assertThat(result.isConsistent()).isTrue();
		assertThat(result.getSuspiciousRoles()).isEmpty();
		assertThat(result.getSuggestions()).isEmpty();
	}
	
	@Test
	void testValidateRoleNamingConsistency_Inconsistent()
	{
		// Use roles where "taskgroup-read" conflicts with "task-group-read"
		Set<String> roles = Set.of("task-read", "task-write", "taskgroup-read", "task-group-read");
		
		NamingConsistencyResult result = KeycloakConfigValidator
				.validateRoleNamingConsistency(roles);
		
		assertThat(result.isConsistent()).isFalse();
		assertThat(result.getSuspiciousRoles()).isNotEmpty();

		// Both "taskgroup-read" and "task-group-read" should be flagged as conflicting
		assertThat(result.getRecommendations()).isNotEmpty();
	}
	
	@Test
	void testValidateTokenLifetime_Optimal()
	{
		int accessTokenLifetime = 300;  // 5 minutes
		int refreshTokenLifetime = 1800; // 30 minutes
		
		TokenLifetimeValidationResult result = KeycloakConfigValidator
				.validateTokenLifetime(accessTokenLifetime, refreshTokenLifetime);
		
		assertThat(result.getWarnings()).isEmpty();
		assertThat(result.getAccessTokenLifetimeSeconds()).isEqualTo(300);
		assertThat(result.getRefreshTokenLifetimeSeconds()).isEqualTo(1800);
	}
	
	@Test
	void testValidateTokenLifetime_TooShort()
	{
		int accessTokenLifetime = 30;  // 30 seconds - too short
		int refreshTokenLifetime = 60;  // 1 minute - too short
		
		TokenLifetimeValidationResult result = KeycloakConfigValidator
				.validateTokenLifetime(accessTokenLifetime, refreshTokenLifetime);
		
		assertThat(result.getWarnings()).isNotEmpty();
		assertThat(result.getSummary()).contains("warning");

		// Should have warnings about short lifetimes
		assertThat(result.getWarnings().stream()
				.anyMatch(w -> w.contains("very short"))).isTrue();
	}
	
	@Test
	void testValidateTokenLifetime_RefreshShorterThanAccess()
	{
		int accessTokenLifetime = 600;  // 10 minutes
		int refreshTokenLifetime = 300; // 5 minutes - shorter than access!
		
		TokenLifetimeValidationResult result = KeycloakConfigValidator
				.validateTokenLifetime(accessTokenLifetime, refreshTokenLifetime);
		
		assertThat(result.getWarnings()).isNotEmpty();
		assertThat(result.getWarnings().stream()
				.anyMatch(w -> w.contains("shorter than access token"))).isTrue();
	}
	
	@Test
	void testValidationReport_FullyValid()
	{
		RoleValidationResult roleValidation = KeycloakConfigValidator
				.validateRoles(Set.of("task-read"), Set.of("task-read"));
		
		AudienceValidationResult audienceValidation = KeycloakConfigValidator
				.validateAudience(Set.of("jeeeraaah-backend"), "jeeeraaah-backend");
		
		NamingConsistencyResult namingConsistency = KeycloakConfigValidator
				.validateRoleNamingConsistency(Set.of("task-read", "task-write"));
		
		TokenLifetimeValidationResult tokenLifetime = KeycloakConfigValidator
				.validateTokenLifetime(300, 1800);
		
		ValidationReport report = ValidationReport.builder()
				.roleValidation(roleValidation)
				.audienceValidation(audienceValidation)
				.namingConsistency(namingConsistency)
				.tokenLifetime(tokenLifetime)
				.build();
		
		assertThat(report.isFullyValid()).isTrue();
		
		String detailedReport = report.getDetailedReport();
		assertThat(detailedReport).isNotNull();
		assertThat(detailedReport).contains("Overall Status");
		assertThat(detailedReport).contains("VALID");
	}
	
	@Test
	void testValidationReport_WithIssues()
	{
		RoleValidationResult roleValidation = KeycloakConfigValidator
				.validateRoles(Set.of("task-read"), Set.of("task-read", "task-write")); // Missing role
		
		AudienceValidationResult audienceValidation = KeycloakConfigValidator
				.validateAudience(Set.of("account"), "jeeeraaah-backend"); // Wrong audience
		
		NamingConsistencyResult namingConsistency = KeycloakConfigValidator
				.validateRoleNamingConsistency(Set.of("task-read"));
		
		TokenLifetimeValidationResult tokenLifetime = KeycloakConfigValidator
				.validateTokenLifetime(30, 60); // Too short
		
		ValidationReport report = ValidationReport.builder()
				.roleValidation(roleValidation)
				.audienceValidation(audienceValidation)
				.namingConsistency(namingConsistency)
				.tokenLifetime(tokenLifetime)
				.build();
		
		assertThat(report.isFullyValid()).isFalse();

		String detailedReport = report.getDetailedReport();
		assertThat(detailedReport).isNotNull();
		assertThat(detailedReport).contains("ISSUES DETECTED");
		assertThat(detailedReport).contains("task-write"); // Missing role
	}
}