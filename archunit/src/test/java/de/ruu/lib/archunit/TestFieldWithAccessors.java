package de.ruu.lib.archunit;

import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TestFieldWithAccessors
{
	@Getter
	@Setter
	@Accessors(fluent = true)
	private static class ClassWithFluentAccessorsOnly
	{
		private boolean booleanField;
	}

	@Test void testFieldsAndAccessors()
	{
		JavaClass clazz = new ClassFileImporter().importClass(ClassWithFluentAccessorsOnly.class);

		FieldWithAccessors fieldAndAccessors =
				new FieldWithAccessors(clazz.getAllFields().iterator().next());

		// For fluent style accessors, the getter and setter should be present
		assertThat(fieldAndAccessors.getter().isPresent()).isEqualTo(true);
		assertThat(fieldAndAccessors.setter().isPresent()).isEqualTo(true);
	}
}