package de.ruu.lib.fx.demo.gen;

import java.io.IOException;

import de.ruu.lib.fx.demo.gen.input.FXBeanModel;
import de.ruu.lib.gen.GeneratorException;
import de.ruu.lib.gen.java.fx.comp.GeneratorFXCompBundle;
import lombok.extern.slf4j.Slf4j;

@Slf4j
class RunnerRUUFXDemoComponentBundleGenerator
{
	public static void main(String[] args) throws IOException, GeneratorException
	{
		Class<?>              fxBeanModelClass = FXBeanModel.class;
		GeneratorFXCompBundle generator;

		log.debug("creating java fx component bundle for java fx bean model {}", fxBeanModelClass.getName());
		generator =
				new GeneratorFXCompBundle
				(
						fxBeanModelClass.getPackageName(),
						"RUUFXDemoComponentMain"
				);
		generator.run();
		log.debug("created  java fx component bundle for java fx bean model {}", fxBeanModelClass.getName());
	}
}