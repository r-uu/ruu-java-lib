package de.ruu.lib.docker.health.check;

import de.ruu.lib.docker.health.HealthCheckResult;
import de.ruu.lib.util.config.mp.ConfigHealthCheck;
import lombok.extern.slf4j.Slf4j;

/**
 * Health check for MicroProfile Config properties.
 * Validates that all required configuration properties are present.
 *
 * <p><b>Checks:</b>
 * <ul>
 *   <li>Database connection properties (jeeeraaah, lib_test)</li>
 *   <li>Keycloak authentication properties</li>
 *   <li>Service URLs</li>
 *   <li>Testing mode properties</li>
 * </ul>
 */
@Slf4j
public class ConfigurationHealthCheck implements HealthCheck
{
	@Override
	public String getName()
	{
		return "Configuration";
	}

	@Override
	public HealthCheckResult check()
	{
		log.info("Checking configuration properties...");

		try
		{
			ConfigHealthCheck configCheck = new ConfigHealthCheck();
			ConfigHealthCheck.Result result = configCheck.validate();

			if (result.isHealthy())
			{
				log.info("  ✅ All required configuration properties are present");
				return HealthCheckResult.success("Configuration");
			}
			else
			{
				log.error("  ❌ Configuration validation failed:");
				result.getErrors().forEach(error -> log.error("    {}", error));

				return HealthCheckResult.failure(
						"Configuration",
						"Missing or invalid properties: " + String.join(", ", result.getErrors()),
						"Check testing.properties in project root",
						"ruu-config-validate"
				);
			}
		}
		catch (Exception e)
		{
			log.error("  ❌ Configuration check failed with exception", e);
			return HealthCheckResult.failure(
					"Configuration",
					"Failed to validate configuration: " + e.getMessage(),
					"Ensure testing.properties exists in project root",
					"ruu-config-setup"
			);
		}
	}
}
