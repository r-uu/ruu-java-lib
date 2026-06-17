package de.ruu.lib.util.classpath;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public interface ResourceContainer
{
	Map<String, ClasspathResource> getClassPathResources() throws IOException;

	File getResourceContainerFile();
}