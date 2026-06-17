package de.ruu.lib.fx.comp.buttons;

import de.ruu.lib.gen.GeneratorException;
import de.ruu.lib.gen.java.fx.comp.GeneratorFXCompBundle;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
public class ButtonsGenerator
{
	public static void main(String[] args) throws IOException, GeneratorException
	{
		generateButtonAdd();
	}

	private static void generateButtonAdd() throws GeneratorException, IOException

	{
		log.debug("generating add button");
		GeneratorFXCompBundle generator =
				new GeneratorFXCompBundle
						(
								"de.ruu.lib.fx.control.buttons",
								"Add"
						);

		generator.run();
	}
}