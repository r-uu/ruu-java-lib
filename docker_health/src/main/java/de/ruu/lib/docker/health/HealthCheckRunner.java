package de.ruu.lib.docker.health;

import de.ruu.lib.docker.health.check.HealthCheck;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Runs multiple health checks and aggregates results.
 *
 * <p>This is the main entry point for performing health checks.
 * It can be configured with any combination of health checks and
 * provides formatted output with fix suggestions.
 *
 * <p><b>Example usage:</b>
 * <pre>
 * HealthCheckRunner runner = HealthCheckRunner.builder()
 *     .addCheck(new DockerDaemonHealthCheck())
 *     .addCheck(new PostgresDatabaseHealthCheck("postgres", "jeeeraaah", 5432))
 *     .addCheck(new KeycloakServerHealthCheck())
 *     .build();
 *
 * boolean allHealthy = runner.runAll();
 * if (!allHealthy) {
 *     List&lt;HealthCheckResult&gt; failures = runner.getFailures();
 *     // Handle failures
 * }
 * </pre>
 */
@Slf4j
public class HealthCheckRunner
{
	private final List<HealthCheck> checks;
	private final List<HealthCheckResult> results = new ArrayList<>();
	private boolean allHealthy = true;

	private HealthCheckRunner(List<HealthCheck> checks)
	{
		this.checks = new ArrayList<>(checks);
	}

	/**
	 * Runs all configured health checks.
	 *
	 * @return true if all checks passed, false otherwise
	 */
	public boolean runAll()
	{
		results.clear();
		allHealthy = true;

		log.info("""
				════════════════════════════════════════════════════════════════
				🏥 Docker Environment Health Check
				════════════════════════════════════════════════════════════════""");

		for (HealthCheck check : checks)
		{
			HealthCheckResult result = check.check();
			results.add(result);

			if (!result.isHealthy())
			{
				allHealthy = false;
			}
		}

		printResults();
		return allHealthy;
	}

	/**
	 * Returns all health check results.
	 */
	public List<HealthCheckResult> getResults()
	{
		return new ArrayList<>(results);
	}

	/**
	 * Returns only failed health check results.
	 */
	public List<HealthCheckResult> getFailures()
	{
		return results.stream()
				.filter(r -> !r.isHealthy())
				.toList();
	}

	/**
	 * Returns true if all checks passed.
	 */
	public boolean isHealthy()
	{
		return allHealthy;
	}

	private void printResults()
	{
		log.info("════════════════════════════════════════════════════════════════");

		if (allHealthy)
		{
			log.info("""
					✅ ALL SERVICES HEALTHY - Ready to start!
					════════════════════════════════════════════════════════════════""");
		}
		else
		{
			List<HealthCheckResult> failures = getFailures();
			log.error("❌ HEALTH CHECK FAILED - {} issue(s) found", failures.size());
			log.info("""
					════════════════════════════════════════════════════════════════
					
					🔧 HOW TO FIX:
					""");

			for (int i = 0; i < failures.size(); i++)
			{
				HealthCheckResult failure = failures.get(i);
				log.info("""
						Issue {}/{}: {}
						  Problem: {}
						  
						  ⚡ Quick fix (alias):
						     {}
						  
						  📝 Full command:
						     {}
						  """,
						i + 1, failures.size(), failure.getService(),
						failure.getProblem(),
						failure.getAlias(),
						failure.getFixCommand());
			}

			log.info("""
					════════════════════════════════════════════════════════════════
					💡 TIP: Copy & paste commands above to fix all issues
					════════════════════════════════════════════════════════════════""");
		}
	}

	/**
	 * Creates a new builder for configuring health checks.
	 */
	public static Builder builder()
	{
		return new Builder();
	}

	/**
	 * Builder for HealthCheckRunner.
	 */
	public static class Builder
	{
		private final List<HealthCheck> checks = new ArrayList<>();

		/**
		 * Adds a single health check.
		 */
		public Builder addCheck(HealthCheck check)
		{
			checks.add(check);
			return this;
		}

		/**
		 * Adds multiple health checks.
		 */
		public Builder addChecks(HealthCheck... checks)
		{
			this.checks.addAll(Arrays.asList(checks));
			return this;
		}

		/**
		 * Adds multiple health checks from a list.
		 */
		public Builder addChecks(List<HealthCheck> checks)
		{
			this.checks.addAll(checks);
			return this;
		}

		/**
		 * Builds the HealthCheckRunner.
		 */
		public HealthCheckRunner build()
		{
			return new HealthCheckRunner(checks);
		}
	}
}
