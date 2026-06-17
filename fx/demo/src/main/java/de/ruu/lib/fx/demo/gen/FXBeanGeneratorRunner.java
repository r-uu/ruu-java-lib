package de.ruu.lib.fx.demo.gen;

import com.tngtech.archunit.core.importer.ClassFileImporter;
import de.ruu.lib.fx.demo.gen.input.FXBeanModel;
import de.ruu.lib.fx.demo.gen.input.FXBeanModelDTO;
import de.ruu.lib.gen.GeneratorException;
import de.ruu.lib.gen.java.fx.bean.FXBeanGenerator;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
class FXBeanGeneratorRunner
{
	public static void main(String[] args) throws GeneratorException, IOException
	{
		Class<?>        fxBeanModelClass       = FXBeanModel.class;
		Class<?>        fxBeanModelSourceClass = FXBeanModelDTO.class;
		FXBeanGenerator generator;

		log.debug("creating java fx bean for java fx bean model {}", fxBeanModelClass.getName());
		generator =
				new FXBeanGenerator
				(
						fxBeanModelClass.getPackageName(),
						"FXBean",
						new ClassFileImporter().importClass(fxBeanModelClass),
						new ClassFileImporter().importClass(fxBeanModelSourceClass)
				);
		log.debug("created  java fx bean for java fx bean model {}", fxBeanModelClass.getName());

		generator.run();
	}
}