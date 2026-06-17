package de.ruu.lib.util.csv;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

public interface CSVRowProcessor
{
	void process(@NonNull String row);

	@Slf4j
	class CSVRowProcessorSimple implements CSVRowProcessor
	{
		/** Public constructor. */
		public CSVRowProcessorSimple() { }

		@Override public void process(@NonNull String row) { log.debug("processing row {}", row); }
	}
	
	static CSVRowProcessor create() { return new CSVRowProcessorSimple(); }
}