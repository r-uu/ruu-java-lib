package de.ruu.lib.jpa.se.hibernate.postgres.demo;

import de.ruu.lib.jpa.core.AbstractRepository;
import de.ruu.lib.jpa.se.hibernate.postgres.demo.HibernatePostgresDemoQualifier;
import jakarta.annotation.PostConstruct;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;

@Singleton
@Slf4j
class SimpleTypeRepository extends AbstractRepository<SimpleTypeEntity, Long>
{
	@HibernatePostgresDemoQualifier
	@Inject private EntityManager entityManager;

	@PostConstruct private void postConstruct() { log.debug("injected entity manager successfully: {}", entityManager != null); }

	@Override protected EntityManager entityManager() { return entityManager; }
}