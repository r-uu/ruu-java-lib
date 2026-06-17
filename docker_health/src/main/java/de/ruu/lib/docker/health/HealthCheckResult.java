package de.ruu.lib.docker.health;

import lombok.Getter;

/**
 * Result of a health check.
 */
@Getter
public class HealthCheckResult
{
	private final boolean healthy;
	private final String service;
	private final String problem;
	private final String fixCommand;
	private final String alias;

	private HealthCheckResult(boolean healthy, String service, String problem, String fixCommand, String alias)
	{
		this.healthy = healthy;
		this.service = service;
		this.problem = problem;
		this.fixCommand = fixCommand;
		this.alias = alias;
	}

	/**
	 * Creates a successful health check result.
	 */
	public static HealthCheckResult success(String service)
	{
		return new HealthCheckResult(true, service, null, null, null);
	}

	/**
	 * Creates a failed health check result.
	 */
	public static HealthCheckResult failure(String service, String problem, String fixCommand, String alias)
	{
		return new HealthCheckResult(false, service, problem, fixCommand, alias);
	}
}
