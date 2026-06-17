package de.ruu.lib.archunit;

import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaField;
import com.tngtech.archunit.core.domain.JavaMethod;
import de.ruu.lib.util.Strings;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;

import java.util.Optional;

/**
 * looks for java bean style or, if not present, for fluent style getter and setter
 */
@Getter
@Accessors(fluent = true)
public class FieldWithAccessors
{
	private final @NonNull JavaField javaField;

	private final Optional<JavaMethod> getter;
	private final Optional<JavaMethod> setter;

	public FieldWithAccessors(@NonNull JavaField javaField)
	{
		this.javaField = javaField;

		getter = findGetter(javaField);
		setter = findSetter(javaField);
	}


	/**
	 * looks for getter (java bean style or fluent style)
	 * @param javaField
	 * @return getter or empty optional if none was found
	 */
	private Optional<JavaMethod> findGetter(@NonNull JavaField javaField)
	{
		JavaClass clazz = javaField.getOwner();

		// Try Java Bean style first (getXxx or isXxx for boolean)
		Optional<JavaMethod> result =
				clazz.tryGetMethod("get" + Strings.firstLetterToUpperCase(javaField.getName()));

		if (result.isPresent()) return result;

		// Try fluent style
		result = clazz.tryGetMethod(javaField.getName());

		if (result.isPresent()) return result;

		return Optional.empty();
	}

	/**
	 * looks for setter (java bean style or fluent style)
	 * @param javaField
	 * @return setter or empty optional if none was found
	 */
	private Optional<JavaMethod> findSetter(@NonNull JavaField javaField)
	{
		JavaClass clazz = javaField.getOwner();

		// Try Java Bean style first
		Optional<JavaMethod> result =
				clazz.tryGetMethod(
						"set" + Strings.firstLetterToUpperCase(javaField.getName()), javaField.getType().getName());

		if (result.isPresent()) return result;

		// Try fluent style
		result = clazz.tryGetMethod(javaField.getName(), javaField.getType().getName());

		if (result.isPresent()) return result;

		return Optional.empty();
	}
}
