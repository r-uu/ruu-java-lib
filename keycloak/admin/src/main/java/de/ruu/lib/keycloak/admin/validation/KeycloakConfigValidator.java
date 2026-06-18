package de.ruu.lib.keycloak.admin.validation;

import lombok.Builder;
import lombok.Singular;
import lombok.Value;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Validates consistency between Keycloak configuration, backend REST APIs, and client REST calls.
 * 
 * <p>This utility helps identify common configuration issues such as:
 * <ul>
 *   <li>Missing roles in Keycloak that are required by backend @RolesAllowed annotations</li>
 *   <li>Audience mismatches between JWT tokens and backend configuration</li>
 *   <li>Role naming inconsistencies (e.g., "taskgroup-read" vs "task-group-read")</li>
 *   <li>Token lifetime configuration issues</li>
 * </ul>
 * 
 * @author r-uu
 * @since 2025-12-27
 */
@Slf4j
public class KeycloakConfigValidator
{
	/**
	 * Validates that all required roles exist in the Keycloak token.
	 * 
	 * @param tokenRoles roles extracted from JWT token (realm_access.roles claim)
	 * @param requiredRoles roles required by the backend (from @RolesAllowed annotations)
	 * @return validation result with any missing roles
	 */
	public static RoleValidationResult validateRoles(Set<String> tokenRoles, Set<String> requiredRoles)
	{
		Set<String> missingRoles = new HashSet<>(requiredRoles);
		missingRoles.removeAll(tokenRoles);
		
		Set<String> extraRoles = new HashSet<>(tokenRoles);
		extraRoles.removeAll(requiredRoles);
		
		boolean valid = missingRoles.isEmpty();
		
		return RoleValidationResult.builder()
				.valid(valid)
				.tokenRoles(tokenRoles)
				.requiredRoles(requiredRoles)
				.missingRoles(missingRoles)
				.extraRoles(extraRoles)
				.build();
	}
	
	/**
	 * Validates that the token audience matches the expected backend audience.
	 * 
	 * @param tokenAudiences audiences from JWT token (aud claim)
	 * @param expectedAudience audience configured in backend server.xml
	 * @return validation result
	 */
	public static AudienceValidationResult validateAudience(
			Set<String> tokenAudiences, 
			String expectedAudience)
	{
		boolean valid = tokenAudiences.contains(expectedAudience);
		
		return AudienceValidationResult.builder()
				.valid(valid)
				.tokenAudiences(tokenAudiences)
				.expectedAudience(expectedAudience)
				.build();
	}
	
	/**
	 * Detects potential role naming inconsistencies.
	 * 
	 * <p>Common issues:
	 * <ul>
	 *   <li>Keycloak uses "task-group-read" but backend uses "taskgroup-read"</li>
	 *   <li>Mixed conventions within the same application</li>
	 * </ul>
	 * 
	 * @param roles all roles to check
	 * @return detected naming issues
	 */
	public static NamingConsistencyResult validateRoleNamingConsistency(Set<String> roles)
	{
		List<String> dashRoles = roles.stream()
				.filter(r -> r.contains("-") && !r.startsWith("default-"))
				.collect(Collectors.toList());
		
		List<String> suspiciousRoles = new ArrayList<>();
		Map<String, String> suggestions = new HashMap<>();
		
		for (String role : roles)
		{
			// Check if a similar role with dashes exists
			for (String dashRole : dashRoles)
			{
				String normalized = dashRole.replace("-", "");
				if (role.replace("-", "").equalsIgnoreCase(normalized) && !role.equals(dashRole))
				{
					suspiciousRoles.add(role);
					suggestions.put(role, dashRole);
				}
			}
		}
		
		return NamingConsistencyResult.builder()
				.consistent(suspiciousRoles.isEmpty())
				.suspiciousRoles(suspiciousRoles)
				.suggestions(suggestions)
				.build();
	}
	
	/**
	 * Validates token lifetime settings.
	 * 
	 * @param accessTokenLifetimeSeconds access token lifetime in seconds
	 * @param refreshTokenLifetimeSeconds refresh token lifetime in seconds
	 * @return validation result with warnings for potentially problematic values
	 */
	public static TokenLifetimeValidationResult validateTokenLifetime(
			int accessTokenLifetimeSeconds,
			int refreshTokenLifetimeSeconds)
	{
		List<String> warnings = new ArrayList<>();
		
		// Access token too short (less than 1 minute)
		if (accessTokenLifetimeSeconds < 60)
		{
			warnings.add(String.format(
					"Access token lifetime very short (%ds). May cause frequent re-authentication.", 
					accessTokenLifetimeSeconds));
		}
		
		// Access token too long (more than 30 minutes)
		if (accessTokenLifetimeSeconds > 1800)
		{
			warnings.add(String.format(
					"Access token lifetime long (%ds = %d min). Consider shorter lifetime for better security.", 
					accessTokenLifetimeSeconds, accessTokenLifetimeSeconds / 60));
		}
		
		// Refresh token shorter than access token
		if (refreshTokenLifetimeSeconds < accessTokenLifetimeSeconds)
		{
			warnings.add(String.format(
					"Refresh token lifetime (%ds) shorter than access token (%ds). This is unusual.",
					refreshTokenLifetimeSeconds, accessTokenLifetimeSeconds));
		}
		
		// Refresh token too short (less than 5 minutes)
		if (refreshTokenLifetimeSeconds < 300)
		{
			warnings.add(String.format(
					"Refresh token lifetime short (%ds). May cause session interruptions.",
					refreshTokenLifetimeSeconds));
		}
		
		return TokenLifetimeValidationResult.builder()
				.accessTokenLifetimeSeconds(accessTokenLifetimeSeconds)
				.refreshTokenLifetimeSeconds(refreshTokenLifetimeSeconds)
				.warnings(warnings)
				.build();
	}
	
	// ===== Result Classes =====
	
	@Value
	@Builder
	public static class RoleValidationResult
	{
		boolean valid;
		Set<String> tokenRoles;
		Set<String> requiredRoles;
		Set<String> missingRoles;
		Set<String> extraRoles;
		
		public String summary()
		{
			if (valid)
			{
				return "✓ All required roles are present in token";
			}
			else
			{
				return String.format("✗ Missing %d required role(s): %s",
						missingRoles.size(),
						String.join(", ", missingRoles));
			}
		}

		public List<String> recommendations()
		{
			if (valid) return List.of();

			List<String> recommendations = new ArrayList<>();
			recommendations.add("Add missing roles in Keycloak Admin Console:");
			for (String role : missingRoles)
			{
				recommendations.add(String.format(
						"  docker exec keycloak /opt/keycloak/bin/kcadm.sh create roles " +
						"-r pragma-realm -s name=%s", role));
			}
			return recommendations;
		}
	}

	@Value
	@Builder
	public static class AudienceValidationResult
	{
		boolean valid;
		Set<String> tokenAudiences;
		String expectedAudience;

		public String summary()
		{
			if (valid)
			{
				return String.format("✓ Token audience contains expected value '%s'", expectedAudience);
			}
			else
			{
				return String.format("✗ Token audience %s does not contain expected '%s'",
						tokenAudiences, expectedAudience);
			}
		}

		public List<String> recommendations()
		{
			if (valid) return List.of();

			List<String> recommendations = new ArrayList<>();
			recommendations.add("Configure audience mapper in Keycloak:");
			recommendations.add(String.format(
					"  1. Navigate to: Clients → pragma-frontend → Client scopes → pragma-frontend-dedicated"));
			recommendations.add("  2. Add mapper → By configuration → Audience");
			recommendations.add(String.format("  3. Set 'Included Custom Audience' = %s", expectedAudience));
			return recommendations;
		}
	}

	@Value
	@Builder
	public static class NamingConsistencyResult
	{
		boolean consistent;
		@Singular List<String> suspiciousRoles;
		Map<String, String> suggestions;

		public String summary()
		{
			if (consistent)
			{
				return "✓ Role naming is consistent";
			}
			else
			{
				return String.format("⚠ Detected %d potential naming inconsistenc%s",
						suspiciousRoles.size(),
						suspiciousRoles.size() == 1 ? "y" : "ies");
			}
		}

		public List<String> recommendations()
		{
			if (consistent) return List.of();

			List<String> recommendations = new ArrayList<>();
			recommendations.add("Consider standardizing role names:");
			for (Map.Entry<String, String> entry : suggestions.entrySet())
			{
				recommendations.add(String.format("  '%s' → '%s'", entry.getKey(), entry.getValue()));
			}
			return recommendations;
		}
	}

	@Value
	@Accessors(fluent = true)
	@Builder
	public static class TokenLifetimeValidationResult
	{
		int accessTokenLifetimeSeconds;
		int refreshTokenLifetimeSeconds;
		@Singular List<String> warnings;

		public String summary()
		{
			if (warnings.isEmpty())
			{
				return String.format("✓ Token lifetimes configured (access=%ds, refresh=%ds)",
						accessTokenLifetimeSeconds, refreshTokenLifetimeSeconds);
			}
			else
			{
				return String.format("⚠ Token lifetime has %d warning(s)", warnings.size());
			}
		}

		public List<String> recommendations()
		{
			if (warnings.isEmpty()) return List.of();

			List<String> recommendations = new ArrayList<>();
			recommendations.add("Review token lifetime configuration in Keycloak:");
			recommendations.add("  Realm Settings → Tokens → Access Token Lifespan");
			recommendations.add("  Realm Settings → Tokens → Refresh Token Lifespan");
			return recommendations;
		}
	}
	
	/**
	 * Comprehensive validation result combining all checks.
	 */
	@Value
	@Builder
	public static class ValidationReport
	{
		RoleValidationResult roleValidation;
		AudienceValidationResult audienceValidation;
		NamingConsistencyResult namingConsistency;
		TokenLifetimeValidationResult tokenLifetime;
		
		public boolean fullyValid()
		{
			return roleValidation.isValid()
					&& audienceValidation.isValid()
					&& namingConsistency.isConsistent()
					&& (tokenLifetime == null || tokenLifetime.warnings().isEmpty());
		}

		public String detailedReport()
		{
			StringBuilder report = new StringBuilder();
			report.append("Keycloak Configuration Validation Report\n");
			report.append("=".repeat(60)).append("\n\n");

			report.append("Role Validation:\n");
			report.append("  ").append(roleValidation.summary()).append("\n");
			if (!roleValidation.isValid())
			{
				roleValidation.recommendations().forEach(r -> report.append("  ").append(r).append("\n"));
			}
			report.append("\n");

			report.append("Audience Validation:\n");
			report.append("  ").append(audienceValidation.summary()).append("\n");
			if (!audienceValidation.isValid())
			{
				audienceValidation.recommendations().forEach(r -> report.append("  ").append(r).append("\n"));
			}
			report.append("\n");

			report.append("Role Naming Consistency:\n");
			report.append("  ").append(namingConsistency.summary()).append("\n");
			if (!namingConsistency.isConsistent())
			{
				namingConsistency.recommendations().forEach(r -> report.append("  ").append(r).append("\n"));
			}
			report.append("\n");

			if (tokenLifetime != null)
			{
				report.append("Token Lifetime:\n");
				report.append("  ").append(tokenLifetime.summary()).append("\n");
				if (!tokenLifetime.warnings().isEmpty())
				{
					tokenLifetime.warnings().forEach(w -> report.append("  ⚠ ").append(w).append("\n"));
					tokenLifetime.recommendations().forEach(r -> report.append("  ").append(r).append("\n"));
				}
			}

			report.append("\n");
			report.append("=".repeat(60)).append("\n");
			report.append("Overall Status: ").append(fullyValid() ? "✓ VALID" : "✗ ISSUES DETECTED").append("\n");

			return report.toString();
		}
	}
}
