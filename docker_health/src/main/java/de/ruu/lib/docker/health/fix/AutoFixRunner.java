package de.ruu.lib.docker.health.fix;

import de.ruu.lib.docker.health.HealthCheckResult;
import de.ruu.lib.docker.health.HealthCheckRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Orchestrates automatic fixes for failed health checks.
 *
 * <p>Uses registered {@link AutoFixStrategy} instances to attempt fixes.
 * After fixes are applied, re-runs health checks to verify success.</p>
 *
 * <h2>Example Usage:</h2>
 * <pre>{@code
 * HealthCheckRunner healthChecker = new HealthCheckRunner();
 * AutoFixRunner autoFix = new AutoFixRunner(healthChecker);
 *
 * // Register fix strategies
 * autoFix.registerStrategy(new DockerContainerStartStrategy());
 * autoFix.registerStrategy(new KeycloakRealmSetupStrategy());
 *
 * // Run health checks with auto-fix
 * if (!autoFix.runWithAutoFix()) {
 *     // Handle failure
 * }
 * }</pre>
 */
public class AutoFixRunner
{
	private static final Logger log = LoggerFactory.getLogger(AutoFixRunner.class);

	private final HealthCheckRunner healthCheckRunner;
	private final List<AutoFixStrategy> strategies = new ArrayList<>();
	private final int recheckDelaySeconds;

	/**
	 * Creates runner with default recheck delay (5 seconds).
	 *
	 * @param healthCheckRunner health check runner to use
	 */
	public AutoFixRunner(HealthCheckRunner healthCheckRunner)
	{
		this(healthCheckRunner, 5);
	}

	/**
	 * Creates runner with custom recheck delay.
	 *
	 * @param healthCheckRunner health check runner to use
	 * @param recheckDelaySeconds seconds to wait before re-checking after fixes
	 */
	public AutoFixRunner(HealthCheckRunner healthCheckRunner, int recheckDelaySeconds)
	{
		this.healthCheckRunner = healthCheckRunner;
		this.recheckDelaySeconds = recheckDelaySeconds;
	}

	/**
	 * Registers an auto-fix strategy.
	 *
	 * @param strategy the strategy to register
	 * @return this runner (for chaining)
	 */
	public AutoFixRunner registerStrategy(AutoFixStrategy strategy)
	{
		strategies.add(strategy);
		log.debug("Registered auto-fix strategy: {}", strategy.getDescription());
		return this;
	}

	/**
	 * Runs health checks and attempts auto-fix if failures detected.
	 *
	 * <p>Workflow:</p>
	 * <ol>
	 *   <li>Run health checks</li>
	 *   <li>If all pass: return {@code true}</li>
	 *   <li>If failures: attempt fixes using registered strategies</li>
	 *   <li>Re-run health checks</li>
	 *   <li>Return {@code true} if all pass, {@code false} otherwise</li>
	 * </ol>
	 *
	 * @return {@code true} if all health checks pass (after auto-fix if needed)
	 */
	public boolean runWithAutoFix()
	{
		// Initial health check
		if (healthCheckRunner.runAll())
		{
			log.debug("All health checks passed - no auto-fix needed");
			return true;
		}

		log.warn("⚠️ Health check failures detected - attempting auto-fix...");

		// Attempt fixes
		boolean anyFixAttempted = false;

		for (HealthCheckResult failure : healthCheckRunner.getFailures())
		{
			String serviceName = failure.getService();
			log.info("Attempting to fix: {}", serviceName);

			// Find matching strategy
			AutoFixStrategy strategy = findStrategy(serviceName);

			if (strategy == null)
			{
				log.warn("No auto-fix strategy available for: {}", serviceName);
				continue;
			}

			log.info("Using strategy: {}", strategy.getDescription());

			if (strategy.fix(serviceName))
			{
				log.info("✅ Fix successful for: {}", serviceName);
				anyFixAttempted = true;
			}
			else
			{
				log.error("❌ Fix failed for: {}", serviceName);
			}
		}

		// If no fixes were attempted, return failure
		if (!anyFixAttempted)
		{
			log.error("❌ No fixes could be attempted");
			return false;
		}

		// Wait before re-checking
		log.info("Waiting {}s before re-checking health...", recheckDelaySeconds);
		try
		{
			Thread.sleep(recheckDelaySeconds * 1000L);
		}
		catch (InterruptedException e)
		{
			Thread.currentThread().interrupt();
			log.warn("Interrupted while waiting: {}", e.getMessage());
		}

		// Re-run health checks
		log.info("Re-running health checks after auto-fix...");
		boolean success = healthCheckRunner.runAll();

		if (success)
		{
			log.info("✅ Auto-fix successful - all health checks passed!");
		}
		else
		{
			log.error("❌ Auto-fix incomplete - some health checks still failing");
		}

		return success;
	}

	/**
	 * Finds first strategy that can handle the given service.
	 *
	 * @param serviceName service name
	 * @return matching strategy or {@code null}
	 */
	private AutoFixStrategy findStrategy(String serviceName)
	{
		return strategies.stream()
			.filter(s -> s.canHandle(serviceName))
			.findFirst()
			.orElse(null);
	}

	/**
	 * Returns the underlying health check runner.
	 *
	 * @return health check runner
	 */
	public HealthCheckRunner getHealthCheckRunner()
	{
		return healthCheckRunner;
	}
}
