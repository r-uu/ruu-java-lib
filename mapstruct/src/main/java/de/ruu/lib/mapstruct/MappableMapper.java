package de.ruu.lib.mapstruct;

import lombok.NonNull;
import org.mapstruct.Context;

public interface MappableMapper<IN extends Mappable<IN, OUT>, OUT extends Mappable<OUT, IN>>
{
	@NonNull OUT map(@NonNull IN in);

	default void beforeMapping(@NonNull IN in, @NonNull @Context OUT out)
	{
		out.beforeMapping(out, in);
	}

	default void afterMapping(@NonNull IN in, @NonNull @Context OUT out)
	{
		out.afterMapping(out, in);
	}
}