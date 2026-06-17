package de.ruu.lib.jdbc.core;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent = true)
@RequiredArgsConstructor
public abstract class JDBCURL
{
	private final String host;
	private final int    port;
	private final String databaseName;

	public abstract String protocol();

	public String asString() { return protocol() + "://" + host + ":" + port + "/" + databaseName; }
}