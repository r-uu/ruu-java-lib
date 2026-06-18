package de.ruu.lib.jpa.se.hibernate;

import static org.hibernate.cfg.AvailableSettings.JAKARTA_JDBC_URL;
import static org.hibernate.cfg.AvailableSettings.JAKARTA_JDBC_DRIVER;
import static org.hibernate.cfg.AvailableSettings.DIALECT;
import static org.hibernate.cfg.AvailableSettings.FORMAT_SQL;
import static org.hibernate.cfg.AvailableSettings.GENERATE_STATISTICS;
import static org.hibernate.cfg.AvailableSettings.HBM2DDL_AUTO;
import static org.hibernate.cfg.AvailableSettings.QUERY_STARTUP_CHECKING;
import static org.hibernate.cfg.AvailableSettings.SHOW_SQL;
import static org.hibernate.cfg.AvailableSettings.STATEMENT_BATCH_SIZE;
import static org.hibernate.cfg.AvailableSettings.USE_QUERY_CACHE;
import static org.hibernate.cfg.AvailableSettings.USE_SECOND_LEVEL_CACHE;
import static org.hibernate.cfg.AvailableSettings.USE_STRUCTURED_CACHE;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Accessors;
import org.hibernate.cfg.AvailableSettings;

@Data
@Builder
@Accessors(fluent = true)
public class PersistenceUnitProperties
{
	public enum HBM2DLLAuto
	{
		NONE       ("none"),
		CREATE_ONLY("save-only"),
		DROP       ("drop"),
//		CREATE     ("save"),      // on startup performs drop, then save
		CREATE     ("create"),      // on startup performs drop, then save
//		CREATE_DROP("save-drop"), // on startup performs drop, then save, on shutdown performs drop
		CREATE_DROP("create-drop"), // on startup performs drop, then save, on shutdown performs drop
		VALIDATE   ("validate"),
		UPDATE     ("update"),
		;

		public final String value;

		HBM2DLLAuto(String value) { this.value = value; }
	}

	private Class<?>    dialect;
	private boolean     formatSQL;
	private boolean     generateStatistics;
	private HBM2DLLAuto hbm2ddlAuto;
	private Class<?>    jdbcDriver;
	private String      jdbcURL;
	private boolean     queryStartupChecking;
	private boolean     showSQL;
	private int         statementBatchSize;
	private boolean     useQueryCache;
	private boolean     useSecondLevelCache;
	private boolean     useStructuredCache;

	/** @return with @code {@code {@link #formatSQL()} == true} and {@code {@link #showSQL()} == true} */
	public static PersistenceUnitProperties defaultProperties()
	{
		return
				PersistenceUnitProperties
						.builder()
								.formatSQL(true)
								.showSQL(  true)
						.build();
	}

	public Map<String, Object> properties()
	{
		Map<String, Object> result = new HashMap<>();

		result.put(FORMAT_SQL,               formatSQL());
		result.put(GENERATE_STATISTICS,      generateStatistics());
		result.put(JAKARTA_JDBC_URL,         jdbcURL());
		result.put(QUERY_STARTUP_CHECKING,   queryStartupChecking());
		result.put(SHOW_SQL,                 showSQL());
		result.put(USE_QUERY_CACHE,          useQueryCache());
		result.put(USE_SECOND_LEVEL_CACHE,   useSecondLevelCache());
		result.put(USE_STRUCTURED_CACHE,     useStructuredCache());
		result.put(STATEMENT_BATCH_SIZE,     statementBatchSize());

		if (dialect     != null) result.put(DIALECT            , dialect.getName());
		if (jdbcDriver  != null) result.put(JAKARTA_JDBC_DRIVER, jdbcDriver.getName());
		if (hbm2ddlAuto != null) result.put(HBM2DDL_AUTO       , hbm2ddlAuto.value);

		return Collections.unmodifiableMap(result);
	}
}