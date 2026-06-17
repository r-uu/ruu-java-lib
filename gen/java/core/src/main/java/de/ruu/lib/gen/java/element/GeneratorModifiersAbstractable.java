package de.ruu.lib.gen.java.element;

import javax.lang.model.element.Element;

import de.ruu.lib.gen.java.context.CompilationUnitContext;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;
import lombok.experimental.Accessors;

/** generator for modifiers of java {@link Element}s that may be abstract */
public interface GeneratorModifiersAbstractable extends GeneratorModifiers
{
	/** @throws IllegalArgumentException if param value is true and isFinal is true */
	GeneratorModifiers setAbstract(boolean isAbstract) throws IllegalArgumentException;
	boolean isAbstract();

	@Getter
	@Accessors(fluent = true)
	@ToString class GeneratorModifiersAbstractableAbstract
			extends GeneratorModifiersAbstract implements GeneratorModifiersAbstractable
	{
		private boolean isAbstract;
		
		protected GeneratorModifiersAbstractableAbstract(@NonNull CompilationUnitContext context) { super(context); }
	
		@Override public GeneratorModifiers setAbstract(boolean isAbstract) throws IllegalArgumentException
		{
			if (isAbstract && isFinal() ) throw new IllegalArgumentException("illegal combination of abstract and final");
			if (isAbstract && isStatic()) throw new IllegalArgumentException("illegal combination of abstract and static");
			this.isAbstract = isAbstract;
			return this;
		}
	}
	class GeneratorModifiersAbstractableSimple extends GeneratorModifiersAbstractableAbstract
	{
		public GeneratorModifiersAbstractableSimple(@NonNull CompilationUnitContext context) { super(context); }
	}

	static GeneratorModifiersAbstractable create(               @NonNull CompilationUnitContext context)
			{ return new GeneratorModifiersAbstractableSimple(context); }
	static GeneratorModifiersAbstractable modifiersAbstractable(@NonNull CompilationUnitContext context)
			{ return create(context); }
}