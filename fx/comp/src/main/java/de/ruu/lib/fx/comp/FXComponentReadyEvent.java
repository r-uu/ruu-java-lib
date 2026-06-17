package de.ruu.lib.fx.comp;

import lombok.extern.slf4j.Slf4j;

/** Event that can be thrown to indicate that a {@link FXCApp} has started successfully. */
@Slf4j
public class FXComponentReadyEvent extends FXComponentReadyEventAbstract<FXCView<FXCService>, FXCService>
{
	public FXComponentReadyEvent(final FXCView<FXCService> view, final FXCService service) {
		super(view, service);
	}

	// public FXCView<FXCService> view() { return source(); }
	//
	// public FXCService service() { return data().orElseThrow(() -> new IllegalStateException("data must not be
	// absent"));}

	/** programmatically specify command line vm option {@code --add-reads de.ruu.lib.fx.comp=ALL-UNNAMED} */
	public static void addReadsUnnamedModule()
	{
		FXComponentReadyEvent.class.getModule().addReads(FXComponentReadyEvent.class.getClassLoader().getUnnamedModule());
	}
}