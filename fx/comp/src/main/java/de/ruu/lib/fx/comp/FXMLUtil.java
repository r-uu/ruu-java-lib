package de.ruu.lib.fx.comp;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
abstract class FXMLUtil
{
	static Parent loadFrom(final FXMLLoader loader)
	{
		// load from fxmlLoader
		try
		{
			final Object object = loader.load();

			if (object instanceof Parent)
			{
				log.debug("loaded {} object from {}", Parent.class.getName(), loader.getLocation().toExternalForm());
				return (Parent) object;
			}
			else
			{
				throw new IllegalStateException(
						"unexpected " + object.getClass() + " loaded from " + loader.getLocation().toExternalForm());
			}
		}
		catch (final IOException e)
		{
			throw new IllegalStateException("failure loading object from " + loader.getClass(), e);
		}
	}
}
