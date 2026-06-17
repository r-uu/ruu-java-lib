package de.ruu.lib.gen.java.naming;

import static de.ruu.lib.util.StringBuilders.sb;

import de.ruu.lib.gen.Generator;
import lombok.Data;
import lombok.NonNull;

/**
 * Import statements look like {@code import x.y.z.T;} where x.y.z.T is the fully qualified name of type T. Import
 * statements can also define imports for static fields or static methods.
 */
public interface GeneratorImportStatement extends Generator
{
	@Data
	class GeneratorImportStatementAbstract implements GeneratorImportStatement
	{
		private final String fullyQualifiedName;

		@Override public StringBuilder generate() { return sb("import " + fullyQualifiedName + ";"); }
	}
	
	class GeneratorImportStatementSimple extends GeneratorImportStatementAbstract
	{
		public GeneratorImportStatementSimple(String fullyQualifiedName) { super(fullyQualifiedName); }
	}

	static GeneratorImportStatement create(@NonNull String fullyQualifiedName)
	{
		return new GeneratorImportStatementSimple(fullyQualifiedName);
	}

	static GeneratorImportStatement importStatement(@NonNull String fullyQualifiedName) { return create(fullyQualifiedName); }
}