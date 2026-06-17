package de.ruu.lib.junit;

import jakarta.inject.Inject;
import lombok.Builder;
import lombok.Getter;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@Getter
@Builder
public class HostPortConfigExtension// implements BeforeAllCallback
{
	@Inject
	@ConfigProperty(name = "host", defaultValue = "localhost")
	private String host;
	@Inject
	@ConfigProperty(name = "port", defaultValue = "9080")
	private int    port;
}