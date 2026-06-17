package de.ruu.lib.util;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.nio.file.Paths;

@Slf4j
class SystemPropertiesTest
{
	@Test void test()
	{
		log.debug("user name: {}", SystemProperties.userName());
		log.debug("user home: {}", SystemProperties.userHome());
		log.debug("work dir : {}", Paths.get(".").toAbsolutePath());
	}
}