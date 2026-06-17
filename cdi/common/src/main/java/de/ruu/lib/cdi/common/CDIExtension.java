package de.ruu.lib.cdi.common;

import jakarta.enterprise.event.Observes;
import jakarta.enterprise.inject.spi.AfterBeanDiscovery;
import jakarta.enterprise.inject.spi.AfterDeploymentValidation;
import jakarta.enterprise.inject.spi.BeanManager;
import jakarta.enterprise.inject.spi.BeforeBeanDiscovery;
import jakarta.enterprise.inject.spi.Extension;
import jakarta.enterprise.inject.spi.ProcessAnnotatedType;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class CDIExtension implements Extension
{
	void beforeBeanDiscovery(@Observes BeforeBeanDiscovery bbd)
	{
		log.debug("beginning the scanning process");
	}

	<T> void processAnnotatedType(@Observes ProcessAnnotatedType<T> pat)
	{
		log.debug("scanning type: " + pat.getAnnotatedType().getJavaClass().getName());
	}

	void afterDeploymentValidation(@Observes AfterDeploymentValidation adv, BeanManager beanManager)
	{
		List<String> beanClasses =
				beanManager
						.getBeans(Object.class)
						.stream()
	          .map(bean -> bean.getBeanClass().getName())
	          .sorted()
	          .toList();

    String logOutput = "finished the deployment validation process, managed beans:\n" + String.join("\n", beanClasses);

    log.debug(logOutput);
	}

	void afterBeanDiscovery(@Observes AfterBeanDiscovery abd)
	{
		log.debug("finished the scanning process");
	}
}
