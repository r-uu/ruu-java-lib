package de.ruu.lib.util.config.mp;

import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.spi.ConfigProviderResolver;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;

class ConfigHealthCheckTest
{
	private Config testConfig;

	@BeforeEach
	void setUp()
	{
		// Create a test config with all required properties
		Map<String, String> props = new HashMap<>();

		// Database properties
		props.put("db.jeeeraaah.host", "localhost");
		props.put("db.jeeeraaah.port", "5432");
		props.put("db.jeeeraaah.database", "jeeeraaah");
		props.put("db.jeeeraaah.username", "jeeeraaah");
		props.put("db.jeeeraaah.password", "jeeeraaah");

		props.put("db.lib_test.host", "localhost");
		props.put("db.lib_test.port", "5432");
		props.put("db.lib_test.database", "lib_test");
		props.put("db.lib_test.username", "lib_test");
		props.put("db.lib_test.password", "lib_test");

		// Keycloak properties
		props.put("keycloak.url", "http://localhost:8080");
		props.put("keycloak.realm", "jeeeraaah-realm");
		props.put("keycloak.client.id", "jeeeraaah-frontend");
		props.put("keycloak.admin.username", "admin");
		props.put("keycloak.admin.password", "admin");

		// Service URLs
		props.put("service.backend.url", "http://localhost:9080/jeeeraaah");
		props.put("service.jasperreports.url", "http://localhost:8090");

		// Testing properties
		props.put("testing", "true");
		props.put("keycloak.test.user", "test");
		props.put("keycloak.test.password", "test");

		testConfig = new TestConfig(props);
	}

	@AfterEach
	void tearDown()
	{
		ConfigProviderResolver.instance().releaseConfig(testConfig);
	}

	@Test
	void testValidConfigurationPasses()
	{
		ConfigHealthCheck check = new ConfigHealthCheck(testConfig);
		ConfigHealthCheck.Result result = check.validate();

		if (!result.isHealthy())
		{
			System.out.println("Validation errors:");
			result.getErrors().forEach(System.out::println);
		}

		assertThat(result.isHealthy()).as("Valid configuration should pass").isTrue();
		assertThat(result.getErrors()).as("Should have no errors").isEmpty();
	}

	@Test
	void testMissingPropertyFails()
	{
		// Create config with missing property
		Map<String, String> props = new HashMap<>();
		props.put("db.jeeeraaah.host", "localhost");
		// Missing other db.jeeeraaah properties

		Config incompleteConfig = new TestConfig(props);
		ConfigHealthCheck check = new ConfigHealthCheck(incompleteConfig);
		ConfigHealthCheck.Result result = check.validate();

		assertThat(result.isHealthy()).as("Incomplete configuration should fail").isFalse();
		assertThat(result.getErrors()).as("Should have errors").isNotEmpty();
		assertThat(result.getErrors().stream()
				.anyMatch(e -> e.contains("db.jeeeraaah.port") || e.contains("db.jeeeraaah.database")))
				.as("Should report missing db.jeeeraaah properties").isTrue();
	}

	@Test
	void testEmptyPropertyFails()
	{
		Map<String, String> props = new HashMap<>();
		props.put("db.jeeeraaah.host", "");  // Empty value

		Config emptyValueConfig = new TestConfig(props);
		ConfigHealthCheck check = new ConfigHealthCheck(emptyValueConfig);
		ConfigHealthCheck.Result result = check.validate();

		assertThat(result.isHealthy()).as("Empty property values should fail").isFalse();
	}

	@Test
	void testPasswordsMaskedInOutput()
	{
		ConfigHealthCheck check = new ConfigHealthCheck(testConfig);
		ConfigHealthCheck.Result result = check.validate();

		Map<String, String> validated = result.getValidatedProperties();

		// Passwords should be masked
		assertThat(validated.get("db.jeeeraaah.password"))
				.as("Database password should be masked")
				.startsWith("***");
		assertThat(validated.get("keycloak.admin.password"))
				.as("Keycloak password should be masked")
				.startsWith("***");

		// Non-passwords should not be masked
		assertThat(validated.get("db.jeeeraaah.host"))
				.as("Host should not be masked")
				.isEqualTo("localhost");
	}

	@Test
	void testWeakPasswordWarningInProduction()
	{
		Map<String, String> props = new HashMap<>(getAllRequiredProperties());
		props.put("testing", "false");  // Production mode
		props.put("db.jeeeraaah.password", "weak");  // Weak password

		Config prodConfig = new TestConfig(props);
		ConfigHealthCheck check = new ConfigHealthCheck(prodConfig);
		ConfigHealthCheck.Result result = check.validate();

		assertThat(result.getWarnings())
				.as("Should have warnings about weak passwords")
				.isNotEmpty();
	}

	@Test
	void testNoPasswordWarningInTestingMode()
	{
		ConfigHealthCheck check = new ConfigHealthCheck(testConfig);
		ConfigHealthCheck.Result result = check.validate();

		// In testing mode, weak passwords are allowed without warnings
		assertThat(result.getWarnings().isEmpty() ||
				result.getWarnings().stream().noneMatch(w -> w.contains("password")))
				.as("Should not warn about passwords in testing mode")
				.isTrue();
	}

	@Test
	void testThrowIfUnhealthy()
	{
		Map<String, String> props = new HashMap<>();
		// Missing all properties

		Config emptyConfig = new TestConfig(props);
		ConfigHealthCheck check = new ConfigHealthCheck(emptyConfig);
		ConfigHealthCheck.Result result = check.validate();

		assertThatThrownBy(result::throwIfUnhealthy)
				.as("Should throw exception for unhealthy config")
				.isInstanceOf(IllegalStateException.class);
	}

	/**
	 * Helper to get all required properties for testing.
	 */
	private Map<String, String> getAllRequiredProperties()
	{
		Map<String, String> props = new HashMap<>();

		props.put("db.jeeeraaah.host", "localhost");
		props.put("db.jeeeraaah.port", "5432");
		props.put("db.jeeeraaah.database", "jeeeraaah");
		props.put("db.jeeeraaah.user", "jeeeraaah");
		props.put("db.jeeeraaah.password", "jeeeraaah");

		props.put("db.lib_test.host", "localhost");
		props.put("db.lib_test.port", "5432");
		props.put("db.lib_test.database", "lib_test");
		props.put("db.lib_test.user", "jeeeraaah");
		props.put("db.lib_test.password", "jeeeraaah");

		props.put("keycloak.url", "http://localhost:8080");
		props.put("keycloak.realm", "jeeeraaah-realm");
		props.put("keycloak.client.id", "jeeeraaah-frontend");
		props.put("keycloak.admin.user", "admin");
		props.put("keycloak.admin.password", "admin");

		props.put("service.backend.url", "http://localhost:9080/jeeeraaah");
		props.put("service.jasperreports.url", "http://localhost:8090");

		props.put("testing", "true");
		props.put("keycloak.test.user", "jeeeraaah");
		props.put("keycloak.test.password", "jeeeraaah");

		return props;
	}
}