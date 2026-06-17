package de.ruu.lib.gen.java;

import static de.ruu.lib.util.Files.writeToFile;

import java.io.IOException;
import java.nio.file.Path;

import de.ruu.lib.util.Files;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

public interface CompilationUnitFileWriter
{
	/** file name extension for java compilation units */
	String FILE_EXTENSION = ".java";

	Path DEFAULT_ROOT_DIRECTORY = Path.of(".", "src/gen/java");

	CompilationUnitFileWriter root(          @NonNull Path root);
	CompilationUnitFileWriter packageName(   @NonNull String packageName);
	CompilationUnitFileWriter simpleFileName(@NonNull String simpleFileName);

	void write(@NonNull String output) throws IOException;

	@Slf4j
	@Getter
	@Accessors(fluent = true)
	abstract class CompilationUnitFileWriterAbstract implements CompilationUnitFileWriter
	{
		private Path root = DEFAULT_ROOT_DIRECTORY;

		private String packageName;
		private String simpleFileName;

		protected CompilationUnitFileWriterAbstract(
				@NonNull String packageName, @NonNull String simpleFileName)
		{
			if (packageName   .isEmpty()) throw new IllegalArgumentException(    "package name may not be empty");
			if (simpleFileName.isEmpty()) throw new IllegalArgumentException("simple file name may not be empty");
			
			this.packageName    = packageName;
			this.simpleFileName = simpleFileName;
		}

		@Override public CompilationUnitFileWriter root(@NonNull Path root) // TODO validation?
		{
			this.root = root;
			return this;
		}

		@Override public CompilationUnitFileWriter packageName(@NonNull String packageName)
		{
			if (packageName   .isEmpty()) throw new IllegalArgumentException(    "package name may not be empty");
			this.packageName = packageName;
			return this;
		}

		@Override public CompilationUnitFileWriter simpleFileName(@NonNull String simpleFileName)
		{
			if (simpleFileName.isEmpty()) throw new IllegalArgumentException("simple file name may not be empty");
			this.simpleFileName = simpleFileName;
			return this;
		}

		@Override public void write(@NonNull String output) throws IOException
		{
			Path path =
					Path.of(
							root.toString(),
							Files.toDirectoryName(packageName),
							simpleFileName + CompilationUnitFileWriter.FILE_EXTENSION);
			writeToFile(output, path);
			log.debug("wrote {}", path.toAbsolutePath());
		}
	}
	
	class CompilationUnitFileWriterSimple extends CompilationUnitFileWriterAbstract
	{
		protected CompilationUnitFileWriterSimple(@NonNull String packageName, @NonNull String simpleFileName)
		{ super(packageName, simpleFileName); }
	}

	static CompilationUnitFileWriter create(@NonNull String packageName, @NonNull String simpleFileName)
			{ return new CompilationUnitFileWriterSimple(packageName, simpleFileName); }
	static CompilationUnitFileWriter writer(@NonNull String packageName, @NonNull String simpleFileName)
			{ return create(packageName, simpleFileName); }
}