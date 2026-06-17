package de.ruu.lib.util.csv;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

/**
 * Processes all csv files in a directory. Processing is configured by processors that can be provided by {@link
 * #csvFileProcessor(CSVFileProcessor)}.
 */
public interface CSVDirectoryProcessor
{
	void process(@NonNull Path csvDirectory);

	CSVDirectoryProcessor csvFileProcessor(@NonNull CSVFileProcessor processor);

	@Getter @Setter @Accessors(fluent = true)
	@Slf4j
	class CSVDirectoryProcessorSimple implements CSVDirectoryProcessor
	{
		private @NonNull CSVFileProcessor csvFileProcessor = CSVFileProcessor.create();

		/** Public constructor. */
		public CSVDirectoryProcessorSimple() { }

		@Override public void process(@NonNull Path csvDirectory)
		{
			try (Stream<Path> paths = Files.list(csvDirectory))
			{
				log.debug("processing directory {}", csvDirectory);
				paths.forEach(path -> csvFileProcessor.process(path));
				log.debug("processed  directory {}", csvDirectory);
			}
			catch (IOException e)
			{
				throw new RuntimeException("failure listing files in " + csvDirectory, e);
			}
		}
	}
	
	static CSVDirectoryProcessor create() { return new CSVDirectoryProcessorSimple(); }
}