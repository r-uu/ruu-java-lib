package de.ruu.lib.fx.comp;

import de.ruu.lib.cdi.se.EventDispatcher;
import de.ruu.lib.util.AbstractEvent;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.extern.slf4j.Slf4j;

/** Event that can be thrown to indicate that a {@link FXCApp} has started successfully. */
@Slf4j
public class FXCAppStartedEvent extends AbstractEvent<FXCApp, FXCView<? extends FXCService>>
{
	@ApplicationScoped
	public static class FXCAppStartedEventDispatcher extends EventDispatcher<FXCAppStartedEvent>
	{
	}

	public FXCAppStartedEvent(final FXCApp source, final FXCView<? extends FXCService> data) {
		super(source, data);
	}

	/** programmatically specify command line vm option {@code --add-reads de.ruu.lib.fx.comp=ALL-UNNAMED} */
	public static void addReadsUnnamedModule()
	{
		FXCAppStartedEvent.class.getModule().addReads(FXCAppStartedEvent.class.getClassLoader().getUnnamedModule());
	}
}