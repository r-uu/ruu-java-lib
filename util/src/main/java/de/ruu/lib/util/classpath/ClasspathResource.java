package de.ruu.lib.util.classpath;

import static de.ruu.lib.util.BooleanFunctions.not;
import static java.util.Objects.isNull;

/** Classpath resource with name {@link #getResourceName()} that exists inside {@link #getResourceContainer()}. */
public abstract class ClasspathResource
{
	private ResourceContainer resourcecontainer;

	private String resourcename;

	protected ClasspathResource(ResourceContainer resourcecontainer, String resourcename)
	{
		this.resourcecontainer = resourcecontainer;
		this.resourcename = resourcename;
	}

	public ResourceContainer getResourceContainer() { return resourcecontainer; }

	public String getResourceName() { return resourcename; }

	public abstract boolean isDirectory();

	public static boolean isResourceAvailableTo(String resource, ClassLoader classLoader)
	{
		return not(isNull(classLoader.getResource(resource)));
//		return classLoader.getResource(resource) != null;
//		URL url = classLoader.getResource(resource);
//		if (isNull(url)) return false;
//		return url.toString().startsWith(classLoader.getResource("").toString());
	}

	public static boolean isResourceAvailableToClassLoaderOf(String resource, Class<?> clazz)
	{
		return isResourceAvailableTo(resource, clazz.getClassLoader());
	}
}