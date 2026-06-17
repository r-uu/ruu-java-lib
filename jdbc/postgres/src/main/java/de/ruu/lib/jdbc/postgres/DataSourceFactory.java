package de.ruu.lib.jdbc.postgres;

import de.ruu.lib.jdbc.core.JDBCURL;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.postgresql.ds.PGSimpleDataSource;
import org.postgresql.ds.common.BaseDataSource;

import javax.sql.DataSource;

@RequiredArgsConstructor
public class DataSourceFactory
{
	@NonNull private final JDBCURL url;
	@NonNull private final String  user;
	@NonNull private final String  password;

	public DataSource create()
	{
		BaseDataSource result = new PGSimpleDataSource();

		result.setURL(     url.asString());
		result.setUser(    user);
		result.setPassword(password);

		return (DataSource) result;
	}
}