package de.ruu.lib.fx.comp;

import de.ruu.lib.util.AbstractEvent;
import lombok.extern.slf4j.Slf4j;

/** Event that can be thrown to indicate that a {@link FXCApp} has started successfully. */
@Slf4j
public abstract class FXComponentReadyEventAbstract<V extends FXCView<S>, S extends FXCService>
		extends AbstractEvent<V, S>
{
	public FXComponentReadyEventAbstract(final V view, final S service) {
		super(view, service);
	}

	public V view()
	{
		return source();
	}

	public S service()
	{
		return data().orElseThrow(() -> new IllegalStateException("data must not be absent"));
	}

	/** programmatically specify command line vm option {@code --add-reads de.ruu.lib.fx.comp=ALL-UNNAMED} */
	public static void addReadsUnnamedModule()
	{
		FXComponentReadyEventAbstract.class.getModule()
				.addReads(FXComponentReadyEventAbstract.class.getClassLoader().getUnnamedModule());
	}
}