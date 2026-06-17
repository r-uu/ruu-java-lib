package de.ruu.lib.cdi.common;

import jakarta.enterprise.inject.spi.CDI;
import lombok.NonNull;

public abstract class CDIUtil
{
	public static <T> void fire (@NonNull T event) { CDI.current().getBeanManager().getEvent().fire(event); }
  public static <T> T select(@NonNull Class<T> type) { return CDI.current().select(type).get(); }
}