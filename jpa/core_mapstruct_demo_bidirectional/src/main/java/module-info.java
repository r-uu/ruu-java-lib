module de.ruu.lib.jpa.core.mapstruct.demo.bidirectional
{
	exports de.ruu.lib.jpa.core.mapstruct.demo.bidirectional;

	requires java.compiler;
	requires java.desktop;

	requires jakarta.annotation;
	requires jakarta.persistence;

	requires de.ruu.lib.jpa.core;
	requires de.ruu.lib.mapstruct;
	requires de.ruu.lib.jpa.core.mapstruct;
	requires de.ruu.lib.util;

	requires static lombok;
	requires org.slf4j;

	opens de.ruu.lib.jpa.core.mapstruct.demo.bidirectional to org.mapstruct;
}