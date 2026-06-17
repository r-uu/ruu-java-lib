package de.ruu.lib.gen.java.fx.comp;

import java.io.IOException;
import java.nio.file.Path;

import de.ruu.lib.gen.GeneratorException;
import de.ruu.lib.util.Files;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GeneratorFXCViewCSS
{
	private String packageName;
	private String simpleFileName;

	public GeneratorFXCViewCSS(String packageName, String simpleFileName)
	{
		this.packageName = packageName;
		this.simpleFileName = simpleFileName;
	}

	public void run() throws GeneratorException, IOException
	{
		Path path =
				Path.of(
						"src/gen/resources", 
						Files.toDirectoryName(packageName),
						simpleFileName + ".css");
		Files.writeToFile("", path);
		log.debug("wrote {}", path.toAbsolutePath());
	}
}