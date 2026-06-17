package de.ruu.lib.util.csv;

import java.nio.file.Path;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

public interface CSVFileProcessor
{
	void process(@NonNull Path csvFile);
	
	CSVFileProcessor csvRowProcessor(@NonNull CSVRowProcessor processor);

	@Slf4j
	class CSVFileProcessorSimple implements CSVFileProcessor
	{
		private @NonNull CSVRowProcessor processor = CSVRowProcessor.create();

		/** Public constructor. */
		public CSVFileProcessorSimple() { }

		@Override public void process(@NonNull Path csvFile)
		{
			log.debug("processing file {}", csvFile.toAbsolutePath().toString());
		}

		@Override public CSVFileProcessor csvRowProcessor(@NonNull CSVRowProcessor processor)
		{
			this.processor = processor;
			return this;
		}
	}
	
	static CSVFileProcessor create() { return new CSVFileProcessorSimple(); }
}