package de.ruu.lib.mapstruct;

import lombok.NonNull;

public interface Mappable<IN extends Mappable<IN, OUT>, OUT extends Mappable<OUT, IN>>
{
	void beforeMapping(@NonNull IN in, @NonNull OUT out);
	void  afterMapping(@NonNull IN in, @NonNull OUT out);
}